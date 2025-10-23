import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class LibraryGUI extends JFrame {
    private Library library = new Library();
    private DefaultTableModel tableModel;
    private JTable table;

    public LibraryGUI() {
        setTitle("Library Management System");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        refreshTable();
    }

    private void initUI() {
        tableModel = new DefaultTableModel(new Object[]{"ID","Title","Author","Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Book");
        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Book");
        JButton refreshBtn = new JButton("Refresh");

        btnPanel.add(addBtn);
        btnPanel.add(issueBtn);
        btnPanel.add(returnBtn);
        btnPanel.add(refreshBtn);

        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> onAdd());
        issueBtn.addActionListener(e -> onIssue());
        returnBtn.addActionListener(e -> onReturn());
        refreshBtn.addActionListener(e -> refreshTable());
    }

    private void onAdd() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        Object[] message = {
            "Title:", titleField,
            "Author:", authorField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Author required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                library.addBook(title, author);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Book added.");
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void onIssue() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to issue.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            boolean ok = library.issueBook(id);
            if (ok) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Book issued.");
            } else JOptionPane.showMessageDialog(this, "Book already issued or invalid ID.");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onReturn() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to return.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            boolean ok = library.returnBook(id);
            if (ok) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Book returned.");
            } else JOptionPane.showMessageDialog(this, "Book is not issued or invalid ID.");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void refreshTable() {
        try {
            List<Book> books = library.getAllBooks();
            tableModel.setRowCount(0);
            for (Book b : books) {
                tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.isIssued() ? "Issued" : "Available"});
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryGUI().setVisible(true);
        });
    }
}
