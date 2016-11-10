

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.kit.ipd.parse.luna.data.token.HypothesisToken;
import edu.kit.ipd.parse.multiasr.asr.ASROutput;
import edu.kit.ipd.parse.multiasr.asr.MultiASR;

/**
 * Created by Me on 02.06.16.
 */
public class MultiASRTest {
	@Test
	public void register() throws Exception {
		final MultiASR multiASR = new MultiASR();
		multiASR.register(new ASRFasade());
	}

	@Test
	public void recognize() throws Exception {
		final MultiASR multiASR = new MultiASR();
		multiASR.register(new ASRFasade());
		final URI uri = this.getClass().getClassLoader().getResource("testaudio.flac").toURI();
		final List<ASROutput> recognize = multiASR.recognize(Paths.get(uri));
		final HypothesisToken token = recognize.get(0).get(0);
		/* TODO: new asserts */
		//assertThat(token, instanceOf(Word.class));
		//assertThat(URI.create(((Word) token).getWord()), equalTo(uri));
	}

	@Test
	public void recognize1() throws Exception {
		final MultiASR multiASR = new MultiASR();
		multiASR.register(new ASRFasade());
		final URI uri = this.getClass().getClassLoader().getResource("testaudio.flac").toURI();
		final List<ASROutput> recognize = multiASR.recognize(Paths.get(uri), Collections.singletonMap("HELLO_WORLD", "1"), null);
		assertThat(recognize.isEmpty(), is(true));
	}

	@Test
	public void registerPostProcessor() throws Exception {
		final MultiASR multiASR = new MultiASR();
		multiASR.registerPostProcessor(asrOutput -> asrOutput);
		multiASR.register(new ASRFasade());
		final URI uri = this.getClass().getClassLoader().getResource("testaudio.flac").toURI();
		final List<ASROutput> recognize = multiASR.recognize(Paths.get(uri));
		assertThat(recognize.size(), is(1));
	}

}