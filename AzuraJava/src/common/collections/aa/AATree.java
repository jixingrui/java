package common.collections.aa;

import java.util.Iterator;
import java.util.Stack;

//AATree class
//
//CONSTRUCTION: with no initializer
//
//******************PUBLIC OPERATIONS*********************
//void insert( x )       --> Insert x
//void remove( x )       --> Remove x
//Comparable find( x )   --> Return item that matches x
//Comparable findMin( )  --> Return smallest item
//Comparable findMax( )  --> Return largest item
//boolean isEmpty( )     --> Return true if empty; else false
//void makeEmpty( )      --> Remove all items
//******************ERRORS********************************
//Exceptions are thrown by insert and remove if warranted

/**
 * Implements an AA-tree. Note that all "matching" is based on the compareTo
 * method.
 * 
 * @author Mark Allen Weiss
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AATree<T extends Comparable> implements Iterable<T> {
	/**
	 * Construct the tree.
	 */
	public AATree() {
		root = AANode.nullNode;
	}

	/**
	 * Insert into the tree.
	 * 
	 * @param x
	 *            the item to insert.
	 */
	public void insert(Comparable x) {
		root = insert(x, root);
	}

	/**
	 * Remove from the tree.
	 * 
	 * @param x
	 *            the item to remove.
	 */
	public void remove(Comparable x) {
		deletedNode = AANode.nullNode;
		root = remove(x, root);
	}

	/**
	 * Find the smallest item in the tree.
	 * 
	 * @return the smallest item or null if empty.
	 */
	public Comparable findMin() {
		if (isEmpty())
			return null;

		AANode ptr = root;

		while (ptr.left != AANode.nullNode)
			ptr = ptr.left;

		return ptr.element;
	}

	/**
	 * Find the largest item in the tree.
	 * 
	 * @return the largest item or null if empty.
	 */
	public Comparable findMax() {
		if (isEmpty())
			return null;

		AANode ptr = root;

		while (ptr.right != AANode.nullNode)
			ptr = ptr.right;

		return ptr.element;
	}

	/**
	 * Find an item in the tree.
	 * 
	 * @param x
	 *            the item to search for.
	 * @return the matching item of null if not found.
	 */

	public Comparable find(Comparable x) {
		AANode current = root;
		AANode.nullNode.element = x;

		for (;;) {
			if (x.compareTo(current.element) < 0)
				current = current.left;
			else if (x.compareTo(current.element) > 0)
				current = current.right;
			else if (current != AANode.nullNode)
				return current.element;
			else
				return null;
		}
	}

	public T findSelfOrSmaller(T x) {
		AANode current = root;
		AANode.nullNode.element = x;
		AANode smaller = null;

		for (;;) {
			if (x.compareTo(current.element) < 0) {
				current = current.left;
			} else if (x.compareTo(current.element) > 0) {
				smaller = current;
				current = current.right;
			} else if (current != AANode.nullNode)
				return (T) current.element;
			else if (smaller != null)
				return (T) smaller.element;
			else
				return null;
		}
	}

	/**
	 * Make the tree logically empty.
	 */
	public void clear() {
		root = AANode.nullNode;
	}

	/**
	 * Test if the tree is logically empty.
	 * 
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty() {
		return root == AANode.nullNode;
	}

	/**
	 * Internal method to insert into a subtree.
	 * 
	 * @param x
	 *            the item to insert.
	 * @param t
	 *            the node that roots the tree.
	 * @return the new root.
	 */
	private AANode insert(Comparable x, AANode t) {
		if (t == AANode.nullNode)
			t = new AANode(x);
		else if (x.compareTo(t.element) < 0)
			t.left = insert(x, t.left);
		else if (x.compareTo(t.element) > 0)
			t.right = insert(x, t.right);
		else
			throw new Error("x is already present: " + x.toString());

		t = skew(t);
		t = split(t);
		return t;
	}

	/**
	 * Internal method to remove from a subtree.
	 * 
	 * @param x
	 *            the item to remove.
	 * @param t
	 *            the node that roots the tree.
	 * @return the new root.
	 */
	private AANode remove(Comparable x, AANode t) {
		if (t != AANode.nullNode) {
			// Step 1: Search down the tree and set lastNode and deletedNode
			lastNode = t;
			if (x.compareTo(t.element) < 0)
				t.left = remove(x, t.left);
			else {
				deletedNode = t;
				t.right = remove(x, t.right);
			}

			// Step 2: If at the bottom of the tree and
			// x is present, we remove it
			if (t == lastNode) {
				if (deletedNode == AANode.nullNode
						|| x.compareTo(deletedNode.element) != 0)
					throw new Error("x is not found: " + x.toString());
				deletedNode.element = t.element;
				t = t.right;
			}

			// Step 3: Otherwise, we are not at the bottom; rebalance
			else if (t.left.level < t.level - 1 || t.right.level < t.level - 1) {
				if (t.right.level > --t.level)
					t.right.level = t.level;
				t = skew(t);
				t.right = skew(t.right);
				t.right.right = skew(t.right.right);
				t = split(t);
				t.right = split(t.right);
			}
		}
		return t;
	}

	/**
	 * Skew primitive for AA-trees.
	 * 
	 * @param t
	 *            the node that roots the tree.
	 * @return the new root after the rotation.
	 */
	private static AANode skew(AANode t) {
		if (t.left.level == t.level)
			t = rotateWithLeftChild(t);
		return t;
	}

	/**
	 * Split primitive for AA-trees.
	 * 
	 * @param t
	 *            the node that roots the tree.
	 * @return the new root after the rotation.
	 */
	private static AANode split(AANode t) {
		if (t.right.right.level == t.level) {
			t = rotateWithRightChild(t);
			t.level++;
		}
		return t;
	}

	/**
	 * Rotate binary tree node with left child.
	 */
	private static AANode rotateWithLeftChild(AANode k2) {
		AANode k1 = k2.left;
		k2.left = k1.right;
		k1.right = k2;
		return k1;
	}

	/**
	 * Rotate binary tree node with right child.
	 */
	private static AANode rotateWithRightChild(AANode k1) {
		AANode k2 = k1.right;
		k1.right = k2.left;
		k2.left = k1;
		return k2;
	}

	private AANode root;

	private static AANode deletedNode;
	private static AANode lastNode;

	// Test program; should print min and max and nothing else
