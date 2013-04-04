/** @author Arturo Pacifico Griffini. */

import java.lang.Exception;

public abstract class BitTrie {

	/** The empty Trie. */
	public static final BitTrie EMPTY = new EmptyTrie();
	
	

	/** The label at this node. Defined only on leaves. */
	abstract public int label() throws Exception;
	
	/** True if this Trie is a leaf (containing a single String). */
	abstract public boolean isLeaf();
	
	/** True if this Trie is empty */
	abstract public boolean isEmpty();
	
	/** The child numbered with character K. Requires that this node
	* not be empty. Child 0 corresponds to ✷. */
	abstract public BitTrie child(int k) throws Exception;
	
	/** Set the child numbered with character K to CHILD. Requires that
	* this node not be empty. (Intended only for internal use. */
	abstract protected void setChild(int k, BitTrie child) throws Exception;


	/** True if X is in this Trie. */
	public boolean isIn(int x) throws Exception {
		BitTrie lp = longestPrefix(x, 0);
		return lp.isLeaf() && lp.label() == x;
	}

	/** The node repsenting the longest prefix of X that
	 * 	matches a int in this trie. */
	private BitTrie longestPrefix(int x, int startLevel) throws Exception {
		if (isEmpty() || isLeaf()) {
			return this;
		} 
		int bit = getBit(x, startLevel);
		if (child(bit).isEmpty()) {
			return this;
		} else {
			return child(bit).longestPrefix(x, startLevel + 1);
		}
	}


	
	/** The result of inserting X into this Trie, if it is not
	* already there, and returning this. This trie is
	* unchanged if X is in it already. */
	public BitTrie insert(int x) throws Exception {
		return insert(x, 0);
	}
	
	/** Assumes this is the node at LEVEL in some trie.
	 *	Returns this after the NUM was inserted.
	 *	No effect if NUM is already in the trie. */
	private BitTrie insert(int num, int level) throws Exception {
		if (isEmpty()) {
			return new LeafTrie(num);
		}
		int bit = getBit(num, level);
		if (isLeaf()) {
			if (num == label()) {
				return this;
			} else if (bit == getBit(label(), level)) {
				return new InnerTrie(bit, insert(num, level + 1));
			} else {
				BitTrie newNode = new InnerTrie(bit, new LeafTrie(num));
				newNode.setChild(getBit(label(), level), this);
				return newNode;
			}
		} else {
			setChild(bit, child(bit).insert(num, level + 1));
			return this;
		}
	}






	/** The result of removing X from this Trie, if it is present.
	* The trie is unchanged if X is not present. */
	public BitTrie remove(int x) {
		return null;
	}

	/* some utility methods declaration. */

	/** Return the bit at INDEX of NUM beginning from the left most. */
	private int getBit(int num, int index) {
		int constant = 1 << (32 - index);
        if ((num & constant) == constant) {
            return 1;
        } else {
            return 0;
        }
	}



}




/** Representation of an empty trie. */
class EmptyTrie extends BitTrie {
	public boolean isEmpty() {return true;}
	public boolean isLeaf() {return false;}
	public int label() throws Exception {throw new Exception();}
	public BitTrie child(int k) throws Exception {throw new Exception();}
	public void setChild(int k, BitTrie child) throws Exception {throw new Exception();}
}


/** Representation of a leaf trie. */
class LeafTrie extends BitTrie {
	private int label;

	/** constructor. a leaf trie containing label l. */
	LeafTrie(int l) {label = l;}

	public boolean isEmpty() {return false;}
	public boolean isLeaf() {return true;}
	public int label() throws Exception {return label;}
	public BitTrie child(int k) throws Exception {return EMPTY;}
	public void setChild(int k, BitTrie child) throws Exception {throw new Exception();}
}

/** Representation of an inner trie. */
class InnerTrie extends BitTrie {
	int val;
	BitTrie zero;
	BitTrie one;

	/** Constructor. A trie with val VALUE and child at 
	 *  indexValue equals to CHILD. */
	InnerTrie(int childValue, BitTrie child) {
		if (childValue == 0) {
			zero = child;
		} else {
			one = child;
		}
	}

	public boolean isEmpty() {return false;}
	public boolean isLeaf() {return false;}
	public int label() throws Exception {throw new Exception();}
	public BitTrie child(int childValue) throws Exception {
		if (childValue == 0) {
			return zero;
		} else {
			return one;
		}
	}
	public void setChild(int childValue, BitTrie child) throws Exception {
		if (childValue == 0) {
			zero = child;
		} else {
			one = child;
		}
	}

}

