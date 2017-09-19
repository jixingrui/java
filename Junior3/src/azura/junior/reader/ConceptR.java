package azura.junior.reader;

import azura.karma.def.KarmaSpace;
import azura.karma.hard.HardItem;
import common.collections.buffer.i.BytesI;
import zz.karma.JuniorEdit.K_Concept;

public class ConceptR extends K_Concept implements BytesI {

	public ConceptR(KarmaSpace space) {
		super(space);
	}

	// cache
	public HardItem hi;

	public int getId() {
		return hi.getNode().getIdAsInt();
	}

}
