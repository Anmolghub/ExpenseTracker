package com.expensetracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.util.*;
import java.util.List;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;

public class ExpenseTrackerGUI extends JFrame {
    private ExpenseDAO dao;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextArea insightsArea;

    public ExpenseTrackerGUI() {
        dao = new ExpenseDAO();

        setTitle("💰 Expense Tracker");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // === TOP PANEL ===
        JLabel title = new JLabel("Expense Tracker", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(40, 90, 160));
        add(title, BorderLayout.NORTH);

        // === LEFT PANEL: FORM ===
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Expense"));

        JTextField txtDate = new JTextField();
        JTextField txtCategory = new JTextField();
        JTextField txtAmount = new JTextField();
        JTextField txtNote = new JTextField();

        JButton btnAdd = new JButton("➕ Add Expense");
        btnAdd.setBackground(new Color(60, 179, 113));
        btnAdd.setForeground(Color.WHITE);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):")); formPanel.add(txtDate);
        formPanel.add(new JLabel("Category:")); formPanel.add(txtCategory);
        formPanel.add(new JLabel("Amount:")); formPanel.add(txtAmount);
        formPanel.add(new JLabel("Note:")); formPanel.add(txtNote);
        formPanel.add(new JLabel("")); formPanel.add(btnAdd);

        add(formPanel, BorderLayout.WEST);

        // === CENTER PANEL: TABLE ===
        // 🔥 Changed ID → Serial No
        String[] cols = {"S.No", "ID (Hidden)", "Date", "Category", "Amount", "Note"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // prevent editing in table directly
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setAutoCreateRowSorter(true);

        // Hide ID column from user
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Expenses"));
        add(scrollPane, BorderLayout.CENTER);

        // === RIGHT PANEL: INSIGHTS ===
        insightsArea = new JTextArea();
        insightsArea.setEditable(false);
        insightsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane insightsPane = new JScrollPane(insightsArea);
        insightsPane.setBorder(BorderFactory.createTitledBorder("📊 Category Insights"));
        insightsPane.setPreferredSize(new Dimension(280, 0));
        add(insightsPane, BorderLayout.EAST);

        // === BOTTOM PANEL ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnRefresh = new JButton("🔄 Refresh");
        JButton btnByCategory = new JButton("📂 View by Category");
        JButton btnMonthly = new JButton("📅 Monthly Total");
        JButton btnUpdate = new JButton("✏ Update");
        JButton btnDelete = new JButton("🗑 Delete");
        JButton btnExport = new JButton("💾 Export CSV");

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnByCategory);
        bottomPanel.add(btnMonthly);
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnExport);

        add(bottomPanel, BorderLayout.SOUTH);

        // === ACTIONS ===
        btnAdd.addActionListener(e -> {
            try {
                String date = txtDate.getText();
                String category = txtCategory.getText();
                double amount = Double.parseDouble(txtAmount.getText());
                String note = txtNote.getText();

                dao.addExpense(date, category, amount, note);
                JOptionPane.showMessageDialog(this, "✅ Expense Added!");
                loadExpenses();

                showSmartSuggestion(category, amount);

                txtDate.setText("");
                txtCategory.setText("");
                txtAmount.setText("");
                txtNote.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "⚠ Invalid input. Please try again.");
            }
        });

        btnRefresh.addActionListener(e -> loadExpenses());

        btnByCategory.addActionListener(e -> {
            String category = JOptionPane.showInputDialog(this, "Enter category:");
            if (category != null && !category.isEmpty()) {
                loadExpensesByCategory(category);
            }
        });

        btnMonthly.addActionListener(e -> {
            String month = JOptionPane.showInputDialog(this, "Enter month (YYYY-MM):");
            if (month != null && !month.isEmpty()) {
                double total = dao.getMonthlyTotal(month);
                JOptionPane.showMessageDialog(this, "📅 Total for " + month + " = ₹" + total);
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 1); // ✅ use hidden ID
                String date = JOptionPane.showInputDialog("Enter new Date:", tableModel.getValueAt(row, 2));
                String category = JOptionPane.showInputDialog("Enter new Category:", tableModel.getValueAt(row, 3));
                double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter new Amount:", tableModel.getValueAt(row, 4).toString()));
                String note = JOptionPane.showInputDialog("Enter new Note:", tableModel.getValueAt(row, 5));

                dao.updateExpense(id, date, category, amount, note);
                loadExpenses();
                JOptionPane.showMessageDialog(this, "✏ Expense Updated!");
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Please select a row first.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 1); // ✅ use hidden ID
                dao.deleteExpense(id);
                loadExpenses();
                JOptionPane.showMessageDialog(this, "🗑 Expense Deleted!");
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Please select a row first.");
            }
        });

        btnExport.addActionListener(e -> exportToCSV());

        loadExpenses();
        setVisible(true);
    }

    private void loadExpenses() {
        List<Expense> list = dao.getAllExpenses();
        loadFilteredExpenses(list);
    }

    private void loadExpensesByCategory(String category) {
        List<Expense> list = dao.getExpensesByCategory(category);
        loadFilteredExpenses(list);
    }

    private void loadFilteredExpenses(List<Expense> list) {
        tableModel.setRowCount(0);
        int sno = 1; // 🔥 serial no counter
        for (Expense e : list) {
            tableModel.addRow(new Object[]{
                sno++, e.getId(), e.getDate(), e.getCategory(), e.getAmount(), e.getNote()
            });
        }
        updateCategoryInsights(list);
    }

    private void updateCategoryInsights(List<Expense> expenses) {
        Map<String, Double> categoryTotals = new HashMap<>();
        double grandTotal = 0;
        for (Expense e : expenses) {
            categoryTotals.put(e.getCategory(),
                categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
            grandTotal += e.getAmount();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total Spending: ₹").append(grandTotal).append("\n\n");
        for (String cat : categoryTotals.keySet()) {
            double amount = categoryTotals.get(cat);
            double percent = (grandTotal > 0) ? (amount / grandTotal) * 100 : 0;
            sb.append(cat).append(": ₹").append(amount)
              .append(" (").append(String.format("%.1f", percent)).append("%)\n");
        }
        insightsArea.setText(sb.toString());
    }

    private void showSmartSuggestion(String category, double newAmount) {
        List<Expense> list = dao.getExpensesByCategory(category);
        if (list.size() <= 1) return;

        double total = 0;
        for (Expense e : list) total += e.getAmount();
        double avg = total / list.size();

        if (newAmount > avg * 1.2) {
            JOptionPane.showMessageDialog(this,
                "💡 You just added ₹" + newAmount + " in " + category + ".\n" +
                "Your average " + category + " expense so far is ₹" + String.format("%.2f", avg) + ".\n" +
                "⚠ This is " + String.format("%.1f", ((newAmount / avg) - 1) * 100) + "% higher than usual!");
        }
    }

    private void exportToCSV() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("expenses.csv"));
            int option = fileChooser.showSaveDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                FileWriter fw = new FileWriter(fileChooser.getSelectedFile());

                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    fw.write(tableModel.getColumnName(i) + ",");
                }
                fw.write("\n");

                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        fw.write(tableModel.getValueAt(r, c).toString() + ",");
                    }
                    fw.write("\n");
                }
                fw.close();
                JOptionPane.showMessageDialog(this, "💾 Exported Successfully!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠ Export Failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerGUI());
    }
}
