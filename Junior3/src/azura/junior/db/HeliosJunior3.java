package azura.junior.db;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import azura.helios6.Helios6;
import azura.helios6.Hnode;
import azura.helios6.read.JoinList;
import azura.helios6.write.Batch;
import azura.junior.engine.def.Concept;
import azura.junior.engine.def.Idea;
import azura.junior.engine.def.JuniorDef;
import azura.junior.engine.def.Mind;
import azura.junior.engine.def.Script;
import azura.junior.engine.def.Soul;
import azura.junior.engine.def.Trigger;
import azura.junior.reader.ConceptR;
import azura.junior.reader.IoType;
import azura.junior.reader.TriggerR;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.HardItem;
import common.algorithm.FastMath;
import common.collections.SortedLinkedList;
import common.graphics.ImageUtil;
import zz.karma.JuniorEdit.K_Idea;

public class HeliosJunior3 extends Helios6<JuniorE> implements Serializable {
	private static final long serialVersionUID = -6074750235415574957L;

	static class SingletonHolder {
		static HeliosJunior3 instance = new HeliosJunior3();
	}

	public static HeliosJunior3 me() {
		return SingletonHolder.instance;
	}

	public static KarmaSpace ksJuniorEdit;
	public static KarmaSpace ksJuniorRun;

	public Hnode tagScript;
	public Hnode tagScriptRoot;
	public Hnode tagProfession;
	public Hnode tagProfessionRoot;
	public Hnode tagRole;
	public Hnode tagSoul;
	public Hnode tagMind;
	public Hnode tagConcept;
	public Hnode tagIdea;
	public Hnode tagTrigger;
	public Hnode tagCause;

	public HeliosJunior3() {
		super("./db/junior.db", serialVersionUID + "", JuniorE.class);
		initTags();
		// debug = true;
	}

	private void initTags() {
		tagScript = getTagNode(JuniorE.script);
		tagScriptRoot = getTagNode(JuniorE.scriptRoot);
		tagProfession = getTagNode(JuniorE.profession);
		tagProfessionRoot = getTagNode(JuniorE.professionRoot);
		tagRole = getTagNode(JuniorE.role);
		tagSoul = getTagNode(JuniorE.soul);
		tagMind = getTagNode(JuniorE.mind);
		tagConcept = getTagNode(JuniorE.concept);
		tagIdea = getTagNode(JuniorE.idea);
		tagTrigger = getTagNode(JuniorE.trigger);
		tagCause = getTagNode(JuniorE.cause);
	}

	// =================== soul ==============
	public Hnode addSoul(Batch batch, Hnode sOrP) {
		Hnode soul = new Hnode();
		batch.link(sOrP, soul).link(tagSoul, soul);
		return soul;
	}

	public Hnode getSoul(Hnode scriptOrIdentity) {
		return join().addFrom(scriptOrIdentity).addFrom(tagSoul).run().get(0);
	}

	public Hnode mindToSoul(Hnode mind) {
		return join().addTo(mind).addFrom(tagSoul).run().get(0);
	}

	public void deleteSoul(Batch batch, Hnode scriptOrIdentity) {
		Hnode soul = getSoul(scriptOrIdentity);
		batch.delink(scriptOrIdentity, soul).delink(tagSoul, soul).delete(soul);
	}

	// ================== mind ===============
	public void addMind(Batch batch, Hnode sOrR, Hnode soul) {
		Hnode mind = new Hnode();
		batch.link(tagMind, mind).link(sOrR, mind).link(soul, mind);
		getTreeList(tagConcept, soul).forEach(concept -> {
			K_Idea ki = new K_Idea(ksJuniorEdit);
			Hnode idea = new Hnode();
			idea.setData(ki.toBytes());
			batch.link(mind, idea).link(concept, idea).link(tagIdea, idea).save(idea);
		});
	}

