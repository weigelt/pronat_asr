package edu.kit.ipd.parse.multiasr.asr;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import edu.kit.ipd.parse.luna.data.token.MainHypothesisToken;

/**
 * Created by Me on 31.03.16.
 */
public class TestWatson extends BasicTest {
	public TestWatson() {
		super("-long.flac");
	}

	@Test
	public void basicTest() throws URISyntaxException {
		final WatsonASR asr = new WatsonASR();
		final List<ASROutput> results = super.test(asr);
	}

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

	@Test
	public void testConfusionNetwork() throws URISyntaxException {
		final WatsonASR asr = new WatsonASR();
		final HashMap<String, String> capabilities = new HashMap<>();
		capabilities.put("CN", "0.2");
		final List<ASROutput> results = super.test(asr, capabilities);
	}
}