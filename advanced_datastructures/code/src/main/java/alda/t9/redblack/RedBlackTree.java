// Feel free to use packages in your own environment, but remember to remove when handing it in
package alda.t9.redblack;
//Wilhelm Durelius widu7139

//RedBlackTree class

//CONSTRUCTION: with no parameters
//
//******************PUBLIC OPERATIONS*********************
//void insert( x )       --> Insert x
//void remove( x )       --> Remove x (unimplemented)
//boolean contains( x )  --> Return true if x is found
//Comparable findMin( )  --> Return smallest item
//Comparable findMax( )  --> Return largest item
//boolean isEmpty( )     --> Return true if empty; else false
//void makeEmpty( )      --> Remove all items
//void printTree( )      --> Print all items
//******************ERRORS********************************
//Throws UnderflowException as appropriate

/**
 * Implements a red-black tree. Note that all "matching" is based on the
 * compareTo method.
 * <p>
 * Modified with a working, but extremely inefficient implementation of remove,
 * and following Checkstyles styleguide.
 * 
 * @author Mark Allen Weiss
 */
public class RedBlackTree<AnyType extends Comparable<? super AnyType>> {
    private static class RedBlackNode<AnyType> {
        private AnyType element; // The data in the node
        private RedBlackNode<AnyType> left; // Left child
        private RedBlackNode<AnyType> right; // Right child
        private int color; // Color
                           //

        // Constructors
        RedBlackNode(AnyType theElement) {
            this(theElement, null, null);
        }

        RedBlackNode(AnyType theElement, RedBlackNode<AnyType> lt, RedBlackNode<AnyType> rt) {
            element = theElement;
            left = lt;
            right = rt;
            color = RedBlackTree.BLACK;
        }

        @Override
        public String toString() {
            return element == null ? "NULL" : element.toString();
        }

    }

    private static final int BLACK = 1; // BLACK must be 1
    private static final int RED = 0;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private RedBlackNode<AnyType> header;
    private RedBlackNode<AnyType> nullNode;
    // Used in insert routine and its helpers
    private RedBlackNode<AnyType> current;
    private RedBlackNode<AnyType> parent;
    private RedBlackNode<AnyType> grand;

    private RedBlackNode<AnyType> great;
    private RedBlackNode<AnyType> found;

    private int dir = 1;
    private int prevDir = 1;

    /**
     * Construct the tree.
     */
    public RedBlackTree() {
        nullNode = new RedBlackNode<>(null);
        nullNode.left = nullNode.right = nullNode;
        header = new RedBlackNode<>(null);
        header.left = header.right = nullNode;
    }

    /**
     * Insert into the tree.
     * 
     * @param item the item to insert.
     */
    public void insert(AnyType item) {
        current = parent = grand = header;
        nullNode.element = item;
        while (compare(item, current) != 0) {
            great = grand;
            grand = parent;
            parent = current;
            current = compare(item, current) < 0 ? current.left : current.right;

            // Check if two red children; fix if so
            if (current.left.color == RED && current.right.color == RED)
                handleReorient(item);
        }

        // Insertion fails if already present
        if (current != nullNode)
            return;
        current = new RedBlackNode<>(item, nullNode, nullNode);

        // Attach to parent
        if (compare(item, parent) < 0)
            parent.left = current;
        else
            parent.right = current;
        handleReorient(item);
    }

