Hotel Reservation System (Java Swing + MySQL)
Overview

This is a desktop-based Hotel Reservation System built using Java Swing for the GUI and MySQL as the database backend.
The system allows you to:

Add new reservations

View all reservations

Get room number by guest name and reservation ID

Update existing reservations

Delete reservations

Exit the system gracefully

All outputs and logs are displayed in the GUI itself using a JTextArea.

Features

Swing-based GUI with an easy-to-use interface.

MySQL integration for storing and managing reservation data.

Live result display inside the application.

Input validation for fields like guest name, room number, and reservation ID.

Clean exit with countdown and message.

Requirements

Java JDK (version 8 or higher)

MySQL server installed locally or remotely

MySQL Connector/J (JDBC driver)

Setup Instructions

1. Clone or Download the Project

Download the source code and open it in your preferred Java IDE (e.g., VS Code, IntelliJ, Eclipse).

2. Create MySQL Database

Open your MySQL Workbench or any SQL client.

Create a new database:

CREATE DATABASE hotel_db;

Create the reservations table:

USE hotel_db;

CREATE TABLE reservations (
reservations_id INT AUTO_INCREMENT PRIMARY KEY,
guest_name VARCHAR(100) NOT NULL,
room_number INT NOT NULL,
contact_number VARCHAR(20) NOT NULL,
reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

3. Configure Database Connection

Open the Java files (Dbcon.java and MyUI.java) and update the database credentials:

String url = "jdbc:mysql://localhost:3306/hotel_db";
String username = "YOUR_MYSQL_USERNAME"; // e.g., "root"
String password = "YOUR_MYSQL_PASSWORD"; // e.g., "123456"

Important: Make sure the MySQL server is running and accessible.

4. Add MySQL Connector/J

Download the MySQL JDBC driver from MySQL Connector/J
.

Add the .jar file to your project's classpath.

5. Run the Application

Run MyUI.java to launch the Swing GUI version.

Run Dbcon.java for the console version (optional).

6. Using the Application

Fill in the Guest Name, Room Number, and Contact Number to add a reservation.

Use the Reservation ID field for operations like Get Room Number, Update, or Delete.

Click the corresponding buttons for each action.

All results will be displayed in the Result area in the GUI.

7. Exiting the Application

Click the Exit System button to see a friendly countdown and exit message.

Notes

Ensure your MySQL user has privileges to CREATE, INSERT, UPDATE, DELETE, SELECT in the database.

Room numbers should be numeric.

Guest names and contact numbers cannot be empty.

All outputs are displayed in the GUI; no console output is required.
