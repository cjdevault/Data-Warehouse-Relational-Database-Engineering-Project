import java.sql.*;
import java.util.Scanner;

public class EventManager {

    // View Upcoming Events 
    public static void viewUpcomingEvents(Connection conn) {
        System.out.println("\n--- Upcoming Events ---");

        String sql = """
            SELECT e.event_id,
                   e.name,
                   e.start_date_time,
                   e.end_date_time,
                   e.max_capacity,
                   r.room_name,
                   s.name AS coordinator_name,
                   COALESCE(
                       COUNT(
                           CASE
                               WHEN er.attendance_status <> 'cancelled'
                               THEN 1
                           END
                       ), 0
                   ) AS current_reg
            FROM event e
            JOIN room r ON e.room_id = r.room_id
            JOIN staff s ON e.coordinator_id = s.staff_id
            LEFT JOIN event_registration er
                ON e.event_id = er.event_id
            WHERE e.start_date_time > SYSTIMESTAMP
            GROUP BY e.event_id, e.name, e.start_date_time, e.end_date_time,
                     e.max_capacity, r.room_name, s.name
            ORDER BY e.start_date_time
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean any = false;
            while (rs.next()) {
                any = true;
                int eventId = rs.getInt("event_id");
                String name = rs.getString("name");
                Timestamp start = rs.getTimestamp("start_date_time");
                Timestamp end = rs.getTimestamp("end_date_time");
                int maxCap = rs.getInt("max_capacity");
                int currentReg = rs.getInt("current_reg");
                String roomName = rs.getString("room_name");
                String coordName = rs.getString("coordinator_name");

                System.out.println("Event ID: " + eventId);
                System.out.println("  Name: " + name);
                System.out.println("  When: " + start + " to " + end);
                System.out.println("  Room: " + roomName);
                System.out.println("  Coordinator: " + coordName);
                System.out.println("  Registered: " + currentReg + " / " + maxCap);
                System.out.println();
            }

            if (!any) {
                System.out.println("No upcoming events found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving upcoming events:");
            e.printStackTrace();
        }
    }

    // Register for an event
    public static void registerForEvent(Connection conn, Scanner scanner) {
        System.out.println("\n--- Register For Event ---");

        try {
            System.out.print("Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            System.out.print("Event ID: ");
            int eventId = Integer.parseInt(scanner.nextLine());

            // check to make sure event exists and isnt full 
            String capSql = """
                SELECT e.name,
                       e.max_capacity,
                       COALESCE(
                           COUNT(
                               CASE
                                   WHEN er.attendance_status <> 'cancelled'
                                   THEN 1
                               END
                           ), 0
                       ) AS current_reg
                FROM event e
                LEFT JOIN event_registration er
                  ON e.event_id = er.event_id
                WHERE e.event_id = ?
                GROUP BY e.name, e.max_capacity
                """;

            String eventName;
            int maxCap;
            int currentReg;

            try (PreparedStatement ps = conn.prepareStatement(capSql)) {
                ps.setInt(1, eventId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No event found with that ID.");
                        return;
                    }
                    eventName = rs.getString("name");
                    maxCap = rs.getInt("max_capacity");
                    currentReg = rs.getInt("current_reg");
                }
            }

            if (currentReg >= maxCap) {
                System.out.println("Event \"" + eventName + "\" is full. Registration not allowed.");
                return;
            }

            // check if same user is already registered 
            String existsSql = """
                SELECT attendance_status, payment_status
                FROM event_registration
                WHERE event_id = ? AND member_id = ?
                """;

            try (PreparedStatement ps = conn.prepareStatement(existsSql)) {
                ps.setInt(1, eventId);
                ps.setInt(2, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String att = rs.getString("attendance_status");
                        System.out.println("Member already has a registration for this event (status = "
                                           + att + ").");
                        System.out.println("Use the booking update/cancel option instead.");
                        return;
                    }
                }
            }

            // payment stuff 
            System.out.print("Amount to pay for this event: ");
            String amountStr = scanner.nextLine().trim();
            double amount = amountStr.isBlank() ? 0.0 : Double.parseDouble(amountStr);

            System.out.print("Has this been paid? (y/n): ");
            String paidStr = scanner.nextLine().trim().toLowerCase();
            String paymentStatus = paidStr.startsWith("y") ? "paid" : "pending";

            // add booking 
            String insertSql = """
                INSERT INTO event_registration
                    (event_registration_id, event_id, member_id,
                     attendance_status, amount_paid, payment_status, registration_date)
                VALUES
                    (seq_event_registration_id.NEXTVAL, ?, ?, 'registered', ?, ?, SYSDATE)
                """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, eventId);
                ps.setInt(2, memberId);
                ps.setDouble(3, amount);
                ps.setString(4, paymentStatus);

                int rows = ps.executeUpdate();
                System.out.println("Registration created (" + rows + " row(s) inserted) "
                                   + "for event \"" + eventName + "\".");
            }

        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input. Registration not created.");
        } catch (SQLException e) {
            System.out.println("Error registering for event:");
            e.printStackTrace();
        }
    }

    // view event details 
    public static void viewEventDetails(Connection conn, Scanner scanner) {
        System.out.println("\n--- Event Details ---");
        try {
            System.out.print("Event ID: ");
            int eventId = Integer.parseInt(scanner.nextLine());

            String eventSql = """
                SELECT e.event_id,
                       e.name,
                       e.start_date_time,
                       e.end_date_time,
                       e.max_capacity,
                       e.event_type,
                       e.description,
                       r.room_name,
                       s.name AS coordinator_name,
                       COALESCE(
                           COUNT(
                               CASE
                                   WHEN er.attendance_status <> 'cancelled'
                                   THEN 1
                               END
                           ), 0
                       ) AS current_reg
                FROM event e
                JOIN room r ON e.room_id = r.room_id
                JOIN staff s ON e.coordinator_id = s.staff_id
                LEFT JOIN event_registration er
                   ON e.event_id = er.event_id
                WHERE e.event_id = ?
                GROUP BY e.event_id, e.name, e.start_date_time, e.end_date_time,
                         e.max_capacity, e.event_type, e.description,
                         r.room_name, s.name
                """;

            String name;
            Timestamp start;
            Timestamp end;
            int maxCap;
            int currentReg;
            String roomName;
            String coordName;
            String eventType;
            String description;

            try (PreparedStatement ps = conn.prepareStatement(eventSql)) {
                ps.setInt(1, eventId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No event found with that ID.");
                        return;
                    }

                    name = rs.getString("name");
                    start = rs.getTimestamp("start_date_time");
                    end = rs.getTimestamp("end_date_time");
                    maxCap = rs.getInt("max_capacity");
                    currentReg = rs.getInt("current_reg");
                    roomName = rs.getString("room_name");
                    coordName = rs.getString("coordinator_name");
                    eventType = rs.getString("event_type");
                    description = rs.getString("description");
                }
            }

            System.out.println("Event ID: " + eventId);
            System.out.println("Name: " + name);
            System.out.println("Type: " + eventType);
            System.out.println("When: " + start + " to " + end);
            System.out.println("Room: " + roomName);
            System.out.println("Coordinator: " + coordName);
            System.out.println("Capacity: " + currentReg + " / " + maxCap);
            System.out.println("Description: " + (description == null ? "-" : description));
            System.out.println();

            String regSql = """
                SELECT er.event_registration_id,
                       m.name AS member_name,
                       er.attendance_status,
                       er.payment_status,
                       er.amount_paid,
                       er.registration_date
                FROM event_registration er
                JOIN member m ON er.member_id = m.member_id
                WHERE er.event_id = ?
                ORDER BY er.registration_date, er.event_registration_id
                """;

            try (PreparedStatement ps = conn.prepareStatement(regSql)) {
                ps.setInt(1, eventId);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        int regId = rs.getInt("event_registration_id");
                        String memberName = rs.getString("member_name");
                        String attStatus = rs.getString("attendance_status");
                        String payStatus = rs.getString("payment_status");
                        double amountPaid = rs.getDouble("amount_paid");
                        Date regDate = rs.getDate("registration_date");

                        System.out.println("  Registration ID: " + regId);
                        System.out.println("    Member: " + memberName);
                        System.out.println("    Status: " + attStatus);
                        System.out.println("    Payment: " + payStatus + " ($" + amountPaid + ")");
                        System.out.println("    Registered on: " + regDate);
                        System.out.println();
                    }

                    if (!any) {
                        System.out.println("No registrations for this event.");
                    }
                }
            }

        } catch (NumberFormatException nfe) {
            System.out.println("Invalid event ID.");
        } catch (SQLException e) {
            System.out.println("Error retrieving event details:");
            e.printStackTrace();
        }
    }

    // cancel or update booking 
    public static void cancelOrUpdateBooking(Connection conn, Scanner scanner) {
        System.out.println("\n--- Cancel / Update Event Booking ---");
        try {
            System.out.print("Event registration ID: ");
            int regId = Integer.parseInt(scanner.nextLine());

            String infoSql = """
                SELECT er.event_id,
                       er.payment_status,
                       er.attendance_status,
                       e.start_date_time
                FROM event_registration er
                JOIN event e ON er.event_id = e.event_id
                WHERE er.event_registration_id = ?
                """;

            int eventId;
            String paymentStatus;
            String attStatus;
            Timestamp start;

            try (PreparedStatement ps = conn.prepareStatement(infoSql)) {
                ps.setInt(1, regId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No booking found with that ID.");
                        return;
                    }
                    eventId = rs.getInt("event_id");
                    paymentStatus = rs.getString("payment_status");
                    attStatus = rs.getString("attendance_status");
                    start = rs.getTimestamp("start_date_time");
                }
            }

            long now = System.currentTimeMillis();
            long cutoff = now + 72L * 60L * 60L * 1000L;  // 72 hours before event

            boolean earlyEnough = start.getTime() >= cutoff;
            boolean refunded = "refunded".equalsIgnoreCase(paymentStatus);

            if (earlyEnough && refunded) {
                String delSql = "DELETE FROM event_registration WHERE event_registration_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(delSql)) {
                    ps.setInt(1, regId);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        System.out.println("Booking not found or already deleted.");
                    } else {
                        System.out.println("Booking deleted (early cancellation with refund).");
                    }
                }
            } else {
                String updSql = """
                    UPDATE event_registration
                    SET attendance_status = 'cancelled'
                    WHERE event_registration_id = ?
                    """;
                try (PreparedStatement ps = conn.prepareStatement(updSql)) {
                    ps.setInt(1, regId);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        System.out.println("Booking not found.");
                    } else {
                        System.out.println("Booking marked as cancelled (history preserved).");
                    }
                }
            }

        } catch (NumberFormatException nfe) {
            System.out.println("Invalid event registration ID.");
        } catch (SQLException e) {
            System.out.println("Error cancelling/updating booking:");
            e.printStackTrace();
        }
    }
}