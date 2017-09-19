package azura.helios.hard10.ie;

public interface HardServerI {

	void hold();

	void add(byte[] data);

	void rename(String name);

	void delete();

	void select();

	void save(byte[] data);

	void jump();

	void unselect_();

	void askMore(boolean up_down, int more);

	void drop_();
}
