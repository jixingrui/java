package azura.karma.editor.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import azura.karma.def.KarmaNow;
import azura.karma.def.KarmaSpace;
import azura.karma.editor.def.KarmaFieldPack;
import azura.karma.editor.def.KarmaTooth;
import azura.karma.run.bean.BeanTypeE;
import common.algorithm.FastMath;
import common.util.ZipUtil;

public class SDK_Java {

	public static void genByProject(KarmaSpace space, ZipUtil zip) {

		for (KarmaNow node : space.typeIterator()) {
			genClassJava(zip, node, space);
		}

	}

	public static byte[] genByProject(KarmaSpace space) {
		ZipUtil zip = new ZipUtil();

		for (KarmaNow node : space.typeIterator()) {
			genClassJava(zip, node, space);
		}

		return zip.toBytes();
	}

	private static void genClassJava(ZipUtil zip, KarmaNow node, KarmaSpace space) {

		String path = node.getPathString();

		StringBuilder sb = new StringBuilder();
		sb.append("java/zz/karma");
		sb.append(path.replace(".", "/"));
		sb.append("/K_" + node.editor.core.name);
		sb.append(".java");

		String content = genClass(path, node, space);

		zip.appendFile(sb.toString(), content);
	}

	public static String genClass(String path, KarmaNow karma, KarmaSpace space) {

		HashSet<Integer> importSet = new HashSet<>();

		HashMap<Integer, String> id_type = new HashMap<Integer, String>();

		String part1, part2, part3;
		StringBuilder sb = new StringBuilder();

		// ================= package ================
		sb.append("package zz.karma").append(path);
		sb.append(";");
		sb.append("\n");

		// ================= import ================
		sb.append("\nimport azura.karma.run.Karma;");
		sb.append("\nimport azura.karma.run.KarmaReaderA;");
		sb.append("\nimport azura.karma.def.KarmaSpace;");

		part1 = sb.toString();

		// ========== to insert import ==========
		sb = new StringBuilder();
		sb.append("\n");

		// ================= class ================
		sb.append(commentClass(karma.editor.core.note));
		sb.append("\npublic class K_" + karma.editor.core.name + " extends KarmaReaderA {");

		sb.append("\n\tpublic static final int type = " + karma.editor.core.tid + ";");
		sb.append("\n");

		sb.append("\n\tpublic K_" + karma.editor.core.name + "(KarmaSpace space) {");
		sb.append("\n\t\tsuper(space, type , " + karma.history.getHead().version + ");");
		sb.append("\n\t}");
		sb.append("\n");

		// =============== field ==============

		ArrayList<KarmaFieldPack> fList = new ArrayList<KarmaFieldPack>();
		fList.addAll(karma.editor.fieldList);

		// from karma
		sb.append("\n\t@Override");
		sb.append("\n\tpublic void fromKarma(Karma karma) {");
		sb.append("\n\t\tif(karma==null) return;");
		for (int idx = 0; idx < fList.size(); idx++) {
			KarmaFieldPack f = fList.get(idx);
			if (f.fork.size() == 1) {
				if (f.core.type == BeanTypeE.KARMA) {
					sb.append("\n\t\t" + f.core.name + ".fromKarma(karma.getKarma(" + idx + "));");
					importSet.add(f.fork.get(0).targetType);
				} else if (f.core.type == BeanTypeE.LIST) {
				}
			} else {
				sb.append("\n\t\t" + f.core.name + " = karma.get" + f.core.type.getterName() + "(" + idx + ");");
			}
		}
		sb.append("\n\t}");
		sb.append("\n");

		// to karma
		sb.append("\n\t@Override");
		sb.append("\n\tpublic Karma toKarma() {");
		for (int idx = 0; idx < fList.size(); idx++) {
			KarmaFieldPack f = fList.get(idx);
			if (f.core.type == BeanTypeE.LIST)
				continue;
			if (f.fork.size() == 1) {
				sb.append("\n\t\tif(" + f.core.name + " != null)");
				sb.append("\n\t\t\tkarma.setKarma(" + idx + ", " + f.core.name + ".toKarma());");
			} else {
				sb.append("\n\t\tkarma.set" + f.core.type.getterName() + "(" + idx + ", " + f.core.name + ");");
			}
		}
		sb.append("\n\t\treturn karma;");
		sb.append("\n\t}");
		sb.append("\n");

		// field
		for (int idx = 0; idx < fList.size(); idx++) {
			KarmaFieldPack f = fList.get(idx);
			sb.append(commentField(f, id_type));
			if (f.fork.size() == 1) {
				if (f.core.type == BeanTypeE.LIST) {
					sb.append("\n\tpublic java.util.List<Karma> " + f.core.name + "=karma.getList(" + idx + ");");
				} else if (f.core.type == BeanTypeE.KARMA) {
					sb.append("\n\tpublic K_" + f.fork.get(0).targetName + " " + f.core.name + "=new K_"
							+ f.fork.get(0).targetName + "(space);");
				}
			} else {
				sb.append("\n\tpublic " + f.core.type.javaName() + " " + f.core.name + ";");
			}
		}
		sb.append("\n");

		// ================ type ==================
		for (Entry<Integer, String> e : id_type.entrySet()) {
			sb.append("\n\tpublic static final int T_" + e.getValue() + " = " + e.getKey() + ";");
		}

		// ================= tail =================
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

	private static String commentClass(String note) {
		note = FastMath.replaceRecursive(note, "\n\n", "\n");
		note = note.replace("\n", "\n*<p>");

		StringBuilder sb = new StringBuilder();
		sb.append("\n/**");
		sb.append("\n*@note " + note);
		sb.append("\n*/");
		return sb.toString();
	}

	private static String commentField(KarmaFieldPack field, HashMap<Integer, String> id_type) {
		String note = field.core.note;
		note = FastMath.replaceRecursive(note, "\n\n", "\n");
		note = note.replace("\n", "\n*<p>");

		StringBuilder sb = new StringBuilder();
		sb.append("\n\t/**");
		sb.append("\n\t*@type " + field.core.type);
		if (field.core.type == BeanTypeE.KARMA || field.core.type == BeanTypeE.LIST) {
			for (KarmaTooth teeth : field.fork) {
				sb.append("\n\t*<p>[" + teeth.targetName + "] " + teeth.note);
				id_type.put(teeth.targetType, teeth.targetName);
			}
		}
		sb.append("\n\t*@note " + note);
		sb.append("\n\t*/");
		return sb.toString();
	}

}
