package edu.kit.ipd.parse.multiasr.asr.spi;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ipd.parse.audio.AudioFormat;
import edu.kit.ipd.parse.multiasr.asr.ASROutput;

public interface IASR {	
	public List<ASROutput> recognize(URI uri, Path audio, Map<String, String> capabilites);

	public Set<AudioFormat> getSupportedAudioFormats();
	
	public default Set<String> getSupportedCapabilities() {
		return Collections.emptySet();
	};
}
