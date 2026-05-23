/* Author: Nathan Tebbs
 * Teacher & TAs: Prof. Lester I. McCann, James Shen, Utkarsh Upadhyay
 * Assignment: Prog4
 * Course: CSc 460
 * Due: December 8, 2025
 *
 * Purpose: Visit management class for the Pet Cafe Management System.
 *          Provides check-in, check-out, and active visit listing.
 *
 * Java version: 16
 */

import java.sql.*;
import java.util.Scanner;

public class VisitManager {

    /**
     * Name: checkIn
     * Purpose: Checks in a customer for an existing reservation by creating a new visit row.
     *
     * Preconditions:
     * - The reservation table is populated.
     * - The membership_tier and member tables exist and are properly linked.
     * - The seq_visit_id sequence exists.
     *
     * Postconditions:
     * - A new row may be inserted into visit.
     * - The new visit is linked to the given reservation_id.
     *
     * Parameters:
     * - conn: open JDBC connection to the database.
     * - scanner: Scanner reading from System.in for user input.
     *
     * Returns:
     * - void
     */
    public static void checkIn(Connection conn, Scanner scanner) {
        System.out.println("\n--- Check In ---");

        int reservationId = readInt(scanner, "Enter reservation_id to check in: ");

        String reservationSql = """
            SELECT r.reservation_id,
                   r.status,
                   m.member_id,
                   mt.name AS tier_name
            FROM reservation r
            JOIN member m ON r.member_id = m.member_id
            JOIN membership_tier mt ON m.membership_tier_id = mt.tier_id
            WHERE r.reservation_id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(reservationSql)) {
            ps.setInt(1, reservationId);

            String status;
            String tierName;
            int memberId;

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No reservation found with id " + reservationId + ".");
                    return;
                }
                status = rs.getString("status");
                memberId = rs.getInt("member_id");
                tierName = rs.getString("tier_name");
            }

            if ("cancelled".equalsIgnoreCase(status)) {
                System.out.println("Reservation " + reservationId + " is cancelled and cannot be checked in.");
                return;
            }

            if (visitForReservationExists(conn, reservationId)) {
                System.out.println("A visit already exists for reservation " + reservationId + ".");
                return;
            }

            Integer visitId = getNextVisitId(conn);
            if (visitId == null) {
                System.out.println("Could not generate a new visit id from seq_visit_id.");
                return;
            }

            String insertVisitSql = """
                INSERT INTO visit (
                    visit_id,
                    reservation_id,
                    current_membership_tier,
                    check_in_time,
                    check_out_time
                )
                VALUES (?, ?, ?, SYSTIMESTAMP, NULL)
                """;

            try (PreparedStatement insertPs = conn.prepareStatement(insertVisitSql)) {
                insertPs.setInt(1, visitId);
                insertPs.setInt(2, reservationId);
                insertPs.setString(3, tierName);

                int rows = insertPs.executeUpdate();
                if (rows == 1) {
                    System.out.printf(
                        "Checked in reservation %d as visit %d for member %d (tier: %s).%n",
                        reservationId, visitId, memberId, tierName
                    );
                } else {
                    System.out.println("Unexpected row count when inserting visit (expected 1).");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error during check-in:");
            e.printStackTrace();
        }
    }

    /**
     * Name: checkOut
     * Purpose: Checks out an active visit by setting its check_out_time and reporting duration.
     *
     * Preconditions:
     * - The visit table exists.
     * - The given visit_id exists and has a NULL check_out_time.
     *
     * Postconditions:
     * - The visit's check_out_time is updated to the current timestamp.
     *
     * Parameters:
     * - conn: open JDBC connection to the database.
     * - scanner: Scanner reading from System.in for user input.
     *
     * Returns:
     * - void
     */
    public static void checkOut(Connection conn, Scanner scanner) {
        System.out.println("\n--- Check Out ---");

        int visitId = readInt(scanner, "Enter visit_id to check out: ");

        String selectSql = "SELECT check_in_time, check_out_time FROM visit WHERE visit_id = ?";

        try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
            selectPs.setInt(1, visitId);

            Timestamp checkIn;
            Timestamp existingCheckOut;

            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No visit found with id " + visitId + ".");
                    return;
                }
                checkIn = rs.getTimestamp("check_in_time");
                existingCheckOut = rs.getTimestamp("check_out_time");
            }

            if (existingCheckOut != null) {
                System.out.println("Visit " + visitId + " is already checked out.");
                return;
            }

            String updateSql =
                "UPDATE visit SET check_out_time = SYSTIMESTAMP WHERE visit_id = ? AND check_out_time IS NULL";

            int updated;
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setInt(1, visitId);
                updated = updatePs.executeUpdate();
            }

            if (updated == 0) {
                System.out.println("Visit " + visitId + " could not be updated (possibly already checked out).");
                return;
            }

            Timestamp checkOut;
            try (PreparedStatement reread =
                     conn.prepareStatement("SELECT check_out_time FROM visit WHERE visit_id = ?")) {
                reread.setInt(1, visitId);
                try (ResultSet rs2 = reread.executeQuery()) {
                    if (!rs2.next()) {
                        System.out.println("Unexpected: visit disappeared after update.");
                        return;
                    }
                    checkOut = rs2.getTimestamp("check_out_time");
                }
            }

            if (checkIn != null && checkOut != null) {
                long millis = checkOut.getTime() - checkIn.getTime();
                double hours = millis / 1000.0 / 60.0 / 60.0;
                System.out.printf("Visit %d checked out. Duration: approximately %.2f hours.%n",
                                  visitId, hours);
            } else {
                System.out.println("Visit " + visitId + " checked out.");
            }

        } catch (SQLException e) {
            System.out.println("Error during check-out:");
            e.printStackTrace();
        }
    }

    /**
     * Name: viewActiveVisits
     * Purpose: Lists all visits where check_out_time is NULL.
     *
     * Preconditions:
     * - The visit, reservation, and member tables exist and are readable.
     *
     * Postconditions:
     * - No database modifications.
     * - Active visit information is printed to the console.
     *
     * Parameters:
     * - conn: open JDBC connection to the database.
     *
     * Returns:
     * - void
     */
    public static void viewActiveVisits(Connection conn) {
        System.out.println("\n--- Active Visits ---");

        String sql = """
            SELECT v.visit_id,
                   v.reservation_id,
                   v.current_membership_tier,
                   v.check_in_time,
                   r.member_id,
                   m.name AS member_name,
                   r.room_id
            FROM visit v
            JOIN reservation r ON v.reservation_id = r.reservation_id
            JOIN member m ON r.member_id = m.member_id
            WHERE v.check_out_time IS NULL
            ORDER BY v.check_in_time
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean any = false;

            System.out.printf("%-8s %-10s %-8s %-22s %-10s %-20s %-15s%n",
                    "VisitID", "ResID", "RoomID", "Check-in Time",
                    "MemberID", "Member Name", "Tier");
            System.out.println("--------------------------------------------------------------------------------");

            while (rs.next()) {
                any = true;
                int visitId = rs.getInt("visit_id");
                int resId = rs.getInt("reservation_id");
                int roomId = rs.getInt("room_id");
                Timestamp checkIn = rs.getTimestamp("check_in_time");
                int memberId = rs.getInt("member_id");
                String memberName = rs.getString("member_name");
                String tier = rs.getString("current_membership_tier");

                System.out.printf("%-8d %-10d %-8d %-22s %-10d %-20s %-15s%n",
                        visitId, resId, roomId,
                        checkIn == null ? "-" : checkIn.toString(),
                        memberId, memberName, tier);
            }

            if (!any) {
                System.out.println("No active visits at the moment.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing active visits:");
            e.printStackTrace();
        }
    }