    // remove method inspired by:
    // https://web.archive.org/web/20171008115143/http://www.eternallyconfuzzled.com/tuts/datastructures/jsw_tut_rbtree.aspx
    public void remove(AnyType toRemove) {
        if (!removalSetup(toRemove))
            return;
        while (current != nullNode) {
            removalLoopInit(toRemove);
            // if current is black and the child we will go to is black
            if (current.color == BLACK && getChild(current, dir).color == BLACK) {
                RedBlackNode<AnyType> sibling = getChild(grand, revDir(prevDir));
                // if next has red child in other direction
                if (getChild(current, revDir(dir)).color == RED) {
                    // Rotate current with its red child to make current red
                    RedBlackNode<AnyType> subTreeRoot = rotate(current, revDir(dir));
                    setColors(subTreeRoot, BLACK, current, RED);
                    grand = setChild(grand, prevDir, subTreeRoot);
                } else if (sibling.color == RED) {
                    RedBlackNode<AnyType> subTreeRoot = rotate(grand, revDir(prevDir));
                    setColors(subTreeRoot, grand.color, grand, RED);
                    great = setChild(great, (great.right == grand) ? RIGHT : LEFT, subTreeRoot);
                    sibling = getChild(grand, revDir(prevDir));
                } else if (sibling != nullNode) {
                    if (sibling.left.color == BLACK && sibling.right.color == BLACK) {
                        setColors(grand, BLACK, sibling, RED, current, RED);
                    } else {
                        int jdir = (great.right == grand) ? RIGHT : LEFT;
                        if (getChild(sibling, revDir(prevDir)).color == RED) {
                            RedBlackNode<AnyType> subTreeRoot = rotate(grand, revDir(prevDir));
                            setChild(great, jdir, subTreeRoot);
                            setColors(subTreeRoot, grand.color, subTreeRoot.left, BLACK, subTreeRoot.right, BLACK);
                        } else {
                            setChild(grand, revDir(prevDir), rotate(sibling, prevDir));
                            RedBlackNode<AnyType> subTreeRoot = rotate(grand, revDir(prevDir));
                            setChild(great, jdir, subTreeRoot);
                            setColors(subTreeRoot, grand.color, subTreeRoot.left, BLACK, subTreeRoot.right, BLACK);
                        }
                        setColors(current, RED);
                    }
                }
            }
            // percolate down
            current = getChild(current, dir);
        }
        // removal of red leaf
        removeFoundNode();
    }

    /**
     * Find the smallest item the tree.
     * 
     * @return the smallest item or throw UnderflowExcepton if empty.
     */
    public AnyType findMin() {
        if (isEmpty())
            throw new UnderflowException();

        RedBlackNode<AnyType> itr = header.right;

        while (itr.left != nullNode)
            itr = itr.left;

        return itr.element;
    }

    /**
     * Find the largest item in the tree.
     * 
     * @return the largest item or throw UnderflowExcepton if empty.
     */
    public AnyType findMax() {
        if (isEmpty())
            throw new UnderflowException();

        RedBlackNode<AnyType> itr = header.right;

        while (itr.right != nullNode)
            itr = itr.right;

        return itr.element;
    }

    /**
     * Find an item in the tree.
     * 
     * @param x the item to search for.
     * @return true if x is found; otherwise false.
     */
    public boolean contains(AnyType x) {
        nullNode.element = x;
        current = header.right;

        for (;;) {
            if (x.compareTo(current.element) < 0)
                current = current.left;
            else if (x.compareTo(current.element) > 0)
                current = current.right;
            else
                return current != nullNode;
        }
    }

    /**
     * Make the tree logically empty.
     */
    public void makeEmpty() {
        header.right = nullNode;
    }

    /**
     * Print the tree contents in sorted order.
     */
    public void printTree() {
        if (isEmpty())
            System.out.println("Empty tree");
        else
            printTree(header.right);
    }

    /**
     * Internal method to print a subtree in sorted order.
     * 
     * @param t the node that roots the subtree.
     */
    private void printTree(RedBlackNode<AnyType> t) {
        if (t != nullNode) {
            printTree(t.left);
            System.out.println(t.element);
            printTree(t.right);
        }
    }

    /**
     * Test if the tree is logically empty.
     * 
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return header.right == nullNode;
    }

    /**
     * Compare item and t.element, using compareTo, with caveat that if t is header,
     * then item is always larger. This routine is called if is possible that t is
     * header. If it is not possible for t to be header, use compareTo directly.
     */
    private int compare(AnyType item, RedBlackNode<AnyType> t) {
        if (t == header)
            return 1;
        else
            return item.compareTo(t.element);
    }

    /**
     * Internal routine that is called during an insertion if a node has two red
     * children. Performs flip and rotations.
     * 
     * @param item the item being inserted.
     */
    private void handleReorient(AnyType item) {
        // Do the color flip
        current.color = RED;
        current.left.color = BLACK;
        current.right.color = BLACK;

        if (parent.color == RED) // Have to rotate
        {
            grand.color = RED;
            if ((compare(item, grand) < 0) != (compare(item, parent) < 0))
                parent = rotate(item, grand); // Start dbl rotate
            current = rotate(item, great);
            current.color = BLACK;
        }
        header.right.color = BLACK; // Make root black
    }

