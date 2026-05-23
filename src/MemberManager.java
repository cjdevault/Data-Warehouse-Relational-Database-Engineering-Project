/* Teacher & TAs: Prof. Lester I. McCann, James Shen, Utkarsh Upadhyay
 * Assignment: Prog4
 * Course: CSc 460
 * Due: December 8, 2025
 *
 * Purpose: This file contains the implementation of the MemberManager class.
 * It provides methods for adding, looking up, updating, and viewing member information.
 */
import java.sql.*;
import java.util.Scanner;

public class MemberManager {

    private final Connection conn;
    private final Scanner scanner;

    public MemberManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    /*
     * Purpose: Adds a new member to the database.
     * Preconditions: The member information must be valid.
     * Postconditions: The member is added to the database.
     */ 
    public void addMember() {
        try {
            System.out.println("\n--- Register New Member ---");

            System.out.print("Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Phone: ");
            String phone = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Date of Birth (YYYY-MM-DD): ");
            String dobStr = scanner.nextLine().trim();
            Date dateOfBirth = Date.valueOf(dobStr);

            System.out.print("Emergency Contact Name (or leave blank): ");
            String emergencyName = scanner.nextLine().trim();
            if (emergencyName.isBlank()) emergencyName = null;

            System.out.print("Emergency Contact Phone (or leave blank): ");
            String emergencyPhone = scanner.nextLine().trim();
            if (emergencyPhone.isBlank()) emergencyPhone = null;

            System.out.print("Membership Tier ID: ");
            int tierId = Integer.parseInt(scanner.nextLine().trim());

            String sql = """
                INSERT INTO member
                    (member_id, name, phone, email, date_of_birth,
                     emergency_contact_name, emergency_contact_phone, membership_tier_id, registration_date)
                VALUES
                    (seq_member_id.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, SYSDATE)
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, phone);
                ps.setString(3, email);
                ps.setDate(4, dateOfBirth);

                if (emergencyName == null) {
                    ps.setNull(5, Types.VARCHAR);
                } else {
                    ps.setString(5, emergencyName);
                }

                if (emergencyPhone == null) {
                    ps.setNull(6, Types.VARCHAR);
                } else {
                    ps.setString(6, emergencyPhone);
                }

                ps.setInt(7, tierId);

                int rows = ps.executeUpdate();
                System.out.println("Inserted " + rows + " member(s).");
            }

        } catch (SQLException e) {
            System.out.println("Error adding member:");
            if (e.getErrorCode() == 1) { // Unique constraint violation
                System.out.println("Email already exists. Please use a different email.");
            } else {
                e.printStackTrace();
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input. Member not added.");
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    /*
     * Purpose: Looks up a member by ID, email, or phone.
     * Preconditions: The member must exist in the database.
     * Postconditions: The member information is displayed.
     */ 
    public void lookupMember() {
        System.out.println("\n--- Look Up Member ---");
        System.out.println("Search by: 1) Member ID, 2) Email, 3) Phone");
        System.out.print("Enter choice (1-3): ");
        String choice = scanner.nextLine().trim();

        try {
            String sql;
            PreparedStatement ps;

            switch (choice) {
                case "1":
                    System.out.print("Enter Member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine().trim());
                    sql = "SELECT * FROM member WHERE member_id = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, memberId);
                    break;

                case "2":
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine().trim();
                    sql = "SELECT * FROM member WHERE email = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, email);
                    break;

                case "3":
                    System.out.print("Enter Phone: ");
                    String phone = scanner.nextLine().trim();
                    sql = "SELECT * FROM member WHERE phone = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, phone);
                    break;

                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            try (ps) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("\n--- Member Information ---");
                    System.out.println("Member ID: " + rs.getInt("member_id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Phone: " + rs.getString("phone"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Date of Birth: " + rs.getDate("date_of_birth"));
                    System.out.println("Emergency Contact Name: " + 
                        (rs.getString("emergency_contact_name") == null ? "-" : rs.getString("emergency_contact_name")));
                    System.out.println("Emergency Contact Phone: " + 
                        (rs.getString("emergency_contact_phone") == null ? "-" : rs.getString("emergency_contact_phone")));
                    System.out.println("Membership Tier ID: " + rs.getInt("membership_tier_id"));
                    System.out.println("Registration Date: " + rs.getDate("registration_date"));
                } else {
                    System.out.println("No member found with the provided information.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error looking up member:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid input format.");
        }
    }

    /*
     * Purpose: Updates the information for a member.
     * Preconditions: The member must exist in the database.
     * Postconditions: The member information is updated.
     */ 
    public void updateMember() {
        try {
            System.out.println("\n--- Update Member Information ---");
            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine().trim());

            // First verify member exists
            String checkSql = "SELECT member_id FROM member WHERE member_id = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, memberId);
                ResultSet rs = checkPs.executeQuery();
                if (!rs.next()) {
                    System.out.println("No member found with ID: " + memberId);
                    return;
                }
            }

            System.out.println("Enter new information (leave blank to keep existing):");

            System.out.print("Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Phone: ");
            String phone = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Membership Tier ID: ");
            String tierIdStr = scanner.nextLine().trim();

            // Build dynamic UPDATE statement
            StringBuilder sqlBuilder = new StringBuilder("UPDATE member SET ");
            boolean hasUpdate = false;

            if (!name.isBlank()) {
                sqlBuilder.append("name = ?");
                hasUpdate = true;
            }
            if (!phone.isBlank()) {
                if (hasUpdate) sqlBuilder.append(", ");
                sqlBuilder.append("phone = ?");
                hasUpdate = true;
            }
            if (!email.isBlank()) {
                if (hasUpdate) sqlBuilder.append(", ");
                sqlBuilder.append("email = ?");
                hasUpdate = true;
            }
            if (!tierIdStr.isBlank()) {
                if (hasUpdate) sqlBuilder.append(", ");
                sqlBuilder.append("membership_tier_id = ?");
                hasUpdate = true;
            }

            if (!hasUpdate) {
                System.out.println("No updates provided.");
                return;
            }

            sqlBuilder.append(" WHERE member_id = ?");

            try (PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
                int paramIndex = 1;

                if (!name.isBlank()) {
                    ps.setString(paramIndex++, name);
                }
                if (!phone.isBlank()) {
                    ps.setString(paramIndex++, phone);
                }
                if (!email.isBlank()) {
                    ps.setString(paramIndex++, email);
                }
                if (!tierIdStr.isBlank()) {
                    ps.setInt(paramIndex++, Integer.parseInt(tierIdStr));
                }

                ps.setInt(paramIndex, memberId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("No member found with that ID.");
                } else {
                    System.out.println("Member information updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating member:");
            if (e.getErrorCode() == 1) { // Unique constraint violation
                System.out.println("Email already exists. Please use a different email.");
            } else {
                e.printStackTrace();
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input.");
        }
    }

    /*
     * Purpose: Views the visit history for a member.
     * Preconditions: The member must exist in the database.
     * Postconditions: The visit history is displayed.
     */ 
    public void viewMemberVisitHistory() {
        System.out.println("\n--- Member Visit History ---");
        try {
            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine().trim());

            String sql = """
                SELECT r.reservation_id, r.reservation_date, r.reservation_time,
                       r.duration_hours, r.status, ro.room_name, ro.room_type,
                       v.check_in_time, v.check_out_time,
                       COUNT(DISTINCT co.order_id) AS order_count,
                       NVL(SUM(co.final_price), 0) AS total_spent,
                       v.current_membership_tier
                FROM reservation r
                JOIN room ro ON r.room_id = ro.room_id
                LEFT JOIN visit v ON r.reservation_id = v.reservation_id
                LEFT JOIN cafe_order co ON v.visit_id = co.visit_id
                WHERE r.member_id = ?
                GROUP BY r.reservation_id, r.reservation_date, r.reservation_time,
                         r.duration_hours, r.status, ro.room_name, ro.room_type,
                         v.check_in_time, v.check_out_time, v.current_membership_tier
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
                        System.out.println("Room: " + rs.getString("room_name") + " (" + rs.getString("room_type") + ")");
                        System.out.println("Check-in: " + (rs.getTimestamp("check_in_time") == null ? "Not checked in" : rs.getTimestamp("check_in_time")));
                        System.out.println("Check-out: " + (rs.getTimestamp("check_out_time") == null ? "Not checked out" : rs.getTimestamp("check_out_time")));
                        System.out.println("Membership Tier: " + (rs.getString("current_membership_tier") == null ? "N/A" : rs.getString("current_membership_tier")));
                        System.out.println("Orders Placed: " + rs.getInt("order_count"));
                        System.out.println("Total Spent: $" + String.format("%.2f", rs.getDouble("total_spent")));
                    }

                    if (!any) {
                        System.out.println("No visit history found for member_id = " + memberId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error viewing visit history:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid member ID.");
        }
    }

    /*
     * Purpose: Views the adoption applications for a member.
     * Preconditions: The member must exist in the database.
     * Postconditions: The adoption applications are displayed.
     */ 
    public void viewMemberAdoptionApplications() {
        System.out.println("\n--- Member Adoption Applications ---");
        try {
            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine().trim());

            String sql = """
                SELECT aa.application_id, aa.application_date, aa.status,
                       p.pet_id, p.name AS pet_name, p.species, p.breed,
                       s.name AS coordinator_name, s.role AS coordinator_role
                FROM adoption_application aa
                JOIN pet p ON aa.pet_id = p.pet_id
                JOIN staff s ON aa.coordinator_id = s.staff_id
                WHERE aa.member_id = ?
                ORDER BY aa.application_date DESC
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memberId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        System.out.println("\n--- Application #" + rs.getInt("application_id") + " ---");
                        System.out.println("Application Date: " + rs.getDate("application_date"));
                        System.out.println("Status: " + rs.getString("status"));
                        System.out.println("Pet ID: " + rs.getInt("pet_id"));
                        System.out.println("Pet Name: " + rs.getString("pet_name"));
                        System.out.println("Species: " + rs.getString("species"));
                        System.out.println("Breed: " + (rs.getString("breed") == null ? "-" : rs.getString("breed")));
                        System.out.println("Coordinator: " + rs.getString("coordinator_name") + " (" + rs.getString("coordinator_role") + ")");
                    }

                    if (!any) {
                        System.out.println("No adoption applications found for member_id = " + memberId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error viewing adoption applications:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid member ID.");
        }
    }

    /*
     * Purpose: Deletes a member from the database.
     * Preconditions: The member must have no active reservations, pending adoption applications, or unpaid food orders.
     * Postconditions: The member is deleted from the database.
     */ 
    public void deleteMember() {
        try {
            System.out.println("\n--- Delete Member ---");
            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine().trim());
    
            // First verify member exists
            String checkMemberSql = "SELECT member_id, name FROM member WHERE member_id = ?";
            String memberName = null;
            try (PreparedStatement checkPs = conn.prepareStatement(checkMemberSql)) {
                checkPs.setInt(1, memberId);
                ResultSet rs = checkPs.executeQuery();
                if (!rs.next()) {
                    System.out.println("No member found with ID: " + memberId);
                    return;
                }
                memberName = rs.getString("name");
            }
    
            // Check for active reservations
            String activeReservationsSql = """
                SELECT COUNT(*) AS reservation_count
                FROM reservation
                WHERE member_id = ? AND status = 'confirmed'
                """;
            
            int activeReservations = 0;
            try (PreparedStatement ps = conn.prepareStatement(activeReservationsSql)) {
                ps.setInt(1, memberId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    activeReservations = rs.getInt("reservation_count");
                }
            }
    
            // Check for pending adoption applications
            String pendingApplicationsSql = """
                SELECT COUNT(*) AS application_count
                FROM adoption_application
                WHERE member_id = ? AND status = 'pending'
                """;
            
            int pendingApplications = 0;
            try (PreparedStatement ps = conn.prepareStatement(pendingApplicationsSql)) {
                ps.setInt(1, memberId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    pendingApplications = rs.getInt("application_count");
                }
            }
    
            // Check for unpaid food orders
            String unpaidOrdersSql = """
                SELECT COUNT(*) AS order_count
                FROM cafe_order co
                JOIN visit v ON co.visit_id = v.visit_id
                JOIN reservation r ON v.reservation_id = r.reservation_id
                WHERE r.member_id = ? AND co.payment_status = 'pending'
                """;
            
            int unpaidOrders = 0;
            try (PreparedStatement ps = conn.prepareStatement(unpaidOrdersSql)) {
                ps.setInt(1, memberId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    unpaidOrders = rs.getInt("order_count");
                }
            }
    
            // Business rule validation
            if (activeReservations > 0) {
                System.out.println("Cannot delete member. Active reservations found: " + activeReservations);
                System.out.println("Please cancel all active reservations before deleting the member.");
                return;
            }
    
            if (pendingApplications > 0) {
                System.out.println("Cannot delete member. Pending adoption applications found: " + pendingApplications);
                System.out.println("Please resolve all pending applications before deleting the member.");
                return;
            }
    
            if (unpaidOrders > 0) {
                System.out.println("Cannot delete member. Unpaid food orders found: " + unpaidOrders);
                System.out.println("Please resolve all unpaid orders before deleting the member.");
                return;
            }
    
            // Confirm deletion
            System.out.println("\nMember: " + memberName + " (ID: " + memberId + ")");
            System.out.print("Are you sure you want to delete this member? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (!"yes".equals(confirmation) && !"y".equals(confirmation)) {
                System.out.println("Deletion cancelled.");
                return;
            }
    
            // Delete related records in proper order (respecting foreign key constraints)
            // Note: Order matters due to foreign key dependencies
            
            conn.setAutoCommit(false); // Start transaction
            
            try {
                // 1. Delete order items (depends on cafe_order)
                String deleteOrderItemsSql = """
                    DELETE FROM order_item
                    WHERE order_id IN (
                        SELECT co.order_id
                        FROM cafe_order co
                        JOIN visit v ON co.visit_id = v.visit_id
                        JOIN reservation r ON v.reservation_id = r.reservation_id
                        WHERE r.member_id = ?
                    )
                    """;
                try (PreparedStatement ps = conn.prepareStatement(deleteOrderItemsSql)) {
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }
    
                // 2. Delete cafe orders (depends on visit)
                String deleteOrdersSql = """
                    DELETE FROM cafe_order
                    WHERE visit_id IN (
                        SELECT v.visit_id
                        FROM visit v
                        JOIN reservation r ON v.reservation_id = r.reservation_id
                        WHERE r.member_id = ?
                    )
                    """;
                try (PreparedStatement ps = conn.prepareStatement(deleteOrdersSql)) {
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }
    
                // 3. Delete visits (depends on reservation)
                String deleteVisitsSql = """
                    DELETE FROM visit
                    WHERE reservation_id IN (
                        SELECT reservation_id
                        FROM reservation
                        WHERE member_id = ?
                    )
                    """;
                try (PreparedStatement ps = conn.prepareStatement(deleteVisitsSql)) {
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }
    
                // 4. Delete reservations (depends on member)
                String deleteReservationsSql = "DELETE FROM reservation WHERE member_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteReservationsSql)) {
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }
    
                // 5. Delete adoption records (depends on adoption_application, must delete before adoption_application)
                String deleteAdoptionRecordsSql = """
                    DELETE FROM adoption_record
                    WHERE application_id IN (
                        SELECT application_id
                        FROM adoption_application
                        WHERE member_id = ?
                    )
                    """;
                try (PreparedStatement ps = conn.prepareStatement(deleteAdoptionRecordsSql)) {
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }
    
                // 6. Delete adoption applications (depends on member)
                String deleteApplicationsSql = "DELETE FROM adoption_application WHERE member_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteApplicationsSql)) {
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }
    
                // 7. Delete event registrations
                String deleteEventRegistrationsSql = "DELETE FROM event_registration WHERE member_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteEventRegistrationsSql)) {
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }
    
                // 8. Delete membership payments
                String deletePaymentsSql = "DELETE FROM membership_payment WHERE member_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deletePaymentsSql)) {
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }
    
                // 9. Finally, delete the member
                String deleteMemberSql = "DELETE FROM member WHERE member_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteMemberSql)) {
                    ps.setInt(1, memberId);
                    int rows = ps.executeUpdate();
                    
                    if (rows == 0) {
                        System.out.println("No member found with that ID.");
                        conn.rollback();
                    } else {
                        conn.commit();
                        System.out.println("Member and all related records deleted successfully.");
                    }
                }
    
            } catch (SQLException e) {
                conn.rollback();
                throw e; // Re-throw to be caught by outer catch
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit
            }
    
        } catch (SQLException e) {
            System.out.println("Error deleting member:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid member ID.");
        }
    }

}

