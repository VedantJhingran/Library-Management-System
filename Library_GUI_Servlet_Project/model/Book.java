package model;
public class Book extends Item {
    public String author;
    public String isbn;
    public boolean available;
    public Book(long id, String title, String author, String isbn, boolean available) {
        super(id, title);
        this.author = author;
        this.isbn = isbn;
        this.available = available;
    }
    @Override
    public String getType() {
        return "Book";
    }
}
