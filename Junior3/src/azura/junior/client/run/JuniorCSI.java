package azura.junior.client.run;

public interface JuniorCSI {

	public void newEgo(int idConcept, int token);

	public void newScene(int idScript, int token);

	public void deleteEgo(int id);

	public void deleteScene(int id);

	public void play(int idScene, int idxRole, int idEgo);

	public void turnOn(int idEgo, int idConcept);

	public void turnOff(int idEgo, int idConcept);

	public void answer(int idEgo, int idConcept, boolean value, int token);

}