	public void deleteMind(Batch batch, Hnode role) {
		Hnode mind = sOrRToMind(role);
		Hnode soul = mindToSoul(mind);
		batch.delink(role, mind).delink(tagMind, mind).delink(soul, mind).delete(mind);
		getTreeList(tagConcept, soul).forEach(concept -> {
			Hnode idea = join().addFrom(mind).addFrom(concept).addFrom(tagIdea).run().get(0);
			batch.delink(mind, idea).delink(concept, idea).delink(tagIdea, idea).delete(idea);
		});
	}

	public Hnode sOrRToMind(Hnode scriptOrRole) {
		return join().addFrom(scriptOrRole).addFrom(tagMind).run().get(0);
	}

	public JoinList soulToMindList(Hnode soul) {
		return join().addFrom(soul).addFrom(tagMind).run();
	}

	public Hnode mindToRole(Hnode mind) {
		return join().addTo(mind).addFrom(tagRole).run().get(0);
	}

	// ================== idea ================

	public Hnode joinIdea(Hnode mind, Hnode concept) {
		return join().addFrom(concept).addFrom(mind).addFrom(tagIdea).run().get(0);
	}

	public JoinList conceptToIdeaList(Hnode concept) {
		return join().addFrom(concept).addFrom(tagIdea).run();
	}

	public boolean hasIdea(Hnode role) {
		JoinList list = join().addFrom(role).addFrom(tagConcept).run();
		return !list.isEmpty();
	}

	public JoinList mindToIdeaList(Hnode mind) {
		return join().addFrom(mind).addFrom(tagIdea).run();
	}

	private Hnode mindToScriptOrRole(Hnode mind) {
		Hnode scriptOrRole = join().addTo(mind).addFrom(tagScript).run().get(0);
		if (scriptOrRole == null)
			scriptOrRole = join().addTo(mind).addFrom(tagRole).run().get(0);
		return scriptOrRole;
	}

	public String reportIdea(Hnode mind, Hnode concept) {
		Hnode idea = joinIdea(mind, concept);
		if (idea == null)
			return null;

		Hnode scriptOrRole = mindToScriptOrRole(mind);

		StringBuilder sb = new StringBuilder();
		sb.append(HardItem.fromNode(concept).name + "." + HardItem.fromNode(scriptOrRole).name);

		JoinList list = null;
		list = join().addFrom(idea).addFrom(tagCause).run();
		sb.append("\r\n======= cause by =======\r\n");
		for (Hnode cause : list) {
			String name = HardItem.fromNode(cause).longName();
			sb.append(name + "\t");
		}
		list = join().addTo(idea).addFrom(tagCause).run();
		sb.append("\r\n======= cause =======\r\n");
		for (Hnode cause : list) {
			Hnode resultIdea = join().addTo(cause).addFrom(tagIdea).run().get(0);
			Hnode resultConcept = join().addTo(resultIdea).addFrom(tagConcept).run().get(0);
			Hnode resultMind = join().addTo(resultIdea).addFrom(tagMind).run().get(0);
			scriptOrRole = mindToScriptOrRole(resultMind);
			String name = HardItem.fromNode(resultConcept).name + "." + HardItem.fromNode(scriptOrRole).name;
			sb.append(name + "\t");
		}
		list = join().addTo(idea).addFrom(tagTrigger).run();
		sb.append("\r\n======= trigger by =======\r\n");
		for (Hnode trigger : list) {
			Hnode hitterIdea = join().addTo(trigger).addFrom(tagIdea).run().get(0);
			Hnode hitterConcept = join().addTo(hitterIdea).addFrom(tagConcept).run().get(0);
			Hnode hitterMind = join().addTo(hitterIdea).addFrom(tagMind).run().get(0);
			scriptOrRole = mindToScriptOrRole(hitterMind);
			String name = HardItem.fromNode(hitterConcept).name + "." + HardItem.fromNode(scriptOrRole).name;
			sb.append(name + "\t");
		}
		list = join().addFrom(idea).addFrom(tagTrigger).run();
		sb.append("\r\n======= trigger =======\r\n");
		for (Hnode trigger : list) {
			String name = HardItem.fromNode(trigger).longName();
			sb.append(name + "\t");
		}
		return sb.toString();
	}

