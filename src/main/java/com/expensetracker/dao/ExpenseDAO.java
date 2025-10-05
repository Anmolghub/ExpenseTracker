package com.expensetracker.dao;

import com.expensetracker.model.Expense;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public void addExpense(String date, String category, double amount, String note) {
        String sql = "INSERT INTO expenses(date, category, amount, note) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, note);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
        }
    }

    // ✅ Get expenses by category
    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE category = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Expense(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("note")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching by category: " + e.getMessage());
        }
        return list;
    }

    // ✅ Get monthly total
    public double getMonthlyTotal(String month) {
        String sql = "SELECT SUM(amount) FROM expenses WHERE substr(date,1,7) = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, month); // e.g., "2025-09"
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Error calculating monthly total: " + e.getMessage());
        }
        return 0;
    }

    // ✅ Get all expenses
    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Expense e = new Expense(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("note"));
                list.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ✅ Update expense
    public void updateExpense(int id, String date, String category, double amount, String note) {
        String sql = "UPDATE expenses SET date=?, category=?, amount=?, note=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, note);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
        }
    }

    // ✅ Delete expense
    public void deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
        }
    }

}