//	public static void main(String[] args) {
//		// testStandard();
//		testSmaller();
//	}

//	private static void testSmaller() {
//		AATree t = new AATree();
//		t.insert(1);
//		t.insert(2);
//		t.insert(3);
//		t.insert(5);
//		t.insert(6);
//		t.insert(8);
//		t.insert(9);
//		t.insert(12);
//
//		// Trace.trace(t.toString());
//
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < 15; i++) {
//			Integer smaller = (Integer) t.findSelfOrSmaller(i);
//			if (smaller == null)
//				smaller = -1;
//			sb.append(smaller + ",");
//		}
//		// Trace.trace(sb.toString());
//	}

	protected static void testStandard() {
		AATree t = new AATree();
		final int NUMS = 40000;
		final int GAP = 307;

		System.out.println("Checking... (no bad output means success)");

		t.insert(new Integer(NUMS * 2));
		t.insert(new Integer(NUMS * 3));
		for (int i = GAP; i != 0; i = (i + GAP) % NUMS)
			t.insert(new Integer(i));
		System.out.println("Inserts complete");

		t.remove(t.findMax());
		for (int i = 1; i < NUMS; i += 2)
			t.remove(new Integer(i));
		t.remove(t.findMax());
		System.out.println("Removes complete");

		if (((Integer) (t.findMin())).intValue() != 2
				|| ((Integer) (t.findMax())).intValue() != NUMS - 2)
			System.out.println("FindMin or FindMax error!");

		for (int i = 2; i < NUMS; i += 2)
			if (((Integer) t.find(new Integer(i))).intValue() != i)
				System.out.println("Error: find fails for " + i);

		for (int i = 1; i < NUMS; i += 2)
			if (t.find(new Integer(i)) != null)
				System.out.println("Error: Found deleted item " + i);
	}

	@Override
	public Iterator<T> iterator() {
		return new BSTIterator(root);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Iterator i = this.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}

}

@SuppressWarnings("rawtypes")
class AANode {
	static AANode nullNode = null;
	static // static initializer for nullNode
	{
		nullNode = new AANode(null);
		nullNode.left = nullNode.right = nullNode;
		nullNode.level = 0;
	}

	// Constructors
	AANode(Comparable theElement) {
		element = theElement;
		left = right = nullNode;
		level = 1;
	}

	Comparable element; // The data in the node
	AANode left; // Left child
	AANode right; // Right child
	int level; // Level
}

@SuppressWarnings("rawtypes")
class BSTIterator implements Iterator {
	Stack<AANode> iteratorStack;
	AANode root, cursor;

	public BSTIterator(AANode root) {
		this.root = root;
		this.cursor = root;
		this.iteratorStack = new Stack<AANode>();
	}

	public boolean hasNext() {
		return (!iteratorStack.empty() || cursor != AANode.nullNode);
	}

	public Comparable next() {
		Comparable nextNodeValue;
		while (cursor != AANode.nullNode) {
			iteratorStack.push(cursor);
			cursor = cursor.left;
		}
		cursor = iteratorStack.pop();
		nextNodeValue = cursor.element;
		cursor = cursor.right;
		return nextNodeValue;
	}

	@Override
	public void remove() {
	}
}
