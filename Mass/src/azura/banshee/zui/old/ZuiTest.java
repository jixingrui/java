package azura.banshee.zui.old;

public class ZuiTest {

	public static void main(String[] args) {
		
		ZuiNode zn=new ZuiNode();
		
		zn.asState().setState("");
		zn.asContainer().move(0,0);
		zn.asImage().setSkin("");
	}

}
