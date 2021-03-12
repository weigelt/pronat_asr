package edu.kit.ipd.pronat.multiasr.asr;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import edu.kit.ipd.pronat.prepipedatamodel.token.MainHypothesisToken;
import org.junit.Ignore;
import org.junit.Test;

public class TestWatson extends BasicTest {
	public TestWatson() {
		super("-long.flac");
	}

	@Ignore
	@Test
	public void basicTest() throws URISyntaxException {
		final WatsonASR asr = new WatsonASR();
		final List<ASROutput> results = super.test(asr);
	}

	@Ignore
	@Test
	public void testNBEST() throws URISyntaxException {
		final WatsonASR asr = new WatsonASR();
		final HashMap<String, String> capabilities = new HashMap<>();
		capabilities.put("NBEST", "5");
		final List<ASROutput> results = super.test(asr, capabilities);
		for (final ASROutput asrOutput : results) {
			for (final MainHypothesisToken mainHypothesisToken : asrOutput) {
				System.out.println(mainHypothesisToken.getPosition() + ": " + mainHypothesisToken.getWord() + " "
						+ mainHypothesisToken.getConfidence());
			}
		}
	}

	@Ignore
	@Test
	public void testConfusionNetwork() throws URISyntaxException {
		final WatsonASR asr = new WatsonASR();
		final HashMap<String, String> capabilities = new HashMap<>();
		capabilities.put("CN", "0.2");
		final List<ASROutput> results = super.test(asr, capabilities);
	}
}