    /**
     * Rotate binary tree node with left child.
     */
    private RedBlackNode<AnyType> rotateWithLeftChild(RedBlackNode<AnyType> node) {
        RedBlackNode<AnyType> leftChild = node.left;
        node.left = leftChild.right;
        leftChild.right = node;
        return leftChild;
    }

    /**
     * Rotate binary tree node with right child.
     */
    private RedBlackNode<AnyType> rotateWithRightChild(RedBlackNode<AnyType> node) {
        RedBlackNode<AnyType> rightChild = node.right;
        node.right = rightChild.left;
        rightChild.left = node;
        return rightChild;
    }

    private int revDir(int dir) {
        return 1 - dir;
    }

    // gets the child of the direction
    private RedBlackNode<AnyType> getChild(RedBlackNode<AnyType> node, int dir) {
        return (dir == LEFT) ? node.left : node.right;
    }

    private RedBlackNode<AnyType> getNonNullChild(RedBlackNode<AnyType> node) {
        return node.left != nullNode ? node.left : node.right;
    }

    private RedBlackNode<AnyType> setChild(RedBlackNode<AnyType> node, int dir, RedBlackNode<AnyType> child) {
        if (dir == LEFT)
            node.left = child;
        else
            node.right = child;

        return child;
    }

    /**
     * Internal routine that performs a single or double rotation. Because the
     * result is attached to the parent, there are four cases. Called by
     * handleReorient.
     * 
     * @param item   the item in handleReorient.
     * @param parent the parent of the root of the rotated subtree.
     * @return the root of the rotated subtree.
     */
    private RedBlackNode<AnyType> rotate(AnyType item, RedBlackNode<AnyType> parent) {
        if (compare(item, parent) < 0)
            return parent.left = compare(item, parent.left) < 0 ? rotateWithLeftChild(parent.left) : // LL
                    rotateWithRightChild(parent.left); // LR
        else
            return parent.right = compare(item, parent.right) < 0 ? rotateWithLeftChild(parent.right) : // RL
                    rotateWithRightChild(parent.right); // RR
    }

    // rotates according to the direction provided
    private RedBlackNode<AnyType> rotate(RedBlackNode<AnyType> t, int dir) {
        if (dir == LEFT) { // Rotate with left child
            RedBlackNode<AnyType> k = t.left;
            t.left = k.right;
            k.right = t;
            return k;
        } else { // Rotate with right child
            RedBlackNode<AnyType> k = t.right;
            t.right = k.left;
            k.left = t;
            return k;
        }
    }

    // long method fixer
    private void setColors(Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Arguments must come in pairs: (RedBlackNode, int)");

        }

        for (int i = 0; i < args.length; i += 2) {

            if (!(args[i] instanceof RedBlackNode<?> node)) {
                throw new IllegalArgumentException(
                        "Argument " + i + " must be a RedBlackNode");
            }

            // if color is not red or black number we throw an exception

            /*
             * varför ger nedan en error i vpl? Fel: Wrapper typ deklaration
             * if (!(args[i + 1] instanceof Integer color) || (int) args[i + 1] > 1 || (int)
             * args[i + 1] < 0) {
             * throw new IllegalArgumentException(
             * "Argument " + (i + 1) + " must be red(1) or black(0)");
             * }
             */

            if ((int) args[i + 1] > 1 || (int) args[i + 1] < 0) {
                throw new IllegalArgumentException(
                        "Argument " + (i + 1) + " must be red(1) or black(0)");
            }

            int color = (int) args[i + 1];

            node.color = color;
        }
    }

    private void removeFoundNode() {
        if (found != nullNode) {
            found.element = parent.element;
            RedBlackNode<AnyType> child = getNonNullChild(parent);
            setChild(grand, (grand.right == parent) ? 1 : 0, child);
        }
        setColors(header.right, BLACK);
    }

    // runs at start of removal method to initialize values and tell us if we should
    // continue looping
    private boolean removalSetup(AnyType toRemove) {
        if (isEmpty() || !contains(toRemove))
            return false;
        found = current = parent = grand = header;

        dir = prevDir = 1;
        return true;
    }

    // runs at start of each loop inside remove method to get the next values
    private void removalLoopInit(AnyType toRemove) {
        great = grand;
        grand = parent;
        parent = current;
        prevDir = dir;
        dir = compare(toRemove, current) < 0 ? LEFT : RIGHT;
        if (compare(toRemove, current) == 0) {
            found = current;
        }
    }
}
