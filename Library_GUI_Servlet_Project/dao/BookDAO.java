package dao;
import model.Book;
import service.LibraryException;
import java.sql.*;
import java.util.*;

public class BookDAO {
    private static final String URL = "jdbc:sqlite:library_singlefile.db";
    static {
        try (Connection c = DriverManager.getConnection(URL);
             Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS books(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT,author TEXT,isbn TEXT,available INTEGER)");
        } catch (Exception e) { e.printStackTrace(); }
    }
    private Connection conn() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public List<Book> findAll() throws LibraryException {
        List<Book> list = new ArrayList<>();
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM books");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getInt("available") == 1));
            }
            return list;
        } catch (Exception e) {
            throw new LibraryException("DB read failed", e);
        }
    }
}
