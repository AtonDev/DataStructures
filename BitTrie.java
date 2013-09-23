
public abstract class BitTrie {

	/** An empty trie. */
	static protected final BitTrie EMPTY = new EmptyTrie();

	/** Number of spaces in one indentation unit. */
    static final int INDENTATION = 3;

	/** The size in bits of an int. */
	static protected int INT_SIZE = 32;

	/** The label at this node. Defined only on leaves. */
	abstract public int label() throws Exception;

	/** True if this Trie is a leaf (containing a single String). */
	abstract public boolean isLeaf();

	/** True if this Trie is empty */
	abstract public boolean isEmpty();

	/** returns child at zero for bit==0 and
	 *  and child at one for bit==1. Works just for innerTrie. */
	abstract protected BitTrie child(int bit) throws Exception;

	/** Stes NEWCHILD at BIT. Requires this to be inner tree. */
	abstract protected void setChild(int bit, BitTrie newChild) throws Exception;


	/** The result of inserting X into this Trie, if it is not
	 *  already there, and returning this. This trie is
	 *  unchanged if X is in it already. */
	public BitTrie insert(int n) throws Exception {
		return insert(n, 0);
	}

	/** Assumes that we are at level LEVEL in the trie
	 *  and constructs recursively the trie. It returns
	 *  this. */
	private BitTrie insert(int n, int level) throws Exception {
		int bit = getBit(n, level);
		if (isLeaf() && label() == n) {
			return this;
		} else if (level == INT_SIZE) {
			return new LeafTrie(n);
		} else if (isEmpty()) {
			InnerTrie newNode = new InnerTrie(bit, insert(n, level + 1));
			return newNode;
		} else {
			setChild(bit, child(bit).insert(n, level + 1));
			return this;
		}
	}


	/** The result of removing N from this Trie, if it is present.
	 *  The trie is unchanged if N is not present. */
	public BitTrie remove(int n) throws Exception {
		return remove(n, 0);
	}

	/** Removes N assuming the trie is at LEVEL. Returns this. */
	private BitTrie remove(int n, int level) throws Exception {
		int bit = getBit(n, level);
		if (isEmpty()) 
			return this;
		else if (isLeaf())
			return EMPTY;
		else {
			setChild(bit, child(bit).remove(n, level + 1));
			return this;
		}
	}

	/** True if X is in this Trie. */
	public boolean isIn(int x) throws Exception {
		return isIn(x, 0);
	}

	/** Returns true if x is in this trie assuming
	 *  it starts at level LEVEL. */
	private boolean isIn(int x, int level) throws Exception {
		int bit = getBit(x, level);
		if (isEmpty())
			return false;
		else if (isLeaf())
			return label() == x;
		else
			return child(bit).isIn(x, level + 1);
	}

	/** Returns the Nth bit of an int NUM beginning
	 *  the indexing from the left. Throws index out of bounds. */
	protected int getBit(int num, int n) {
		if (n < 0 || n > INT_SIZE - 1)
			return -1;
		int mask = 1 << (INT_SIZE - (n + 1));
		if ((num & mask) == 0)
			return 0;
		return 1;
	}

	/* Prints the bitTrie. */
	public void print() throws Exception {
	    print("root", 0);
	}

	/* Prints bitTrie starting at INDENT. */
	private void print(Object obj, int indent) throws Exception {
	    if (isEmpty())
	        println(obj + "  E", indent);
	    else if (isLeaf()) {
	        String strFormat = "%" + INT_SIZE + "s";
	        String str = (String.format(
	                strFormat, Integer.toBinaryString(label())).replace(" ", "0"));
	        println(obj + "  " + str + "  " + label(), indent);
	    } else {
	        child(0).print(0, indent + INDENTATION);
	        println(obj, indent);
	        child(1).print(1, indent + INDENTATION);
	    }

	}

	/** Prints OBJ at INDENT. */
	private void println(Object obj, int indent) {
	    for (int k = 0; k < indent * INDENTATION; k += 1) {
            System.out.print(" ");
        }
	    System.out.println(obj);
	}

}


/** Representation of an empty trie. */
class EmptyTrie extends BitTrie {
	@Override
    public int label() throws Exception {throw new Exception();}
	@Override
    public boolean isLeaf() {return false;}
	@Override
    public boolean isEmpty() {return true;}
	@Override
    protected BitTrie child(int bit) throws Exception {throw new Exception();}
	@Override
    protected void setChild(int bit, BitTrie newChild) throws Exception {throw new Exception();}
}

/** Representation of a leaf trie. */
class LeafTrie extends BitTrie {
	int _label;
	/** Constructor that sets this.lable to LABEL. */
	public LeafTrie(int label) {
		_label = label;
	}
	@Override
    public int label() throws Exception {return _label;}
	@Override
    public boolean isLeaf() {return true;}
	@Override
    public boolean isEmpty() {return false;}
	@Override
    protected BitTrie child(int bit) throws Exception {throw new Exception();}
	@Override
    protected void setChild(int bit, BitTrie newChild) throws Exception {throw new Exception();}
}

/** Representation of an inner trie. */
class InnerTrie extends BitTrie {
	/** Stores the child at bit==0. */
	private BitTrie _zero;
	/** Stores the child at bit==1. */
	private BitTrie _one;
	/** Constructor. Initializes trie with no children. */
	protected InnerTrie() {
		_zero = EMPTY;
		_one = EMPTY;
	}
	/** Constructor. Initializes trie with children NEWCHILD at zero or one
	 *  according to BIT.  */
	protected InnerTrie(int bit, BitTrie newChild) {
		_zero = EMPTY;
		_one = EMPTY;
		setChild(bit, newChild);

	}
	@Override
    public int label() throws Exception {throw new Exception();}
	@Override
    public boolean isLeaf() {return false;}
	@Override
    public boolean isEmpty() {return false;}
	@Override
    protected BitTrie child(int bit) throws Exception {
		if (bit == 0)
			return _zero;
		return _one;
	}
	@Override
    protected void setChild(int bit, BitTrie newChild) {
		if (bit == 0)
			_zero = newChild;
		else
			_one = newChild;
	}
}

