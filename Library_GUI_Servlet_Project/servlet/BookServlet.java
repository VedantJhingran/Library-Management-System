package servlet;
import dao.BookDAO;
import model.Book;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/books")
public class BookServlet extends HttpServlet {
    private final BookDAO dao = new BookDAO();
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            List<Book> books = dao.findAll();
            out.print("[");
            for (int i=0;i<books.size();i++) {
                Book b = books.get(i);
                out.print("{\"id\":"+b.id+",\"title\":\""+b.title+
                        "\",\"author\":\""+b.author+
                        "\",\"available\":"+b.available+"}");
                if (i<books.size()-1) out.print(",");
            }
            out.print("]");
        } catch (Exception e) {
            out.print("{\"error\":\""+e.getMessage()+"\"}");
        }
    }
}
