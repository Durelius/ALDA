package alda;

/*
 * Denna klass ska förberedas för att kunna användas som nyckel i en hashtabell. 
 * Du får göra nödvändiga ändringar även i klasserna MyString och ISBN10.
 * 
 * Hashkoden ska räknas ut på ett effektivt sätt och följa de regler och 
 * rekommendationer som finns för hur en hashkod ska konstrueras. Notera i en 
 * kommentar i koden hur du har tänkt när du konstruerat din hashkod.
 */
public class Book {
  private MyString title;
  private MyString author;
  private ISBN10 isbn;
  private MyString content;
  private int price;
  private final static int primeHash = 47;

  public Book(String title, String author, String isbn, String content, int price) {
    this.title = new MyString(title);
    this.author = new MyString(author);
    this.isbn = new ISBN10(isbn);
    this.content = new MyString(content);
    this.price = price;
  }

  public MyString getTitle() {
    return title;
  }

  public MyString getAuthor() {
    return author;
  }

  public ISBN10 getIsbn() {
    return isbn;
  }

  public MyString getContent() {
    return content;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Book))
      return false;
    Book other = (Book) o;
    return this.isbn.equals(other.isbn)
        && this.title.equals(other.title)
        && this.author.equals(other.author)
        && this.content.equals(other.content);
  }

  @Override
  public String toString() {
    return String.format("\"%s\" by %s Price: %d ISBN: %s length: %s", title, author, price, isbn,
        content.length());
  }

  /*
   * Jag implementearade hashcode för ISBN10 och
   * myString med hjälp av primtal och ASCII värdena för karaktärerna.
   * Sedan beräknar vi en ny hashkod för book med hjälp av de tidigare
   * hashkoderna för egenskaperna hos boken, samt det gånger det valda primtalet.
   * Valde att inte ta med content som en del av hashkodningen, då det kan vara en
   * extremt lång sträng, och är resten av egenskaperna samma så bör det vara
   * samma bok.
   * Detta gör att ordningen på värdena spelar roll, både på subklasserna och på
   * boken.
   * Resultatet blir en bra spridning av hashkoder, i testerna får vi
   * -1548337450 och 1408898365 vilket är extremt långt ifrån varandra, för ganska
   * lika värden.
   * Även equals behövde implementeras för stödklasserna och book-klassen.
   * Overflow är tillåtet här, det skadar inte metoden.
   */
  @Override
  public int hashCode() {
    int hash = primeHash + isbn.hashCode();
    hash = hash * primeHash + (title.hashCode());
    hash = hash * primeHash + (author.hashCode());
    System.out.println(hash);
    return hash;
  }

}
