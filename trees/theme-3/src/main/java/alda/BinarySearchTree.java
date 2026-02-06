/*
 * You are encouraged to use packages for the assignments, but it's not a requirement.
 * However, when submitting any code in ilearn through VPL you *MUST* remove the package.
 */
package alda;

/**
 * Denna klass representerar ett binärt sökträd. Ni får inte ändra *NÅGOT* i
 * denna klass förutom paketet om du vill använda ett sådant. Alla ändringar
 * som krävs för att klassen ska fungera ska göras i BinarySearchTreeNode.
 * 
 * @author henrikbe
 * 
 */
public class BinarySearchTree<T extends Comparable<T>> {

  private BinarySearchTreeNode<T> root;

  public boolean add(T data) {
    if (root == null) {
      root = new BinarySearchTreeNode<T>(data);
      return true;
    } else {
      return root.add(data);
    }
  }

  public boolean remove(T data) {
    int originalSize = size();
    if (root != null)
      root = root.remove(data);
    return size() < originalSize;
  }

  public boolean contains(T data) {
    return root == null ? false : root.contains(data);
  }

  public void clear() {
    root = null;
  }

  public int size() {
    if (root == null)
      return 0;
    else
      return root.size();
  }

  public int depth() {
    if (root == null)
      return -1;
    else
      return root.depth();
  }

  public String toString() {
    return "[" + (root == null ? "" : root.toString()) + "]";
  }

}
