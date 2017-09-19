package azura.helios5;

import java.util.EnumMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import azura.helios5.batch.Batch;

public class Helios5<E extends Enum<E>> extends Helios5Core {
	private EnumMap<E, HeliosNode> enum_Node;

	private ExecutorService writerThread = Executors.newSingleThreadExecutor();

	/**
	 * @param path
	 *            db file location
	 * @param clazz
	 *            tag enum
	 * @param cacheSize
	 *            default=8192
	 */
	public Helios5(String path, Class<E> clazz) {
		this(path, clazz, 8192);
	}

	/**
	 * @param path
	 *            db file location
	 * @param clazz
	 *            tag enum
	 * @param cacheSize
	 */
	public Helios5(String path, Class<E> clazz, int cacheSize) {
		super(path, cacheSize);

		// init tags
		enum_Node = new EnumMap<>(clazz);
		E[] values = clazz.getEnumConstants();

		for (int i = 0; i < values.length; i++) {
			HeliosNode node = new HeliosNode(values[i]);
			enum_Node.put(values[i], node);
		}
		super.initTag(enum_Node.values());
	}

	@Override
	protected void wipe() {
		super.wipe();
		super.initTag(enum_Node.values());
	};

	public HeliosNode getTagNode(E tag) {
		return enum_Node.get(tag);
	}

	/**
	 * @link if the link is already there, fine, nothing changed
	 * @delink if the link is not here, fine, nothing changed
	 * @delete the node must be orphan otherwise the batch will rollback
	 */
	public final CompletableFuture<Batch> executeAsync(Batch batch) {
		return CompletableFuture.supplyAsync(() -> {
			this.executeNow(batch);
			return batch;
		}, writerThread);
	}
}
