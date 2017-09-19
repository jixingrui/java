package azura.junior.client;

import azura.junior.client.role.农田;
import azura.junior.client.role.宰相;
import azura.junior.client.role.将领;

public class TestMain {

	public static void main(String[] args) {

		CSI server = null;
		Client engine = new Client(server);

		宰相 chanceller = new 宰相(engine);
		将领 general = new 将领(engine);
		农田 field = new 农田(engine);

		field.i干旱();
		chanceller.i干旱();

		// 农田_将领 fieldWork = new 农田_将领(engine, general, field);
		// fieldWork.i考绩差();

		general.i听说国王斩了个贪官();
	}

}
