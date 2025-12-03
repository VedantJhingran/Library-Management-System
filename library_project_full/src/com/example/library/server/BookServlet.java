package com.example.library.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class BookServlet extends HttpServlet {

    private final LibraryService service = new LibraryService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String search = req.getParameter("search");

        try {
            List<Book> list = (search != null && !search.isEmpty())
                    ? service.search(search)
                    : service.listAll();

            resp.getWriter().print(gson.toJson(list));
            resp.setStatus(200);

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print("{"error":"" + e.getMessage() + ""}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader r = req.getReader()) {
            Book b = gson.fromJson(r, Book.class);
            Book saved = service.add(b);

            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.getWriter().print(gson.toJson(saved));

        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().print("{"error": "" + e.getMessage() + ""}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pi = req.getPathInfo();
            if (pi == null || pi.length() < 2) throw new IllegalArgumentException("Invalid id");
            long id = Long.parseLong(pi.substring(1));
            service.delete(id);
            resp.setStatus(204);

        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().print("{"error": "" + e.getMessage() + ""}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.length() < 2) throw new IllegalArgumentException("Invalid path");
            String[] parts = path.split("/");
            long id = Long.parseLong(parts[1]);

            if (parts.length == 3 && parts[2].equalsIgnoreCase("borrow")) {
                service.borrow(id);
                resp.getWriter().print("{\"ok\":true}");
                return;
            }

            if (parts.length == 3 && parts[2].equalsIgnoreCase("return")) {
                service.returnBook(id);
                resp.getWriter().print("{\"ok\":true}");
                return;
            }

            Book b = gson.fromJson(req.getReader(), Book.class);
            b.setId(id);
            service.update(b);

            resp.setContentType("application/json");
            resp.getWriter().print(gson.toJson(b));

        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().print("{"error": "" + e.getMessage() + ""}");
        }
    }
}
