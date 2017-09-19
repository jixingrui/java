package azura.zombie.logic;

import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import avalon.ice4.Body;
import avalon.ice4.Eye;
import avalon.ice4.IceRoom4;
import avalon.ice4.Walker;
import avalon.ice4.i.EyeI;
import avalon.ice4.i.SeeE;
import azura.ice.watch.Watcher;
import common.algorithm.FastMath;

public abstract class Actor extends Watcher implements EyeI {
	public Walker body;
	public Eye eye;

	public Actor(IceRoom4 room, int x, int y, int z, int angle, byte[] skin, int width, int height) {
		body = room.newWalker(x, y, angle, skin);
		super.id = body.id;
		body.cargo = this;
		eye = room.newEye(this, x, y, width, height);
	}

	@Override
	public void eyeSee(SeeE event, Body one) {
		// log.info("see " + event.name() + " " + one.toString());
		if (eye == null) {
			CompletableFuture.runAsync(() -> eyeSee(event, one));
			return;
		}

		// if (event == SeeE.MOVE) {
		// log.info(body.cargo.getClass().getSimpleName() + " at " +
		// one.toString());
		// }
		if (this.body == one && (event == SeeE.NEW || event == SeeE.MOVE)) {
			// log.info(body.cargo.getClass().getSimpleName() + " at " +
			// one.toString());
			eye.move(one.x, one.y);
			super.watcherMoved(one.x, one.y);
		}
	}

	@SuppressWarnings("unchecked")
	public <C> CompletableFuture<C> findNearest(Class<C> clazz) {
		CompletableFuture<C> future = new CompletableFuture<C>();

		eye.everybodyInVisual().thenAccept((seeList) -> {
			TreeSet<SortUnit> sorter = new TreeSet<>();
			seeList.forEach(walker -> {
				if (walker instanceof Walker == false)
					return;

				SortUnit pw = new SortUnit(body, (Walker) walker);
				sorter.add(pw);
			});

			for (SortUnit pw : sorter) {
				if (clazz.isInstance(pw.two.cargo)) {
					C result = (C) pw.two.cargo;
					future.complete(result);
				}
			}
		});

		return future;
	}

	class SortUnit implements Comparable<SortUnit> {
		double dist;
		private Walker two;

		public SortUnit(Walker one, Walker two) {
			this.two = two;
			dist = FastMath.dist(one.x, one.y, two.x, two.y);
		}

		@Override
		public int compareTo(SortUnit other) {
			if (this.dist > other.dist)
				return 1;
			else if (this.dist < other.dist)
				return -1;
			else
				return 0;
		}

		@Override
		public boolean equals(Object obj) {
			SortUnit other = (SortUnit) obj;
			return this.compareTo(other) == 0;
		}
	}
}
