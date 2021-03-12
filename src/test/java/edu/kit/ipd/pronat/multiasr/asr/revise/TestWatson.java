package edu.kit.ipd.pronat.multiasr.asr.revise;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import edu.kit.ipd.pronat.multiasr.asr.WatsonASR;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.ipd.pronat.multiasr.asr.Capability;
import edu.kit.ipd.pronat.multiasr.asr.MultiASR;
import edu.kit.ipd.pronat.multiasr.asr.MultiASRException;

public class TestWatson extends AbstractASRTest {
	private final MultiASR multiASR;

	public TestWatson() throws MultiASRException {
		multiASR = new MultiASR();
		multiASR.register(new WatsonASR());
	}

	@Ignore
	@Test
	public void basic() throws URISyntaxException, IOException {
		forAll(multiASR::recognize);
	}

	@Ignore
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
