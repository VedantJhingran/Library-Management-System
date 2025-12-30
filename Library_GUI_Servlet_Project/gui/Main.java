package gui;
import dao.BookDAO;
import model.Book;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Main extends JFrame {
    private final BookDAO dao = new BookDAO();
    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"ID","Title","Author","ISBN","Available"},0);

    public Main() {
        setTitle("Library Management System");
        setSize(800,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JButton load = new JButton("Load Books");
        load.addActionListener(e -> loadBooks());
        add(load, BorderLayout.SOUTH);
        loadBooks();
    }

    private void loadBooks() {
        try {
            model.setRowCount(0);
            List<Book> list = dao.findAll();
            for (Book b : list) {
                model.addRow(new Object[]{b.id,b.title,b.author,b.isbn,b.available});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error loading data");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
