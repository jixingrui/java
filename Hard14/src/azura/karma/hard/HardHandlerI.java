package azura.karma.hard;

import azura.helios6.Hnode;

public interface HardHandlerI {

	Hnode getTagNode();

	void setHardCode(HardCode hc);

	boolean isTree();

	void add();

	void rename(String name);

	void delete();

	void save(byte[] newData);

	void drop();

	void notifySelect();

	void notifyUnselect();

	void notifyRefreshRelatedAll();

	void notifyRefreshRelatedRoot();

}