	// =============== cause =================
	public boolean CauseExist(Hnode caused, Hnode by) {
		if (caused.getId() == 0 || by.getId() == 0)
			return false;
		JoinList jl = join().addFrom(tagCause).addFrom(caused).addTo(by).run();
		return !jl.isEmpty();
	}

	public Hnode causeToIdea(Hnode bridge) {
		return join().addFrom(bridge).addFrom(tagIdea).run().get(0);
	}

	public JoinList ideaToCauseList(Hnode idea) {
		return join().addTo(idea).addFrom(tagCause).run();
	}

	// ================= trigger ====================
	public boolean triggerExist(Hnode host, Hnode target) {
		if (host.getId() == 0 || target.getId() == 0)
			return false;
		JoinList jl = join().addFrom(tagTrigger).addFrom(host).addTo(target).run();
		return !jl.isEmpty();
	}

	public Hnode triggerToIdea(Hnode bridge) {
		return join().addFrom(bridge).addFrom(tagIdea).run().get(0);
	}

	public JoinList ideaToTriggerList(Hnode idea) {
		return join().addTo(idea).addFrom(tagTrigger).run();
	}

	public Hnode ideaToConcept(Hnode idea) {
		return join().addTo(idea).addFrom(tagConcept).run().get(0);
	}

	public List<Hnode> soulToConceptList(Hnode soul) {
		return getTreeList(tagConcept, soul);
	}

	// ================= engine ==================
	public JuniorDef genEngine() {
		final JuniorDef def = new JuniorDef();

		// profession-soul
		join().addFrom(tagProfession).run().forEach(professionNode -> {
			Hnode soulNode = join().addFrom(professionNode).addFrom(tagSoul).run().get(0);
			Soul soul = loadSoul(soulNode);
			def.profession_Soul.put(professionNode.getIdAsInt(), soul);
		});

		final HashMap<Long, IdeaCache> id_IdeaCache = new HashMap<>();
		// script
		join().addFrom(tagScript).run().forEach(scriptNode -> {
			Script script = new Script(scriptNode.getIdAsInt());
			script.name = HardItem.fromNode(scriptNode).name;
			def.id_Script.put(script.id, script);

			// script-soul
			Hnode scriptSoul = join().addFrom(scriptNode).addFrom(tagSoul).run().get(0);
			script.soul = loadSoul(scriptSoul);
			def.script_Soul.put(script.id, script.soul);

			ArrayList<Mind> mindList = new ArrayList<>();
			// script-mind
			Hnode scriptMindNode = join().addFrom(scriptNode).addFrom(tagMind).run().get(0);
			loadIdea(script.name, scriptMindNode, mindList, id_IdeaCache, script.soul);
			// role-mind
			join().addFrom(scriptNode).addFrom(tagRole).run().forEach(roleNode -> {
				Hnode roleMind = join().addFrom(roleNode).addFrom(tagMind).run().get(0);
				Hnode roleProfession = join().addTo(roleNode).addFrom(tagProfession).run().get(0);
				Soul identitySoul = def.profession_Soul.get(roleProfession.getIdAsInt());
				HardItem hi = HardItem.fromNode(roleNode);
				String roleName = hi.name;
				loadIdea(roleName, roleMind, mindList, id_IdeaCache, identitySoul);
			});
			script.mindList = mindList.toArray(new Mind[] {});
		});

		// cause-trigger
		id_IdeaCache.values().forEach(ic -> {
			// cause
			join().addFrom(ic.node).addFrom(tagCause).run().forEach(cause -> {
				Hnode causeBy = join().addFrom(cause).addFrom(tagIdea).run().get(0);
				IdeaCache found = id_IdeaCache.get(causeBy.getId());
				ic.causeBy.add(found.idea);
				found.causeTo.add(ic.idea);
			});
			// flashy
			if (ic.idea.flashy) {
				Trigger triggerOff = new Trigger();
				triggerOff.target = ic.idea;
				triggerOff.value = Integer.MIN_VALUE;
				ic.idea.setTriggerFlashy(triggerOff);
				// ic.triggerOn.add(triggerOn);
			}
			// trigger
			SortedLinkedList<HardItem> triggerList = new SortedLinkedList<>();
			join().addFrom(ic.node).addFrom(tagTrigger).run().forEach(triggerN -> {
				HardItem hi = HardItem.fromNode(triggerN);
				triggerList.addSort(hi);
			});

			triggerList.forEach(hi -> {
				TriggerR triggerR = new TriggerR(ksJuniorEdit);
				triggerR.fromBytes(hi.data);
				if (triggerR.on == 0 && triggerR.off == 0)
					return;

				Hnode triggerTo = join().addFrom(hi.getNode()).addFrom(tagIdea).run().get(0);
				IdeaCache found = id_IdeaCache.get(triggerTo.getId());

				if (triggerR.on != 0) {
					Trigger triggerOn = new Trigger();
					triggerOn.target = found.idea;
					triggerOn.value = triggerR.on;
					ic.triggerOn.add(triggerOn);
				}
				if (triggerR.off != 0) {
					Trigger triggerOff = new Trigger();
					triggerOff.target = found.idea;
					triggerOff.value = triggerR.off;
					ic.triggerOff.add(triggerOff);
				}
			});
		});

		id_IdeaCache.values().forEach(ic -> {
			ic.idea.causeBy = ic.causeBy.toArray(new Idea[] {});
			ic.idea.causeTo = ic.causeTo.toArray(new Idea[] {});
			ic.idea.triggerOn = ic.triggerOn.toArray(new Trigger[] {});
			ic.idea.triggerOff = ic.triggerOff.toArray(new Trigger[] {});
		});
		return def;
	}

