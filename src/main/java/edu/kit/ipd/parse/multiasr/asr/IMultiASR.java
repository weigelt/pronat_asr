package edu.kit.ipd.parse.multiasr.asr;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ipd.parse.multiasr.asr.spi.IASR;
import edu.kit.ipd.parse.multiasr.asr.spi.IPostProcessor;

public interface IMultiASR {
	public boolean register(IASR asr);
	
	public boolean registerPostProcessor(IPostProcessor pp);

	public List<ASROutput> recognize(Path audio);

	public List<ASROutput> recognize(Path sourceAudio, Map<String, String> requiredCapabilites,
			Map<String, String> optionalCapabilites);
}
