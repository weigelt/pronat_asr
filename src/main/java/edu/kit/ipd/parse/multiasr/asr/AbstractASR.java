package edu.kit.ipd.parse.multiasr.asr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.multiasr.asr.spi.IASR;

/**
 * Abstract ASR base class that supplies configuration and logger to it's
 * subclasses
 */
public abstract class AbstractASR implements IASR {
	private static final Logger logger = LoggerFactory.getLogger(AbstractASR.class);
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