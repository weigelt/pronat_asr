package edu.kit.ipd.parse.multiasr.asr;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kohsuke.MetaInfServices;

import com.google.common.base.Strings;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

import edu.kit.ipd.parse.audio.AudioFormat;
import edu.kit.ipd.parse.luna.data.token.HypothesisToken;
import edu.kit.ipd.parse.luna.data.token.HypothesisTokenType;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.multiasr.asr.spi.IASR;

/**
 * Created by Me on 17.03.16.
 */
@MetaInfServices(IASR.class)
public class WatsonASR extends AbstractASR {
	//TODO: migrate to 3.5.0 or higher
	Properties props;

	private final String ID = "IBM-WATSON";

	private static Pattern HESITATION_PATTERN = Pattern.compile("%HESITATION", Pattern.CASE_INSENSITIVE);

	//Property constants
	private final String USERNAME_PROP = "USERNAME";
	private final String PASSWORD_PROP = "PASSWORD";
	private final String LANGUAGE_PROP = "LANGUAGE";
	private final String API_PROP = "API";
	private final String ENDPOINT_PROP = "ENDPOINT";

	private static Set<String> capabilities = Capability.toCapabilites(Capability.N_BEST, Capability.CONFUSION_NETWORK, Capability.TIMINGS, Capability.WORD_CONFIDENCE);
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
		super.setIdentifier(ID);
		this.service = new SpeechToText();
		props = ConfigManager.getConfiguration(getClass());
		service.setEndPoint(props.getProperty(ENDPOINT_PROP));
		service.setUsernameAndPassword(props.getProperty(USERNAME_PROP), props.getProperty(PASSWORD_PROP));
	}

	@Override
	public List<ASROutput> recognize(URI uri, Path audio, Map<String, String> capabilites) {
		final StringBuilder sb = new StringBuilder();

		capabilites.forEach((k, v) -> sb.append(k).append("!").append(v).append("+"));

		SpeechResults response;

		final RecognizeOptions recognizeOptions = new RecognizeOptions();
		recognizeOptions.continuous(true);
		recognizeOptions.contentType(HttpMediaType.AUDIO_FLAC);
		if (capabilites.containsKey(Capability.identifiers.N_BEST)) {
			try {
				final Integer nbest = Integer.valueOf(capabilites.get(Capability.identifiers.N_BEST));
				recognizeOptions.maxAlternatives(nbest);
			} catch (final NumberFormatException e) {
				this.logger().warn("Invalid NBEST count - using default value");
				recognizeOptions.maxAlternatives(5);
			}
		}
		if (capabilites.containsKey(Capability.identifiers.CONFUSION_NETWORK)) {
			try {
				recognizeOptions.wordAlternativesThreshold(Double.valueOf(capabilites.get(Capability.identifiers.CONFUSION_NETWORK)));
			} catch (final NumberFormatException e) {
				this.logger().warn("Invalid CN threshold - using default value");
				recognizeOptions.wordAlternativesThreshold(0.2d);
			}
		}
		if (capabilites.containsKey(Capability.identifiers.TIMINGS)) {
			recognizeOptions.timestamps(true);
		}
		if (capabilites.containsKey(Capability.identifiers.WORD_CONFIDENCE)) {
			recognizeOptions.wordConfidence(true);
		}
		//TODO: left out profanity filter because version 2.9.0 don't have it
		//recognizeOptions.profanityFilter(false);
		//recognizeOptions.model("en-UK_BroadbandModel");
		//recognizeOptions.interimResults(true);
		response = this.service.recognize(audio.toFile(), recognizeOptions);

		if (response != null) {
			final List<ASROutput> output = new ArrayList<>();

			int max = 0;

			for (final Transcript transcript : response.getResults()) {
				if (transcript.getAlternatives().size() > max) {
					max = transcript.getAlternatives().size();
				}
			}

			for (int i = 0; i < max; ++i) {
				final ASROutput asrOutput = new ASROutput(ID);

				for (final Transcript transcript : response.getResults()) {
					final int size = transcript.getAlternatives().size();

					asrOutput.addAll(parse(transcript.getAlternatives().get(i % size)));
				}

				output.add(asrOutput);
			}

			return output;
		}

		return null;
	}

	private List<HypothesisToken> parse(SpeechAlternative speechAlternative) {
		final List<HypothesisToken> out = new ArrayList<HypothesisToken>();

		final String transcript = speechAlternative.getTranscript();

		final Matcher matcher = HESITATION_PATTERN.matcher(transcript);

		int last = 0;

		final float score = (float) (speechAlternative.getConfidence() != null ? speechAlternative.getConfidence() : 0);

		int index = 0;

		while (matcher.find()) {
			System.out.println("Hello!");
			final int start = matcher.start();
			if (start > 0) {
				final String current = transcript.substring(last, start).trim();
				System.out.println("Current: " + current);
				if(!Strings.isNullOrEmpty(current)) {
					out.add(new HypothesisToken(current, index, score, HypothesisTokenType.WORD));
				}
			}

			out.add(new HypothesisToken(HESITATION_PATTERN.pattern(), index, score, HypothesisTokenType.HESITATION));

			last = matcher.end();
			index++;
		}

		//		final Word token = new Word(null, transcript.substring(last).trim());
		//		if (score != null) {
		//			token.add(Score.class, score);
		//		}
		out.add(new HypothesisToken(transcript.substring(last).trim(), index, score, HypothesisTokenType.WORD));
		//		final Delimiter delimiter = new Delimiter(null, Delimiter.Type.SENTENCE_END);
		//		if (score != null) {
		//			delimiter.add(Score.class, score);
		//		}
		out.add(new HypothesisToken(".", index, score, HypothesisTokenType.PUNCTUATION));

		return out;
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
