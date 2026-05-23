/* Teacher & TAs: Prof. Lester I. McCann, James Shen, Utkarsh Upadhyay
 * Assignment: Prog4
 * Course: CSc 460
 * Due: December 8, 2025
 *
 * Purpose: Main application for Pet Cafe Management System. Provides a menu-driven
 * text interface for managing members, reservations, visits, orders, pets,
 * adoptions, events, and generating reports.
 * 
 * Usage:
 *   javac PetCafeApplication.java
 *   java PetCafeApplication <oracle_username> <oracle_password>
 *
 * Java version: 16
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PetCafeApplication {
    
    /*
     * Purpose: Main driver for the Pet Cafe Management System.
     *          Handles command-line arguments, database connection, and menu loop.
     *
     * Pre-condition: Username and password provided as command-line arguments.
     *
     * Post-condition: User operations are executed and results displayed.
     *
     * Parameters: args[0] - Oracle username, args[1] - Oracle password
     *
     * Returns: void
     */
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 2) {
            System.out.println("\nUsage:  java PetCafeApplication <username> <password>\n"
                            + "    where <username> is your Oracle DBMS username,\n"
                            + "    and <password> is your Oracle password.\n");
            System.exit(-1);
        }
        
        String username = args[0];
        String password = args[1];
        
        // Get database connection
        Connection dbconn = null;
        try {
            dbconn = DatabaseConfig.getConnection(username, password);
            System.out.println("Successfully connected to database.\n");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database. Exiting.");
            System.exit(-1);
        }
        
        // Display menu and process user choices
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        
        while (!exit) {
            displayMainMenu();
            System.out.print("Enter choice: ");
            
            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.\n");
                scanner.nextLine(); // Clear invalid input
                continue;
            }
            
            System.out.println();
            
            try {
                switch (choice) {
                    case 1:
                        handleMemberManagement(dbconn, scanner);
                        break;
                    case 2:
                        handleReservationManagement(dbconn, scanner);
                        break;
                    case 3:
                        handleVisitManagement(dbconn, scanner);
                        break;
                    case 4:
                        handleOrderManagement(dbconn, scanner);
                        break;
                    case 5:
                        handlePetManagement(dbconn, scanner);
                        break;
                    case 6:
                        handleAdoptionManagement(dbconn, scanner);
                        break;
                    case 7:
                        handleEvents(dbconn, scanner);
                        break;
                    case 8:
                        handleReports(dbconn, scanner);
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select 0-8.\n");
                }
            } catch (SQLException e) {
                System.err.println("SQLException occurred during operation:");
                System.err.println("Message:   " + e.getMessage());
                System.err.println("SQLState:  " + e.getSQLState());
                System.err.println("ErrorCode: " + e.getErrorCode());
                System.out.println();
            }
        }
        
        // Close resources
        scanner.close();
        try {
            dbconn.close();
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    /*
     * Purpose: Displays the main menu of the Pet Cafe Management System.
     */
    private static void displayMainMenu() {
        System.out.println("\n=== Pet Cafe Management System ===");
        System.out.println("1. Member Management");
        System.out.println("2. Reservation Management");
        System.out.println("3. Visit Management");
        System.out.println("4. Order Management");
        System.out.println("5. Pet Management");
        System.out.println("6. Adoption Management");
        System.out.println("7. Events");
        System.out.println("8. Reports");
        System.out.println("0. Exit");
    }
    
    /*
     * Purpose: Handles Member Management sub-menu and operations.
     */
    private static void handleMemberManagement(Connection dbconn, Scanner scanner) throws SQLException {
        boolean back = false;
        MemberManager memberManager = new MemberManager(dbconn, scanner);
        
        while (!back) {
            System.out.println("\n--- Member Management ---");
            System.out.println("1. Register New Member");
            System.out.println("2. Look Up Member");
            System.out.println("3. Update Member Information");
            System.out.println("4. View Member Visit History");
            System.out.println("5. View Member Adoption Applications");
            System.out.println("6. Delete Member");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String subChoice = scanner.nextLine().trim();
            System.out.println();
            
            switch (subChoice) {
                case "1":
                    memberManager.addMember();
                    break;
                case "2":
                    memberManager.lookupMember();
                    break;
                case "3":
                    memberManager.updateMember();
                    break;
                case "4":
                    memberManager.viewMemberVisitHistory();
                    break;
                case "5":
                    memberManager.viewMemberAdoptionApplications();
                    break;
                case "6":
                    memberManager.deleteMember();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /*
     * Purpose: Handles Reservation Management sub-menu and operations.
     */
    private static void handleReservationManagement(Connection dbconn, Scanner scanner) throws SQLException {
        boolean back = false;
        ReservationManager reservationManager = new ReservationManager(dbconn, scanner);
        
        while (!back) {
            System.out.println("\n--- Reservation Management ---");
            System.out.println("1. Create Reservation");
            System.out.println("2. View Available Slots");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View My Reservations");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String subChoice = scanner.nextLine().trim();
            System.out.println();
            
            switch (subChoice) {
                case "1":
                    reservationManager.createReservation();
                    break;
                case "2":
                    reservationManager.viewAvailableSlots();
                    break;
                case "3":
                    reservationManager.cancelReservation();
                    break;
                case "4":
                    reservationManager.viewMemberReservations();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
		/*
		 * Purpose: Handles Visit Management sub-menu and operations.
		 */
		private static void handleVisitManagement(Connection dbconn, Scanner scanner)
				throws SQLException {
				boolean back = false;

				while (!back) {
					System.out.println("\n--- Visit Management ---");
					System.out.println("1. Check In (Add Visit)");
					System.out.println("2. Check Out (Update Visit)");
					System.out.println("3. Delete Visit");
					System.out.println("4. View Active Visits");
					System.out.println("0. Back to Main Menu");
					System.out.print("Enter choice: ");

					String subChoice = scanner.nextLine().trim();
					System.out.println();

					switch (subChoice) {
						case "1":
							VisitManager.checkIn(dbconn, scanner);
							break;
						case "2":
							VisitManager.checkOut(dbconn, scanner);
							break;
						case "3":
							VisitManager.deleteVisit(dbconn, scanner);
							break;
						case "4":
							VisitManager.viewActiveVisits(dbconn);
							break;
						case "0":
							back = true;
							break;
						default:
							System.out.println("Invalid choice. Please try again.");
					}
				}
		}

    
    /*
     * Purpose: Handles Order Management sub-menu and operations.
     */
    private static void handleOrderManagement(Connection dbconn, Scanner scanner) throws SQLException {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Order Management ---");
            System.out.println("1 View Menu");
            System.out.println("2 Place Order");
            System.out.println("3 Update Order");
            System.out.println("4 Delete Order");
            System.out.println("5 View Order History");
            System.out.println("6 Calculate Bill");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String subChoice = scanner.nextLine().trim();
            System.out.println();
            
            switch (subChoice) {
                case "1":
                    OrderManager.displayMenu(dbconn);
                    break;
                case "2":
                    OrderManager.placeOrder(dbconn, scanner);
                    break;
                case "3":
                    OrderManager.updateOrder(dbconn, scanner);
                    break;
                case "4":
                    OrderManager.deleteOrder(dbconn, scanner);
                    break;
                case "5":
                    OrderManager.viewOrderHistory(dbconn, scanner); 
                    break; 
                case "6":
                    OrderManager.calculateBill(dbconn, scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /*
     * Purpose: Handles Pet Management sub-menu and operations.
     */
    private static void handlePetManagement(Connection dbconn, Scanner scanner) throws SQLException {
        boolean back = false;
        PetManager petManager = new PetManager(dbconn, scanner);
        HealthRecordManager healthRecordManager = new HealthRecordManager(dbconn, scanner);
        BehavioralAssessmentManager behavioralAssessmentManager = new BehavioralAssessmentManager(dbconn, scanner);
        
        while (!back) {
            System.out.println("\n--- Pet Management ---");
            System.out.println("1. List All Pets");
            System.out.println("2. Add New Pet");
            System.out.println("3. Update Pet Status");
            System.out.println("4. View Pet Health Records");
            System.out.println("5. Add Health Record");
            System.out.println("6. Update Health Record Status");
            System.out.println("7 View Pet Behavioral Assessments");
            System.out.println("8 Add Behavioral Assessment");
            System.out.println("9 Update Behavioral Assessment Result");
	    System.out.println("10 Delete Pet");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String subChoice = scanner.nextLine().trim();
            System.out.println();
            
            switch (subChoice) {
                case "1":
                    petManager.listPets();
                    break;
                case "2":
                    petManager.addPet();
                    break;
                case "3":
                    petManager.updatePetStatus();
                    break;
                case "4":
                    healthRecordManager.listHealthRecordsForPet();
                    break;
                case "5":
                    healthRecordManager.addHealthRecord();
                    break;
                case "6":
                    healthRecordManager.updateHealthRecordStatus();
                    break;
                case "7":
                    behavioralAssessmentManager.listAssessmentsForPet();
                    break;
                case "8":
                    behavioralAssessmentManager.addAssessment();
                    break;
                case "9":
                    behavioralAssessmentManager.updateAssessmentResult();
                    break;
		case "10":
		    petManager.deletePet();
		    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /*
     * Purpose: Handles Adoption Management sub-menu and operations.
     */
    private static void handleAdoptionManagement(Connection dbconn, Scanner scanner) throws SQLException {
        boolean back = false;
        AdoptionManager adoptionManager = new AdoptionManager(dbconn, scanner);
        
        while (!back) {
            System.out.println("\n--- Adoption Management ---");
            System.out.println("1 Submit Adoption Application");
            System.out.println("2 View Application for Member");
            System.out.println("3 View Applications for Pet");
            System.out.println("4 Update Application Status");
            System.out.println("5 Finalize Adoption");
	    System.out.println("6 Delete Adoption Application");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String subChoice = scanner.nextLine().trim();
            System.out.println();
            
            switch (subChoice) {
                case "1":
                    adoptionManager.submitApplication();
                    break;
                case "2":
                    adoptionManager.listApplicationsForMember();
                    break;
                case "3":
                    adoptionManager.listApplicationsForPet();
                    break;
                case "4":
                    adoptionManager.updateApplicationStatus();
                    break;
                case "5":
                    adoptionManager.finalizeAdoption();
                    break;
		case "6":
		    adoptionManager.deleteApplication();
		    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /*
     * Purpose: Handles Events sub-menu and operations.
     */
    private static void handleEvents(Connection dbconn, Scanner scanner) throws SQLException {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Events ---");
            System.out.println("1 View Upcoming Events");
            System.out.println("2 Register for Event");
            System.out.println("3 View Event Details");
            System.out.println("4 Cancel or Update Booking");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String subChoice = scanner.nextLine().trim();
            System.out.println();
            
            switch (subChoice) {
                case "1":
                    EventManager.viewUpcomingEvents(dbconn);
                    //System.out.println("View Upcoming Events - Not yet implemented");
                    break;
                case "2":
                    EventManager.registerForEvent(dbconn, scanner);
                    //System.out.println("Register for Event - Not yet implemented");
                    break;
                case "3":
                    EventManager.viewEventDetails(dbconn, scanner);
                    //System.out.println("View Event Details - Not yet implemented");
                    break;
                case "4":
                    EventManager.cancelOrUpdateBooking(dbconn, scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /*
     * Purpose: Handles Reports sub-menu and operations.
     */
    private static void handleReports(Connection dbconn, Scanner scanner) throws SQLException {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Reports ---");
            System.out.println("1 Pet Popularity Report");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String subChoice = scanner.nextLine().trim();
            System.out.println();
            
            switch (subChoice) {
                case "1":
                    ReportManager.petPopularityReport(dbconn, scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

