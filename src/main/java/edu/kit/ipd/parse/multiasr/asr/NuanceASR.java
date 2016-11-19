package edu.kit.ipd.parse.multiasr.asr;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ipd.parse.audio.AudioFormat;

public class NuanceASR extends AbstractASR {

	public NuanceASR() {
		super("config-asr-nuance-master.xml");
	}

	@Override
	public List<ASROutput> recognize(URI uri, Path audio, Map<String, String> capabilites) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<AudioFormat> getSupportedAudioFormats() {
		// TODO Auto-generated method stub
		return null;
	}

}
