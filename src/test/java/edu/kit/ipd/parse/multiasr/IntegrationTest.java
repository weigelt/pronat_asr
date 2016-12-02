package edu.kit.ipd.parse.multiasr;

import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ipd.multiasr.MultiASRPipelineStage;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.data.token.AlternativeHypothesisToken;
import edu.kit.ipd.parse.luna.data.token.MainHypothesisToken;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;

public class IntegrationTest {

	private static MultiASRPipelineStage masr;
	private static PrePipelineData ppd;

	@BeforeClass
	public static void setUp() {
		masr = new MultiASRPipelineStage();
		masr.init();
	}

	//TODO: proper asserts
	@Test
	public void testPipelineIntegration() {
		ppd = new PrePipelineData();
		Path inputPath = null;
		try {
			inputPath = Paths.get(IntegrationTest.class.getClassLoader().getResource("testaudio.flac").toURI());
		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(inputPath);
		ppd.setInputFilePath(inputPath);
		try {
			masr.exec(ppd);
		} catch (final PipelineStageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<MainHypothesisToken> mainHyp = null;
		try {
			mainHyp = ppd.getMainHypothesis();
		} catch (final MissingDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(mainHyp);
		for (final MainHypothesisToken mainHypothesisToken : mainHyp) {
			System.out.print(mainHypothesisToken.getWord() + "--" + mainHypothesisToken.getConfidence() + " | ");
			for (final AlternativeHypothesisToken altHypothesisToken : mainHypothesisToken.getAlternatives()) {
				System.out.print(altHypothesisToken.getWord() + "--" + altHypothesisToken.getConfidence() + " | ");
			}
			System.out.println();
		}

	}

}
