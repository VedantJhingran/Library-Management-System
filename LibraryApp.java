import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// Book class
class Book {
    private int id;
    private String title;
    private String author;
    private int quantity;

    public Book(int id, String title, String author, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    // getters & setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
}

// Library class
class Library {
    private java.util.List<Book> books;

    public Library() {
        books = new ArrayList<>();
    }

    public void addBook(Book book) { books.add(book); }
    public java.util.List<Book> getBooks() { return books; }

    public void removeBook(int id) {
        books.removeIf(book -> book.getId() == id);
    }

    public Book findBook(int id) {
        for (Book b : books) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    public void updateBook(int id, String newTitle, String newAuthor, int newQty) {
        Book b = findBook(id);
        if (b != null) {
            b.setTitle(newTitle);
            b.setAuthor(newAuthor);
            b.setQuantity(newQty);
        }
    }
}

// GUI class
public class LibraryApp extends JFrame {
    private Library library;
    private JTable table;
    private DefaultTableModel tableModel;

    public LibraryApp() {
        library = new Library();

        setTitle("Library Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Quantity"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        JButton addBtn = new JButton("Add Book");
        JButton removeBtn = new JButton("Remove Book");
        JButton updateBtn = new JButton("Update Book");
        JButton searchBtn = new JButton("Search Book");

        JPanel panel = new JPanel();
        panel.add(addBtn);
        panel.add(removeBtn);
        panel.add(updateBtn);
        panel.add(searchBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        // Add Book
        addBtn.addActionListener(e -> {
            try {
                String idStr = JOptionPane.showInputDialog("Enter ID:");
                if (idStr == null) return; // cancel pressed

                String title = JOptionPane.showInputDialog("Enter Title:");
                if (title == null) return;

                String author = JOptionPane.showInputDialog("Enter Author:");
                if (author == null) return;

                String qtyStr = JOptionPane.showInputDialog("Enter Quantity:");
                if (qtyStr == null) return;

                if (idStr.trim().isEmpty() || title.trim().isEmpty() ||
                        author.trim().isEmpty() || qtyStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required!");
                    return;
                }

                int id = Integer.parseInt(idStr);
                int qty = Integer.parseInt(qtyStr);

                library.addBook(new Book(id, title, author, qty));
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID and Quantity must be numbers!");
            }
        });

        // Remove Book
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) tableModel.getValueAt(row, 0);
                library.removeBook(id);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Select a book first!");
            }
        });

        // Update Book
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    int id = (int) tableModel.getValueAt(row, 0);

                    String title = JOptionPane.showInputDialog("New Title:");
                    if (title == null) return;

                    String author = JOptionPane.showInputDialog("New Author:");
                    if (author == null) return;

                    String qtyStr = JOptionPane.showInputDialog("New Quantity:");
                    if (qtyStr == null) return;

                    if (title.trim().isEmpty() || author.trim().isEmpty() || qtyStr.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "All fields are required!");
                        return;
                    }

                    int qty = Integer.parseInt(qtyStr);

                    library.updateBook(id, title, author, qty);
                    refreshTable();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a number!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a book first!");
            }
        });

        // Search Book
        searchBtn.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog("Enter ID to search:");
            if (idStr == null) return;

            try {
                int id = Integer.parseInt(idStr);
                Book b = library.findBook(id);
                if (b != null) {
                    JOptionPane.showMessageDialog(this, "Found: " + b.getTitle() +
                            " by " + b.getAuthor() + " (Qty: " + b.getQuantity() + ")");
                } else {
                    JOptionPane.showMessageDialog(this, "Book not found!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID must be a number!");
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Book b : library.getBooks()) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getQuantity()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryApp().setVisible(true));
    }
}
