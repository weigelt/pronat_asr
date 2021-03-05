package edu.kit.ipd.parse.multiasr.asr;

import java.io.IOException;
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

import com.darkprograms.speech.recognizer.GoogleResponse;
import com.darkprograms.speech.recognizer.Recognizer;
import com.google.common.base.Strings;

import edu.kit.ipd.parse.audio.AudioFormat;
import edu.kit.ipd.parse.luna.data.token.HypothesisTokenType;
import edu.kit.ipd.parse.luna.data.token.MainHypothesisToken;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.multiasr.MultiASRPipelineStage;

/**
 * Created by Me on 02.04.16.
 */
public class GoogleASR extends AbstractASR {

	//TODO clean up --> Tokenizing --> Hypothesis creation
	Properties props;

	private final String ID = "Google";

	private static final String API_KEY_PROP = "APIKEY";

	private static Set<String> capabilities = Capability.toCapabilites(Capability.N_BEST);

	private static final int SAMPLE_RATE = MultiASRPipelineStage.SAMPLE_RATE;

	private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("(.*?)([,\\.])?(\\s|(?<!\\G)\\z)+");

	private static Set<AudioFormat> formats = new CopyOnWriteArraySet<>(Arrays.asList(new AudioFormat() {
		//44100, 16, 1, true, false
		@Override
		public String getFormat() {
			return "flac";
		}

		@Override
		public int getChannels() {
			return 1;
		}

		@Override
		public int getSamplingRate() {
			return SAMPLE_RATE;
		}
	}));

	public GoogleASR() {
		super();
		super.setIdentifier(ID);
		props = ConfigManager.getConfiguration(getClass());
	}

	@Override
	public List<ASROutput> recognize(URI uri, Path audio, Map<String, String> capabilites) {
		try {

			final StringBuilder sb = new StringBuilder();

			capabilites.forEach((k, v) -> sb.append(k + "!" + v + "+"));

			GoogleResponse response;

			Recognizer recognizer;
			//TODO: creat init method for ASRs and move stuff like that
			final String apiKey = props.getProperty(API_KEY_PROP);
			if (Strings.isNullOrEmpty(apiKey)) {
				throw new RuntimeException("ApiKey not set");
			}
			recognizer = new Recognizer(Recognizer.Languages.AUTO_DETECT, apiKey);

			if (capabilites.containsKey(Capability.identifiers.N_BEST)) {
				response = recognizer.getRecognizedDataForFlac(audio.toFile(),
						Integer.valueOf(capabilites.get(Capability.identifiers.N_BEST)), SAMPLE_RATE);
			} else {
				response = recognizer.getRecognizedDataForFlac(audio.toFile(), 1, SAMPLE_RATE);
			}

			if (response == null) {
				logger().error("Could not get google response");
			}

			if (response != null) {
				final List<ASROutput> output = new ArrayList<>();
				for (final String possibility : response.getAllPossibleResponses()) {
					final String confidence = response.getConfidence();
					System.out.println(possibility);
					parse(possibility, output, confidence);
				}
				return output;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			logger().error("Could not get google response");
		}
		return null;
	}

	private void parse(String transcript, List<ASROutput> output, String confidence) {
		if (Strings.isNullOrEmpty(transcript)) {
			return;
		}

		float conf;

		if (!Strings.isNullOrEmpty(confidence)) {
			conf = Float.valueOf(confidence);
		} else {
			conf = 0;
		}

		final ASROutput out = new ASROutput(ID);

		final Matcher matcher = PUNCTUATION_PATTERN.matcher(transcript);

		int i = 0;

		while (matcher.find()) {
			final String text = matcher.group(1);

			if (!Strings.isNullOrEmpty(text)) {
				out.add(new MainHypothesisToken(text, i, conf, HypothesisTokenType.WORD));
				i++;
			}
			//TODO: ignore punctuation for now
			//			final String punctuation = matcher.group(2);
			//
			//			if (!Strings.isNullOrEmpty(punctuation)) {
			//				if (punctuation.equalsIgnoreCase(",")) {
			//					out.add(new MainHypothesisToken(text, i, conf, HypothesisTokenType.PUNCTUATION));
			//			i++;
			//				} else {
			//					out.add(new MainHypothesisToken(text, i, conf, HypothesisTokenType.PUNCTUATION));
			//			i++;
			//				}
			//			}
		}

		output.add(out);
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
