package azura.ice.watch;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Watch {
	Mover host;
	Mover target;
	ComparatorE signal;
	int triggerValue;
	Runnable callBack;

	int currentValue;
	boolean currentState;

	public void move(int newValue) {
		currentValue = newValue;
		switch (signal) {
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
		return host == other.host && target == other.target && signal == other.signal
				&& triggerValue == other.triggerValue;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(host).append(target).append(signal).append(triggerValue).toHashCode();
	}
}
