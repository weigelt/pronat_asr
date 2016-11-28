package edu.kit.ipd.parse.multiasr.asr;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

/**
 * Created by Me on 31.03.16.
 */
public class TestWatson extends BasicTest {
	public TestWatson() {
		super("-16.flac");
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
		for (final ASROutput asro : results) {
			for (int i = 0; i < asro.size(); i++) {
				System.out.print(asro.get(i).getWord() + ", " + asro.get(i).getConfidence());
			}
			System.out.println();
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