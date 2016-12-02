package edu.kit.ipd.parse.multiasr.asr;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.kit.ipd.parse.audio.AudioFormat;
import edu.kit.ipd.parse.luna.tools.ConfigManager;

public class NuanceASR extends AbstractASR {

	private final String ID = "Nuance";

	Properties props;

	public NuanceASR() {
		super();
		super.setIdentifier(ID);
		props = ConfigManager.getConfiguration(getClass());
	}

	@Override
	public List<ASROutput> recognize(URI uri, Path audio, Map<String, String> capabilites) {
		// TODO Auto-generated method stub
		// To be implemented soon.
		return null;
	}

	@Override
	public Set<AudioFormat> getSupportedAudioFormats() {
		// TODO Auto-generated method stub
		// To be implemented soon.
		return null;
	}

}
