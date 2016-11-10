

import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ipd.parse.audio.AudioFormat;
import edu.kit.ipd.parse.multiasr.asr.ASROutput;
import edu.kit.ipd.parse.multiasr.asr.spi.IASR;

/**
 * Created by Me on 02.06.16.
 */
public class ASRFasade implements IASR {
	private static final Set<AudioFormat> AUDIO_FORMATS = Collections.singleton(new AudioFormat() {
		@Override
		public String getFormat() {
			return "flac";
		}
	});

	@Override
	public List<ASROutput> recognize(URI uri, Path audio, Map<String, String> capabilites) {
		/* TODO: new test */
		final ASROutput asrOutput = new ASROutput(null);
		//asrOutput.add(new HypothesisToken(word, position, confidence, type));
		//asrOutput.add(new Word(null, uri.toString()));
		return Collections.singletonList(asrOutput);
	}

	@Override
	public Set<AudioFormat> getSupportedAudioFormats() {
		return AUDIO_FORMATS;
	}

	@Override
	public Set<String> getSupportedCapabilities() {
		return Collections.singleton("N_BEST");
	}
}
