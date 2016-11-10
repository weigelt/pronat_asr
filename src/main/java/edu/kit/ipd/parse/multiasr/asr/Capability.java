package edu.kit.ipd.parse.multiasr.asr;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Me on 02.04.16.
 */
public enum Capability {
    CONFUSION_NETWORK(identifiers.CONFUSION_NETWORK),
    N_BEST(identifiers.N_BEST),
    WORD_CONFIDENCE(identifiers.WORD_CONFIDENCE),
    TIMINGS(identifiers.TIMINGS);

    public final class identifiers {
        public static final String CONFUSION_NETWORK = "CN";

        public static final String N_BEST = "N_BEST";

        public static final String WORD_CONFIDENCE = "WORD_CONFIDENCE";

        public static final String TIMINGS = "TIMINGS";
    }

    private final String identifier;

    public String identifier() {
        return identifier;
    }

    private Capability(String identifier) {
        this.identifier = identifier;
    }

    public static Set<String> toCapabilites(Capability ... capabilities) {
        ArrayList<String> list = new ArrayList<>(capabilities.length);

        for(Capability capability : capabilities) {
            list.add(capability.identifier());
        }

        return new CopyOnWriteArraySet<>(list);
    }
}
