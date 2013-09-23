package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/** A Set2D implemented with a QuadTree.  The type argument, Point,
 *  indicates the type of points contained in the set.  The rather
 *  involved type parameter structure here allows you to extend
 *  QuadPoint, and thus add additional data and methods to the points
 *  you store.
 *  @author Arturo Pacifico Griffini
 */
public class QuadTree<Point> extends Set2D<Point> {


    /** An empty set of Points that uses VIEW to extract position information.
     *  The argument TRANSITIONSIZE has no   externally
     *  visible effect, but may affect performance.  It is intended to specify
     *  the largest set Points that resides un-subdivided in a single node of
     *  the tree.  While space-efficient, such nodes have slower search times
     *  as their size increases.
     *  */
    public QuadTree(PointView<Point> view, int transitionSize) {
        super(view);
        _transitionSize = transitionSize;
        _nodeOrigin = new double[2];
        _elements = new ArrayList<Point>();
        _subdivided = false;
        _elementsSize = 0;
        _treeSize = 0;
    }

    /* PUBLIC METHODS.  See Set2D.java for documentation */

    /** Returns my transitionSize parameter (supplied to my constructor). */
    public int getTransitionSize() {
        return _transitionSize;
    }

    @Override
    public int size() {
        if (_subdivided) {
            return _northEast.size() + _northWest.size()
                    + _southWest.size() + _southEast.size();
        } else {
            return this._elementsSize;
        }
    }

    @Override
    public void add(Point p) {
        _treeSize += 1;
        if (_subdivided) {
            QuadTree<Point> qt = quadrant(p);
            qt.add(p);
        } else {
            if (!this.contains(p)) {
                if (!containsSameCoord(p)) {
                    _elementsSize += 1;
                }
                _elements.add(p);
            }
        }
        if (_elementsSize > _transitionSize) {
            subDivideNode();
        }
    }

    @Override
    public void remove(Point p) {
        if (_subdivided) {
            QuadTree<Point> qt = quadrant(p);
            qt.remove(p);
        } else {
            if (_elements.contains(p)) {
                _elements.remove(p);
                _treeSize -= 1;
                if (!containsSameCoord(p)) {
                    _elementsSize -= 1;
                }
            }
        }
    }

    @Override
    public boolean contains(Point p) {
        if (_subdivided) {
            QuadTree<Point> qt = quadrant(p);
            return qt.contains(p);
        } else {
            boolean contains = false;
            Iterator<Point> it = _elements.iterator();
            while (it.hasNext()) {
                if (this.getView().equals(p, it.next())) {
                    contains = true;
                    break;
                }
            }
            return contains;
        }
    }

    @Override
    public Iterator<Point> iterator() {
        LinkedList<Point> list = new LinkedList<Point>();
        iterateAll(list);
        return list.iterator();
    }

    @Override
    public Iterator<Point> iterator(double xl, double yl,
                                    double xu, double yu) {
        LinkedList<Point> list = new LinkedList<Point>();
        iterateRange(xl, yl, xu, yu, list);
        return list.iterator();
    }

    /* END OF PUBLIC MEMBERS */

    /** The maximum size for an unsubdivided node. */
    private final int _transitionSize;

    /** The root of the tree. */
    private ArrayList<Point> _elements;

    /** contains the reference point where the division is made. */
    private final double[] _nodeOrigin;

    /** NorthWest children of the quadTree. */
    private QuadTree<Point> _northWest;

    /** NorthEast children of the quadTree. */
    private QuadTree<Point> _northEast;

    /** NorthWest children of the quadTree. */
    private QuadTree<Point> _southWest;

    /** NorthEast children of the quadTree. */
    private QuadTree<Point> _southEast;


    /** Size of the elements array. Duplicates
     * count as one. */
    private int _elementsSize;

    /** Size of the tree. */
    private int _treeSize;

    /** True if elements have been subdivided in
     * in their nodes. */
    private boolean _subdivided;


    /** Called when the _elemtents overflows. It redistributes
     * the points in _elements in its various children.
     */
    private void subDivideNode() {
        setOriginNode();
        _subdivided = true;
        _northWest = new QuadTree<Point>(getView(), _transitionSize);
        _northEast = new QuadTree<Point>(getView(), _transitionSize);
        _southWest = new QuadTree<Point>(getView(), _transitionSize);
        _southEast = new QuadTree<Point>(getView(), _transitionSize);
        Iterator<Point> it = _elements.iterator();
        while (it.hasNext()) {
            Point p = it.next();
            QuadTree<Point> qt = quadrant(p);
            qt.add(p);
        }
        _elements = null;
        _elementsSize = 0;
    }

    /** Sets the reference point named originNode
     * to the point taken as the average of all points in _elements.
     */
    private void setOriginNode() {
        double sumX = 0;
        double sumY = 0;
        Iterator<Point> it = _elements.iterator();
        while (it.hasNext()) {
            Point p = it.next();
            sumX += x(p);
            sumY += y(p);
        }
        _nodeOrigin[0] = sumX / _elements.size();
        _nodeOrigin[1] = sumY / _elements.size();
    }

    /** Returns true only if there is another
     * point in _elements with the same coordinates as P.
     *
     * @param p
     * @return
     */
    private boolean containsSameCoord(Point p) {
        boolean elementsContainsP = false;
        Iterator<Point> it = _elements.iterator();
        while (it.hasNext()) {
            Point pt = it.next();
            if (x(pt) == x(p) && y(pt) == y(p)) {
                elementsContainsP = true;
                break;
            }
        }
        return elementsContainsP;
    }

