package azura.junior.engine.def;

import com.esotericsoftware.kryo.util.IntMap;

public class Soul {
	public final int id;
	public String name;
	public IntMap<Concept> id_Concept = new IntMap<>();
	
	public Soul(int id){
		this.id=id;
	}

	public Concept getConcept(int id) {
		return id_Concept.get(id);
	}
}
