import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Dbcon {

    public static void main(String[] args) throws SQLException ,InterruptedException {

        String url = "jdbc:mysql://localhost:3306/hotel_db";
        String username = "root";
        String password = "123456789";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection con = DriverManager.getConnection(url, username, password)) {

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println();
                System.out.println("HOTEL RESERVATION SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View reservations");
                System.out.println("3. Get room number by guest");
                System.out.println("4. Update reservation");
                System.out.println("5. Delete reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        reserveRoom(con, scanner);
                        break;
                    case 2:
                        viewReservations(con);
                        break;
                    case 3:
                        getRoomNumber(con, scanner);
                        break;
                    case 4:
                        updateReservation(con, scanner);
                        break;
                    case 5:
                        deleteReservation(con, scanner);
                        break;
                    case 0:
                        exit();   // call your exit method
                        return;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

    // 1. RESERVE ROOM
    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            scanner.nextLine();
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();

            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES ('"
                    + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. VIEW RESERVATIONS
    private static void viewReservations(Connection connection) throws SQLException {

        String sql = "SELECT reservations_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("\nCurrent Reservations:");
            System.out.println("+------------------+-----------------+---------------+----------------------+-----------------------+");
            System.out.println("| Reservation ID   | Guest Name      | Room Number   | Contact Number       | Reservation Date      |");
            System.out.println("+------------------+-----------------+---------------+----------------------+-----------------------+");

            while (resultSet.next()) {

                int reservationId = resultSet.getInt("reservations_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf(
                        "| %-16d | %-15s | %-13d | %-20s | %-21s |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate
                );
            }

            System.out.println("+------------------+-----------------+---------------+----------------------+-----------------------+");
        }
    }

    // 3. GET ROOM NUMBER
    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();

            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations " +
                         "WHERE reservations_id = " + reservationId +
                         " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {

                    int roomNumber = resultSet.getInt("room_number");

                    System.out.println("Room number for Reservation ID " + reservationId +
                                       " and Guest '" + guestName + "' is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. UPDATE RESERVATION
    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();

            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.nextLine();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                         "room_number = " + newRoomNumber + ", " +
                         "contact_number = '" + newContactNumber + "' " +
                         "WHERE reservations_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. DELETE RESERVATION
    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservations_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CHECK IF RESERVATION EXISTS
    private static boolean reservationExists(Connection connection, int reservationId) {

        String sql = "SELECT reservations_id FROM reservations WHERE reservations_id = " + reservationId;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    //exit 

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        
        int i = 5;
        while (i > 0) {
            System.out.print(".");
            Thread.sleep(450); // Pause for 450 milliseconds
            i--;
        }

        System.out.println();
        System.out.println("Thank You For Using Hotel Reservation System!!!");
    }

}
