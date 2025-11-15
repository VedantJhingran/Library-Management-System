// ===============================
// Main.java (Part 1 of 2)
// SINGLE FILE: Library Management System
// Paste Part 2 immediately after this block.
// Then: javac Main.java && java Main
// ===============================

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

// ------------------------------
// 1) ABSTRACT ITEM (OOP)
// ------------------------------
abstract class Item {
    long id;
    String title;
    Item(long id, String title) { this.id = id; this.title = title; }
    public abstract String getType();
}

// ------------------------------
// 2) BOOK (inherits Item)
// ------------------------------
class Book extends Item {
    String author;
    String isbn;
    boolean available;
    Book(long id, String title, String author, String isbn, boolean available) {
        super(id, title);
        this.author = author;
        this.isbn = isbn;
        this.available = available;
    }
    @Override public String getType() { return "Book"; }
}

// ------------------------------
// 3) Exceptions
// ------------------------------
class LibraryException extends Exception {
    LibraryException(String msg) { super(msg); }
    LibraryException(String msg, Throwable t) { super(msg, t); }
}
class BookNotAvailableException extends LibraryException {
    BookNotAvailableException(String msg) { super(msg); }
}

// ------------------------------
// 4) Generic DAO interface
// ------------------------------
interface LibraryDAO<T, ID> {
    T findById(ID id) throws LibraryException;
    List<T> findAll() throws LibraryException;
    T save(T t) throws LibraryException;
    void update(T t) throws LibraryException;
    void delete(ID id) throws LibraryException;
}

// ------------------------------
// 5) BookDAO - implements DAO using SQLite JDBC
// ------------------------------
class BookDAO implements LibraryDAO<Book, Long> {
    private static final String URL = "jdbc:sqlite:library_singlefile.db";

    static {
        // create table if not exists
        try (Connection c = DriverManager.getConnection(URL);
             Statement st = c.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS books(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "author TEXT," +
                    "isbn TEXT," +
                    "available INTEGER)";
            st.execute(sql);
        } catch (Exception e) {
            // If DB creation fails, print stack and continue (will propagate later)
            e.printStackTrace();
        }
    }

    private Connection conn() throws SQLException { return DriverManager.getConnection(URL); }

    @Override
    public Book findById(Long id) throws LibraryException {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (Exception e) { throw new LibraryException("findById failed", e); }
    }

    @Override
    public List<Book> findAll() throws LibraryException {
        List<Book> list = new ArrayList<>();
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM books");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (Exception e) { throw new LibraryException("findAll failed", e); }
    }

    @Override
    public Book save(Book b) throws LibraryException {
        String sql = "INSERT INTO books(title,author,isbn,available) VALUES(?,?,?,?)";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.title);
            ps.setString(2, b.author);
            ps.setString(3, b.isbn);
            ps.setInt(4, b.available ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) b.id = keys.getLong(1);
            }
            return b;
        } catch (Exception e) { throw new LibraryException("save failed", e); }
    }

    @Override
    public void update(Book b) throws LibraryException {
        String sql = "UPDATE books SET title=?,author=?,isbn=?,available=? WHERE id=?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.title);
            ps.setString(2, b.author);
            ps.setString(3, b.isbn);
            ps.setInt(4, b.available ? 1 : 0);
            ps.setLong(5, b.id);
            ps.executeUpdate();
        } catch (Exception e) { throw new LibraryException("update failed", e); }
    }

    @Override
    public void delete(Long id) throws LibraryException {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) { throw new LibraryException("delete failed", e); }
    }

    private Book map(ResultSet rs) throws Exception {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("available") == 1
        );
    }
}

// ------------------------------
// 6) LibraryService - business logic (thread-safe)
// ------------------------------
class LibraryService {
    private final BookDAO dao = new BookDAO();

    // borrow: checks availability and updates within synchronized context
    public synchronized void borrow(long id) throws LibraryException, BookNotAvailableException {
        Book b = dao.findById(id);
        if (b == null) throw new LibraryException("Book not found");
        if (!b.available) throw new BookNotAvailableException("Book already borrowed");
        b.available = false;
        dao.update(b);
    }

    // return book
    public synchronized void returnBook(long id) throws LibraryException {
        Book b = dao.findById(id);
        if (b == null) throw new LibraryException("Book not found");
        b.available = true;
        dao.update(b);
    }
}

// ------------------------------
// 7) GUI class START (Main) - part 1
// ------------------------------
public class Main extends JFrame {

    private final BookDAO dao = new BookDAO();
    private final LibraryService service = new LibraryService();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Title", "Author", "ISBN", "Available"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);
    private final JTextField tfTitle  = new JTextField(14);
    private final JTextField tfAuthor = new JTextField(14);
    private final JTextField tfIsbn   = new JTextField(12);
    private final JTextField tfSearch = new JTextField(12);

