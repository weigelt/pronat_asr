package edu.kit.ipd.parse.multiasr.asr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import edu.kit.ipd.parse.audio.AudioFormat;

public class TestAbstractASR {
	private class ASRDummy extends AbstractASR {
		public ASRDummy(String configFile) {
			super(configFile);
		}

		@Override
		public List<ASROutput> recognize(URI uri, Path audio, Map<String, String> capabilites) {
			return null;
		}

		@Override
		public Set<AudioFormat> getSupportedAudioFormats() {
			return null;
		}

		@Override
		public Set<String> getSupportedCapabilities() {
			return null;
		}
	}
	
	@Test
	public void testLoadConfig() {
		AbstractASR asr = new ASRDummy("testconfig.xml");
		Configuration config = asr.getConfig();
		assertNotNull(config);
		assertFalse(config.isEmpty());
		assertTrue(config.getBoolean("test-value-1"));
	}
}
