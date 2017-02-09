package edu.kit.ipd.parse.multiasr.asr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechWordAlternatives;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import edu.kit.ipd.parse.audio.AudioFormat;
import edu.kit.ipd.parse.luna.data.token.AlternativeHypothesisToken;
import edu.kit.ipd.parse.luna.data.token.HypothesisTokenType;
import edu.kit.ipd.parse.luna.data.token.MainHypothesisToken;
import edu.kit.ipd.parse.luna.tools.ConfigManager;

/**
 * Created by Me on 17.03.16.
 */
public class WatsonASR extends AbstractASR {
	Properties props;

	private final String ID = "IBM-WATSON";

	private static Pattern HESITATION_PATTERN = null;

	private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("(.*?)([,\\.])?(\\s|(?<!\\G)\\z)+");

	private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-z|']+");

	//Property constants
	private final String USERNAME_PROP = "USERNAME";
	private final String PASSWORD_PROP = "PASSWORD";
	private final String MODEL_PROP = "MODEL";
	//	private final String API_PROP = "API";
	//	private final String ENDPOINT_PROP = "ENDPOINT";
	private final String HESITATION_PATTERN_PROP = "HESITATION_PATTERN";

	private final List<SpeechResults> srList = new ArrayList<>();

	private static Set<String> capabilities = Capability.toCapabilites(Capability.N_BEST, Capability.CONFUSION_NETWORK, Capability.TIMINGS,
			Capability.WORD_CONFIDENCE);
	private static Set<AudioFormat> formats = new CopyOnWriteArraySet<>(Arrays.asList(new AudioFormat() {
		//44100, 16, 1, true, false
		@Override
		public String getFormat() {
			return "flac";
		}

		@Override
		public int getSamplingRate() {
			return 16000;
		}
	}));

	private final SpeechToText service;

	public WatsonASR() {
		super();
		super.setIdentifier(ID);
		this.service = new SpeechToText();
		props = ConfigManager.getConfiguration(getClass());
		service.setUsernameAndPassword(props.getProperty(USERNAME_PROP), props.getProperty(PASSWORD_PROP));
		HESITATION_PATTERN = Pattern.compile(props.getProperty(HESITATION_PATTERN_PROP));
	}

