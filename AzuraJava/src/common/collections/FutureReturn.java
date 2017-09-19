package common.collections;

import com.google.common.util.concurrent.FutureCallback;

public abstract class FutureReturn<RETURN> implements FutureCallback<RETURN> {

	public abstract void onDone(RETURN result);

	@Override
	public void onSuccess(RETURN result) {
		onDone(result);
	}

	@Override
	public void onFailure(Throwable t) {
	}
}
