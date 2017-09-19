package common.collections;

import java.util.ArrayList;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.util.FileUtil;

public class DupList implements BytesI {
	private ArrayList<Integer> list = new ArrayList<>();

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		int last = 0;
		int occor = 0;
		for (int current : list) {
			if (occor == 0) {// init
				zb.writeZint(current);
				last = current;
				occor = 1;
			} else if (current == last) {
				occor++;
			} else {
				zb.writeZint(occor);
				zb.writeZint(current);
				last = current;
				occor = 1;
			}
		}
		zb.writeZint(occor);

		byte[] zip = FileUtil.compress(zb.toBytes(), false);

		return zip;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		bytes = FileUtil.uncompress(bytes);
		if(bytes.length<=1)
			return;
		
		ZintBuffer zb = new ZintBuffer(bytes);
		while (zb.hasRemaining()) {
			int value = zb.readZint();
			int occor = zb.readZint();
			for (int j = 0; j < occor; j++) {
				list.add(value);
			}
		}
	}

	public void push(int id) {
		list.add(id);
	}

	public int get(Integer id) {
		return list.get(id);
	}

}
