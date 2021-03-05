package edu.kit.ipd.parse.multiasr;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.kit.ipd.parse.luna.data.token.MainHypothesisToken;
import edu.kit.ipd.parse.multiasr.asr.ASROutput;
import edu.kit.ipd.parse.multiasr.asr.MultiASR;

public class MultiASRTest {
	@Test
	public void register() throws Exception {
		final MultiASR multiASR = new MultiASR();
		multiASR.register(new ASRFasade());
	}

	@Ignore
	@Test
	public void recognize() throws Exception {
		final MultiASR multiASR = new MultiASR();
		multiASR.register(new ASRFasade());
		final URI uri = getClass().getClassLoader().getResource("testaudio.flac").toURI();
		final List<ASROutput> recognize = multiASR.recognize(Paths.get(uri));
		final MainHypothesisToken token = recognize.get(0).get(0);
		/* TODO: new asserts */
		//assertThat(token, instanceOf(Word.class));
		//assertThat(URI.create(((Word) token).getWord()), equalTo(uri));
	}

	@Test
	public void recognize1() throws Exception {
		final MultiASR multiASR = new MultiASR();
		multiASR.register(new ASRFasade());
		final URI uri = getClass().getClassLoader().getResource("testaudio.flac").toURI();
		final List<ASROutput> recognize = multiASR.recognize(Paths.get(uri), Collections.singletonMap("HELLO_WORLD", "1"), null);
		assertThat(recognize.isEmpty(), is(true));
	}

	@Test
	public void registerPostProcessor() throws Exception {
		final MultiASR multiASR = new MultiASR();
		multiASR.registerPostProcessor(asrOutput -> asrOutput);
		multiASR.register(new ASRFasade());
		final URI uri = getClass().getClassLoader().getResource("testaudio.flac").toURI();
		final List<ASROutput> recognize = multiASR.recognize(Paths.get(uri));
		assertThat(recognize.size(), is(1));
	}

}