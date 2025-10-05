package com.expensetracker;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        ExpenseDAO dao = new ExpenseDAO();
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        while (true) {
            System.out.println("\n=== Expense Tracker ===");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Expenses by Category");
            System.out.println("4. View Monthly Total");
            System.out.println("5. Add Random Expenses (for testing)");
            System.out.println("6. Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            if (choice == 1) {
                System.out.print("Date (YYYY-MM-DD): ");
                String date = sc.nextLine();
                System.out.print("Category: ");
                String category = sc.nextLine();
                System.out.print("Amount: ");
                double amount = sc.nextDouble();
                sc.nextLine();
                System.out.print("Note: ");
                String note = sc.nextLine();

                dao.addExpense(date, category, amount, note);
                System.out.println("✅ Expense added!");
            }
            else if (choice == 2) {
                List<Expense> list = dao.getAllExpenses();
                System.out.println("\n--- All Expenses ---");
                for (Expense e : list) System.out.println(e);
            }
            else if (choice == 3) {
                System.out.print("Enter category: ");
                String category = sc.nextLine();
                List<Expense> list = dao.getExpensesByCategory(category);
                System.out.println("\n--- " + category + " Expenses ---");
                for (Expense e : list) System.out.println(e);
            }
            else if (choice == 4) {
                System.out.print("Enter month (YYYY-MM): ");
                String month = sc.nextLine();
                double total = dao.getMonthlyTotal(month);
                System.out.println("Total expenses in " + month + ": " + total);
            }
            else if (choice == 5) {
                addRandomExpenses(dao, sc, rand);
            }
            else if (choice == 6) {
                System.out.println("Bye!");
                break;
            }
        }
        sc.close();
    }

    private static void addRandomExpenses(ExpenseDAO dao, Scanner sc, Random rand) {
        String[] categories = {"Food", "Travel", "Shopping", "Bills", "Entertainment"};
        String[] notes = {"Lunch", "Bus ticket", "Clothes", "Electricity", "Movie", "Snacks", "Petrol"};

        System.out.print("How many random expenses do you want to add? ");
        int count = sc.nextInt();
        sc.nextLine(); // consume newline

        for (int i = 0; i < count; i++) {
            String date = "2025-09-" + (10 + rand.nextInt(15)); // random date between 10–25
            String category = categories[rand.nextInt(categories.length)];
            double amount = 50 + rand.nextInt(500); // between 50–550
            String note = notes[rand.nextInt(notes.length)];

            dao.addExpense(date, category, amount, note);
        }

        System.out.println("✅ " + count + " random expenses added!");
    }
}
