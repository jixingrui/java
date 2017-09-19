package avalon.ice4.i;

import avalon.ice4.Body;
import avalon.ice4.Walker;

public interface EyeI {

	abstract void eyeSee(SeeE event, Body one);

	abstract void eyeSeeSpeak(Body one, byte[] msg);

	abstract void eyeSeePath(Walker one);

}
