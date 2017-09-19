package azura.expresso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import azura.expresso.ns.FamilySimple;

import common.collections.UintMap;

public class NameSpace {

	private UintMap<Clazz> id_Class;

//	private IdGenStore<Datum> oid_Datum = new IdGenStore<>();

	NameSpace() {

	}

	public NameSpace(int[][][] data) {
		load(data);
	}

	public Datum newDatum(int idClass, boolean remote) {
		Clazz clazz = getClass(idClass);
		Datum datum = new Datum(clazz);
		clazz.populateBean(datum);
//		if (remote)
//			oid_Datum.add(datum);
		return datum;
	}

	public Datum newDatum(int idClass) {
		return newDatum(idClass, false);
	}
	
//	public Datum getDatum(int oid){
//		return oid_Datum.get(oid);
//	}

	Clazz getClass(int id) {
		Clazz clazz = id_Class.get(id);
		if (clazz == null)
			throw new IllegalArgumentException("Expresso class not found");
		return clazz;
	}

	private void populateClass(Collection<Integer> classDefAll) {
		id_Class = new UintMap<Clazz>(classDefAll);
		for (int id : classDefAll) {
			Clazz c = new Clazz(this, id);
			id_Class.put(id, c);
		}
	}

	void load(int[][][] classStore) {

		// class def
		List<Integer> classDefAll = new ArrayList<Integer>();
		for (int[][] classDef : classStore) {
			int idClass = classDef[0][0];
			classDefAll.add(idClass);
		}
		populateClass(classDefAll);

		// family
		FamilySimple family = new FamilySimple();
		for (int[][] classDef : classStore) {
			int classId = classDef[0][0];
			int[] parents = classDef[1];
			for (int j = 0; j < parents.length; j++)
				family.link(parents[j], classId);
		}

		// ancestor
		for (int[][] classDef : classStore) {
			int idClass = classDef[0][0];
			Clazz clazz = getClass(idClass);
			clazz.populateAncestor(family.getAncestorSet(idClass));
		}

		// field prepare
		HashMap<Integer, List<FieldLoader>> class_FieldDef = new HashMap<Integer, List<FieldLoader>>();
		for (int[][] classDef : classStore) {
			int idClass = classDef[0][0];
			List<FieldLoader> fieldList = new ArrayList<FieldLoader>();
			class_FieldDef.put(idClass, fieldList);

			for (int i = 2; i < classDef.length; i++) {
				int[] fieldDef = classDef[i];

				FieldLoader field = new FieldLoader();
				field.id = fieldDef[0];
				field.type = fieldDef[1];
				int isListValue = fieldDef[2];
				field.isList = (isListValue == 0) ? false : true;
				fieldList.add(field);
			}
		}

		// field
		for (Entry<Integer, List<FieldLoader>> e : class_FieldDef.entrySet()) {
			Clazz clazz = getClass(e.getKey());
			TreeSet<FieldLoader> fieldDefAll = new TreeSet<FieldLoader>(
					e.getValue());
			Set<Integer> ancSet = family.getAncestorSet(e.getKey());
			for (int anc : ancSet) {
				List<FieldLoader> fieldAnc = class_FieldDef.get(anc);
				fieldDefAll.addAll(fieldAnc);
			}
			clazz.populateField(fieldDefAll);
		}

	}

//	public void dispose(Datum datum) {
//		oid_Datum.remove(datum.oid);
//	}

}

class FieldLoader implements Comparable<FieldLoader> {
	int id, type;
	boolean isList;

	@Override
	public int compareTo(FieldLoader other) {
		if (id > other.id)
			return 1;
		else if (id < other.id)
			return -1;
		else
			return 0;
	}
}