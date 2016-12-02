package edu.kit.ipd.parse.multiasr.asr;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

/**
 * Created by Me on 31.03.16.
 */
public class TestGoogle extends BasicTest {
	public TestGoogle() {
		super("-8.flac");
	}

	@Test
	public void basicTest() throws URISyntaxException {
		final GoogleASR asr = new GoogleASR();
		final List<ASROutput> results = super.test(asr);
	}

	@Test
	public void testNBEST() throws URISyntaxException {
		final GoogleASR asr = new GoogleASR();
		final HashMap<String, String> capabilities = new HashMap<>();
		capabilities.put("NBEST", "5");
		final List<ASROutput> results = super.test(asr, capabilities);
	}

	@Test
	public void testIdentifier() {
		final GoogleASR asr = new GoogleASR();
		assertEquals("Google", asr.getIdentifier());
	}
}
