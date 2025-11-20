import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class MyUI {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Hotel Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Title
        JLabel titleLabel = new JLabel("Hotel Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(titleLabel, gbc);

        // Guest Name
        JLabel nameLabel = new JLabel("Guest Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        frame.add(nameField, gbc);

        // Room Number
        JLabel roomLabel = new JLabel("Room Number:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        frame.add(roomLabel, gbc);

        JTextField roomField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        frame.add(roomField, gbc);

        // Contact Number
        JLabel contactLabel = new JLabel("Contact Number:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        frame.add(contactLabel, gbc);

        JTextField contactField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        frame.add(contactField, gbc);

        // Reservation ID (for get/update/delete)
        JLabel idLabel = new JLabel("Reservation ID:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        frame.add(idLabel, gbc);

        JTextField idField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        frame.add(idField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton addBtn = new JButton("Add Reservation");
        JButton viewBtn = new JButton("View Reservations");
        JButton getRoomBtn = new JButton("Get Room Number");
        JButton updateBtn = new JButton("Update Reservation");
        JButton deleteBtn = new JButton("Delete Reservation");
        JButton exitBtn = new JButton("Exit System");
        buttonPanel.add(addBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(getRoomBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(exitBtn);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(buttonPanel, gbc);

        // Result TextArea
        JTextArea resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        frame.add(scrollPane, gbc);

        // Database details
        String url = "jdbc:mysql://localhost:3306/hotel_db";
        String username = "root";
        String password = "123456789";

        // --- ACTIONS ---

        // Add Reservation
        addBtn.addActionListener(e -> {
            String guestName = nameField.getText().trim();
            String roomNumStr = roomField.getText().trim();
            String contact = contactField.getText().trim();

            if (guestName.isEmpty() || roomNumStr.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields!");
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                int roomNumber = Integer.parseInt(roomNumStr);
                String sql = "INSERT INTO reservations (guest_name, room_number, contact_number, reservation_date) VALUES (?, ?, ?, NOW())";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, guestName);
                stmt.setInt(2, roomNumber);
                stmt.setString(3, contact);

                int rows = stmt.executeUpdate();
                resultArea.setText(rows > 0 ? "Reservation added successfully!" : "Failed to add reservation.");
                nameField.setText("");
                roomField.setText("");
                contactField.setText("");
                stmt.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(frame, "Room number must be numeric!");
            }
        });

        // View Reservations
        viewBtn.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT reservations_id, guest_name, room_number, contact_number, reservation_date FROM reservations");

                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%-5s %-20s %-10s %-15s %-20s\n", "ID", "Guest Name", "Room", "Contact", "Date"));
                sb.append("---------------------------------------------------------------\n");

                while (rs.next()) {
                    sb.append(String.format("%-5d %-20s %-10d %-15s %-20s\n",
                            rs.getInt("reservations_id"),
                            rs.getString("guest_name"),
                            rs.getInt("room_number"),
                            rs.getString("contact_number"),
                            rs.getTimestamp("reservation_date")));
                }
                resultArea.setText(sb.toString());
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage());
            }
        });

        // Get Room Number
        getRoomBtn.addActionListener(e -> {
            String guestName = nameField.getText().trim();
            String idStr = idField.getText().trim();
            if (guestName.isEmpty() || idStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter Guest Name and Reservation ID!");
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                int resId = Integer.parseInt(idStr);
                PreparedStatement stmt = conn.prepareStatement("SELECT room_number FROM reservations WHERE reservations_id=? AND guest_name=?");
                stmt.setInt(1, resId);
                stmt.setString(2, guestName);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    resultArea.setText("Room number: " + rs.getInt("room_number"));
                } else {
                    resultArea.setText("Reservation not found.");
                }

                rs.close();
                stmt.close();
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // Update Reservation
        updateBtn.addActionListener(e -> {
            String idStr = idField.getText().trim();
            String guestName = nameField.getText().trim();
            String roomStr = roomField.getText().trim();
            String contact = contactField.getText().trim();

            if (idStr.isEmpty() || guestName.isEmpty() || roomStr.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields!");
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                int resId = Integer.parseInt(idStr);
                int roomNumber = Integer.parseInt(roomStr);

                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE reservations SET guest_name=?, room_number=?, contact_number=? WHERE reservations_id=?");
                stmt.setString(1, guestName);
                stmt.setInt(2, roomNumber);
                stmt.setString(3, contact);
                stmt.setInt(4, resId);

                int rows = stmt.executeUpdate();
                resultArea.setText(rows > 0 ? "Reservation updated successfully!" : "Update failed.");
                stmt.close();
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // Delete Reservation
        deleteBtn.addActionListener(e -> {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter Reservation ID!");
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                int resId = Integer.parseInt(idStr);
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM reservations WHERE reservations_id=?");
                stmt.setInt(1, resId);

                int rows = stmt.executeUpdate();
                resultArea.setText(rows > 0 ? "Reservation deleted successfully!" : "Delete failed.");
                stmt.close();
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // Exit System
        exitBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    resultArea.setText("Exiting System");
                    for (int i = 0; i < 5; i++) {
                        resultArea.append(".");
                        Thread.sleep(450);
                    }
                    resultArea.append("\nThank You For Using Hotel Reservation System!!!");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        frame.pack();
        frame.setVisible(true);
    }
}



