package edu.kit.ipd.parse.multiasr.asr;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.luna.data.token.HypothesisToken;

public class ASROutput extends AbstractList<HypothesisToken> implements List<HypothesisToken> {
	private final List<HypothesisToken> hypothesis;

	private final String ASR_ID;

	public ASROutput(String asrId, int size) {
		this.ASR_ID = asrId;
		this.hypothesis = new ArrayList<>(size);
	}


	public ASROutput(String asrId) {
		this.ASR_ID = asrId;
		this.hypothesis = new ArrayList<>();
	}

	@Override
	public HypothesisToken get(int index) {
		return hypothesis.get(index);
	}

	@Override
	public int size() {
		return hypothesis.size();
	}


	/**
	 * @return the aSR_ID
	 */
	public String getASRid() {
		return ASR_ID;
	}

	@Override
	public void add(int index, HypothesisToken element) {
		hypothesis.add(index, element);
	}

	@Override
	public HypothesisToken remove(int index) {
		return hypothesis.remove(index);
	}

	@Override
	public HypothesisToken set(int index, HypothesisToken element) {
		return hypothesis.set(index, element);
	}
}
