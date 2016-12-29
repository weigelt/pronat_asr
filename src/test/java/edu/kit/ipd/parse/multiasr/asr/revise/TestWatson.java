package edu.kit.ipd.parse.multiasr.asr.revise;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.Test;

import edu.kit.ipd.parse.multiasr.asr.Capability;
import edu.kit.ipd.parse.multiasr.asr.MultiASR;
import edu.kit.ipd.parse.multiasr.asr.MultiASRException;
import edu.kit.ipd.parse.multiasr.asr.WatsonASR;

/**
 * Created by Me on 05.04.16.
 */
public class TestWatson extends AbstractASRTest {
	private final MultiASR multiASR;

	public TestWatson() throws MultiASRException {
		multiASR = new MultiASR();
		multiASR.register(new WatsonASR());
	}

	@Test
	public void basic() throws URISyntaxException, IOException {
		forAll(multiASR::recognize);
	}

	@Test
	public void testCN() throws IOException, URISyntaxException {
		final HashMap<String, String> capabilities = new HashMap<>();
		capabilities.put(Capability.identifiers.CONFUSION_NETWORK, "0");
		//capabilities.put(Capability.identifiers.N_BEST, "5");
		capabilities.put(Capability.identifiers.TIMINGS, "true");
		//capabilities.put(Capability.identifiers.WORD_CONFIDENCE, "true");
		forAll((p) -> multiASR.recognize(p, capabilities, null));
	}
}
