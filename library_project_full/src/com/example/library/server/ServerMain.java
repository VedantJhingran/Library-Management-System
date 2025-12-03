package com.example.library.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.sql.*;
import java.util.*;

// ---------------- MODEL ----------------
class Book {
    private long id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;

    public Book() {}
    public Book(long id, String title, String author, String isbn, boolean available) {
        this.id = id; this.title = title; this.author = author; this.isbn = isbn; this.available = available;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getAuthor() { return author; }
    public void setAuthor(String a) { this.author = a; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String i) { this.isbn = i; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean a) { this.available = a; }
}

// ---------------- DAO ----------------
class BookDAO {
    private static final String URL = "jdbc:sqlite:library.db";

    static {
        try (Connection c = DriverManager.getConnection(URL);
             Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS books(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "author TEXT," +
                    "isbn TEXT," +
                    "available INTEGER)");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Connection conn() throws SQLException { return DriverManager.getConnection(URL); }

    Book save(Book b) throws SQLException {
        String sql = "INSERT INTO books(title,author,isbn,available) VALUES(?,?,?,?)";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getIsbn());
            ps.setInt(4, b.isAvailable() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) b.setId(rs.getLong(1));
            }
        }
        return b;
    }

    void update(Book b) throws SQLException {
        String sql = "UPDATE books SET title=?,author=?,isbn=?,available=? WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getIsbn());
            ps.setInt(4, b.isAvailable() ? 1 : 0);
            ps.setLong(5, b.getId());
            ps.executeUpdate();
        }
    }

    void delete(long id) throws SQLException {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    Book findById(long id) throws SQLException {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    List<Book> findAll() throws SQLException {
        List<Book> out = new ArrayList<>();
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM books");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    List<Book> search(String q) throws SQLException {
        List<Book> out = new ArrayList<>();
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE lower(title) LIKE ?")) {
            ps.setString(1, "%" + q.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private Book map(ResultSet rs) throws SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("available") == 1
        );
    }
}

// ---------------- SERVICE ----------------
class LibraryService {
    private final BookDAO dao = new BookDAO();

    List<Book> listAll() throws SQLException { return dao.findAll(); }
    List<Book> search(String q) throws SQLException { return dao.search(q); }
    Book add(Book b) throws SQLException {
        if (b.getTitle() == null || b.getTitle().trim().isEmpty())
            throw new IllegalArgumentException("Title required");
        return dao.save(b);
    }
    void delete(long id) throws SQLException { dao.delete(id); }

    void update(Book b) throws SQLException { dao.update(b); }

    synchronized void borrow(long id) throws SQLException {
        Book b = dao.findById(id);
        if (b == null) throw new IllegalStateException("Book not found");
        if (!b.isAvailable()) throw new IllegalStateException("Already borrowed");
        b.setAvailable(false);
        dao.update(b);
    }

    synchronized void returnBook(long id) throws SQLException {
        Book b = dao.findById(id);
        if (b == null) throw new IllegalStateException("Book not found");
        b.setAvailable(true);
        dao.update(b);
    }
}

// ---------------- SERVER START ----------------
public class ServerMain {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");

        Server server = new Server(8080);
        ServletContextHandler ctx = new ServletContextHandler();
        ctx.setContextPath("/");
        server.setHandler(ctx);

        ctx.addServlet(com.example.library.server.BookServlet.class, "/api/books/*");

        server.start();
        System.out.println("SERVER RUNNING → http://localhost:8080");
        server.join();
    }
}
