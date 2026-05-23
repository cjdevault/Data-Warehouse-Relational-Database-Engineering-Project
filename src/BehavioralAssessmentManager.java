import java.sql.*;
import java.util.Scanner;

public class BehavioralAssessmentManager {

    private final Connection conn;
    private final Scanner scanner;

    public BehavioralAssessmentManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void addAssessment() {
        try {
            System.out.println("\n--- Add Behavioral Assessment ---");

            System.out.print("Pet ID: ");
            int petId = Integer.parseInt(scanner.nextLine());

            System.out.print("Staff ID (handler/vet/etc.): ");
            int staffId = Integer.parseInt(scanner.nextLine());

            System.out.print("Assessment result (approved/conditional/not_approved/pending): ");
            String result = scanner.nextLine().trim().toLowerCase();

            System.out.print("Notes (optional, can be blank): ");
            String notes = scanner.nextLine();
            if (notes.isBlank()) notes = null;

            String sql = """
                INSERT INTO behavioral_assessment
                    (assessment_id, pet_id, staff_id, assessment_date,
                     assessment_result, notes)
                VALUES
                    (seq_behavioral_assessment_id.NEXTVAL, ?, ?, SYSDATE,
                     ?, ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, petId);
                ps.setInt(2, staffId);
                ps.setString(3, result);

                if (notes == null) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, notes);
                }

                int rows = ps.executeUpdate();
                System.out.println("Inserted " + rows + " behavioral assessment(s).");
            }

        } catch (SQLException e) {
            System.out.println("Error adding behavioral assessment:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input. Assessment not added.");
        }
    }

    public void listAssessmentsForPet() {
        System.out.println("\n--- Behavioral Assessments For Pet ---");
        try {
            System.out.print("Pet ID: ");
            int petId = Integer.parseInt(scanner.nextLine());

            String sql = """
                SELECT ba.assessment_id,
                       ba.assessment_date,
                       ba.assessment_result,
                       ba.notes,
                       s.name AS staff_name
                FROM behavioral_assessment ba
                JOIN staff s ON ba.staff_id = s.staff_id
                WHERE ba.pet_id = ?
                ORDER BY ba.assessment_date DESC, ba.assessment_id DESC
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, petId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        int id = rs.getInt("assessment_id");
                        Date date = rs.getDate("assessment_date");
                        String result = rs.getString("assessment_result");
                        String notes = rs.getString("notes");
                        String staffName = rs.getString("staff_name");

                        System.out.println("Assessment ID: " + id);
                        System.out.println("  Date: " + date);
                        System.out.println("  Staff: " + staffName);
                        System.out.println("  Result: " + result);
                        System.out.println("  Notes: " + (notes == null ? "-" : notes));
                        System.out.println();
                    }

                    if (!any) {
                        System.out.println("No behavioral assessments found for pet_id = " + petId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error listing behavioral assessments:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid pet ID.");
        }
    }

    public void updateAssessmentResult() {
        System.out.println("\n--- Update Behavioral Assessment ---");
        try {
            System.out.print("Assessment ID: ");
            int assessmentId = Integer.parseInt(scanner.nextLine());

            System.out.print("New result (approved/conditional/not_approved/pending): ");
            String newResult = scanner.nextLine().trim().toLowerCase();

            System.out.print("New notes (leave blank to keep existing): ");
            String newNotes = scanner.nextLine();

            String sql;
            boolean changeNotes = !newNotes.isBlank();

            if (changeNotes) {
                sql = "UPDATE behavioral_assessment SET assessment_result = ?, notes = ? WHERE assessment_id = ?";
            } else {
                sql = "UPDATE behavioral_assessment SET assessment_result = ? WHERE assessment_id = ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newResult);

                if (changeNotes) {
                    ps.setString(2, newNotes);
                    ps.setInt(3, assessmentId);
                } else {
                    ps.setInt(2, assessmentId);
                }

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("No assessment found with that ID.");
                } else {
                    System.out.println("Behavioral assessment updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating behavioral assessment:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid assessment_id.");
        }
    }
}

