import java.sql.*;
import java.util.*;

public class Library {
    public void addBook(String title, String author) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            String query = "INSERT INTO books (title, author) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, title);
                ps.setString(2, author);
                ps.executeUpdate();
            }
        }
    }

    public java.util.List<Book> getAllBooks() throws SQLException {
        java.util.List<Book> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String q = "SELECT * FROM books";
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(q)) {
                while (rs.next()) {
                    Book b = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("isIssued")
                    );
                    list.add(b);
                }
            }
        }
        return list;
    }

    public boolean issueBook(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            String q = "UPDATE books SET isIssued = TRUE WHERE id = ? AND isIssued = FALSE";
            try (PreparedStatement ps = con.prepareStatement(q)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean returnBook(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            String q = "UPDATE books SET isIssued = FALSE WHERE id = ? AND isIssued = TRUE";
            try (PreparedStatement ps = con.prepareStatement(q)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        }
    }
}