	@Override
	public List<ASROutput> recognize(URI uri, Path audio, Map<String, String> capabilites) {

		List<ASROutput> out = new ArrayList<>();

		final StringBuilder sb = new StringBuilder();

		capabilites.forEach((k, v) -> sb.append(k).append("!").append(v).append("+"));

		Integer nbest = 0;

		Double CNthreshold = 0d;

		boolean timestamps = false;

		boolean wordConfidence = false;

		if (capabilites.containsKey(Capability.identifiers.N_BEST)) {
			try {
				nbest = Integer.valueOf(capabilites.get(Capability.identifiers.N_BEST));
			} catch (final NumberFormatException e) {
				this.logger().warn("Invalid NBEST count - using default value");
				nbest = 5;
			}
		}

		if (capabilites.containsKey(Capability.identifiers.TIMINGS)) {
			timestamps = true;
		}
		if (capabilites.containsKey(Capability.identifiers.WORD_CONFIDENCE)) {
			wordConfidence = true;
		}

		if (capabilites.containsKey(Capability.identifiers.CONFUSION_NETWORK)) {
			try {
				CNthreshold = Double.valueOf(capabilites.get(Capability.identifiers.CONFUSION_NETWORK));
			} catch (final NumberFormatException e) {
				this.logger().warn("Invalid CN threshold - using default value");
				CNthreshold = 0.2d;
			}
		}

		//move all to config
		final RecognizeOptions recognizeOptions = new RecognizeOptions.Builder().continuous(true).wordConfidence(true)
				.profanityFilter(false).maxAlternatives(nbest).timestamps(timestamps).wordConfidence(wordConfidence)
				.wordAlternativesThreshold(CNthreshold).model(props.getProperty(MODEL_PROP)).contentType(HttpMediaType.AUDIO_FLAC)
				.interimResults(true).build();
		final BaseRecognizeCallback callback = new BaseRecognizeCallback() {
			@Override
			public void onTranscription(SpeechResults speechResults) {
				srList.add(speechResults);
			}

			@Override
			public void onDisconnected() {
				synchronized (ID) {
					ID.notifyAll();
				}
			}
		};

		try {
			service.recognizeUsingWebSocket(new FileInputStream(audio.toFile()), recognizeOptions, callback);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		synchronized (ID) {
			try {
				ID.wait();
				out = parseList();
			} catch (final InterruptedException e) {
				//when the object is interrupted
			}
		}
		return out;
	}

	protected List<ASROutput> parseList() {
		final List<ASROutput> out = new ArrayList<>();
		final Iterator<SpeechResults> srIterator = srList.iterator();
		while (srIterator.hasNext()) {
			final SpeechResults currSR = srIterator.next();
			if (srList.indexOf(currSR) + 1 < srList.size()) {
				if (currSR.getResultIndex() == srList.get(srList.indexOf(currSR) + 1).getResultIndex()) {
					srIterator.remove();
				}
			}
		}
		for (final SpeechResults sr : srList) {
			if (sr != null) {
				final List<Transcript> transcriptList = sr.getResults();
				for (final Transcript transcript : transcriptList) {
					final ASROutput asrOut = new ASROutput(ID);
					for (int i = 0; i < transcript.getWordAlternatives().size(); i++) {
						final SpeechWordAlternatives swa = transcript.getWordAlternatives().get(i);
						final double currStart = swa.getStartTime();
						final double currEnd = swa.getEndTime();
						final MainHypothesisToken currMainHyp = new MainHypothesisToken(swa.getAlternatives().get(0).getWord(), i,
								swa.getAlternatives().get(0).getConfidence(), checkType(swa.getAlternatives().get(0).getWord()), currStart,
								currEnd);
						for (int j = 1; j < swa.getAlternatives().size(); j++) {
							currMainHyp.addAlternative(new AlternativeHypothesisToken(swa.getAlternatives().get(j).getWord(), i,
									swa.getAlternatives().get(j).getConfidence(), checkType(swa.getAlternatives().get(0).getWord()),
									currStart, currEnd));
						}
						asrOut.add(currMainHyp);
					}
					out.add(asrOut);
				}
			}
		}
		return out;

	}

	public List<ASROutput> recognizeOld(URI uri, Path audio, Map<String, String> capabilites) {

		final List<ASROutput> out = new ArrayList<>();

		final StringBuilder sb = new StringBuilder();

		capabilites.forEach((k, v) -> sb.append(k).append("!").append(v).append("+"));

		final SpeechResults response;

		Integer nbest = 0;

		Double CNthreshold = 0d;

		boolean timestamps = false;

		boolean wordConfidence = false;

		if (capabilites.containsKey(Capability.identifiers.N_BEST)) {
			try {
				nbest = Integer.valueOf(capabilites.get(Capability.identifiers.N_BEST));
			} catch (final NumberFormatException e) {
				this.logger().warn("Invalid NBEST count - using default value");
				nbest = 5;
			}
		}

		if (capabilites.containsKey(Capability.identifiers.TIMINGS)) {
			timestamps = true;
		}
		if (capabilites.containsKey(Capability.identifiers.WORD_CONFIDENCE)) {
			wordConfidence = true;
		}

		if (capabilites.containsKey(Capability.identifiers.CONFUSION_NETWORK)) {
			try {
				CNthreshold = Double.valueOf(capabilites.get(Capability.identifiers.CONFUSION_NETWORK));
			} catch (final NumberFormatException e) {
				this.logger().warn("Invalid CN threshold - using default value");
				CNthreshold = 0.2d;
			}
		}

		//move all to config
		final RecognizeOptions recognizeOptions = new RecognizeOptions.Builder().continuous(true).wordConfidence(true)
				.profanityFilter(false).maxAlternatives(nbest).timestamps(timestamps).wordConfidence(wordConfidence)
				.wordAlternativesThreshold(CNthreshold).model(props.getProperty(MODEL_PROP)).contentType(HttpMediaType.AUDIO_FLAC).build();

		response = service.recognize(audio.toFile(), recognizeOptions).execute();

		if (response != null) {
			final Transcript transcript = response.getResults().get(0);
			final ASROutput asrOut = new ASROutput(ID);
			for (int i = 0; i < transcript.getWordAlternatives().size(); i++) {
				final SpeechWordAlternatives swa = transcript.getWordAlternatives().get(i);
				final double currStart = swa.getStartTime();
				final double currEnd = swa.getEndTime();
				final MainHypothesisToken currMainHyp = new MainHypothesisToken(swa.getAlternatives().get(0).getWord(), i,
						swa.getAlternatives().get(0).getConfidence(), checkType(swa.getAlternatives().get(0).getWord()), currStart,
						currEnd);
				for (int j = 1; j < swa.getAlternatives().size(); j++) {
					currMainHyp.addAlternative(new AlternativeHypothesisToken(swa.getAlternatives().get(j).getWord(), i,
							swa.getAlternatives().get(j).getConfidence(), checkType(swa.getAlternatives().get(0).getWord()), currStart,
							currEnd));
				}
				asrOut.add(currMainHyp);
			}
			out.add(asrOut);
		} else {
			logger().warn("Could not get Watson ASR response!");
		}
		return out;
	}

	private HypothesisTokenType checkType(String token) {
		final Matcher pm = PUNCTUATION_PATTERN.matcher(token);
		final Matcher hm = HESITATION_PATTERN.matcher(token);
		final Matcher wm = WORD_PATTERN.matcher(token);
		if (pm.find()) {
			return HypothesisTokenType.PUNCTUATION;
		} else if (hm.find()) {
			return HypothesisTokenType.HESITATION;
		} else if (wm.find()) {
			return HypothesisTokenType.WORD;
		} else {
			return HypothesisTokenType.MISC;
		}

	}

	@Override
	public Set<AudioFormat> getSupportedAudioFormats() {
		return formats;
	}

	@Override
	public Set<String> getSupportedCapabilities() {
		return capabilities;
	}
}
