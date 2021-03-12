package edu.kit.ipd.pronat.multiasr.asr.revise;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import edu.kit.ipd.pronat.multiasr.asr.GoogleASR;
import edu.kit.ipd.pronat.multiasr.asr.MultiASR;
import edu.kit.ipd.pronat.multiasr.asr.MultiASRException;

/**
 * Created by Me on 04.04.16.
 */
public class TestGoogle extends AbstractASRTest {
	private final MultiASR multiASR;

	public TestGoogle() throws MultiASRException {
		multiASR = new MultiASR();
		multiASR.register(new GoogleASR());
	}

	@Test
	public void basic() throws URISyntaxException, IOException {
		forAll(multiASR::recognize);
	}

	@Ignore
	@Test
	public void regression1() throws URISyntaxException {
		final URL resource = getClass().getClassLoader().getResource("speeches/21fde0tade1none00_1443900464090_3.wav");
		multiASR.recognize(Paths.get(resource.toURI()));
	}
}
