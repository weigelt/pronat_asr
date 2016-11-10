package edu.kit.ipd.parse.audio;

@FunctionalInterface
public interface AudioFormatMatcher {
	public boolean match(AudioFormat source, AudioFormat target);
}