	private void loadIdea(String mindName, Hnode mindNode, ArrayList<Mind> mindList,
			HashMap<Long, IdeaCache> id_IdeaCache, Soul soul) {
		Mind mind = new Mind(mindName);
		mind.idx = mindList.size();
		mindList.add(mind);

		Hnode soulNode = join().addTo(mindNode).addFrom(tagSoul).run().get(0);
		getTreeList(tagConcept, soulNode).forEach(conceptNode -> {
			Concept c = soul.getConcept(conceptNode.getIdAsInt());
			Hnode ideaNode = joinIdea(mindNode, conceptNode);
			if (ideaNode == null)
				throw new Error();

			K_Idea idea = new K_Idea(ksJuniorEdit);
			idea.fromBytes(ideaNode.getData());

			IdeaCache ic = new IdeaCache();
			ic.idea = new Idea(c, mind, idea.defaultValue);
			ic.idea.flashy = idea.flashy;
			ic.idea.outLink = idea.outLink;
			ic.node = ideaNode;
			id_IdeaCache.put(ideaNode.getId(), ic);
			mind.concept_Idea.put(ic.idea.concept.id, ic.idea);
		});
	}

	private Soul loadSoul(Hnode soulNode) {
		Soul soul = new Soul(soulNode.getIdAsInt());
		Hnode sOrI = join().addTo(soulNode).addFrom(tagScript).run().get(0);
		if (sOrI == null) {
			sOrI = join().addTo(soulNode).addFrom(tagProfession).run().get(0);
		}
		soul.name = HardItem.fromNode(sOrI).name;
		getTreeList(tagConcept, soulNode).forEach(conceptNode -> {
			ConceptR conceptR = new ConceptR(ksJuniorEdit);
			conceptR.hi = HardItem.fromNode(conceptNode);
			conceptR.fromBytes(conceptR.hi.data);

			Concept c = new Concept(conceptNode.getIdAsInt());
			c.ioType = IoType.values()[conceptR.ioType];
			c.counterTrigger = conceptR.counterTrigger;
			c.name = conceptR.hi.longName();

			soul.id_Concept.put(conceptR.getId(), c);
		});
		return soul;
	}

	class IdeaCache {
		Hnode node;
		Idea idea;

