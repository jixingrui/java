package common.collections.timer;

public interface IFreezerFood {

	/**
	 * @return is freeze successful
	 */
	public boolean tryFreeze();

	public void unfreezeHandler();
	
	public void heartBeatHandler();
}
