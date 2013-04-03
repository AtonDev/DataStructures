/** @author Arturo Pacifico Griffini. */



public abstract class BitTrie {

	/** The empty Trie. */
	public static final Trie EMPTY = new EmptyTrie();
	
	/** True if X is in this Trie. */
	public boolean isIn(int x);
	
	/** The result of inserting X into this Trie, if it is not
	* already there, and returning this. This trie is
	* unchanged if X is in it already. */
	public BitTrie insert(int x);
	
	/** The result of removing X from this Trie, if it is present.
	* The trie is unchanged if X is not present. */
	public BitTrie remove(int x);

	/** The label at this node. Defined only on leaves. */
	abstract public int label();
	
	/** True if this Trie is a leaf (containing a single String). */
	abstract public boolean isLeaf();
	
	/** True if this Trie is empty */
	abstract public boolean isEmpty();
	
	/** The child numbered with character K. Requires that this node
	* not be empty. Child 0 corresponds to ✷. */
	abstract public BitTrie child(int k);
	
	/** Set the child numbered with character K to CHILD. Requires that
	* this node not be empty. (Intended only for internal use. */
	abstract protected void setChild(int k, BitTrie child);
}











/** Representation of an empty trie. */
class EmptyTrie extends BitTrie {
	public boolean isEmpty() {return true;}
	public boolean isLeaf() {return false;}
	public int label() {throw new Exception();}
	public BitTrie child(int k) {throw new Exception();}
	public void setChild(int k, BitTrie child) {throw new Exception();}
}


/** Representation of a leaf trie. */
class LeafTrie extends BitTrie {
	private int lablel;

	/** constructor. a leaf trie containing label l. */
	LeafTrie(int l) {label = l;}

	public boolean isEmpty() {return false;}
	public boolean isLeaf() {return true;}
	public int label() {return label;}
	public BitTrie child(int k) {return EMPTY;}
	public void setChild(int k, BitTrie child) {throw new Exception();}
}

/** Representation of an inner trie. */
class InnerTrie extends BitTrie {

	InnerTrie(int childIndex, BitTrie child) {
		
	}

}