		ArrayList<Idea> causeBy = new ArrayList<>();
		ArrayList<Idea> causeTo = new ArrayList<>();
		ArrayList<Trigger> triggerOn = new ArrayList<>();
		ArrayList<Trigger> triggerOff = new ArrayList<>();
	}

	public JoinList scriptToRoleList(Hnode script) {
		return join().addFrom(script).addFrom(tagRole).run();
	}

	public Hnode roleToProfession(Hnode role) {
		return join().addTo(role).addFrom(tagProfession).run().get(0);
	}

	public void fillIdea(Hnode soul, Hnode concept) {
		Batch batch = new Batch();
		join().addFrom(soul).addFrom(tagMind).run().forEach(mind -> {
			K_Idea ki = new K_Idea(ksJuniorEdit);
			Hnode idea = new Hnode();
			idea.setData(ki.toBytes());
			batch.link(mind, idea).link(concept, idea).link(tagIdea, idea).save(idea);
		});
		execute(batch);
	}

	public boolean isConceptNaked(Hnode concept) {
		JoinList ideaList = join().addFrom(concept).addFrom(tagIdea).run();
		for (Hnode idea : ideaList) {
			boolean hasRelation = false;
			hasRelation = !join().addFrom(idea).addFrom(tagTrigger).run().isEmpty();
			if (hasRelation)
				return false;
			hasRelation = !join().addTo(idea).addFrom(tagTrigger).run().isEmpty();
			if (hasRelation)
				return false;
			hasRelation = !join().addFrom(idea).addFrom(tagCause).run().isEmpty();
			if (hasRelation)
				return false;
			hasRelation = !join().addTo(idea).addFrom(tagCause).run().isEmpty();
			if (hasRelation)
				return false;
		}
		return true;
	}

	public void clearIdea(Batch batch, Hnode concept) {
		join().addFrom(concept).addFrom(tagIdea).run().forEach(idea -> {
			Hnode mind = join().addFrom(tagMind).addTo(idea).run().get(0);
			batch.delink(concept, idea).delink(tagIdea, idea).delink(mind, idea).delete(idea);
		});
	}

	mxGraph graph;
	Object parent;

	class IdeaLink {
		Hnode me;
		mxCell myVertex;
		ArrayList<Hnode> trigger_idea = new ArrayList<>();
		ArrayList<Hnode> triggerBy_idea = new ArrayList<>();
		ArrayList<Hnode> cause_idea = new ArrayList<>();
		ArrayList<Hnode> causeBy_idea = new ArrayList<>();
	}

	// ========================== draw ========================
	public byte[] drawRelation(Hnode mind, Hnode concept) {
		Hnode idea = joinIdea(mind, concept);

		ArrayList<IdeaLink> relatedIdeaList = new ArrayList<>();
		HashMap<Hnode, IdeaLink> Hnode_IdeaLink = new HashMap<>();

		LinkedList<Hnode> ideaProbeStack = new LinkedList<>();
		ideaProbeStack.push(idea);
		HashSet<Hnode> doneSet = new HashSet<>();
		while (ideaProbeStack.size() > 0) {
			Hnode currentIdea = ideaProbeStack.removeFirst();
			if (doneSet.contains(currentIdea))
				continue;

			doneSet.add(currentIdea);
			IdeaLink iL = new IdeaLink();
			iL.me = currentIdea;
			relatedIdeaList.add(iL);
			Hnode_IdeaLink.put(currentIdea, iL);
			join().addFrom(currentIdea).addFrom(tagCause).run().forEach((cause) -> {
				Hnode n = join().addFrom(cause).addFrom(tagIdea).run().get(0);
				iL.cause_idea.add(n);
				ideaProbeStack.add(n);
			});
			join().addTo(currentIdea).addFrom(tagCause).run().forEach((cause) -> {
				Hnode n = join().addTo(cause).addFrom(tagIdea).run().get(0);
				iL.causeBy_idea.add(n);
				ideaProbeStack.add(n);
			});
			join().addFrom(currentIdea).addFrom(tagTrigger).run().forEach((trigger) -> {
				Hnode n = join().addFrom(trigger).addFrom(tagIdea).run().get(0);
				iL.trigger_idea.add(n);
				ideaProbeStack.add(n);
			});
			if (currentIdea == idea) {
				join().addTo(currentIdea).addFrom(tagTrigger).run().forEach((trigger) -> {
					Hnode n = join().addTo(trigger).addFrom(tagIdea).run().get(0);
					iL.triggerBy_idea.add(n);
					ideaProbeStack.add(n);
				});
			}
		}

		graph = new mxGraph();
		parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();

		mxStylesheet style = graph.getStylesheet();
		Map<String, Object> vstyle = new HashMap<String, Object>(style.getDefaultVertexStyle());
		vstyle.clear();
		vstyle.put(mxConstants.STYLE_FONTSIZE, "22");
		vstyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
		vstyle.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_CENTER);
		vstyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		vstyle.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
		vstyle.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
		vstyle.put(mxConstants.STYLE_SPACING_TOP, 5);
		vstyle.put(mxConstants.STYLE_FONTFAMILY, "微软雅黑");
		vstyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");

