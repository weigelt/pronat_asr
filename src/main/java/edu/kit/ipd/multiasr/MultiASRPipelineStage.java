package edu.kit.ipd.multiasr;

import edu.kit.ipd.parse.luna.data.AbstractPipelineData;
import edu.kit.ipd.parse.luna.pipeline.IPipelineStage;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.multiasr.asr.GoogleASR;
import edu.kit.ipd.parse.multiasr.asr.MultiASR;
import edu.kit.ipd.parse.multiasr.asr.WatsonASR;

public class MultiASRPipelineStage implements IPipelineStage {

	private static final String ID = "multiasr";

	private MultiASR multiASR;
	private GoogleASR googleASR;
	private WatsonASR watsonASR;

	@Override
	public void init() {
		multiASR = new MultiASR();
		googleASR = new GoogleASR();
		watsonASR = new WatsonASR();
		multiASR.register(googleASR);
		multiASR.register(watsonASR);
		multiASR.autoRegisterPostProcessors();
	}

	@Override
	public void exec(AbstractPipelineData data) throws PipelineStageException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getID() {
		return ID;
	}

}
