package azura.junior.engine.def;

import azura.junior.reader.IoType;

public class Concept {
	public final int id;
	public String name;
	public IoType ioType;
	public int counterTrigger;

	public Concept(int id) {
		this.id = id;
	}
}
