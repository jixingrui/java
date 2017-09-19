package azura.junior.hard;

import azura.helios6.Hnode;
import azura.junior.HardCenter;
import azura.junior.db.HeliosJunior3;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;

public class ConceptCopyHandler implements HardHandlerI {

	public HardCode hc;
	public HardCenter center;

	@Override
	public Hnode getTagNode() {
		return HeliosJunior3.me().tagConcept;
	}

	@Override
	public void setHardCode(HardCode hc) {
		this.hc = hc;
	}

	@Override
	public boolean isTree() {
		return true;
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
		Hnode conceptCopy = hc.selectedItem.getNode();
		center.ideaCopy = HeliosJunior3.me().joinIdea(center.currentMindCopy(),
				conceptCopy);
	}

	@Override
	public void notifyUnselect() {
		center.ideaCopy = null;
	}

	@Override
	public void notifyRefreshRelatedAll() {
	}

	@Override
	public void notifyRefreshRelatedRoot() {
	}

}
