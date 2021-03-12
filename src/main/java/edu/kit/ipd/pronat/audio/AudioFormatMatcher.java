package edu.kit.ipd.pronat.audio;

@FunctionalInterface
public interface AudioFormatMatcher {
	public boolean match(AudioFormat source, AudioFormat target);
}
