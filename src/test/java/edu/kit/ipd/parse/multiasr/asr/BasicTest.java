package edu.kit.ipd.parse.multiasr.asr;

import edu.kit.ipd.parse.multiasr.asr.ASROutput;
import edu.kit.ipd.parse.multiasr.asr.spi.IASR;
import edu.kit.ipd.parse.revise.support.EmptyMap;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Created by Me on 31.03.16.
 */
public class BasicTest {
    private final String extension;

    public BasicTest(String extension) {
        this.extension = extension;
    }

    public List<ASROutput> test(IASR asr, Map<String, String> capabilities) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource("testaudio" + extension);

        List<ASROutput> results = asr.recognize(url.toURI(), Paths.get(url.toURI()), capabilities);

        return results;
    }

    public List<ASROutput> test(IASR asr) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource("testaudio" + extension);

        List<ASROutput> results = asr.recognize(url.toURI(), Paths.get(url.toURI()), new EmptyMap<String, String>());

        return results;
    }
}
