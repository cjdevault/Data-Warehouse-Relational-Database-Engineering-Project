/* Author: Nathan Tebbs
 * Teacher & TAs: Prof. Lester I. McCann, James Shen, Utkarsh Upadhyay
 * Assignment: Prog4
 * Course: CSc 460
 * Due: December 8th, 2025
 *
 * Purpose: Management class for the Main application of the Pet Cafe Management System. Provides
 * the Order menu, placement/updating/deleting system, history display, and bill calculation.
 *
 * Java version: 16
 */

import java.sql.*;
import java.util.Scanner;

public class OrderManager {
    // 4.1 View Menu

    /**
     * Name: displayMenu
     * Purpose: Prints all menu items so the user can view available food and drink options.
     *
     * Preconditions:
     * - The menu_item table exists and is readable.
     *
     * Postconditions:
     * - No database modifications are made.
     * - Menu item information is printed to the console.
     *
     * Parameters:
     * - conn: open JDBC connection.
     *
     * Returns:
     * - void
     */
    public static void displayMenu(Connection conn) {
        System.out.println("\n--- Cafe Menu ---");

        String sql = """
            SELECT menu_item_id, name, food_price, category, description
            FROM menu_item
            ORDER BY category, name
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean any = false;

            System.out.printf("%-5s %-22s %-10s %-15s %-40s%n",
                    "ID", "Name", "Price", "Category", "Description");
            System.out.println("--------------------------------------------------------------------------------");

            while (rs.next()) {
                any = true;
                int id = rs.getInt("menu_item_id");
                String name = rs.getString("name");
                double price = rs.getDouble("food_price");
                String category = rs.getString("category");
                String description = rs.getString("description");

                System.out.printf("%-5d %-22s $%-9.2f %-15s %-40s%n",
                        id, name, price, category, description == null ? "" : description);
            }

            if (!any) {
                System.out.println("No menu items found.");
            }

        } catch (SQLException e) {
            System.out.println("Error displaying menu:");
            e.printStackTrace();
        }
    }

    // 4.2 Place Order (ADD)
    /**
     * Name: placeOrder
     * Purpose: Creates a new cafe order for an existing visit and allows multiple items to be added.
     *
     * Preconditions:
     * - The user must enter a valid visit_id that exists in the visit table.
     * - The seq_order_id sequence must exist to generate new order IDs.
     *
     * Postconditions:
     * - A new row may be inserted into cafe_order.
     * - One or more rows may be inserted into order_item.
     * - The final_price and discount_amount fields of cafe_order are updated.
     * - If no items are added, the order header may be deleted as an empty order.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     *
     * Returns:
     * - void
     */
    public static void placeOrder(Connection conn, Scanner scanner) {
        System.out.println("\n--- Place Order ---");

        int visitId = readInt(scanner, "Enter visit_id for this order: ");

        if (!visitExists(conn, visitId)) {
            System.out.println("No visit found with visit_id " + visitId + ". Please create/check in a visit first.");
            return;
        }

        Integer orderId = getNextOrderId(conn);
        if (orderId == null) {
            System.out.println("Could not generate a new order id.");
            return;
        }

        String insertOrderSql = """
            INSERT INTO cafe_order (order_id, visit_id, order_time, final_price, payment_status, discount_amount)
            VALUES (?, ?, SYSTIMESTAMP, 0, 'pending', 0)
            """;

        try (PreparedStatement insertOrderPs = conn.prepareStatement(insertOrderSql)) {
            insertOrderPs.setInt(1, orderId);
            insertOrderPs.setInt(2, visitId);
            int rows = insertOrderPs.executeUpdate();

            if (rows != 1) {
                System.out.println("Unexpected row count when inserting order header.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error creating order header:");
            e.printStackTrace();
            return;
        }

        boolean addingItems = true;
        while (addingItems) {
            System.out.println();
            displayMenu(conn);

            int menuItemId = readInt(scanner, "Enter menu_item_id to add (0 to finish): ");
            if (menuItemId == 0) {
                addingItems = false;
                break;
            }

            int quantity = readInt(scanner, "Enter quantity: ");
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }

            Double unitPrice = fetchMenuItemPrice(conn, menuItemId);
            if (unitPrice == null) {
                System.out.println("No menu item found with id " + menuItemId + ". Please try again.");
                continue;
            }

            String insertItemSql = """
                INSERT INTO order_item (order_id, menu_item_id, quantity, item_price)
                VALUES (?, ?, ?, ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(insertItemSql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, menuItemId);
                ps.setInt(3, quantity);
                ps.setDouble(4, unitPrice);

                int rows = ps.executeUpdate();
                if (rows != 1) {
                    System.out.println("Warning: order item was not inserted.");
                } else {
                    System.out.printf("Added item %d (qty %d) at $%.2f each.%n",
                            menuItemId, quantity, unitPrice);
                }
            } catch (SQLException e) {
                System.out.println("Error inserting order item:");
                e.printStackTrace();
            }
        }

