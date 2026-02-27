// Feel free to use packages in your own environment, but remember to remove when handing it in
package alda.t9.redblack;
//RedBlackTree class

import java.lang.reflect.Field;

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

    private static int LEFT = 0;
    private static int RIGHT = 1;

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

    // remove method inspired by:
    // https://web.archive.org/web/20171008115143/http://www.eternallyconfuzzled.com/tuts/datastructures/jsw_tut_rbtree.aspx
    public void remove(AnyType x) {
        if (isEmpty() || !contains(x))
            return;

        RedBlackNode<AnyType> current = header.right;
        RedBlackNode<AnyType> parent = header;
        RedBlackNode<AnyType> grand = nullNode;
        RedBlackNode<AnyType> great = nullNode;
        RedBlackNode<AnyType> found = nullNode;

        int iDir = 1; // direction from parent to current (1 = right, 0 = left)
        int prevDir = 1; // previous direction

        while (current != nullNode) {
            great = grand;
            grand = parent;
            parent = current;
            prevDir = iDir;
            iDir = compare(x, current) < 0 ? LEFT : RIGHT;

            if (compare(x, current) == 0) {
                found = current;
            }

            // if current is black and the child we will go to is black
            if (current.color == BLACK && getChild(current, iDir).color == BLACK) {
                RedBlackNode<AnyType> sibling = getChild(grand, revDir(prevDir));

                if (getChild(current, revDir(iDir)).color == RED) {
                    // Rotate current with its red child to make current red
                    RedBlackNode<AnyType> subTreeRoot = rotate(current, revDir(iDir));
                    subTreeRoot.color = BLACK;
                    current.color = RED;
                    setChild(grand, prevDir, subTreeRoot);
                    grand = subTreeRoot;
                } else if (sibling.color == RED) {
                    RedBlackNode<AnyType> subTreeRoot = rotate(grand, revDir(prevDir));
                    setChild(great, (great.right == grand) ? RIGHT : LEFT, subTreeRoot);

                    subTreeRoot.color = grand.color;
                    grand.color = RED;

                    great = subTreeRoot;
                    sibling = getChild(grand, revDir(prevDir));

                } else if (sibling != nullNode) {
                    if (sibling.left.color == BLACK && sibling.right.color == BLACK) {
                        grand.color = BLACK;
                        sibling.color = RED;
                        current.color = RED;
                    } else {
                        // sibling has a red child
                        int jDir = (great.right == grand) ? RIGHT : LEFT;
                        if (getChild(sibling, revDir(prevDir)).color == RED) {
                            RedBlackNode<AnyType> r = rotate(grand, revDir(prevDir));
                            setChild(great, jDir, r);
                            r.color = grand.color;
                            r.left.color = BLACK;
                            r.right.color = BLACK;
                        } else {
                            setChild(grand, revDir(prevDir), rotate(sibling, prevDir));
                            RedBlackNode<AnyType> r = rotate(grand, revDir(prevDir));
                            setChild(great, jDir, r);
                            r.color = grand.color;
                            r.left.color = BLACK;
                            r.right.color = BLACK;
                        }
                        current.color = RED;
                    }
                }
            }
            // percolate down
            current = getChild(current, iDir);
        }

        // removal of leaf
        if (found != nullNode) {
            found.element = parent.element;
            RedBlackNode<AnyType> child = getNonNullChild(parent);
            setChild(grand, (grand.right == parent) ? 1 : 0, child);
        }
        header.right.color = BLACK;

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

    private void setChild(RedBlackNode<AnyType> node, int dir, RedBlackNode<AnyType> child) {
        if (dir == LEFT)
            node.left = child;
        else
            node.right = child;
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

    private void printTreeWithColor() {
        RedBlackNode<AnyType> root = header.right;
        if (root == nullNode) {
            System.out.println("[empty tree]");
            return;
        }
        System.out.println("-----TREE START-----");
        printTree(root, "", true);
        System.out.println("-----TREE END-----");
    }

    private void printTree(RedBlackNode<AnyType> node, String prefix, boolean isRight) {
        if (node == nullNode)
            return;

        // Print right subtree first (so tree reads left=bottom, right=top)
        printTree(node.right, prefix + (isRight ? "    " : "|   "), true);

        // Print current node
        String connector = isRight ? "┌── " : "└── ";
        String color = node.color == RED ? "(R)" : "(B)";
        System.out.println(prefix + connector + node.element + color);

        // Print left subtree
        printTree(node.left, prefix + (isRight ? "|   " : "    "), false);
    }

    private boolean bothChildrenBlack(RedBlackNode<AnyType> node) {
        return node.left.color == BLACK && node.right.color == BLACK;
    }

    private void flipColor(RedBlackNode<AnyType> node) {
        if (node == nullNode)
            return;
        node.color = node.color == RED ? BLACK : RED;
    }

    private void validateRedBlackTree() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2]; // [2] = whoever called this method
        printTreeWithColor();
        try {
            Object header = this.header;
            Object root = getRight(header);
            int expectedBlackNodes = countExpectedBlackNodes(root, nullNode);
            validateRedBlackTree(expectedBlackNodes, 0, BLACK, root, nullNode);
        } catch (Exception e) {
            System.err.println("Called from " + caller.getMethodName() + "() line " + caller.getLineNumber() + ": "
                    + e.getMessage());
            System.exit(1);
        }
    }

    private void validateRedBlackTree(int expectedBlackNodes, int actualBlackNodes, int parentColor, Object node,
            Object nullNode) throws NoSuchFieldException, IllegalAccessException {
        if (node == nullNode) {
            if (actualBlackNodes != expectedBlackNodes) {
                throw new RuntimeException("Wrong number of black nodes in path: expected "
                        + expectedBlackNodes + " but got " + actualBlackNodes);
            }
        } else {
            int color = getColor(node);
            switch (color) {
                case BLACK:
                    actualBlackNodes++;
                    break;
                case RED:
                    if (parentColor != BLACK) {
                        throw new RuntimeException("Red node has a red parent — two consecutive red nodes");
                    }
                    break;
                default:
                    throw new RuntimeException("Unexpected color: " + color);
            }
            validateRedBlackTree(expectedBlackNodes, actualBlackNodes, color, getLeft(node), nullNode);
            validateRedBlackTree(expectedBlackNodes, actualBlackNodes, color, getRight(node), nullNode);
        }
    }

    private int getColor(Object node)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = node.getClass().getDeclaredField("color");
        field.setAccessible(true);
        return field.getInt(node);
    }

    private Object getLeft(Object node)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = node.getClass().getDeclaredField("left");
        field.setAccessible(true);
        return field.get(node);
    }

    private Object getRight(Object node)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = node.getClass().getDeclaredField("right");
        field.setAccessible(true);
        return field.get(node);
    }

    private int countExpectedBlackNodes(Object root, Object nullNode)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        int count = 0;
        Object node = root;
        while (node != nullNode) {
            if (getColor(node) == BLACK)
                count++;
            node = getLeft(node);
        }
        return count;
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
