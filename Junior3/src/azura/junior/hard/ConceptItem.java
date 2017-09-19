package azura.junior.hard;

import azura.junior.db.HeliosJunior3;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import zz.karma.JuniorEdit.K_Concept;
import zz.karma.JuniorEdit.K_Idea;

public class ConceptItem implements BytesI {
	K_Concept concept = new K_Concept(HeliosJunior3.ksJuniorEdit);
	K_Idea idea = new K_Idea(HeliosJunior3.ksJuniorEdit);

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(concept.toBytes());
		zb.writeBytesZ(idea.toBytes());
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb=new ZintBuffer(bytes);
		concept.fromBytes(zb.readBytesZ());
		idea.fromBytes(zb.readBytesZ());
	}

}