        if (!recalculateOrderTotals(conn, orderId)) {
            System.out.println("No items added; deleting empty order.");
            deleteEmptyOrder(conn, orderId);
        }
    }

    // UPDATE existing order
    /**
     * Name: updateOrder
     * Purpose: Modifies an existing order by changing quantities, adding items, or removing items.
     *
     * Preconditions:
     * - The user must enter an order_id that exists in the cafe_order table.
     *
     * Postconditions:
     * - order_item rows may be inserted, updated, or deleted.
     * - The cafe_order final price is recalculated.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     *
     * Returns:
     * - void
     */
    public static void updateOrder(Connection conn, Scanner scanner) {
        System.out.println("\n--- Update Order ---");
        int orderId = readInt(scanner, "Enter order_id to update: ");

        if (!orderExists(conn, orderId)) {
            System.out.println("No order found with order_id " + orderId + ".");
            return;
        }

        boolean done = false;
        while (!done) {
            System.out.println();
            printOrderItems(conn, orderId);

            System.out.println("\nUpdate Options:");
            System.out.println("1. Change item quantity");
            System.out.println("2. Add new item to order");
            System.out.println("3. Remove item from order");
            System.out.println("0. Finish updating");
            String choice = readLine(scanner, "Select option: ");

            switch (choice) {
                case "1":
                    changeItemQuantity(conn, scanner, orderId);
                    break;
                case "2":
                    addItemToExistingOrder(conn, scanner, orderId);
                    break;
                case "3":
                    removeItemFromOrder(conn, scanner, orderId);
                    break;
                case "0":
                    done = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        if (!recalculateOrderTotals(conn, orderId)) {
            System.out.println("Order has no items; you may want to delete this order.");
        }
    }

    // DELETE order
    /**
     * Name: deleteOrder
     * Purpose: Removes an order and all associated order items from the database.
     *
     * Preconditions:
     * - The user must enter an order_id that exists in cafe_order.
     * - The user must confirm deletion.
     *
     * Postconditions:
     * - All order_item entries for the order are deleted.
     * - The cafe_order entry is deleted.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     *
     * Returns:
     * - void
     */
    public static void deleteOrder(Connection conn, Scanner scanner) {
        System.out.println("\n--- Delete Order ---");
        int orderId = readInt(scanner, "Enter order_id to delete: ");

        if (!orderExists(conn, orderId)) {
            System.out.println("No order found with order_id " + orderId + ".");
            return;
        }

        printOrderItems(conn, orderId);
        String confirm = readLine(scanner, "Are you sure you want to delete this order? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        String deleteItemsSql = "DELETE FROM order_item WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM cafe_order WHERE order_id = ?";

        try (PreparedStatement psItems = conn.prepareStatement(deleteItemsSql);
             PreparedStatement psOrder = conn.prepareStatement(deleteOrderSql)) {

            psItems.setInt(1, orderId);
            psItems.executeUpdate();

            psOrder.setInt(1, orderId);
            int rows = psOrder.executeUpdate();

            if (rows == 1) {
                System.out.println("Order " + orderId + " deleted.");
            } else {
                System.out.println("Warning: order header not deleted as expected.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting order:");
            e.printStackTrace();
        }
    }

    // 4.3 View Order History (by visit)
    /**
     * Name: viewOrderHistory
     * Purpose: Displays all orders and order items associated with a specific visit.
     *
     * Preconditions:
     * - The visit_id may or may not have orders.
     *
     * Postconditions:
     * - No database modifications are made.
     * - Order details are printed to the console.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     *
     * Returns:
     * - void
     */
    public static void viewOrderHistory(Connection conn, Scanner scanner) {
        System.out.println("\n--- View Order History ---");
        int visitId = readInt(scanner, "Enter visit_id: ");

        String sql = """
            SELECT o.order_id,
                   o.order_time,
                   o.payment_status,
                   oi.menu_item_id,
                   m.name AS item_name,
                   oi.quantity,
                   oi.item_price
            FROM cafe_order o
            JOIN order_item oi ON o.order_id = oi.order_id
            JOIN menu_item m ON oi.menu_item_id = m.menu_item_id
            WHERE o.visit_id = ?
            ORDER BY o.order_time, o.order_id, oi.menu_item_id
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                int lastOrderId = -1;

                while (rs.next()) {
                    any = true;
                    int orderId = rs.getInt("order_id");
                    Timestamp ts = rs.getTimestamp("order_time");
                    String status = rs.getString("payment_status");
                    int menuItemId = rs.getInt("menu_item_id");
                    String itemName = rs.getString("item_name");
                    int qty = rs.getInt("quantity");
                    double itemPrice = rs.getDouble("item_price");

                    if (orderId != lastOrderId) {
                        System.out.println();
                        System.out.printf("Order %d | Time: %s | Status: %s%n",
                                orderId, ts.toString(), status);
                        System.out.println("--------------------------------------------------------");
                        System.out.printf("%-5s %-25s %-8s %-10s%n",
                                "Item", "Name", "Qty", "Item Price");
                        lastOrderId = orderId;
                    }

                    System.out.printf("%-5d %-25s %-8d $%-9.2f%n",
                            menuItemId, itemName, qty, itemPrice);
                }

                if (!any) {
                    System.out.println("No orders found for visit_id " + visitId + ".");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error viewing order history:");
            e.printStackTrace();
        }
    }

    // 4.4 Calculate Bill (sum final_price per visit)
    /**
     * Name: calculateBill
     * Purpose: Computes the total amount due for all orders associated with a visit.
     *
     * Preconditions:
     * - The visit_id may or may not have related cafe_order rows.
     *
     * Postconditions:
     * - Total charges and discounts are printed to the console.
     * - No database data is modified.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     *
     * Returns:
     * - void
     */
    public static void calculateBill(Connection conn, Scanner scanner) {
        System.out.println("\n--- Calculate Bill ---");
        int visitId = readInt(scanner, "Enter visit_id: ");

        String sql = """
            SELECT NVL(SUM(final_price), 0) AS total_amount,
                   NVL(SUM(discount_amount), 0) AS total_discount,
                   COUNT(*) AS order_count
            FROM cafe_order
            WHERE visit_id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int orderCount = rs.getInt("order_count");
                    double totalAmount = rs.getDouble("total_amount");
                    double totalDiscount = rs.getDouble("total_discount");

                    if (orderCount == 0) {
                        System.out.println("No orders found for this visit.");
                    } else {
                        System.out.printf("Number of orders for visit %d: %d%n", visitId, orderCount);
                        System.out.printf("Total discount applied    : $%.2f%n", totalDiscount);
                        System.out.printf("Total bill (after discount): $%.2f%n", totalAmount);
                    }
                } else {
                    System.out.println("No result returned for bill calculation.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error calculating bill:");
            e.printStackTrace();
        }
    }

    // ===== helpers for UPDATE/DELETE =====

    /**
     * Name: printOrderItems
     * Purpose: Prints all items belonging to a given order.
     *
     * Preconditions:
     * - orderId may or may not exist.
     *
     * Postconditions:
     * - No database changes.
     * - Prints item list to the console.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - orderId: the ID of the order whose items should be displayed.
     *
     * Returns:
     * - void
     */
    private static void printOrderItems(Connection conn, int orderId) {
        String sql = """
            SELECT oi.menu_item_id,
                   m.name,
                   oi.quantity,
                   oi.item_price
            FROM order_item oi
            JOIN menu_item m ON oi.menu_item_id = m.menu_item_id
            WHERE oi.order_id = ?
            ORDER BY oi.menu_item_id
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                System.out.printf("%-5s %-25s %-8s %-10s%n",
                        "Item", "Name", "Qty", "Item Price");
                System.out.println("-------------------------------------------------");
                while (rs.next()) {
                    any = true;
                    int menuItemId = rs.getInt("menu_item_id");
                    String name = rs.getString("name");
                    int qty = rs.getInt("quantity");
                    double price = rs.getDouble("item_price");
                    System.out.printf("%-5d %-25s %-8d $%-9.2f%n",
                            menuItemId, name, qty, price);
                }
                if (!any) {
                    System.out.println("No items in this order.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error printing order items:");
            e.printStackTrace();
        }
    }

    /**
     * Name: changeItemQuantity
     * Purpose: Changes the quantity of an item in an order or removes it if the quantity is set to zero.
     *
     * Preconditions:
     * - orderId must exist.
     * - menu_item_id may or may not be in the order.
     *
     * Postconditions:
     * - The quantity for the item may be updated.
     * - The item may be removed.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     * - orderId: the order being modified.
     *
     * Returns:
     * - void
     */
    private static void changeItemQuantity(Connection conn, Scanner scanner, int orderId) {
        int menuItemId = readInt(scanner, "Enter menu_item_id to modify: ");
        int newQty = readInt(scanner, "Enter new quantity (0 to remove): ");

        if (newQty < 0) {
            System.out.println("Quantity cannot be negative.");
            return;
        }

        if (newQty == 0) {
            removeItemFromOrder(conn, orderId, menuItemId);
            return;
        }

        String sql = """
            UPDATE order_item
            SET quantity = ?
            WHERE order_id = ? AND menu_item_id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, orderId);
            ps.setInt(3, menuItemId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No matching item found in this order.");
            } else {
                System.out.println("Quantity updated.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating quantity:");
            e.printStackTrace();
        }
    }

    /**
     * Name: addItemToExistingOrder
     * Purpose: Adds a new menu item to an existing order.
     *
     * Preconditions:
     * - orderId must exist.
     * - The user must enter a valid menu_item_id and a positive quantity.
     *
     * Postconditions:
     * - A new row may be added to order_item.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     * - orderId: identifier of the order being modified.
     *
     * Returns:
     * - void
     */
    private static void addItemToExistingOrder(Connection conn, Scanner scanner, int orderId) {
        System.out.println();
        displayMenu(conn);

        int menuItemId = readInt(scanner, "Enter menu_item_id to add: ");
        int quantity = readInt(scanner, "Enter quantity: ");
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        Double unitPrice = fetchMenuItemPrice(conn, menuItemId);
        if (unitPrice == null) {
            System.out.println("No menu item found with id " + menuItemId + ".");
            return;
        }

        String sql = """
            INSERT INTO order_item (order_id, menu_item_id, quantity, item_price)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, menuItemId);
            ps.setInt(3, quantity);
            ps.setDouble(4, unitPrice);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.println("Item added to order.");
            } else {
                System.out.println("Warning: item was not added.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding item to order:");
            e.printStackTrace();
        }
    }

    /**
     * Name: removeItemFromOrder
     * Purpose: Prompts the user for a menu_item_id and removes the corresponding item from the order.
     *
     * Preconditions:
     * - orderId exists.
     *
     * Postconditions:
     * - A matching order_item row may be deleted.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - scanner: Scanner for user input.
     * - orderId: identifier of the order to modify.
     *
     * Returns:
     * - void
     */
    private static void removeItemFromOrder(Connection conn, Scanner scanner, int orderId) {
        int menuItemId = readInt(scanner, "Enter menu_item_id to remove: ");
        removeItemFromOrder(conn, orderId, menuItemId);
    }

    /**
     * Name: removeItemFromOrder
     * Purpose: Removes a specific item from an order using orderId and menuItemId.
     *
     * Preconditions:
     * - orderId exists.
     * - menuItemId may or may not be in the order.
     *
     * Postconditions:
     * - If present, the order_item row is deleted.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - orderId: the order being modified.
     * - menuItemId: the item to remove.
     *
     * Returns:
     * - void
     */
    private static void removeItemFromOrder(Connection conn, int orderId, int menuItemId) {
        String sql = """
            DELETE FROM order_item
            WHERE order_id = ? AND menu_item_id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, menuItemId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No matching item found to remove.");
            } else {
                System.out.println("Item removed from order.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing item from order:");
            e.printStackTrace();
        }
    }

    /**
     * Name: recalculateOrderTotals
     * Purpose: Computes subtotal, discount, and final price for an order and updates cafe_order.
     *
     * Preconditions:
     * - orderId identifies an existing cafe_order.
     * - Zero or more order_item rows may exist.
     *
     * Postconditions:
     * - cafe_order.final_price and discount_amount are updated.
     * - If no items exist, the totals are set to zero.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - orderId: the order whose totals should be recalculated.
     *
     * Returns:
     * - true if the order has items and totals were updated.
     * - false if no items exist or an error occurred.
     */
    private static boolean recalculateOrderTotals(Connection conn, int orderId) {
        String subtotalSql = """
            SELECT NVL(SUM(quantity * item_price), 0) AS subtotal
            FROM order_item
            WHERE order_id = ?
            """;

        double subtotal = 0.0;
        try (PreparedStatement ps = conn.prepareStatement(subtotalSql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    subtotal = rs.getDouble("subtotal");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error calculating subtotal:");
            e.printStackTrace();
            return false;
        }

        if (subtotal <= 0.0) {
            String zeroSql = """
                UPDATE cafe_order
                SET final_price = 0, discount_amount = 0
                WHERE order_id = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(zeroSql)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error zeroing order totals:");
                e.printStackTrace();
            }
            return false;
        }

        double discountAmount = 0.0; // extension point: compute discounts here
        double finalPrice = subtotal - discountAmount;

        String updateOrderSql = """
            UPDATE cafe_order
            SET final_price = ?, discount_amount = ?
            WHERE order_id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
            ps.setDouble(1, finalPrice);
            ps.setDouble(2, discountAmount);
            ps.setInt(3, orderId);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.printf("Order %d totals updated. Final price: $%.2f (discount: $%.2f)%n",
                        orderId, finalPrice, discountAmount);
            } else {
                System.out.println("Warning: order header not updated with totals.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating order totals:");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // ===== generic helpers =====

    /**
     * Name: readInt
     * Purpose: Reads an integer from the console, retrying on invalid input.
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
     * Name: visitExists
     * Purpose: Checks whether a given visit_id exists.
     *
     * Preconditions:
     * - visit table exists.
     *
     * Postconditions:
     * - No database modifications.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - visitId: the identifier to check.
     *
     * Returns:
     * - true if the visit exists.
     * - false otherwise or on error.
     */
    private static boolean visitExists(Connection conn, int visitId) {
        String sql = "SELECT visit_id FROM visit WHERE visit_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking visit existence:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Name: orderExists
     * Purpose: Checks whether a given order_id exists in cafe_order.
     *
     * Preconditions:
     * - cafe_order table exists.
     *
     * Postconditions:
     * - No database modifications.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - orderId: the identifier to check.
     *
     * Returns:
     * - true if the order exists.
     * - false otherwise or on error.
     */
    private static boolean orderExists(Connection conn, int orderId) {
        String sql = "SELECT order_id FROM cafe_order WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking order existence:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Name: getNextOrderId
     * Purpose: Retrieves the next sequence value from seq_order_id.
     *
     * Preconditions:
     * - The sequence seq_order_id exists.
     *
     * Postconditions:
     * - Sequence state advances by one.
     *
     * Parameters:
     * - conn: open JDBC connection.
     *
     * Returns:
     * - The next integer from the sequence, or null if an error occurs.
     */
    private static Integer getNextOrderId(Connection conn) {
        String sql = "SELECT seq_order_id.NEXTVAL AS next_id FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching next order id from seq_order_id:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Name: fetchMenuItemPrice
     * Purpose: Retrieves the price of a menu item.
     *
     * Preconditions:
     * - menu_item table exists.
     *
     * Postconditions:
     * - No database modifications.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - menuItemId: the menu item to look up.
     *
     * Returns:
     * - The item's price, or null if not found or on error.
     */
    private static Double fetchMenuItemPrice(Connection conn, int menuItemId) {
        String sql = "SELECT food_price FROM menu_item WHERE menu_item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("food_price");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching menu item price:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Name: deleteEmptyOrder
     * Purpose: Deletes an order header when it has no items.
     *
     * Preconditions:
     * - orderId may or may not exist.
     *
     * Postconditions:
     * - The corresponding row in cafe_order is deleted if present.
     *
     * Parameters:
     * - conn: open JDBC connection.
     * - orderId: the identifier of the order to delete.
     *
     * Returns:
     * - void
     */
    private static void deleteEmptyOrder(Connection conn, int orderId) {
        String sql = "DELETE FROM cafe_order WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting empty order:");
            e.printStackTrace();
        }
    }
}
