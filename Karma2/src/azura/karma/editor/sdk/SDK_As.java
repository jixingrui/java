package azura.karma.editor.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import azura.karma.def.KarmaNow;
import azura.karma.def.KarmaSpace;
import azura.karma.editor.def.KarmaDefPack;
import azura.karma.editor.def.KarmaFieldPack;
import azura.karma.editor.def.KarmaTooth;
import azura.karma.run.bean.BeanTypeE;
import common.algorithm.FastMath;
import common.util.ZipUtil;

public class SDK_As {

	// private static final Logger log = Logger.getLogger(SDK_As.class);

	public static void genByProject(KarmaSpace space, ZipUtil zip) {

		for (KarmaNow node : space.typeIterator()) {
			genClassAs(zip, node, space);
		}

	}

	public static byte[] genByProject(KarmaSpace space) {
		ZipUtil zip = new ZipUtil();

		for (KarmaNow node : space.typeIterator()) {
			genClassAs(zip, node, space);
		}

		return zip.toBytes();
	}

	private static void genClassAs(ZipUtil zip, KarmaNow node, KarmaSpace space) {

		LinkedList<String> pathCh = node.getPath();
		LinkedList<String> path = new LinkedList<String>();
		Iterator<String> it = pathCh.iterator();
		while (it.hasNext()) {
			String ch = it.next();
			String py = PinYinWrap.convert(ch);
			path.add(py);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("as3/zz/karma");
		int i = 0;
		for (; i < path.size() - 1; i++) {
			sb.append("/" + path.get(i));
		}
		sb.append("/K_" + path.get(i));
		sb.append(".as");

		path.removeLast();

		String content = genClass(path, node, space);

		zip.appendFile(sb.toString(), content);
	}

	public static String genClass(LinkedList<String> path, KarmaNow karma, KarmaSpace space) {

		ArrayList<KarmaFieldPack> fList = karma.editor.fieldList;

		HashSet<Integer> importSet = new HashSet<>();

		String className = PinYinWrap.convert(karma.editor.core.name);

		HashMap<Integer, String> id_type = new HashMap<Integer, String>();

		String part1, part2, part3;

		StringBuilder sb = new StringBuilder();

		// ================= package ================
		sb.append("package zz.karma");
		for (String chunk : path) {
			sb.append("." + chunk);
		}
		sb.append("{");
		sb.append("\n");

		// ================= import ================
		// sb.append("\nimport azura.karma.run.Karma;");

		sb.append("\nimport azura.common.collections.ZintBuffer;");
		sb.append("\nimport azura.karma.run.Karma;");
		sb.append("\nimport azura.karma.run.KarmaReaderA;");
		sb.append("\nimport azura.karma.run.bean.KarmaList;");
		sb.append("\nimport azura.karma.def.KarmaSpace;");
		sb.append("\n");

		part1 = sb.toString();
		// ========== to insert import ==========
		sb = new StringBuilder();
		sb.append("\n");
		// ================= class ================
		sb.append(commentClass(karma.editor));
		sb.append("\n\tpublic class K_" + className + " extends KarmaReaderA {");

		sb.append("\n\t\tpublic static const type:int = " + karma.editor.core.tid + ";");
		sb.append("\n");

		sb.append("\n\t\tpublic function K_" + className + "(space:KarmaSpace) {");
		sb.append("\n\t\t\tsuper(space, type , " + karma.history.getHead().version + ");");

		for (int idx = 0; idx < fList.size(); idx++) {
			KarmaFieldPack f = fList.get(idx);
			if (f.fork.size() == 1) {
				if (f.core.type == BeanTypeE.LIST) {
				} else if (f.core.type == BeanTypeE.KARMA) {
					String fName = PinYinWrap.convert(f.core.name);
					sb.append("\n\t\t" + fName + "=new K_" + f.fork.get(0).targetName + "(space);");
				}
			}
		}

		sb.append("\n\t\t}");
		sb.append("\n");

		// =============== field ==============

		// from karma
		sb.append("\n\t\toverride public function fromKarma(karma:Karma):void {");
		sb.append("\n\t\t\tif(karma==null) return;");
		for (int idx = 0; idx < fList.size(); idx++) {
			KarmaFieldPack f = fList.get(idx);
			String fName = PinYinWrap.convert(f.core.name);
			if (f.fork.size() == 1) {
				if (f.core.type == BeanTypeE.KARMA) {
					sb.append("\n\t\t\t" + fName + ".fromKarma(karma.getKarma(" + idx + "));");
					importSet.add(f.fork.get(0).targetType);
				} else if (f.core.type == BeanTypeE.LIST) {
				}
			} else {
				sb.append("\n\t\t\t" + fName + " = karma.get" + f.core.type.getterName() + "(" + idx + ");");
			}
		}
		sb.append("\n\t\t}");
		sb.append("\n");

		// to karma
		sb.append("\n\t\toverride public function toKarma():Karma {");
		for (int idx = 0; idx < fList.size(); idx++) {
			KarmaFieldPack f = fList.get(idx);
			String fName = PinYinWrap.convert(f.core.name);
			if (f.core.type == BeanTypeE.LIST)
				continue;
			if (f.fork.size() == 1) {
				sb.append("\n\t\t\tif(" + fName + " != null)");
				sb.append("\n\t\t\t\tkarma.setKarma(" + idx + ", " + fName + ".toKarma());");
			} else {
				sb.append("\n\t\t\tkarma.set" + f.core.type.getterName() + "(" + idx + ", " + fName + ");");
			}
		}
		sb.append("\n\t\t\treturn karma;");
		sb.append("\n\t\t}");
		sb.append("\n");

		// field
		for (int idx = 0; idx < fList.size(); idx++) {
			KarmaFieldPack f = fList.get(idx);
			String fName = PinYinWrap.convert(f.core.name);
			sb.append(commentField(f, id_type));
			if (f.fork.size() == 1) {
				if (f.core.type == BeanTypeE.LIST) {
					sb.append("\n\t\tpublic var " + fName + ":KarmaList=karma.getList(" + idx + ");");
				} else if (f.core.type == BeanTypeE.KARMA) {
					sb.append("\n\t\tpublic var " + fName + ":K_" + f.fork.get(0).targetName + ";");
				}
			} else {
				sb.append("\n\t\tpublic var " + fName + ":" + f.core.type.as3Name() + ";");
			}
		}
		sb.append("\n");

		// ================ type ==================
		for (Entry<Integer, String> e : id_type.entrySet()) {
			String tName = PinYinWrap.convert(e.getValue());
			sb.append(commentType(e.getValue()));
			sb.append("\n\t\tpublic static const T_" + tName + ":int = " + e.getKey() + ";");
		}

		// ================= tail =================
		sb.append("\n\t}");
		sb.append("\n}");
		part3 = sb.toString();

		// ========== part2 =========
		sb = new StringBuilder();
		for (int imp : importSet) {
			KarmaNow ki = space.getDef(imp);
			sb.append("\nimport zz.karma").append(ki.getPathString()).append(".K_").append(ki.editor.core.name)
					.append(";");
		}
		part2 = sb.toString();

		return part1 + part2 + part3;
	}

	private static String commentClass(KarmaDefPack karma) {
		String note = karma.core.note;
		note = FastMath.replaceRecursive(note, "\n\n", "\n");
		note = note.replace("*", "&#42;");
		note = note.replace("\n", "\n\t*<p>");

		StringBuilder sb = new StringBuilder();
		sb.append("\n\t/**");
		// sb.append("\n\t*" + karma.name);
		sb.append("\n\t*<p>note: " + note);
		sb.append("\n\t*/");
		return sb.toString();
	}

	private static String commentField(KarmaFieldPack field, HashMap<Integer, String> id_type) {
		String note = field.core.note;
		note = FastMath.replaceRecursive(note, "\n\n", "\n");
		note = note.replace("*", "&#42;");
		note = note.replace("\n", "\n\t\t*<p>");

		StringBuilder sb = new StringBuilder();
		sb.append("\n\t\t/**");
		// sb.append("\n\t\t*" + field.core.name);
		sb.append("\n\t\t*<p>type = " + field.core.type);
		if (field.core.type == BeanTypeE.KARMA || field.core.type == BeanTypeE.LIST) {
			for (KarmaTooth teeth : field.fork) {
				sb.append("\n\t\t*<p>[" + teeth.targetName + "] " + teeth.note);
				id_type.put(teeth.targetType, teeth.targetName);
			}
		}
		sb.append("\n\t\t*<p> --note-- ");
		sb.append("\n\t\t*<p>" + note);
		sb.append("\n\t\t*/");
		return sb.toString();
	}

	private static String commentType(String type) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t\t/**");
		sb.append("\n\t\t*" + type);
		sb.append("\n\t\t*/");
		return sb.toString();
	}
}
