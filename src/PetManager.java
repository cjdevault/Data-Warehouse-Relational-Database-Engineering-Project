import java.sql.*;
import java.util.Scanner;

public class PetManager {

    private final Connection conn;
    private final Scanner scanner;

    public PetManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void addPet() {
        try {
            System.out.println("\n--- Add New Pet ---");

            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Species (e.g. cat, dog): ");
            String species = scanner.nextLine();

            System.out.print("Breed (skip by leaving blank): ");
            String breed = scanner.nextLine();
            if (breed.isBlank()) breed = null;

            System.out.print("Age (years, or leave blank): ");
            String ageStr = scanner.nextLine();
            Integer age = ageStr.isBlank() ? null : Integer.parseInt(ageStr);

            System.out.print("Room ID (or leave blank): ");
            String roomStr = scanner.nextLine();
            Integer roomId = roomStr.isBlank() ? null : Integer.parseInt(roomStr);

            System.out.print("Temperament (or leave blank): ");
            String temperament = scanner.nextLine();
            if (temperament.isBlank()) temperament = null;

            System.out.print("Special needs (or leave blank): ");
            String specialNeeds = scanner.nextLine();
            if (specialNeeds.isBlank()) specialNeeds = null;

            System.out.print("Current status (available_adoption/pending/adopted/resident/sick/deceased): ");
            String status = scanner.nextLine();

            String sql = """
                INSERT INTO pet
                    (pet_id, name, species, breed, age, date_of_arrival,
                     room_id, temperament, special_needs, current_status)
                VALUES
                    (seq_pet_id.NEXTVAL, ?, ?, ?, ?, SYSDATE,
                     ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, species);

                if (breed == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, breed);
                if (age == null) ps.setNull(4, Types.NUMERIC); else ps.setInt(4, age);
                if (roomId == null) ps.setNull(5, Types.NUMERIC); else ps.setInt(5, roomId);
                if (temperament == null) ps.setNull(6, Types.VARCHAR); else ps.setString(6, temperament);
                if (specialNeeds == null) ps.setNull(7, Types.VARCHAR); else ps.setString(7, specialNeeds);

                ps.setString(8, status);

                int rows = ps.executeUpdate();
                System.out.println("Inserted " + rows + " pet(s).");
            }

        } catch (SQLException e) {
            System.out.println("Error adding pet:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid numeric input. Pet not added.");
        }
    }

    public void deletePet() {
        System.out.println("\n--- Delete Pet ---");
        try {
            System.out.print("Pet ID to delete: ");
            int petId = Integer.parseInt(scanner.nextLine());

            String getPetSql = """
                SELECT current_status
                FROM pet
                WHERE pet_id = ?
                """;

            String status;
            try (PreparedStatement ps = conn.prepareStatement(getPetSql)) {
                ps.setInt(1, petId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No pet found with ID " + petId);
                        return;
                    }
                    status = rs.getString("current_status");
                }
            }

            // Will only allow deletion if pet is deceased, adopted and all follow ups completed
            if (!"adopted".equalsIgnoreCase(status) &&
                !"deceased".equalsIgnoreCase(status)) {
                System.out.println("Cannot delete pet with status '" + status +
                                   "'. Only adopted or deceased pets may be deleted.");
                return;
            }

            String pendingAppSql = """
                SELECT COUNT(*) AS cnt
                FROM adoption_application
                WHERE pet_id = ? AND status = 'pending'
                """;
            try (PreparedStatement ps = conn.prepareStatement(pendingAppSql)) {
                ps.setInt(1, petId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    int count = rs.getInt("cnt");
                    if (count > 0) {
                        System.out.println("Cannot delete pet: there are pending adoption applications.");
                        return;
                    }
                }
            }

            // CHeck for active health records
            String activeHealthSql = """
                SELECT COUNT(*) AS cnt
                FROM health_record
                WHERE pet_id = ?
                  AND (status IS NULL OR status NOT IN ('completed','closed','void'))
                """;
            try (PreparedStatement ps = conn.prepareStatement(activeHealthSql)) {
                ps.setInt(1, petId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    int count = rs.getInt("cnt");
                    if (count > 0) {
                        System.out.println("Cannot delete pet: there are active health records requiring attention.");
                        return;
                    }
                }
            }

            // Attempt the delete but chcek dependant rows first
            String deleteSql = "DELETE FROM pet WHERE pet_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, petId);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("No pet deleted (unexpected).");
                } else {
                    System.out.println("Pet " + petId + " deleted.");
                }
            } catch (SQLException e) {
                System.out.println("Error: could not delete pet because related records still exist " +
                                   "(behavioral assessments, visits, etc.).");
                System.out.println("Please clean up dependent data first.");
            }

        } catch (NumberFormatException nfe) {
            System.out.println("Invalid pet ID.");
        } catch (SQLException e) {
            System.out.println("Error deleting pet:");
            e.printStackTrace();
        }
   }


    public void listPets() {
        System.out.println("\n--- List of Pets ---");

        String sql = """
            SELECT pet_id, name, species, breed, current_status
            FROM pet
            ORDER BY pet_id
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("pet_id");
                String name = rs.getString("name");
                String species = rs.getString("species");
                String breed = rs.getString("breed");
                String status = rs.getString("current_status");

                System.out.printf("%d | %s | %s | %s | %s%n",
                        id, name, species,
                        (breed == null ? "-" : breed),
                        status);
            }

        } catch (SQLException e) {
            System.out.println("Error listing pets:");
            e.printStackTrace();
        }
    }

    public void updatePetStatus() {
        try {
            System.out.println("\n--- Update Pet Status ---");
            System.out.print("Enter pet_id: ");
            int petId = Integer.parseInt(scanner.nextLine());

            System.out.print("New status (available_adoption/pending/adopted/resident/sick/deceased): ");
            String newStatus = scanner.nextLine();

            String sql = "UPDATE pet SET current_status = ? WHERE pet_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, petId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("No pet found with that ID.");
                } else {
                    System.out.println("Pet status updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating pet status:");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid pet_id.");
        }
    }
}

