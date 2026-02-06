package alda;

import java.util.Arrays;

public class MyString {

  private char[] data;
  private final int primeHash = 17;

  public MyString(String title) {
    data = title.toCharArray();
  }

  public int length() {
    return data.length;
  }

  @Override
  public String toString() {
    return new String(data);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof MyString))
      return false;
    MyString other = (MyString) o;
    return Arrays.equals(data, other.data);
  }

  @Override
  public int hashCode() {
    int hash = primeHash;
    for (char c : data) {
      hash = (hash * primeHash) + c;
    }
    return hash;
  }

}
