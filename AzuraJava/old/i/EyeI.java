package old.azura.avalon.ice.i;

import old.azura.avalon.ice.Jumper;
import old.azura.avalon.ice.Walker;

public interface EyeI {

	abstract void eyeSeeIn(Walker walker);

	abstract void eyeSeeChange(Walker walker);

	abstract void eyeSeeEvent(Jumper unit, byte[] msg);

	abstract void eyeSeeMove(Walker walker);

	abstract void eyeSeeWalking(Walker walker);

	abstract void eyeSeeOut(Walker walker);

	abstract void eyeDispose();
}
