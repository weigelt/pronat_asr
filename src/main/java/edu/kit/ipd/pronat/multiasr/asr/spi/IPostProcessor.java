package edu.kit.ipd.pronat.multiasr.asr.spi;

import edu.kit.ipd.pronat.multiasr.asr.ASROutput;

@FunctionalInterface
public interface IPostProcessor {
	public ASROutput process(ASROutput asrOutput);
}
