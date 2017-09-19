package azura.junior;

import azura.helios6.Hnode;
import azura.junior.hard.CauseHandler;
import azura.junior.hard.ConceptCopyHandler;
import azura.junior.hard.ConceptHandler;
import azura.junior.hard.ProfessionHandler;
import azura.junior.hard.RoleCopyHandler;
import azura.junior.hard.RoleHandler;
import azura.junior.hard.ScriptHandler;
import azura.junior.hard.TriggerHandler;

public class HardCenter {

	public ScriptHandler scriptHandler = new ScriptHandler();
	public RoleHandler roleHandler = new RoleHandler();
	public RoleCopyHandler roleCopyHandler = new RoleCopyHandler();
	public ConceptHandler conceptHandler = new ConceptHandler();
	public ConceptCopyHandler conceptCopyHandler = new ConceptCopyHandler();
	public TriggerHandler triggerHandler = new TriggerHandler();
	public CauseHandler causeHandler = new CauseHandler();
	public ProfessionHandler identityHandler = new ProfessionHandler();

	// ======================
	public Hnode idea;
	public Hnode scriptMind;
	public Hnode scriptSoul;
	public Hnode roleMind;
	public Hnode roleSoul;
	public Hnode roleCopyMind;
	public Hnode roleCopySoul;
	public Hnode ideaCopy;
	public Hnode script;

	public HardCenter() {
		scriptHandler.center = this;
		roleHandler.center = this;
		roleCopyHandler.center = this;
		conceptHandler.center = this;
		conceptCopyHandler.center = this;
		triggerHandler.center = this;
		causeHandler.center = this;
		identityHandler.center = this;
	}

	public void clear() {
		idea = null;
		ideaCopy = null;
		scriptMind = null;
		scriptSoul = null;
		roleMind = null;
		roleSoul = null;
		roleCopyMind = null;
		roleCopySoul = null;
	}

	public Hnode currentMind() {
		if (roleMind != null)
			return roleMind;
		else
			return scriptMind;
	}

	public Hnode currentMindCopy() {
		if (roleCopyMind != null)
			return roleCopyMind;
		else
			return scriptMind;
	}

}
