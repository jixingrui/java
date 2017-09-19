package azura.junior.client;

public interface JuniorOutputI {

	void newEgoR(int token, int idEgo);

	void newSceneR(int token, int idScene);

	void ask(int idEgo, int idConcept, int token);

	void output(int idEgo, int idConcept);

	void log(LogLevelE level, int idEgo, String name, String reason);

	void outLink(int idEgo, int idConcept, String name, String reason);
}
