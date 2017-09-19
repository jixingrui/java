package azura.junior.client;

import common.collections.ArrayListAuto;

public abstract class ScriptA extends ProA {
	private ArrayListAuto<ProA> roleList = new ArrayListAuto<>();

	public ScriptA(int type, JuniorInputI client) {
		super(type, client);
	}

	public void play(int idxRole, ProA ego) {
		roleList.set(idxRole, ego);
		client.play(this.id, idxRole, ego.id);
	}

	public ProA getRole(int idxRole) {
		return roleList.get(idxRole);
	}

	@Override
	public void dispose() {
		if(roleList==null)
			return;
		client.deleteScene(id);
		roleList=null;
	}

}