    /**
     * Name: deleteVisit
     * Purpose: Deletes a visit record if it exists and has no associated orders.
     *
     * Preconditions:
     * - visit table exists.
     * - The user enters a valid or invalid visit_id.
     *
     * Postconditions:
     * - If the visit exists and has no cafe_order rows, it is deleted.
     * - If the visit has orders, it is not deleted and a message is printed.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     *
     * Returns:
     * - void
     */
    public static void deleteVisit(Connection conn, Scanner scanner) {
        System.out.println("\n--- Delete Visit ---");
        
        int visitId = readInt(scanner, "Enter visit_id to delete: ");
        
        // Check if visit exists
        String selectSql = "SELECT check_in_time, check_out_time FROM visit WHERE visit_id = ?";
        
        Timestamp checkInTime;
        Timestamp checkOutTime;
        
        try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
            selectPs.setInt(1, visitId);
            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No visit found with id " + visitId + ".");
                    return;
                }
                checkInTime = rs.getTimestamp("check_in_time");
                checkOutTime = rs.getTimestamp("check_out_time");
            }
        } catch (SQLException e) {
            System.out.println("Error looking up visit:");
            e.printStackTrace();
            return;
        }
        
        // Optional rule: do not allow deletion if there are orders attached
        if (visitHasOrders(conn, visitId)) {
            System.out.println("Cannot delete visit " + visitId +
                               " because it has associated orders.");
            return;
        }
        
        // Warn if still active
        if (checkOutTime == null) {
            System.out.println("Warning: visit " + visitId + " is still active (no check-out recorded).");
        }
        
        String confirm = readLine(scanner,
                                  "Are you sure you want to delete this visit? This cannot be undone. (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            return;
        }
        
        String deleteSql = "DELETE FROM visit WHERE visit_id = ?";
        
        try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
            deletePs.setInt(1, visitId);
            int rows = deletePs.executeUpdate();
            if (rows == 1) {
                System.out.println("Visit " + visitId + " deleted.");
            } else {
                System.out.println("Unexpected: visit " + visitId + " was not deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting visit:");
            e.printStackTrace();
        }
    }
    
    /**
     * Name: visitHasOrders
     * Purpose: Checks whether any cafe_order rows reference the given visit_id.
     *
     * Preconditions:
     * - cafe_order table exists.
     *
     * Postconditions:
     * - No database modifications.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - visitId: visit identifier to check.
     *
     * Returns:
     * - true if at least one cafe_order exists for this visit.
     * - false otherwise or on error.
     */
    private static boolean visitHasOrders(Connection conn, int visitId) {
        String sql = "SELECT 1 FROM cafe_order WHERE visit_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking for orders on visit:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Name: readLine
     * Purpose: Reads a complete line of text from the console.
     *
     * Preconditions:
     * - Scanner is open and reading from System.in.
     *
     * Postconditions:
     * - No database modifications.
     *
     * Parameters:
     * - scanner: Scanner instance.
     * - prompt: the message shown before reading input.
     *
     * Returns:
     * - The text entered by the user.
     */
    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }


    /**
     * Name: readInt
     * Purpose: Reads an integer from the console, retrying until the input is valid.
     *
     * Preconditions:
     * - scanner is open and reading from System.in.
     *
     * Postconditions:
     * - No database modifications.
     *
     * Parameters:
     * - scanner: Scanner used for reading user input.
     * - prompt: message printed before reading the value.
     *
     * Returns:
     * - The integer entered by the user.
     */
    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    /**
     * Name: getNextVisitId
     * Purpose: Fetches the next value from the seq_visit_id sequence.
     *
     * Preconditions:
     * - The seq_visit_id sequence exists and is accessible.
     *
     * Postconditions:
     * - No table data is modified (sequence state advances).
     *
     * Parameters:
     * - conn: open JDBC connection.
     *
     * Returns:
     * - The next visit_id, or null if retrieval fails.
     */
    private static Integer getNextVisitId(Connection conn) {
        String sql = "SELECT seq_visit_id.NEXTVAL AS next_id FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching next visit id from seq_visit_id:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Name: visitForReservationExists
     * Purpose: Checks whether a visit already exists for the given reservation_id.
     *
     * Preconditions:
     * - visit table exists.
     *
     * Postconditions:
     * - No database modifications.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - reservationId: reservation identifier to check.
     *
     * Returns:
     * - true if a visit row exists for the reservation.
     * - false otherwise or on error.
     */
    private static boolean visitForReservationExists(Connection conn, int reservationId) {
        String sql = "SELECT visit_id FROM visit WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking for existing visit by reservation_id:");
            e.printStackTrace();
            return false;
        }
    }
}
