package edu.kit.ipd.parse.multiasr;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.kit.ipd.parse.multiasr.asr.GoogleASR;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.luna.data.AbstractPipelineData;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PipelineDataCastException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.data.token.MainHypothesisToken;
import edu.kit.ipd.parse.luna.pipeline.IPipelineStage;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.multiasr.asr.ASROutput;
import edu.kit.ipd.parse.multiasr.asr.WatsonASR;

@MetaInfServices(IPipelineStage.class)
public class MultiASRPipelineStage implements IPipelineStage {

	private static final String ID = "multiasr";

	private static final Logger logger = LoggerFactory.getLogger(MultiASRPipelineStage.class);

	private PrePipelineData prePipeData;

	private HashMap<String, String> capabilities;

	//private MultiASR multiASR;
	// only take Watson for the time beeing
	private GoogleASR googleASR;
	//private WatsonASR watsonASR;

	@Override
	public void init() {
		logger.info("Initializing Multi ASR...");
		//multiASR = new MultiASR();
		googleASR = new GoogleASR();
		//watsonASR = new WatsonASR();
		// watson and google both do nbest
		capabilities = new HashMap<>();
		capabilities.put("NBEST", "5");
		// only take Watson for the time beeing
		//		multiASR.register(googleASR);
		//multiASR.register(watsonASR);
		//multiASR.autoRegisterPostProcessors();
		logger.info("...Done!");
	}

	@Override
	public void exec(AbstractPipelineData data) throws PipelineStageException {
		// try to get data as pre pipeline data. If this fails, return
		logger.info("Running Multi ASR");
		try {
			prePipeData = data.asPrePipelineData();
		} catch (final PipelineDataCastException e) {
			logger.error("Cannot process on data - PipelineData unreadable", e);
			throw new PipelineStageException(e);
		}
		Path inputFilePath;
		try {
			inputFilePath = prePipeData.getInputFilePath();
		} catch (final MissingDataException e) {
			logger.error("No input file defined... Aborting!", e);
			throw new PipelineStageException(e);
		}
		final URI uri = inputFilePath.toUri();
		//		final List<ASROutput> recognize = watsonASR.recognize(null, Paths.get(uri), capabilities);
		final List<ASROutput> recognize = googleASR.recognize(null, Paths.get(uri), capabilities);
		final List<MainHypothesisToken> mhtl = new ArrayList<>();
		for (final ASROutput asrOutput : recognize) {
			mhtl.addAll(asrOutput);
		}
		prePipeData.setMainHypothesis(mhtl);
		//		final Iterator<ASROutput> outIterator = recognize.iterator();
		//		if (outIterator.hasNext()) {
		//			prePipeData.setMainHypothesis(outIterator.next());
		//		} else {
		//			logger.warn("No main Hypothesis!");
		//		}
		//		while (outIterator.hasNext()) {
		//			prePipeData.addAltHypothesis(outIterator.next());
		//		}
		logger.info("Terminating Multi ASR");
	}

	@Override
	public String getID() {
		return ID;
	}
}