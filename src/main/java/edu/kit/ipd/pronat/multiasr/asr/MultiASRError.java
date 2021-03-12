package edu.kit.ipd.pronat.multiasr.asr;

import java.io.Serial;

/**
 * Created by Me on 27.04.16.
 */
public class MultiASRError extends Error {
	@Serial private static final long serialVersionUID = 8481915581257031935L;

	public MultiASRError(String s) {
		super(s);
	}

	public MultiASRError(Throwable e) {
		super(e);
	}
}
