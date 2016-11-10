package edu.kit.ipd.parse.multiasr.asr;

/**
 * Created by Me on 11.03.16.
 */
public class MultiASRException extends Exception {
    public MultiASRException(String message) {
        super(message);
    }

    public MultiASRException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiASRException(Throwable cause) {
        super(cause);
    }

    public MultiASRException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MultiASRException() {
    }
}
