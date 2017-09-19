package azura.ice.watch;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import common.collections.CompareE;

public class Watch {
	Watcher host;
	Watcher target;
	Pair pair;

	CompareE condition;
	int triggerValue;
	Runnable callBack;

	private boolean currentState;

	public void check(int newValue) {
		switch (condition) {
		case Equal:
			happen(newValue == triggerValue);
			break;
		case Larger:
			happen(newValue > triggerValue);
			break;
		case LargerOrEqual:
			happen(newValue >= triggerValue);
			break;
		case Smaller:
			happen(newValue < triggerValue);
			break;
		case SmallerOrEqual:
			happen(newValue <= triggerValue);
			break;
		}
	}

	private void happen(boolean newState) {
		if (currentState == false && newState == true) {
			callBack.run();
		}
		currentState = newState;
	}

	@Override
	public boolean equals(Object obj) {
		Watch other = (Watch) obj;
		return host == other.host && target == other.target && condition == other.condition
				&& triggerValue == other.triggerValue;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(host).append(target).append(condition).append(triggerValue).toHashCode();
	}

	public void dispose() {
		pair.removeWatch(this);
	}

}
