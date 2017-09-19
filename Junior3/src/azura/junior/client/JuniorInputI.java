package azura.junior.client;

public interface JuniorInputI {

	void newEgo(ProA ego);

	void deleteEgo(int id);

	void newScene(ProA scene);

	void deleteScene(int id);

	void play(int idScene, int idxRole, int idEgo);

	void input(ProA host, int idConcept);

}