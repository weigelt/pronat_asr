package edu.kit.ipd.parse.multiasr.asr;

import java.io.IOException;

/**
 * Created by Me on 27.04.16.
 */
public class MultiASRError extends Error {
    public MultiASRError(String s) {
        super(s);
    }

    public MultiASRError(Throwable e) {
        super(e);
    }
}
