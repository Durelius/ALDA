/*
 * You are encouraged to use packages for the assignments, but it's not a requirement.
 * However, when submitting any code in ilearn through VPL you *MUST* remove the package.
 */
package alda;
//widu7139 Wilhelm Durelius

import java.util.Objects;

/**
 * 
 * Detta är den enda av de tre klasserna ni ska göra några ändringar i. (Om ni
 * inte vill lägga till fler testfall.) Det är också den enda av klasserna ni
 * ska lämna in. Glöm inte att namn och användarnamn ska stå i en kommentar
 * högst upp, och att en eventuell paketdeklarationen måste plockas bort vid
 * inlämningen för att koden ska gå igenom de automatiska testerna.
 * 
 * De ändringar som är tillåtna är begränsade av följande:
 * <ul>
 * <li>Ni får INTE
 * byta namn på klassen.
 * <li>Ni får INTE lägga till några fler instansvariabler.
 * <li>Ni får INTE lägga till några statiska variabler.
 * <li>Ni får INTE använda
 * några loopar någonstans. Detta gäller också alterntiv till loopar, så som
 * strömmar.
 * <li>Ni FÅR lägga till fler metoder, dessa ska då vara privata.
 * <li>Ni får INTE låta NÅGON metod ta en parameter av typen
 * BinarySearchTreeNode. Enbart den generiska typen (T eller vad ni väljer att
 * kalla den), String, StringBuilder, StringBuffer, samt primitiva typer är
 * tillåtna.
 * </ul>
 * 
 * @author henrikbe
 * 
 * @param <T>
 */

public class BinarySearchTreeNode<T extends Comparable<T>> {

  /*
   * The @SuppressWarnings below *SHOULD* be removed.
   */

  private T data;
  private BinarySearchTreeNode<T> left;
  private BinarySearchTreeNode<T> right;

  public BinarySearchTreeNode(T data) {
    this.data = data;
  }

  public boolean add(T data) {
    int res = data.compareTo(this.data);
    if (res == 0)
      return false;
    else if (res > 0) {
      if (right == null) {
        right = new BinarySearchTreeNode<T>(data);
        return true;
      }
      return right.add(data);
    } else {
      if (left == null) {
        left = new BinarySearchTreeNode<T>(data);
        return true;
      }
      return left.add(data);
    }

  }

  private T findMin() {
    if (left != null)
      return left.findMin();

    return this.data;
  }

  private void inOrderStr(StringBuilder sb) {
    if (left != null)
      left.inOrderStr(sb);
    sb.append(data).append(", ");
    if (right != null)
      right.inOrderStr(sb);
  }

  private BinarySearchTreeNode<T> findNode(T data) {
    if (equals(this.data, data)) {
      return this;
    }

    if (data.compareTo(this.data) > 0) {

      return right != null ? right.findNode(data) : null;
    } else {
      return left != null ? left.findNode(data) : null;
    }

  }

  public BinarySearchTreeNode<T> remove(T data) {

    if (equals(this.data, data) && size() == 1) {
      return null;

    } else if (equals(this.data, data)) {

      System.out.println("toDelete: " + data);
      // root node to remove has 1 child on left side
      if (left != null && right == null) {
        this.data = left.data;
        right = left.right != null ? left.right : null;
        left = left.left != null ? left.left : null;
      }
      // root node to remove has 1 child on right side
      else if (left == null && right != null) {
        this.data = right.data;
        left = right.left != null ? right.left : null;
        right = right.right != null ? right.right : null;

      }

      // root node to remove has 2 children
      else if (left != null && right != null) {
        var succ = findNode(right.findMin());
        var tempData = this.data;
        this.data = succ.data;
        succ.data = tempData;
        remove(tempData);
      }
    } else if (left != null && equals(left.data, data)) {
      var toRemove = left;
      if (toRemove.left == null && toRemove.right == null) {
        left = null;
      }

      // node to remove has 1 child on left side
      else if (toRemove.left != null && toRemove.right == null) {
        left = left.left;
      }
      // node to remove has 1 child on right side
      else if (toRemove.left == null && toRemove.right != null) {
        left = left.right;
      }

      // node to remove has 2 children
      else if (toRemove.left != null & toRemove.right != null) {
        var succ = findNode(toRemove.right.findMin());
        var tempData = left.data;
        left.data = succ.data;
        succ.data = tempData;
        remove(tempData);
      }

    } else if (right != null && equals(right.data, data)) {
      var toRemove = right;
      if (toRemove.left == null && toRemove.right == null) {
        right = null;
      }

      // node to remove has 1 child on left side
      else if (toRemove.left != null && toRemove.right == null) {
        right = right.left;
      }
      // node to remove has 1 child on right side
      else if (toRemove.left == null && toRemove.right != null) {
        right = right.right;
      }

      // node to remove has 2 children
      else if (toRemove.left != null & toRemove.right != null) {
        var succ = findNode(toRemove.right.findMin());
        var tempData = right.data;
        right.data = succ.data;
        succ.data = tempData;
        remove(tempData);
      }

    }

    else if (right != null && data.compareTo(this.data) > 0)
      right.remove(data);
    else if (left != null && data.compareTo(this.data) < 0)
      left.remove(data);

    return this;
  }

  private boolean equals(T first, T second) {
    return Objects.equals(first, second);
  }

  public boolean contains(T data) {
    return findNode(data) != null;
  }

  public int size() {
    int size = traverseSize();
    return size;
  }

  private int traverseSize() {
    int leftSize = 0;
    int rightSize = 0;
    if (left != null)
      leftSize = left.traverseSize();
    if (right != null)
      rightSize = right.traverseSize();

    return leftSize + rightSize + 1;
  }

  public int depth() {
    return countDepth(0);
  }

  public int countDepth(int depth) {
    int depthLeft = depth;
    int depthRight = depth;
    if (left != null)
      depthLeft = left.countDepth(depth + 1);
    if (right != null)
      depthRight = left.countDepth(depth + 1);

    return Math.max(depthLeft, depthRight);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    inOrderStr(sb);
    if (sb.length() > 0)
      sb.delete(sb.length() - 2, sb.length());
    return sb.toString();
  }

}
