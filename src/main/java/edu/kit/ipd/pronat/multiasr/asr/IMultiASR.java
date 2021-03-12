package edu.kit.ipd.pronat.multiasr.asr;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import edu.kit.ipd.pronat.multiasr.asr.spi.IASR;
import edu.kit.ipd.pronat.multiasr.asr.spi.IPostProcessor;

public interface IMultiASR {
	public boolean register(IASR asr);

	public boolean registerPostProcessor(IPostProcessor pp);

	public List<ASROutput> recognize(Path audio);

	public List<ASROutput> recognize(Path sourceAudio, Map<String, String> requiredCapabilites, Map<String, String> optionalCapabilites);
}
