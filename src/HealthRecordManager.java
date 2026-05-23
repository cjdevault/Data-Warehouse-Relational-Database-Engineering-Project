import java.sql.*;
import java.util.Scanner;

public class HealthRecordManager {

    private final Connection conn;
    private final Scanner scanner;

    public HealthRecordManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void addHealthRecord() {
        try {
            System.out.println("\n--- Add Health Record ---");

            System.out.print("Pet ID: ");
            int petId = Integer.parseInt(scanner.nextLine());

            System.out.print("Staff ID (vet/handler): ");
            int staffId = Integer.parseInt(scanner.nextLine());

            System.out.println("Record type options: vaccination, checkup, grooming, feeding, medication, other");
            System.out.print("Record type: ");
            String recordType = scanner.nextLine().trim().toLowerCase();

            System.out.print("Next due date (YYYY-MM-DD) or blank if N/A: ");
            String nextDueStr = scanner.nextLine().trim();

            System.out.print("Status (e.g., normal, void, corrected): ");
            String status = scanner.nextLine().trim();

            System.out.print("Notes (optional, you may leav this blank): ");
            String notes = scanner.nextLine();
            if (notes.isBlank()) notes = null;

            String sql = """
                INSERT INTO health_record
                    (health_record_id, pet_id, staff_id, record_date,
                     record_type, next_due_date, status, notes)
                VALUES
                    (seq_health_record_id.NEXTVAL, ?, ?, SYSDATE,
                     ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, petId);
                ps.setInt(2, staffId);
                ps.setString(3, recordType);

                if (nextDueStr.isBlank()) {
                    ps.setNull(4, Types.DATE);
                } else {
                    ps.setDate(4, Date.valueOf(nextDueStr));
                }

                ps.setString(5, status);

                if (notes == null) {
                    ps.setNull(6, Types.VARCHAR);
                } else {
                    ps.setString(6, notes);
                }

                int rows = ps.executeUpdate();
                System.out.println("Inserted " + rows + " health record(s).");
            }

        } catch (SQLException e) {
            System.out.println("Error adding health record:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input. Health record not added.");
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    public void listHealthRecordsForPet() {
        System.out.println("\n--- Health Records For Pet ---");
        try {
            System.out.print("Pet ID: ");
            int petId = Integer.parseInt(scanner.nextLine());

            String sql = """
                SELECT hr.health_record_id,
                       hr.record_date,
                       hr.record_type,
                       hr.next_due_date,
                       hr.status,
                       hr.notes,
                       s.name AS staff_name
                FROM health_record hr
                JOIN staff s ON hr.staff_id = s.staff_id
                WHERE hr.pet_id = ?
                ORDER BY hr.record_date DESC, hr.health_record_id DESC
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, petId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        int id = rs.getInt("health_record_id");
                        Date recDate = rs.getDate("record_date");
                        String type = rs.getString("record_type");
                        Date nextDue = rs.getDate("next_due_date");
                        String status = rs.getString("status");
                        String notes = rs.getString("notes");
                        String staffName = rs.getString("staff_name");

                        System.out.println("Record ID: " + id);
                        System.out.println("  Date: " + recDate);
                        System.out.println("  Type: " + type);
                        System.out.println("  Staff: " + staffName);
                        System.out.println("  Status: " + status);
                        System.out.println("  Next due: " + (nextDue == null ? "-" : nextDue));
                        System.out.println("  Notes: " + (notes == null ? "-" : notes));
                        System.out.println();
                    }

                    if (!any) {
                        System.out.println("No health records found for pet_id = " + petId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error listing health records:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid pet ID.");
        }
    }

    public void updateHealthRecordStatus() {
        System.out.println("\n--- Update Health Record Status ---");
        try {
            System.out.print("Health record ID: ");
            int recordId = Integer.parseInt(scanner.nextLine());

            System.out.print("New status (e.g., normal, void, corrected): ");
            String newStatus = scanner.nextLine().trim();

            System.out.print("New notes (leave blank to keep existing): ");
            String newNotes = scanner.nextLine();

            String sql;
            boolean changeNotes = !newNotes.isBlank();

            if (changeNotes) {
                sql = "UPDATE health_record SET status = ?, notes = ? WHERE health_record_id = ?";
            } else {
                sql = "UPDATE health_record SET status = ? WHERE health_record_id = ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);

                if (changeNotes) {
                    ps.setString(2, newNotes);
                    ps.setInt(3, recordId);
                } else {
                    ps.setInt(2, recordId);
                }

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("No health record found with that ID.");
                } else {
                    System.out.println("Health record updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating health record:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid health_record_id.");
        }
    }
}

