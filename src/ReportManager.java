import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ReportManager {

    /**
     * Pet Popularity Report
     *
     * For custom query
     */
    public static void petPopularityReport(Connection conn, Scanner scanner) {
        System.out.println("\n--- Pet Popularity Report ---");

        try {
            System.out.print(
                "Filter by species (cat/dog/small_animal), or press Enter for all: "
            );
            String speciesInput = scanner.nextLine().trim().toLowerCase();
            
            // SQL 
            StringBuilder sb = new StringBuilder();
            sb.append("""
                SELECT
                    p.pet_id,
                    p.name       AS pet_name,
                    p.species,
                    p.current_status,
                    COUNT(a.application_id)            AS total_applications,
                    COUNT(r.adoption_record_id)        AS completed_adoptions
                FROM pet p
                LEFT JOIN adoption_application a
                    ON a.pet_id = p.pet_id
                LEFT JOIN adoption_record r
                    ON r.application_id = a.application_id
                """);
            boolean filterBySpecies = !speciesInput.isEmpty();

            if (filterBySpecies) {
                sb.append("WHERE LOWER(p.species) = ?\n");
            }

            sb.append("GROUP BY p.pet_id, p.name, p.species, p.current_status\n");
            // removed HAVING with parameter
            sb.append("ORDER BY total_applications DESC, pet_name\n");

            String sql = sb.toString();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int idx = 1;

                if (filterBySpecies) {
                    ps.setString(idx++, speciesInput);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;

                    System.out.println();
                    System.out.printf(
                        "%-4s  %-15s  %-13s  %-18s  %-18s  %-20s%n",
                        "ID",
                        "Pet Name",
                        "Species",
                        "Status",
                        "# Applications",
                        "# Completed Adoptions"
                    );
                    System.out.println(
                        "----  ---------------  -------------  ------------------  ------------------  --------------------"
                    );

                    while (rs.next()) {
                        any = true;
                        int petId = rs.getInt("pet_id");
                        String petName = rs.getString("pet_name");
                        String species = rs.getString("species");
                        String status = rs.getString("current_status");
                        int totalApplications = rs.getInt("total_applications");
                        int completedAdoptions = rs.getInt("completed_adoptions");

                        System.out.printf(
                            "%-4d  %-15s  %-13s  %-18s  %-18d  %-20d%n",
                            petId,
                            petName,
                            species,
                            status,
                            totalApplications,
                            completedAdoptions
                        );
                    }
                    if (!any) {
                        System.out.println("No pets matched the given criteria.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error generating Pet Popularity Report:");
            System.out.println("Message:   " + e.getMessage());
            System.out.println("SQLState:  " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode());
        }
        System.out.println();
    }
}