package edu.kit.ipd.parse.audio;

public interface AudioFormat {
	default public int getChannels() {
		return -1;
	}
	
	default public String getCodec() {
		return null;
	}
	
	default public String getFormat() {
		return null;
	}
		
	default	public int getBitrate() {
		return -1;
	}

	default int getSamplingRate() {
		return -1;
	}
}
