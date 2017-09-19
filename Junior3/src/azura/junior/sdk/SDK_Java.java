package azura.junior.sdk;

import java.util.ArrayList;
import java.util.List;

import azura.helios6.Hnode;
import azura.helios6.read.JoinList;
import azura.junior.db.HeliosJunior3;
import azura.junior.reader.ConceptR;
import azura.junior.reader.IoType;
import azura.karma.hard.HardItem;
import common.algorithm.FastMath;
import common.util.ZipUtil;

public class SDK_Java {
	private static HeliosJunior3 db = HeliosJunior3.me();

	public static void genSdk(ZipUtil zip) {
//		ZipUtil zip = new ZipUtil();

		// JoinList scriptList = db.join().addFrom(db.tagScript).run();
		List<Hnode> scriptList = db.getTreeList(db.tagScript, db.tagScriptRoot);
		for (Hnode script : scriptList) {
			genRoleJava(zip, script, true);
		}

		// JoinList identityList = db.join().addFrom(db.tagIdentity).run();
		List<Hnode> identityList = db.getTreeList(db.tagProfession, db.tagProfessionRoot);
		for (Hnode identity : identityList) {
			genRoleJava(zip, identity, false);
		}

//		return zip.toBytes();
	}

	private static void genRoleJava(ZipUtil zip, Hnode node, boolean script_identity) {

		HardItem hi = HardItem.fromNode(node);

		String folder = script_identity ? "script" : "identity";

		StringBuilder sb = new StringBuilder();
		sb.append("src/zz/junior/" + folder + "/");
		String jType = script_identity ? "JS" : "JI";
		sb.append("/" + jType + hi.name + "");
		sb.append(".java");

		String content = genSoul(hi, script_identity);

		zip.appendFile(sb.toString(), content);
	}

	public static String genSoul(HardItem hiScriptOrIdentity, boolean script_identity) {

		Hnode soulNode = db.getSoul(hiScriptOrIdentity.getNode());

		StringBuilder sb = new StringBuilder();

		String folder = script_identity ? "script" : "identity";
		String superClass = script_identity ? "ScriptA" : "ProA";
		String jType = script_identity ? "JS" : "JI";

		// ================= package ================
		sb.append("package zz.junior." + folder + ";");
		sb.append("\n");

		// ================= import ================
		sb.append("\nimport org.apache.log4j.Logger;");
		sb.append("\nimport azura.junior.client.JuniorInputI;");
		sb.append("\nimport azura.junior.client." + superClass + ";");
		sb.append("\n");

		// ================= class ================
		sb.append("\npublic abstract class " + jType + hiScriptOrIdentity.name + " extends " + superClass + " {");

		sb.append("\n\tpublic static final Logger log = Logger.getLogger(" + jType + hiScriptOrIdentity.name
				+ ".class);");
		sb.append("\n");

		sb.append("\n\tpublic " + jType + hiScriptOrIdentity.name + "(JuniorInputI client) {");
		sb.append("\n\t\tsuper(" + hiScriptOrIdentity.getNode().getIdAsInt() + ", client);");

		if (folder.equals("script")) {
			sb.append("\n\t\tclient.newScene(this);");
		} else if (folder.equals("identity")) {
			sb.append("\n\t\tclient.newEgo(this);");
		} else {
			throw new Error();
		}

		sb.append("\n\t}");

		// ========== prepare concept ===========

		List<Hnode> jlConcept = db.soulToConceptList(soulNode);
		ArrayList<ConceptR> conceptList = new ArrayList<ConceptR>();
		for (Hnode conceptNode : jlConcept) {
			HardItem hi = HardItem.fromNode(conceptNode);
			ConceptR concept = new ConceptR(HeliosJunior3.ksJuniorEdit);
			conceptList.add(concept);

			concept.hi = hi;
			concept.fromBytes(hi.data);
			hi.name = hi.name.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]", "_");
		}

		// ============= role ===========
		if (script_identity) {
			sb.append("\n\n\t//========= role ===========");
			JoinList jl = HeliosJunior3.me().scriptToRoleList(hiScriptOrIdentity.getNode());
			for (int i = 0; i < jl.size(); i++) {
				Hnode roleNode = jl.get(i);
				String name = HardItem.fromNode(roleNode).longName();
				name = name.replace(".", "_");
				sb.append("\n\tpublic final int r" + name + "=" + (i + 1) + ";");
			}
		}

		// ============ input =======
		sb.append("\n\n\t//========= input ===========");
		for (ConceptR concept : conceptList) {
			if (concept.ioType == IoType.INPUT.ordinal()) {
				sb.append(comment(concept.note));
				sb.append("\n\tpublic void i" + concept.hi.name + "(){");
				sb.append("\n\t\tclient.input(this," + concept.getId() + ");");
				sb.append("\n\t}");
			}
		}

		// ============ ask =======
		sb.append("\n\n\t//========= ask ===========");
		for (ConceptR concept : conceptList) {
			if (concept.ioType == IoType.ASK.ordinal()) {
				sb.append(comment(concept.note));
				sb.append("\n\tpublic abstract boolean a" + concept.hi.name + "();");
			}
		}
		// =========== link ======
		sb.append("\n\n\t@Override");
		sb.append("\n\tprotected final boolean ask(int concept){");
		sb.append("\n\t\tswitch(concept){");

		for (ConceptR concept : conceptList) {
			if (concept.ioType == IoType.ASK.ordinal()) {
				sb.append("\n\t\t case " + concept.getId() + ":{");
				sb.append("\n\t\t\treturn a" + concept.hi.name + "();");
				sb.append("\n\t\t}");
			}
		}
		sb.append("\n\t\t}");
		sb.append("\n\t\tthrow new Error();");
		sb.append("\n\t}");

		// ============ output =======
		sb.append("\n\n\t//========= output ===========");
		for (ConceptR concept : conceptList) {
			if (concept.ioType == IoType.OUTPUT.ordinal()) {
				sb.append(comment(concept.note));
				sb.append("\n\tpublic abstract void o" + concept.hi.name + "();");
			}
		}
		// =========== link ======
		sb.append("\n\n\t@Override");
		sb.append("\n\tprotected final void output(int concept){");
		sb.append("\n\t\tswitch(concept){");

		for (ConceptR concept : conceptList) {
			if (concept.ioType == IoType.OUTPUT.ordinal()) {
				sb.append("\n\t\t case " + concept.getId() + ":{");
				sb.append("\n\t\t\to" + concept.hi.name + "();");
				sb.append("\n\t\t\tbreak;");
				sb.append("\n\t\t}");
			}
		}

		sb.append("\n\t\t}");
		sb.append("\n\t}");
		// ============= tail ========
		sb.append("\n}");

		return sb.toString();
	}

	private static String comment(String note) {
		if (note.length() == 0)
			return "";

		note = FastMath.replaceRecursive(note, "\n\n", "\n");
		note = note.replace("\n", "\n\t*<p>");

		StringBuilder sb = new StringBuilder();
		sb.append("\n\t/**");
		sb.append("\n\t* " + note);
		sb.append("\n\t*/");
		return sb.toString();
	}
}