    public Main() {
        super("Library Management System");
        setSize(900, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // TOP panel - inputs
        JPanel top = new JPanel();
        top.add(new JLabel("Title:")); top.add(tfTitle);
        top.add(new JLabel("Author:")); top.add(tfAuthor);
        top.add(new JLabel("ISBN:")); top.add(tfIsbn);
        JButton btnAdd = new JButton("Add");
        top.add(btnAdd);
        btnAdd.addActionListener(e -> addBook());

        // BOTTOM panel - controls
        JPanel bot = new JPanel();
        JButton btnDelete = new JButton("Delete");
        JButton btnBorrow = new JButton("Borrow");
        JButton btnReturn = new JButton("Return");
        JButton btnSearch = new JButton("Search");
        bot.add(btnDelete); bot.add(btnBorrow); bot.add(btnReturn);
        bot.add(new JLabel("Search Title:")); bot.add(tfSearch); bot.add(btnSearch);

        btnDelete.addActionListener(e -> deleteBook());
        btnBorrow.addActionListener(e -> borrowBook());
        btnReturn.addActionListener(e -> returnBook());
        btnSearch.addActionListener(e -> search());

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bot, BorderLayout.SOUTH);

        // load existing books from DB
        loadBooks();
    }
    // ===============================
// Main.java (Part 2 of 2)
// Continue methods and main()
// ===============================

    // add book in background thread
    void addBook() {
        final String title = tfTitle.getText().trim();
        if (title.isEmpty()) { JOptionPane.showMessageDialog(this, "Title required"); return; }
        final String author = tfAuthor.getText().trim();
        final String isbn = tfIsbn.getText().trim();

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                Book b = new Book(0, title, author, isbn, true);
                dao.save(b);
                return null;
            }
            protected void done() { clearInputs(); loadBooks(); }
        }.execute();
    }

    // delete selected book
    void deleteBook() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        long id = ((Number)model.getValueAt(r, 0)).longValue();
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                dao.delete(id);
                return null;
            }
            protected void done() { loadBooks(); }
        }.execute();
    }

    // borrow selected book (background)
    void borrowBook() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        long id = ((Number)model.getValueAt(r, 0)).longValue();
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                try {
                    service.borrow(id);
                } catch (BookNotAvailableException ex) {
                    // rethrow to be handled in done()
                    throw ex;
                }
                return null;
            }
            protected void done() {
                try {
                    get(); // will throw if exception occurred
                    loadBooks();
                } catch (Exception ex) {
                    if (ex.getCause() instanceof BookNotAvailableException)
                        JOptionPane.showMessageDialog(Main.this, ex.getCause().getMessage());
                    else
                        JOptionPane.showMessageDialog(Main.this, "Error: " + ex.getMessage());
                    loadBooks();
                }
            }
        }.execute();
    }

    // return selected book
    void returnBook() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        long id = ((Number)model.getValueAt(r, 0)).longValue();
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                service.returnBook(id);
                return null;
            }
            protected void done() { loadBooks(); }
        }.execute();
    }

    // search by title
    void search() {
        final String q = tfSearch.getText().trim().toLowerCase();
        new SwingWorker<List<Book>, Void>() {
            protected List<Book> doInBackground() throws Exception {
                return dao.findAll();
            }
            protected void done() {
                try {
                    List<Book> list = get();
                    model.setRowCount(0);
                    for (Book b : list) {
                        if (q.isEmpty() || b.title.toLowerCase().contains(q)) {
                            model.addRow(new Object[]{b.id, b.title, b.author, b.isbn, b.available});
                        }
                    }
                } catch (Exception e) { JOptionPane.showMessageDialog(Main.this, "Error loading search"); }
            }
        }.execute();
    }

    // load all books
    void loadBooks() {
        new SwingWorker<List<Book>, Void>() {
            protected List<Book> doInBackground() throws Exception { return dao.findAll(); }
            protected void done() {
                try {
                    List<Book> list = get();
                    model.setRowCount(0);
                    for (Book b : list) model.addRow(new Object[]{b.id, b.title, b.author, b.isbn, b.available});
                } catch (Exception e) { JOptionPane.showMessageDialog(Main.this, "Error loading books"); }
            }
        }.execute();
    }

    void clearInputs() { tfTitle.setText(""); tfAuthor.setText(""); tfIsbn.setText(""); tfSearch.setText(""); }

    // main method to start GUI
    public static void main(String[] args) {
        // ensure SQLite JDBC driver is available on classpath - included in modern JREs when using the sqlite-jdbc jar
        SwingUtilities.invokeLater(() -> {
            try {
                Main m = new Main();
                m.setVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start: " + t.getMessage());
            }
        });
    }
}

