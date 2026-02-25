// Feel free to use packages in your own environment, but remember to remove when handing it in
package alda.t9.redblack;
//RedBlackTree class

import java.rmi.UnexpectedException;

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

        /**
         * Find the smallest child of the node.
         * 
         * @return the smallest item or throw UnderflowExcepton if empty.
         */
        public RedBlackNode<AnyType> findMin(RedBlackNode<AnyType> nullNode) {
            var current = this;

            while (current.left != nullNode)
                current = current.left;

            return current;
        }

    }

    private static final int BLACK = 1; // BLACK must be 1

    private static final int RED = 0;
    private RedBlackNode<AnyType> header;
    private RedBlackNode<AnyType> nullNode;
    // Used in insert routine and its helpers
    private RedBlackNode<AnyType> current;
    private RedBlackNode<AnyType> parent;
    private RedBlackNode<AnyType> grand;

    private RedBlackNode<AnyType> great;

    static int LEFT = 1;
    static int RIGHT = 2;

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

    /**
     * Remove from the tree.
     * <p>
     * The current version works, but is extremely inefficient since it copies the
     * entire content for each remove operation. Your job is to replace it with an
     * efficient version that works on the tree directly.
     * 
     * @param x the item to remove.
     */
    public void removeOld(AnyType x) {
        if (!contains(x))
            return;
        // Fusk-implementation
        RedBlackTree<AnyType> newTree = new RedBlackTree<>();

        remove(newTree, header.right, x);

        header = newTree.header;
        nullNode = newTree.nullNode;
    }

    public void remove(AnyType toRemove) {
        if (!contains(toRemove))
            return;

        current = parent = grand = header;
        nullNode.element = toRemove;
        header.color = RED;
        while (compare(toRemove, current) != 0) {
            great = grand;
            grand = parent;
            parent = current;
            current = compare(toRemove, current) < 0 ? current.left : current.right;
            var x = current;
            var t = parent.left == x ? parent.right : parent.left;
            var p = parent;

            if (bothChildrenBlack(x)) {
                boolean didChange = false;
                RedBlackNode<AnyType> newSubRoot = nullNode;
                if (bothChildrenBlack(t)) {
                    flipColor(x);
                    flipColor(t);
                    flipColor(p);
                } else if (t.right.color == RED) {
                    newSubRoot = rotateLeft(p);
                    t.color = p.color;
                    p.color = BLACK;
                    t.right.color = BLACK;
                    didChange = true;
                } else if (t.left.color == RED) {
                    t.color = p.color; 
                    p.color = BLACK; 
                    t.left.color = BLACK; 
                    p.right = rotateRight(p.right);
                    newSubRoot = rotateLeft(p);
                    didChange = true;
                } else {
                    System.out.println("impossible case");
                    throw new UnderflowException();
                }
                if (!didChange)
                    continue;
                if (grand == nullNode) {
                    header.right = newSubRoot;
                } else if (grand.left == parent) {
                    grand.left = newSubRoot;
                } else {
                    grand.right = newSubRoot;
                }
                continue;
            } else {

                if (x.color == RED) {
                    continue;
                }
                if (t.color == RED) {

                }
            }

        }
        // toRemove is a leaf
        if (current.left == nullNode && current.right == nullNode)

        {
            if (current.color == BLACK)
                System.out.println("Deleting black node");
            if (compare(toRemove, parent) < 0)
                parent.left = nullNode;
            else
                parent.right = nullNode;
            // toremove has 2 children
        } else if (current.left != nullNode && current.right != nullNode) {
            RedBlackNode<AnyType> succ = current.right.findMin(nullNode);
            var temp = succ.element;
            succ.element = current.element;
            current.element = temp;
            remove(toRemove);
            return;
        } else if (current.right != nullNode && current.left == nullNode) {
            var child = current.right;
            RedBlackNode<AnyType> succ = child.findMin(nullNode);
            var temp = succ.element;
            succ.element = current.element;
            current.element = temp;
            remove(toRemove);
            return;
        } else if (current.right == nullNode && current.left != nullNode) {
            var child = current.left;
            RedBlackNode<AnyType> succ = child.findMin(nullNode);
            var temp = succ.element;
            succ.element = current.element;
            current.element = temp;
            remove(toRemove);
            return;
        }

        header.color = BLACK;
    }

    RedBlackNode<AnyType> rotateLeft(RedBlackNode<AnyType> p) {
        RedBlackNode<AnyType> r = p.right;
        p.right = r.left;
        r.left = p;
        return r;
    }

    RedBlackNode<AnyType> rotateRight(RedBlackNode<AnyType> p) {
        RedBlackNode<AnyType> l = p.left;
        p.left = l.right;
        l.right = p;
        return l;
    }

    private boolean bothChildrenBlack(RedBlackNode<AnyType> node) {
        return node.left.color == BLACK && node.right.color == BLACK;
    }

    private void flipColor(RedBlackNode<AnyType> node) {
        node.color = node.color == RED ? BLACK : RED;
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

    private void remove(RedBlackTree<AnyType> newTree, RedBlackNode<AnyType> node, AnyType x) {
        if (node != nullNode) {
            if (!node.element.equals(x)) {
                newTree.insert(node.element);
            }
            remove(newTree, node.left, x);
            remove(newTree, node.right, x);
        }
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

}
