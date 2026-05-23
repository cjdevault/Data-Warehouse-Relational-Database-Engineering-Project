import java.sql.*;
import java.util.Scanner;

public class AdoptionManager {

    private final Connection conn;
    private final Scanner scanner;

    public AdoptionManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void submitApplication() {
        try {
            System.out.println("\n--- Submit Adoption Application ---");

            System.out.print("Pet ID: ");
            int petId = Integer.parseInt(scanner.nextLine());

            System.out.print("Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            System.out.print("Coordinator staff ID: ");
            int coordId = Integer.parseInt(scanner.nextLine());

            System.out.print("Application date (YYYY-MM-DD, blank = today): ");
            String dateStr = scanner.nextLine().trim();

            String sql = """
                INSERT INTO adoption_application
                    (application_id, pet_id, member_id, application_date,
                     status, coordinator_id)
                VALUES
                    (seq_adoption_application_id.NEXTVAL, ?, ?, 
                     COALESCE(TO_DATE(?, 'YYYY-MM-DD'), SYSDATE),
                     'pending', ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, petId);
                ps.setInt(2, memberId);

                if (dateStr.isBlank()) {
                    ps.setNull(3, Types.VARCHAR); 
                } else {
                    ps.setString(3, dateStr);
                }

                ps.setInt(4, coordId);

                int rows = ps.executeUpdate();
                System.out.println("Inserted " + rows + " adoption application(s). (status = pending)");
            }

        } catch (SQLException e) {
            System.out.println("Error submitting adoption application:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input. Application not submitted.");
        }
    }

    public void listApplicationsForPet() {
        System.out.println("\n--- Adoption Applications For Pet ---");
        try {
            System.out.print("Pet ID: ");
            int petId = Integer.parseInt(scanner.nextLine());

            String sql = """
                SELECT aa.application_id,
                       aa.application_date,
                       aa.status,
                       m.member_id,
                       m.name AS member_name,
                       s.name AS coord_name,
                       ar.adoption_record_id,
                       ar.adoption_date
                FROM adoption_application aa
                JOIN member m ON aa.member_id = m.member_id
                JOIN staff s ON aa.coordinator_id = s.staff_id
                LEFT JOIN adoption_record ar ON ar.application_id = aa.application_id
                WHERE aa.pet_id = ?
                ORDER BY aa.application_date DESC, aa.application_id DESC
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, petId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        int appId = rs.getInt("application_id");
                        Date appDate = rs.getDate("application_date");
                        String status = rs.getString("status");
                        int memberId = rs.getInt("member_id");
                        String memberName = rs.getString("member_name");
                        String coordName = rs.getString("coord_name");
                        
			Integer adoptionRecordId = null;
			Object arObj = rs.getObject("adoption_record_id");
			if (arObj != null) {
				adoptionRecordId = ((Number) arObj).intValue();
			}
			Date adoptionDate = rs.getDate("adoption_date");

                        System.out.println("Application ID: " + appId);
                        System.out.println("  Application date: " + appDate);
                        System.out.println("  Status: " + status);
                        System.out.println("  Member: " + memberName + " (ID " + memberId + ")");
                        System.out.println("  Coordinator: " + coordName);
                        if (adoptionRecordId != null) {
                            System.out.println("  Adoption record ID: " + adoptionRecordId +
                                               " (date: " + adoptionDate + ")");
                        } else {
                            System.out.println("  No adoption record yet");
                        }
                        System.out.println();
                    }

                    if (!any) {
                        System.out.println("No applications found for pet_id = " + petId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error listing adoption applications:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid pet ID.");
        }
    }

    public void listApplicationsForMember() {
        System.out.println("\n--- Adoption Applications For Member ---");
        try {
            System.out.print("Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            String sql = """
                SELECT aa.application_id,
                       aa.application_date,
                       aa.status,
                       p.pet_id,
                       p.name AS pet_name,
                       s.name AS coord_name,
                       ar.adoption_record_id,
                       ar.adoption_date
                FROM adoption_application aa
                JOIN pet p ON aa.pet_id = p.pet_id
                JOIN staff s ON aa.coordinator_id = s.staff_id
                LEFT JOIN adoption_record ar ON ar.application_id = aa.application_id
                WHERE aa.member_id = ?
                ORDER BY aa.application_date DESC, aa.application_id DESC
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memberId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        int appId = rs.getInt("application_id");
                        Date appDate = rs.getDate("application_date");
                        String status = rs.getString("status");
                        int petId = rs.getInt("pet_id");
                        String petName = rs.getString("pet_name");
                        String coordName = rs.getString("coord_name");
                        Integer adoptionRecordId = null;
			Object arObj = rs.getObject("adoption_record_id");
			if (arObj != null) {
				adoptionRecordId = ((Number) arObj).intValue();
			}
                        Date adoptionDate = rs.getDate("adoption_date");

                        System.out.println("Application ID: " + appId);
                        System.out.println("  Application date: " + appDate);
                        System.out.println("  Status: " + status);
                        System.out.println("  Pet: " + petName + " (ID " + petId + ")");
                        System.out.println("  Coordinator: " + coordName);
                        if (adoptionRecordId != null) {
                            System.out.println("  Adoption record ID: " + adoptionRecordId +
                                               " (date: " + adoptionDate + ")");
                        } else {
                            System.out.println("  No adoption record yet");
                        }
                        System.out.println();
                    }
                    if (!any) {
                        System.out.println("No applications found for member_id = " + memberId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error listing adoption applications for member:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid member ID.");
        }
    }

    public void updateApplicationStatus() {
        System.out.println("\n--- Update Adoption Application Status ---");
        try {
            System.out.print("Application ID: ");
            int appId = Integer.parseInt(scanner.nextLine());

            System.out.print("New status (pending/approved/rejected/withdrawn): ");
            String newStatus = scanner.nextLine().trim().toLowerCase();

            String sql = "UPDATE adoption_application SET status = ? WHERE application_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, appId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("No application found with that ID.");
                } else {
                    System.out.println("Application status updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating adoption application status:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid application ID.");
        }
    }

    public void finalizeAdoption() {
        System.out.println("\n--- Finalize Adoption ---");
        try {
            System.out.print("Application ID (should be approved): ");
            int appId = Integer.parseInt(scanner.nextLine());

            String queryApp = """
                SELECT pet_id, status
                FROM adoption_application
                WHERE application_id = ?
                """;

            int petId;
            String status;

            try (PreparedStatement ps = conn.prepareStatement(queryApp)) {
                ps.setInt(1, appId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No application found with that ID.");
                        return;
                    }
                    petId = rs.getInt("pet_id");
                    status = rs.getString("status");
                }
            }

            if (!"approved".equalsIgnoreCase(status)) {
                System.out.println("Warning: application is not approved (status = " + status + ").");
                System.out.print("Finalize anyway? (y/n): ");
                String ans = scanner.nextLine().trim().toLowerCase();
                if (!ans.equals("y")) {
                    System.out.println("Aborted.");
                    return;
                }
            }

            String checkRecord = "SELECT adoption_record_id FROM adoption_record WHERE application_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkRecord)) {
                ps.setInt(1, appId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("An adoption record already exists for this application (ID = "
                                           + rs.getInt("adoption_record_id") + ").");
                        return;
                    }
                }
            }

            System.out.print("Adoption fee: ");
            double fee = Double.parseDouble(scanner.nextLine());

            System.out.print("Adoption date (YYYY-MM-DD, blank = today): ");
            String adopDateStr = scanner.nextLine().trim();

            System.out.print("Follow-up date (YYYY-MM-DD, blank = none): ");
            String followStr = scanner.nextLine().trim();

            System.out.print("Notes (optional): ");
            String notes = scanner.nextLine();
            if (notes.isBlank()) notes = null;

            String insertRecord = """
                INSERT INTO adoption_record
                    (adoption_record_id, application_id, adoption_date,
                     adoption_fee, follow_up_date, notes)
                VALUES
                    (seq_adoption_record_id.NEXTVAL, ?, 
                     COALESCE(TO_DATE(?, 'YYYY-MM-DD'), SYSDATE),
                     ?, ?, ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(insertRecord)) {
                ps.setInt(1, appId);

                if (adopDateStr.isBlank()) {
                    ps.setNull(2, Types.VARCHAR); 
                } else {
                    ps.setString(2, adopDateStr);
                }

                ps.setDouble(3, fee);

                if (followStr.isBlank()) {
                    ps.setNull(4, Types.DATE);
                } else {
                    ps.setDate(4, Date.valueOf(followStr));
                }

                if (notes == null) {
                    ps.setNull(5, Types.VARCHAR);
                } else {
                    ps.setString(5, notes);
                }

                int rows = ps.executeUpdate();
                System.out.println("Inserted " + rows + " adoption record(s).");
            }

            
            String updatePet = "UPDATE pet SET current_status = 'adopted' WHERE pet_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updatePet)) {
                ps.setInt(1, petId);
                int rows = ps.executeUpdate();
                System.out.println("Updated pet (ID " + petId + ") status to 'adopted'.");
            }

        } catch (SQLException e) {
            System.out.println("Error finalizing adoption:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input.");
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    public void deleteApplication() {
    	System.out.println("\n--- Delete Adoption Application ---");
   	 try {
       	    System.out.print("Application ID to delete: ");
            int appId = Integer.parseInt(scanner.nextLine());

            String getStatusSql = """
              SELECT status
              FROM adoption_application
              WHERE application_id = ?
              """;

            String status;
            try (PreparedStatement ps = conn.prepareStatement(getStatusSql)) {
                ps.setInt(1, appId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No application found with ID " + appId);
                        return;
                    }
                    status = rs.getString("status");
                }
            }

            if (!"pending".equalsIgnoreCase(status)) {
                System.out.println("Cannot delete application with status '" + status +
                               "'. Mark it as 'withdrawn' instead.");
                return;
            }

            String checkRecordSql = """
                SELECT COUNT(*) AS cnt
                FROM adoption_record
                WHERE application_id = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(checkRecordSql)) {
                ps.setInt(1, appId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    int count = rs.getInt("cnt");
                    if (count > 0) {
                        System.out.println("Cannot delete application: an adoption record already exists.");
                        return;
                    }
                }
            }

            String deleteSql = "DELETE FROM adoption_application WHERE application_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, appId);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("No application deleted (unexpected).");
                } else {
                    System.out.println("Adoption application " + appId + " deleted.");
                }
            }

        } catch (NumberFormatException nfe) {
            System.out.println("Invalid application ID.");
        } catch (SQLException e) {
            System.out.println("Error deleting adoption application:");
            e.printStackTrace();
        }
    }

}

