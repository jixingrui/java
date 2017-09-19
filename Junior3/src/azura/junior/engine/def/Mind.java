package azura.junior.engine.def;

import com.esotericsoftware.kryo.util.IntMap;

//script or role
public class Mind {
	public int idx;
	public String name;
	public Script script;
	public IntMap<Idea> concept_Idea = new IntMap<Idea>();

	public Mind(String name) {
		this.name = name;
	}

	public Idea getIdea(int concept) {
		return concept_Idea.get(concept);
	}
}