		for (IdeaLink item : relatedIdeaList) {
			concept = join().addTo(item.me).addFrom(tagConcept).run().get(0);
			ConceptR conceptR = new ConceptR(ksJuniorEdit);
			conceptR.hi = HardItem.fromNode(concept);
			conceptR.fromBytes(conceptR.hi.data);

			K_Idea ki = new K_Idea(ksJuniorEdit);
			ki.fromBytes(item.me.getData());

			if (conceptR.ioType == IoType.INPUT.ordinal())
				vstyle.put(mxConstants.STYLE_FONTCOLOR, "#047639");
			else if (conceptR.ioType == IoType.OUTPUT.ordinal())
				vstyle.put(mxConstants.STYLE_FONTCOLOR, "#bc0d0d");
			else if (conceptR.ioType == IoType.ASK.ordinal())
				vstyle.put(mxConstants.STYLE_FONTCOLOR, "#0c19c4");
			else
				vstyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");

			StringBuilder sb = new StringBuilder();
			vstyle.forEach((name, value) -> {
				sb.append(name + "=" + value + ";");
			});

			mxCell v = (mxCell) graph.insertVertex(parent, null, conceptR.hi.longName(), 0, 0, 80, 40, sb.toString());

			graph.updateCellSize(v);
			item.myVertex = v;

			graph.updateCellSize(v);
		}

		for (IdeaLink item : relatedIdeaList) {
			item.cause_idea.forEach((cause) -> {
				graph.insertEdge(parent, null, "", Hnode_IdeaLink.get(cause).myVertex, item.myVertex,
						"strokeColor=0xff0000;endArrow=oval;");
			});
		}

		for (IdeaLink item : relatedIdeaList) {
			HashSet<Hnode> triggerSet = new HashSet<>();
			join().addFrom(item.me).addFrom(tagTrigger).run().forEach((triggerB) -> {
				if (triggerSet.contains(triggerB))
					return;
				triggerSet.add(triggerB);

				Hnode trigger = join().addFrom(triggerB).addFrom(tagIdea).run().get(0);

				HardItem hi = HardItem.fromNode(triggerB);
				TriggerR tr = new TriggerR(ksJuniorEdit);
				tr.fromBytes(hi.data);
				String arrow = "classic";
				if (tr.on < 0)
					arrow = "diamond";

				int color = FastMath.randomBrightColor();
				graph.insertEdge(parent, null, "", item.myVertex, Hnode_IdeaLink.get(trigger).myVertex,
						"strokeColor=" + color + ";endArrow=" + arrow + ";");
			});

		}

		// graph.repaint();
		graph.getModel().endUpdate();
		mxIGraphLayout layout = new mxHierarchicalLayout(graph);
		layout.execute(graph.getDefaultParent());
		BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, java.awt.Color.WHITE, true, null);
		return ImageUtil.encodePng(image);
	}
}
