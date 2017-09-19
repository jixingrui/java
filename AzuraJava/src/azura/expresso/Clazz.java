package azura.expresso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import azura.expresso.field.FieldA;
import azura.expresso.field.FieldBoolean;
import azura.expresso.field.FieldBytes;
import azura.expresso.field.FieldDatum;
import azura.expresso.field.FieldDouble;
import azura.expresso.field.FieldInt;
import azura.expresso.field.FieldList;
import azura.expresso.field.FieldString;

import common.collections.UintMap;

public class Clazz {

	int id;
	private UintMap<FieldA> id_Field;
	private UintMap<Integer> ancestorSet;
	NameSpace ns;
	List<FieldA> fieldAll = new ArrayList<FieldA>();

	Clazz(NameSpace ns, int id) {
		this.ns = ns;
		this.id = id;
	}

	void populateAncestor(Collection<Integer> ancSet) {
		ancestorSet = new UintMap<Integer>(ancSet);
	}

	void populateField(TreeSet<FieldLoader> defAll) {
		List<Integer> idFieldAll = new ArrayList<Integer>();
		for (FieldLoader fd : defAll) {
			FieldA field = newField(fieldAll.size(), fd.type, fd.isList);
			fieldAll.add(field);
			idFieldAll.add(fd.id);
		}
		id_Field = new UintMap<FieldA>(idFieldAll);

		int i = 0;
		for (FieldLoader fd : defAll) {
			FieldA field = fieldAll.get(i);
			id_Field.put(fd.id, field);
			i++;
		}
	}

	private FieldA newField(int idxBean, int type, boolean isList) {
		FieldA f = null;
		PrimitiveE pe = PrimitiveE.valueOf(type);
		if (pe == null) {
			f = new FieldDatum(idxBean, type, ns);
		} else {
			switch (pe) {
			case Int:
				f = new FieldInt(idxBean);
				break;
			case String:
				f = new FieldString(idxBean);
				break;
			case Boolean:
				f = new FieldBoolean(idxBean);
				break;
			case Bytes:
				f = new FieldBytes(idxBean);
				break;
			case Double:
				f = new FieldDouble(idxBean);
				break;
			}
		}
		if (isList) {
			f = new FieldList(idxBean, f);
		}
		return f;
	}

	boolean isInstanceOf(int idEpo) {
		return id == idEpo || ancestorSet.containsKey(idEpo);
	}

	void populateBean(Datum datum) {
		Integer[] keys = id_Field.getKeys();
		datum.beanList = new Bean[keys.length];
		for (int i = 0; i < keys.length; i++) {
			datum.beanList[i] = id_Field.get(keys[i]).newBean();
		}
	}

	int fieldToIdxBean(int idField) {
		FieldA fa = id_Field.get(idField);
		return fa.idxBean;
	}

	public static void eat(Datum eater, Datum food) {
		if (eater.clazz == food.clazz) {
			for (int i = 0; i < eater.beanList.length; i++) {
				eater.beanList[i].eat(food.beanList[i]);
			}
		} else if (eater.clazz.ns != food.clazz.ns)
			throw new IllegalArgumentException("namespaces do not match");
		else {
			HashSet<Integer> eaterSet = new HashSet<Integer>();
			eaterSet.addAll(Arrays.asList(eater.clazz.id_Field.getKeys()));
			eaterSet.retainAll(Arrays.asList(food.clazz.id_Field.getKeys()));
			for (int id : eaterSet) {
				eater.getBean(id).eat(food.getBean(id));
			}
		}
	}
}
