package edu.kit.ipd.parse.revise.support;

public class LoadableClassDummy implements ILoadableClassDummy {
	private int res;
	
	public LoadableClassDummy() {
		this.res = 0;
	}

	@Override
	public String getResult() {
		return Integer.toString(res);
	}

	@Override
	public void init(int i) {
		this.res = i;
	}

	@Override
	public void add(int i) {
		this.res += i;
	}
}

