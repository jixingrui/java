package azura.junior.hard;

import azura.helios6.Hnode;
import azura.junior.HardCenter;
import azura.junior.db.HeliosJunior3;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;

public class RoleCopyHandler implements HardHandlerI {

	public HardCode hc;
	public HardCenter center;

	@Override
	public Hnode getTagNode() {
		return HeliosJunior3.me().tagRole;
	}

	@Override
	public void setHardCode(HardCode hc) {
		this.hc = hc;
	}

	@Override
	public boolean isTree() {
		return false;
	}

	@Override
	public void add() {
	}

	@Override
	public void rename(String name) {
	}

	@Override
	public void delete() {
	}

	@Override
	public void save(byte[] newData) {
	}

	@Override
	public void drop() {
	}

	@Override
	public void notifySelect() {
		center.roleCopyMind = HeliosJunior3.me().sOrRToMind(
				hc.selectedItem.getNode());
		center.roleCopySoul = HeliosJunior3.me()
				.mindToSoul(center.roleCopyMind);
		center.conceptCopyHandler.hc.setRoot(center.roleCopySoul);
	}

	@Override
	public void notifyUnselect() {
		center.roleCopyMind = null;
		center.roleCopySoul = null;
		center.conceptCopyHandler.hc.setRoot(center.scriptSoul);
	}

	@Override
	public void notifyRefreshRelatedAll() {
	}

	@Override
	public void notifyRefreshRelatedRoot() {
	}

}
