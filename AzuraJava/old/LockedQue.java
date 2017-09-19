package common.collections.buffer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockedQue extends ConcurrentLinkedQueue<ZintBuffer> {

	private static final long serialVersionUID = 1307027808494673061L;

	public final AtomicBoolean locked=new AtomicBoolean();
}
