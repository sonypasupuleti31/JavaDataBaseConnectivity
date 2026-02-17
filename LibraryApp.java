package librarySystem;
import java.sql.*;
import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("\n--- Library Menu ---");
            System.out.println("1. View All Students");
            System.out.println("2. View All Books");
            System.out.println("3. Borrow Book");
            System.out.println("4. View Borrowed Books");
            System.out.println("5. Return Book");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewStudents();
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    borrowBook(sc);
                    break;
                case 4:
                    viewBorrowedBooks();
                    break;
                case 5:
                    returnBook(sc);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice");
            }

        } while (choice != 6);

        sc.close();
    }

    public static void viewStudents() {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DBConnection.connection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM student");

            System.out.println("\n--- Student List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("studentId") +
                        ", Name: " + rs.getString("name") +
                        ", Dept: " + rs.getString("department"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    public static void viewBooks() {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DBConnection.connection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM library");

            System.out.println("\n--- Book List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("bookId") +
                        ", Name: " + rs.getString("bookName") +
                        ", Author: " + rs.getString("author") +
                        ", Available: " + rs.getInt("availableCount"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    public static void borrowBook(Scanner sc) {
        Connection con = null;
        PreparedStatement psCheck = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        try {
            con = DBConnection.connection();

            System.out.print("Enter Student ID: ");
            int studentId = sc.nextInt();

            System.out.print("Enter Book ID: ");
            int bookId = sc.nextInt();

            psCheck = con.prepareStatement("SELECT availableCount FROM library WHERE bookId = ?");
            psCheck.setInt(1, bookId);
            rs = psCheck.executeQuery();

            if (rs.next()) {
                int available = rs.getInt("availableCount");
                if (available > 0) {
                    psInsert = con.prepareStatement("INSERT INTO status (studentId, bookId, borrowDate) VALUES (?, ?, CURDATE())");
                    psInsert.setInt(1, studentId);
                    psInsert.setInt(2, bookId);
                    psInsert.executeUpdate();

                    psUpdate = con.prepareStatement("UPDATE library SET availableCount = availableCount - 1 WHERE bookId = ?");
                    psUpdate.setInt(1, bookId);
                    psUpdate.executeUpdate();

                    System.out.println("Book borrowed successfully!");
                } 
                else {
                    System.out.println("Book not available!");
                }
            } else {
                System.out.println("Book ID not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (psCheck != null) psCheck.close(); } catch (Exception e) {}
            try { if (psInsert != null) psInsert.close(); } catch (Exception e) {}
            try { if (psUpdate != null) psUpdate.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    public static void viewBorrowedBooks() {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DBConnection.connection();
            stmt = con.createStatement();
            String query = "SELECT s.name AS student, l.bookName, st.borrowDate, st.returnDate " +
                    "FROM status st JOIN student s ON st.studentId = s.studentId " +
                    "JOIN library l ON st.bookId = l.bookId";
            rs = stmt.executeQuery(query);

            System.out.println("\n--- Borrowed Books ---");
            while (rs.next()) {
                System.out.println("Student: " + rs.getString("student") +
                        ", Book: " + rs.getString("bookName") +
                        ", Borrowed: " + rs.getDate("borrowDate") +
                        ", Returned: " + rs.getDate("returnDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    public static void returnBook(Scanner sc) {
        Connection con = null;
        PreparedStatement psReturn = null;
        PreparedStatement psUpdate = null;

        try {
            con = DBConnection.connection();

            System.out.print("Enter Student ID: ");
            int studentId = sc.nextInt();

            System.out.print("Enter Book ID: ");
            int bookId = sc.nextInt();

            psReturn = con.prepareStatement("UPDATE status SET returnDate = CURDATE() WHERE studentId = ? AND bookId = ? AND returnDate IS NULL");
            psReturn.setInt(1, studentId);
            psReturn.setInt(2, bookId);
            int rowsUpdated = psReturn.executeUpdate();

            if (rowsUpdated > 0) {
                psUpdate = con.prepareStatement("UPDATE library SET availableCount = availableCount + 1 WHERE bookId = ?");
                psUpdate.setInt(1, bookId);
                psUpdate.executeUpdate();

                System.out.println("Book returned successfully!");
            } else {
                System.out.println("No such borrowing record found or already returned.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (psReturn != null) psReturn.close(); } catch (Exception e) {}
            try { if (psUpdate != null) psUpdate.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
}
