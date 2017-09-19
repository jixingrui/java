package azura.karma.editor.def;

import java.util.List;

import azura.karma.run.bean.BeanTypeE;
import common.collections.ArrayListAuto;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class KarmaFieldPack implements ZintCodecI {

	public KarmaField core = new KarmaField();

	// packing
	public List<KarmaTooth> fork = new ArrayListAuto<KarmaTooth>();

	@Override
	public void readFrom(ZintReaderI reader) {
		core.readFrom(reader);

		if (core.type == BeanTypeE.KARMA || core.type == BeanTypeE.LIST) {
			fork.clear();
			int size = reader.readZint();
			for (int i = 0; i < size; i++) {
				KarmaTooth tooth = new KarmaTooth();
				tooth.readFrom(reader);
				fork.add(tooth);
			}
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		core.writeTo(writer);

		if (core.type == BeanTypeE.KARMA || core.type == BeanTypeE.LIST) {
			writer.writeZint(fork.size());
			for (KarmaTooth tooth : fork) {
				tooth.writeTo(writer);
			}
		}
	}
}
