package azura.ice.watch;

public class Test {

	public static void main(String[] args) {
		Garden garden = new Garden();
		Mover zombie = garden.newMover();

		Mover gunner = garden.newMover();
		gunner = garden.getMover(gunner.id);
		gunner.move(12, 33);
		gunner.watch(zombie, ComparatorE.Larger, 100, () -> {
			garden.deleteMover(zombie.id);
		});
	}

}
