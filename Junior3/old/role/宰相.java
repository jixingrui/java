package azura.junior.client.role;

import azura.junior.client.Client;
import azura.junior.drop.宰相Sdk;

public class 宰相 extends 宰相Sdk {

	public int population;
	public int foodStock;

	public 宰相(Client engine) {
		super(engine);
	}

	@Override
	public void 报喜() {
		log.info("陛下，风调雨顺国泰民安，可以再收一个妃子了。");
	}

	@Override
	public void 请陛下振作() {
		log.info("陛下，财政压力紧迫，需要对民众收重税方能缓解。");
	}

	@Override
	public void 计算存粮() {
		int lasting = foodStock / population;
		if (lasting < 3)
			i存粮不足三月();
		else if (lasting > 12)
			i存粮足够一年();
	}

	@Override
	public void 计算武力是否足够() {
	}

}
