package service;
import dao.BookDAO;
import model.Book;
import java.util.List;

public class LibraryService {
    private final BookDAO dao = new BookDAO();
    public synchronized void borrow(long id) throws LibraryException {
        List<Book> books = dao.findAll();
        for (Book b : books) {
            if (b.id == id) {
                if (!b.available) throw new BookNotAvailableException("Already borrowed");
                b.available = false;
                return;
            }
        }
        throw new LibraryException("Book not found");
    }
}
