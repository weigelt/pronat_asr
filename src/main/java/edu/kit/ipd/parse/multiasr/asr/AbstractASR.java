package edu.kit.ipd.parse.multiasr.asr;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.kit.ipd.parse.multiasr.asr.spi.IASR;

/**
 * Abstract ASR base class that supplies configuration and logger to it's subclasses
 */
public abstract class AbstractASR implements IASR {
	private Configuration config;
	private final Logger logger = LogManager.getLogger();
	private String id;
	protected final static String SEPERATOR = ":/";

	/**
	 * Loads the ASRs config file and sets up its logger
	 *
	 * @param configFile Name of the config file for this ASR implementation
	 */
	public AbstractASR(String configFile) {
		final DefaultConfigurationBuilder cb = new DefaultConfigurationBuilder();
		cb.setFile(new File(configFile));
		try {
			this.config = cb.getConfiguration(true);
		} catch (final ConfigurationException e) {
			e.printStackTrace();
		}
	}

	protected Logger logger() {
		return logger;
	}

	protected Configuration getConfig() {
		return config;
	}

	protected String getIdentifier() {
		return id;
	}

	protected void setIdentifier(String id) {
		this.id = id;
	}
}