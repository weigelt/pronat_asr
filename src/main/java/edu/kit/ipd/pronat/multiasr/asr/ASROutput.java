package edu.kit.ipd.pronat.multiasr.asr;

import edu.kit.ipd.pronat.prepipedatamodel.token.MainHypothesisToken;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class ASROutput extends AbstractList<MainHypothesisToken> implements List<MainHypothesisToken> {
	private final List<MainHypothesisToken> hypothesis;

	private final String ASR_ID;

	public ASROutput(String asrId, int size) {
		ASR_ID = asrId;
		hypothesis = new ArrayList<>(size);
	}

	public ASROutput(String asrId) {
		ASR_ID = asrId;
		hypothesis = new ArrayList<>();
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