    /** Returns the quadrant in which point P
     * is supposed to go in.
     *
     * @param p
     * @return
     */
    private QuadTree<Point> quadrant(Point p) {
        int quad = quadrant(x(p), y(p), _nodeOrigin[0], _nodeOrigin[1]);
        switch (quad) {
        case 1:
            return _northEast;
        case 2:
            return _northWest;
        case 3:
            return _southWest;
        default:
            return _southEast;
        }
    }

    /** Returns the quadrant in which point [X, Y]
     * is supposed to go in.
     *
     * @param x
     * @param y
     * @return
     */
    private QuadTree<Point> quadrant(double x, double y) {
        int quad = quadrant(x, y, _nodeOrigin[0], _nodeOrigin[1]);
        switch (quad) {
        case 1:
            return _northEast;
        case 2:
            return _northWest;
        case 3:
            return _southWest;
        default:
            return _southEast;
        }
    }

    /** Adds all the points of the quadtree to L. */
    public void iterateAll(LinkedList<Point> l) {
        if (_subdivided) {
            _northEast.iterateAll(l);
            _northWest.iterateAll(l);
            _southWest.iterateAll(l);
            _southEast.iterateAll(l);
        } else {
            Iterator<Point> it = _elements.iterator();
            while (it.hasNext()) {
                l.add(it.next());
            }
        }
    }

    /** Adds all points in the quadtree that fall in the boundary
     * ([XL, YL], [XU, YU]) to the list L.
     *
     * @param xl
     * @param yl
     * @param xu
     * @param yu
     * @param l
     *
     */
    public void iterateRange(double xl, double yl,
            double xu, double yu, LinkedList<Point> l) {
        if (_subdivided) {
            if (isWithin(_nodeOrigin[0], _nodeOrigin[1], xl, yl, xu, yu)) {
                _northEast.iterateRange(xl, yl, xu, yu, l);
                _northWest.iterateRange(xl, yl, xu, yu, l);
                _southWest.iterateRange(xl, yl, xu, yu, l);
                _southEast.iterateRange(xl, yl, xu, yu, l);
            } else if (inOneQuadrant(
                    _nodeOrigin[0], _nodeOrigin[1], xl, yl, xu, yu)) {
                QuadTree<Point> qt = quadrant(xl, yl);
                qt.iterateRange(xl, yl, xu, yu, l);
            } else {
                if (xl >= _nodeOrigin[0]) {
                    _northEast.iterateRange(xl, yl, xu, yu, l);
                    _southEast.iterateRange(xl, yl, xu, yu, l);
                } else if (yl > _nodeOrigin[1]) {
                    _northEast.iterateRange(xl, yl, xu, yu, l);
                    _northWest.iterateRange(xl, yl, xu, yu, l);
                } else if (xu > _nodeOrigin[0]) {
                    _southWest.iterateRange(xl, yl, xu, yu, l);
                    _southEast.iterateRange(xl, yl, xu, yu, l);
                } else {
                    _southWest.iterateRange(xl, yl, xu, yu, l);
                    _northWest.iterateRange(xl, yl, xu, yu, l);
                }
            }
        } else {
            Iterator<Point> it = _elements.iterator();
            while (it.hasNext()) {
                Point p = it.next();
                if (isWithin(x(p), y(p), xl, yl, xu, yu)) {
                    l.add(p);
                }
            }
        }
    }

    /** Checks whether the rectangle ([LLX, LLY],[URX, URY])
     *  intersects with only one quadrant in respect to the
     *  the cartesian plane with origin [X, Y].
     * @param x
     * @param y
     * @param llx
     * @param lly
     * @param urx
     * @param ury
     * @return
     */
    private boolean inOneQuadrant(double x, double y,
            double llx, double lly,
            double urx, double ury) {
        boolean horCond = llx < x == urx < x;
        boolean verCond = lly < y == ury < y;
        return horCond && verCond;
    }


    /** Dump QT indented by INDENT indentation units. */
    public void print(QuadTree<Point> qt, int indent) {
        if (qt._subdivided) {
            System.out.println();
            print(qt._northEast, indent + INDENTATION);
            System.out.println();
            print(qt._northWest, indent + INDENTATION);
            String s = String.format("( %.1f, %.1f )",
                    qt._nodeOrigin[0], qt._nodeOrigin[1]);
            println(s, indent);
            print(qt._southWest, indent + INDENTATION);
            System.out.println();
            print(qt._southEast, indent + INDENTATION);
            System.out.println();
        } else if (qt._elements != null) {
            Iterator<Point> it = qt._elements.iterator();
            String s = "";
            while (it.hasNext()) {
                Point p = it.next();
                s += "( " + x(p) + " , " + y(p) + " ); ";
            }
            println(s, indent);
        }
    }

    /** Number of spaces in one indentation unit. */
    static final int INDENTATION = 4;

    /** Print OBJ, indented by INDENT indentation units, followed by a
     *  newline. */
    private static void println(Object obj, int indent) {
        for (int k = 0; k < indent * INDENTATION; k += 1) {
            System.out.print(" ");
        }
        System.out.println(obj);
    }

}

