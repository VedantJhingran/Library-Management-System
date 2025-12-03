package com.example.library.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.List;

class BookDTO {
    long id; String title; String author; String isbn; boolean available;
}

public class ClientMain extends JFrame {

    private final Gson gson = new Gson();

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"ID","Title","Author","ISBN","Available"},0) {
                public boolean isCellEditable(int r,int c){return false;}
            };

    private JTable table = new JTable(model);

    JTextField tfTitle = new JTextField(12);
    JTextField tfAuthor = new JTextField(12);
    JTextField tfIsbn = new JTextField(12);
    JTextField tfSearch = new JTextField(12);

    public ClientMain() {
        super("Library Client App");
        setSize(900,520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel top = new JPanel();
        top.add(new JLabel("Title:")); top.add(tfTitle);
        top.add(new JLabel("Author:")); top.add(tfAuthor);
        top.add(new JLabel("ISBN:")); top.add(tfIsbn);

        JButton add = new JButton("Add");
        top.add(add);
        add.addActionListener(e -> addBook());

        JPanel bottom = new JPanel();
        JButton del = new JButton("Delete");
        JButton borrow = new JButton("Borrow");
        JButton ret = new JButton("Return");
        JButton search = new JButton("Search");

        bottom.add(del); bottom.add(borrow); bottom.add(ret);
        bottom.add(new JLabel("Search:")); bottom.add(tfSearch); bottom.add(search);

        del.addActionListener(e -> deleteBook());
        borrow.addActionListener(e -> borrowBook());
        ret.addActionListener(e -> returnBook());
        search.addActionListener(e -> loadBooks(tfSearch.getText()));

        add(top,BorderLayout.NORTH);
        add(new JScrollPane(table),BorderLayout.CENTER);
        add(bottom,BorderLayout.SOUTH);

        loadBooks("");
    }

    private void loadBooks(String q) {
        new SwingWorker<List<BookDTO>,Void>() {
            protected List<BookDTO> doInBackground() throws Exception {
                String url = "http://localhost:8080/api/books" +
                        (q.isEmpty()?"" : "?search=" + URLEncoder.encode(q,"UTF-8"));
                return getList(url);
            }
            protected void done() {
                try {
                    List<BookDTO> list = get();
                    model.setRowCount(0);
                    for (BookDTO b : list)
                        model.addRow(new Object[]{b.id,b.title,b.author,b.isbn,b.available});
                } catch (Exception ignored){}
            }
        }.execute();
    }

    private List<BookDTO> getList(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        try (InputStream in = c.getInputStream()) {
            Type t = new TypeToken<List<BookDTO>>(){}.getType();
            return gson.fromJson(new InputStreamReader(in), t);
        }
    }

    private void addBook() {
        BookDTO b = new BookDTO();
        b.title = tfTitle.getText().trim();
        b.author = tfAuthor.getText().trim();
        b.isbn = tfIsbn.getText().trim();
        b.available = true;

        if (b.title.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Title required");
            return;
        }

        new SwingWorker<Void,Void>() {
            protected Void doInBackground() throws Exception {
                URL url = new URL("http://localhost:8080/api/books");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setDoOutput(true);
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type","application/json");
                try (OutputStream out = c.getOutputStream()) {
                    out.write(gson.toJson(b).getBytes());
                }
                c.getResponseCode();
                return null;
            }
            protected void done() {
                loadBooks("");
                tfTitle.setText(""); tfAuthor.setText(""); tfIsbn.setText(""); 
            }
        }.execute();
    }

    private long getSelectedId() {
        int r = table.getSelectedRow();
        if (r < 0) return -1;
        return ((Number)model.getValueAt(r,0)).longValue();
    }

    private void deleteBook() {
        long id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this,"Select a row"); return; }

        request("http://localhost:8080/api/books/"+id, "DELETE");
        loadBooks(""); 
    }

    private void borrowBook() {
        long id = getSelectedId();
        if (id < 0) return;
        request("http://localhost:8080/api/books/"+id+"/borrow", "PUT");
        loadBooks(""); 
    }

    private void returnBook() {
        long id = getSelectedId();
        if (id < 0) return;
        request("http://localhost:8080/api/books/"+id+"/return", "PUT");
        loadBooks(""); 
    }

    private void request(String urlStr, String method) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection c = (HttpURLConnection)url.openConnection();
            c.setRequestMethod(method);
            c.getResponseCode();
        } catch (Exception ignored){}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientMain().setVisible(true));
    }
}
