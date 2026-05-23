/* Teacher & TAs: Prof. Lester I. McCann, James Shen, Utkarsh Upadhyay
 * Assignment: Prog4
 * Course: CSc 460
 * Due: December 8, 2025
 *
 * Purpose: This file contains the implementation of the ReservationManager class.
 * It provides methods for creating, viewing, and canceling reservations.
 */
import java.sql.*;
import java.util.Scanner;

public class ReservationManager {

    private final Connection conn;
    private final Scanner scanner;

    public ReservationManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void createReservation() {
        try {
            System.out.println("\n--- Create Reservation ---");

            System.out.print("Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Reservation Date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            Date reservationDate = Date.valueOf(dateStr);

            System.out.print("Reservation Time (YYYY-MM-DD HH:MM:SS): ");
            String timeStr = scanner.nextLine().trim();
            Timestamp reservationTime = Timestamp.valueOf(timeStr);

            System.out.print("Duration (hours, 0.5 to 8.0): ");
            double durationHours = Double.parseDouble(scanner.nextLine().trim());

            if (durationHours < 0.5 || durationHours > 8) {
                System.out.println("Duration must be between 0.5 and 8.0 hours.");
                return;
            }

            // Check room capacity
            String capacitySql = "SELECT max_capacity FROM room WHERE room_id = ?";
            int maxCapacity;
            try (PreparedStatement capPs = conn.prepareStatement(capacitySql)) {
                capPs.setInt(1, roomId);
                ResultSet rs = capPs.executeQuery();
                if (!rs.next()) {
                    System.out.println("Room not found with ID: " + roomId);
                    return;
                }
                maxCapacity = rs.getInt("max_capacity");
            }

            // Check for overlapping reservations and count current reservations
            String overlapSql = """
                SELECT COUNT(*) AS reservation_count
                FROM reservation
                WHERE room_id = ?
                  AND status = 'confirmed'
                  AND reservation_time < (? + NUMTODSINTERVAL(?, 'HOUR'))
                  AND (reservation_time + NUMTODSINTERVAL(duration_hours, 'HOUR')) > ?
                """;

            int existingReservations = 0;
            try (PreparedStatement overlapPs = conn.prepareStatement(overlapSql)) {
                overlapPs.setInt(1, roomId);
                overlapPs.setTimestamp(2, reservationTime);
                overlapPs.setDouble(3, durationHours);
                overlapPs.setTimestamp(4, reservationTime);

                ResultSet rs = overlapPs.executeQuery();
                if (rs.next()) {
                    existingReservations = rs.getInt("reservation_count");
                }
            }

            if (existingReservations >= maxCapacity) {
                System.out.println("Room is at full capacity for the requested time slot.");
                System.out.println("Current reservations: " + existingReservations + " / " + maxCapacity);
                return;
            }

            // Create the reservation
            String sql = """
                INSERT INTO reservation
                    (reservation_id, member_id, room_id, reservation_date,
                     reservation_time, duration_hours, status)
                VALUES
                    (seq_reservation_id.NEXTVAL, ?, ?, ?, ?, ?, 'confirmed')
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memberId);
                ps.setInt(2, roomId);
                ps.setDate(3, reservationDate);
                ps.setTimestamp(4, reservationTime);
                ps.setDouble(5, durationHours);

                int rows = ps.executeUpdate();
                System.out.println("Reservation created successfully. " + rows + " reservation(s) inserted.");
                System.out.println("Current room occupancy: " + (existingReservations + 1) + " / " + maxCapacity);
            }

        } catch (SQLException e) {
            System.out.println("Error creating reservation:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input. Reservation not created.");
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid date/time format.");
            System.out.println("Date format: YYYY-MM-DD");
            System.out.println("Time format: YYYY-MM-DD HH:MM:SS (e.g., 2025-12-08 14:30:00)");
        }
    }

    public void viewAvailableSlots() {
        System.out.println("\n--- View Available Slots ---");
        try {
            System.out.print("Room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Date to check (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            Date checkDate = Date.valueOf(dateStr);

            // Get room capacity
            String capacitySql = "SELECT max_capacity, room_name FROM room WHERE room_id = ?";
            int maxCapacity;
            String roomName;
            try (PreparedStatement capPs = conn.prepareStatement(capacitySql)) {
                capPs.setInt(1, roomId);
                ResultSet rs = capPs.executeQuery();
                if (!rs.next()) {
                    System.out.println("Room not found with ID: " + roomId);
                    return;
                }
                maxCapacity = rs.getInt("max_capacity");
                roomName = rs.getString("room_name");
            }

            System.out.println("\nRoom: " + roomName + " (Capacity: " + maxCapacity + ")");
            System.out.println("Date: " + checkDate);
            System.out.println("\n--- Existing Reservations ---");

            // Get all reservations for this room on this date
            // Qualify the member_id with the table alias to resolve ambiguity
            String sql = """
                SELECT r.reservation_id, r.reservation_time, r.duration_hours, r.status,
                       r.member_id, m.name AS member_name
                FROM reservation r
                JOIN member m ON r.member_id = m.member_id
                WHERE r.room_id = ?
                  AND TRUNC(r.reservation_date) = ?
                ORDER BY r.reservation_time
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, roomId);
                ps.setDate(2, checkDate);

                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        System.out.println("\nReservation #" + rs.getInt("reservation_id"));
                        System.out.println("  Time: " + rs.getTimestamp("reservation_time"));
                        System.out.println("  Duration: " + rs.getDouble("duration_hours") + " hours");
                        System.out.println("  Status: " + rs.getString("status"));
                        System.out.println("  Member: " + rs.getString("member_name") + " (ID: " + rs.getInt("member_id") + ")");
                    }

                    if (count == 0) {
                        System.out.println("No reservations found for this date.");
                    } else {
                        System.out.println("\nTotal reservations: " + count + " / " + maxCapacity);
                        System.out.println("Available slots: " + (maxCapacity - count));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error viewing available slots:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input.");
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    public void cancelReservation() {
        try {
            System.out.println("\n--- Cancel Reservation ---");
            System.out.print("Enter Reservation ID: ");
            int reservationId = Integer.parseInt(scanner.nextLine().trim());

            // Check if reservation exists and can be cancelled
            String checkSql = """
                SELECT r.reservation_id, r.reservation_time, r.status,
                       COUNT(co.order_id) AS order_count
                FROM reservation r
                LEFT JOIN visit v ON r.reservation_id = v.reservation_id
                LEFT JOIN cafe_order co ON v.visit_id = co.visit_id
                WHERE r.reservation_id = ?
                GROUP BY r.reservation_id, r.reservation_time, r.status
                """;

            boolean canCancel = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, reservationId);
                ResultSet rs = checkPs.executeQuery();

                if (!rs.next()) {
                    System.out.println("Reservation not found with ID: " + reservationId);
                    return;
                }

                Timestamp reservationTime = rs.getTimestamp("reservation_time");
                String status = rs.getString("status");
                int orderCount = rs.getInt("order_count");

                // Check business rules: must be before scheduled time and no orders
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (reservationTime.before(now)) {
                    System.out.println("Cannot cancel reservation. The scheduled time has already passed.");
                    return;
                }

                if (orderCount > 0) {
                    System.out.println("Cannot cancel reservation. Food orders have been placed for this visit.");
                    System.out.println("Orders found: " + orderCount);
                    return;
                }

                if ("cancelled".equals(status)) {
                    System.out.println("Reservation is already cancelled.");
                    return;
                }

                canCancel = true;
            }

            if (canCancel) {
                String sql = "UPDATE reservation SET status = 'cancelled' WHERE reservation_id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, reservationId);

                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        System.out.println("No reservation found with that ID.");
                    } else {
                        System.out.println("Reservation cancelled successfully.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error cancelling reservation:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid reservation ID.");
        }
    }

    public void viewMemberReservations() {
        System.out.println("\n--- View Member Reservations ---");
        try {
            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine().trim());

            String sql = """
                SELECT r.reservation_id, r.reservation_date, r.reservation_time,
                       r.duration_hours, r.status,
                       ro.room_id, ro.room_name, ro.room_type,
                       v.visit_id, v.check_in_time, v.check_out_time
                FROM reservation r
                JOIN room ro ON r.room_id = ro.room_id
                LEFT JOIN visit v ON r.reservation_id = v.reservation_id
                WHERE r.member_id = ?
                ORDER BY r.reservation_date DESC, r.reservation_time DESC
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memberId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        System.out.println("\n--- Reservation #" + rs.getInt("reservation_id") + " ---");
                        System.out.println("Date: " + rs.getDate("reservation_date"));
                        System.out.println("Time: " + rs.getTimestamp("reservation_time"));
                        System.out.println("Duration: " + rs.getDouble("duration_hours") + " hours");
                        System.out.println("Status: " + rs.getString("status"));
                        System.out.println("Room: " + rs.getString("room_name") + " (ID: " + rs.getInt("room_id") + ", Type: " + rs.getString("room_type") + ")");
                        
                        int visitId = rs.getInt("visit_id");
                        if (!rs.wasNull()) {
                            System.out.println("Visit ID: " + visitId);
                            System.out.println("Check-in: " + (rs.getTimestamp("check_in_time") == null ? "Not checked in" : rs.getTimestamp("check_in_time")));
                            System.out.println("Check-out: " + (rs.getTimestamp("check_out_time") == null ? "Not checked out" : rs.getTimestamp("check_out_time")));
                        } else {
                            System.out.println("Visit: Not yet checked in");
                        }
                    }

                    if (!any) {
                        System.out.println("No reservations found for member_id = " + memberId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error viewing reservations:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid member ID.");
        }
    }
}

