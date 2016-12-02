package edu.kit.ipd.parse.multiasr.asr;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.luna.data.token.MainHypothesisToken;

public class ASROutput extends AbstractList<MainHypothesisToken> implements List<MainHypothesisToken> {
	private final List<MainHypothesisToken> hypothesis;

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
	public MainHypothesisToken get(int index) {
		return hypothesis.get(index);
	}

	@Override
	public int size() {
		return hypothesis.size();
	}


	/**
	 * @return the ASR_ID
	 */
	public String getASRid() {
		return ASR_ID;
	}

	@Override
	public void add(int index, MainHypothesisToken element) {
		hypothesis.add(index, element);
	}

	@Override
	public MainHypothesisToken remove(int index) {
		return hypothesis.remove(index);
	}

	@Override
	public MainHypothesisToken set(int index, MainHypothesisToken element) {
		return hypothesis.set(index, element);
	}
}
