package edu.kit.ipd.parse.multiasr.asr.spi;

import edu.kit.ipd.parse.multiasr.asr.ASROutput;

@FunctionalInterface
public interface IPostProcessor {
	public ASROutput process(ASROutput asrOutput);
}
