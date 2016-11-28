package edu.kit.ipd.parse.multiasr.asr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.kit.ipd.parse.multiasr.asr.spi.IASR;

/**
 * Abstract ASR base class that supplies configuration and logger to it's
 * subclasses
 */
public abstract class AbstractASR implements IASR {
	private final Logger logger = LogManager.getLogger();
	private String id;
	protected final static String SEPERATOR = ":/";

	protected Logger logger() {
		return logger;
	}

	protected String getIdentifier() {
		return id;
	}

	protected void setIdentifier(String id) {
		this.id = id;
	}
}