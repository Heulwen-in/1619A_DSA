package controller;

import java.util.*;

import ds.ArrayQueue;
import ds.ArrayStack;
import model.Book;
import model.BookItem;
import model.Order;
import services.Searcher;
import services.Sorter;

public class OrderProcessingSystem {
    private ArrayQueue<Order> orderQueue = new ArrayQueue<>(16);
    private List<Order> processedOrders = new ArrayList<>();
    private List<Book> catalog = new ArrayList<>();
    private ArrayList<BookItem> cart = new ArrayList<>();
    private final Scanner scanner;

    public static void main(String[] args) {
        OrderProcessingSystem sys = new OrderProcessingSystem();
        sys.initCatalog();
        sys.menu();
    }

    public OrderProcessingSystem() {
        this.scanner = new Scanner(System.in);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { scanner.close(); } catch (Exception ignore) {}
        }));
    }

    private void initCatalog() {
        catalog.add(new Book("The Hobbit", "J.R.R. Tolkien", 5, 12.99));
        catalog.add(new Book("1984", "George Orwell", 4, 10.50));
        catalog.add(new Book("Clean Code", "Robert C. Martin", 3, 35.00));
        catalog.add(new Book("Effective Java", "Joshua Bloch", 2, 42.00));
        catalog.add(new Book("Seulgi", "Heulwen", 3, 15.75));
    }

    private void menu() {
        Scanner sc = this.scanner;
        while (true) {
            System.out.println("\n=== Online Bookstore | Menu ===");
            System.out.println("1 Customer");
            System.out.println("2 Admin");
            System.out.println("3 Exit");
            System.out.print("Choose> ");
            String ch = sc.nextLine().trim();

            switch (ch) {
                case "1": customerMenu(sc); break;
                case "2": adminMenu(sc); break;
                case "3": return;
                default: System.out.println("Invalid");
            }
        }
    }

    private void customerMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1 View books");
            System.out.println("2 Add book to cart");
            System.out.println("3 Place order");
            System.out.println("4 Track order");
            System.out.println("5 Back");
            System.out.print("Choose> ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": showCatalog(); break;
                case "2": addToCart(sc); break;
                case "3": confirmOrder(sc); break;
                case "4": trackOrder(sc); break;
                case "5": return;
                default: System.out.println("Invalid");
            }
        }
    }

    private void adminMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1 View books");
            System.out.println("2 Create book");
            System.out.println("3 Edit book");
            System.out.println("4 Delete book");
            System.out.println("5 View customer orders");
            System.out.println("6 Process next order (FIFO)");
            System.out.println("7 Process an order (choose by ID)");
            System.out.println("8 Back");
            System.out.print("Choose> ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": showCatalog(); break;
                case "2": createBook(sc); break;
                case "3": editBook(sc); break;
                case "4": deleteBook(sc); break;
                case "5": viewCustomerOrders(); break;
                case "6": processNextOrder(sc); break;
                case "7": processOrderBySelection(sc); break;
                case "8": return;
                default: System.out.println("Invalid");
            }
        }
    }

    private void showCatalog() {
        if (catalog.isEmpty()) { System.out.println("No books available."); return; }
        Scanner sc = this.scanner;
        System.out.println("Choose category to view:");
        System.out.println("1 Title (A-Z)");
        System.out.println("2 Author (A-Z)");
        System.out.print("Choice> ");
        String ch = sc.nextLine().trim();

        List<Book> list = new ArrayList<>(catalog);
        if ("2".equals(ch)) {
            list.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
        } else {
            list.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
        }

        String header = String.format("%-4s | %-30s | %-22s | %-8s | %-5s", "ID", "Title", "Author", "Price", "Stock");
        String sep = new String(new char[Math.min(90, header.length())]).replace('\0','-');
        System.out.println(header);
        System.out.println(sep);
        for (int i = 0; i < list.size(); i++) {
            Book b = list.get(i);
            System.out.println(String.format("%-4d | %-30s | %-22s | $%-7.2f | %-5d",
                (i+1), trimTo(b.getTitle(),30), trimTo(b.getAuthor(),22), b.getPrice(), b.getStock()));
        }
    }

    private String trimTo(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max-1)) + "\u2026";
    }

    private void addToCart(Scanner sc) {
        showCatalog();
        System.out.print("Select book number> ");
        int idx = Integer.parseInt(sc.nextLine()) - 1;
        if (idx<0 || idx>=catalog.size()) return;
        // Since showCatalog prints a sorted view copy, map by title selection back to original catalog by current ordering.
        Book b = catalog.get(idx);
        System.out.print("Quantity> ");
        int q = Integer.parseInt(sc.nextLine());
        if (q<=0 || q>b.getStock()) {
            System.out.println("Invalid quantity");
            return;
        }
        cart.add(new BookItem(b.getTitle(), b.getAuthor(), q));
        System.out.println("Added to cart.");
    }

    private void createBook(Scanner sc) {
        System.out.print("Title> ");
        String title = sc.nextLine().trim();
        System.out.print("Author> ");
        String author = sc.nextLine().trim();
        System.out.print("Stock> ");
        int stock = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Price> $");
        double price = Double.parseDouble(sc.nextLine().trim());
        if (stock < 0) { System.out.println("Stock must be >= 0"); return; }
        catalog.add(new Book(title, author, stock, price));
        System.out.println("Book created.");
    }

    private void editBook(Scanner sc) {
        showCatalog();
        if (catalog.isEmpty()) return;
        System.out.print("Select book number to edit> ");
        int idx = Integer.parseInt(sc.nextLine()) - 1;
        if (idx < 0 || idx >= catalog.size()) { System.out.println("Invalid index"); return; }
        Book b = catalog.get(idx);
        System.out.println("Editing: " + b);
        System.out.print("New title (blank to keep)> ");
        String newTitle = sc.nextLine();
        System.out.print("New author (blank to keep)> ");
        String newAuthor = sc.nextLine();
        System.out.print("New stock (-1 to keep)> ");
        int newStock = Integer.parseInt(sc.nextLine());
        System.out.print("New price (-1 to keep)> $");
        double newPrice = Double.parseDouble(sc.nextLine());

        String t = newTitle.isEmpty() ? b.getTitle() : newTitle;
        String a = newAuthor.isEmpty() ? b.getAuthor() : newAuthor;
        int s = (newStock < 0) ? b.getStock() : newStock;
        double p = (newPrice < 0) ? b.getPrice() : newPrice;
        catalog.set(idx, new Book(t, a, s, p));
        System.out.println("Book updated.");
    }

    private void deleteBook(Scanner sc) {
        showCatalog();
        if (catalog.isEmpty()) return;
        System.out.print("Select book number to delete> ");
        int idx = Integer.parseInt(sc.nextLine()) - 1;
        if (idx < 0 || idx >= catalog.size()) { System.out.println("Invalid index"); return; }
        Book removed = catalog.remove(idx);
        System.out.println("Deleted: " + removed);
    }

    private void confirmOrder(Scanner sc) {
        if (cart.isEmpty()) {
            System.out.println("Cart empty.");
            return;
        }
        System.out.print("Customer name> ");
        String name = sc.nextLine();
        System.out.print("Address> ");
        String addr = sc.nextLine();
        Order o = new Order(name, addr);
        for (BookItem i : cart) {
            o.addItem(i);
            for (Book b : catalog) {
                if (b.getTitle().equals(i.getTitle())) {
                    b.reduceStock(i.getQuantity());
                }
            }
        }
        cart.clear();
        orderQueue.enqueue(o);
        System.out.println("Order placed. ID = " + o.getOrderId());
        printOrderSummary(o);
    }

    

    private void processOrderBySelection(Scanner sc) {
        if (orderQueue.isEmpty()) {
            System.out.println("No orders in queue.");
            return;
        }
        // Snapshot current queue without disturbing order (rotate through)
        List<Order> snapshot = new ArrayList<>();
        int n = orderQueue.size();
        for (int i = 0; i < n; i++) {
            Order tmp = orderQueue.dequeue();
            snapshot.add(tmp);
            orderQueue.enqueue(tmp);
        }

        System.out.println("Queued orders:");
        for (int i = 0; i < snapshot.size(); i++) {
            System.out.println((i+1)+". ID="+snapshot.get(i).getOrderId()+" | Customer="+snapshot.get(i).getCustomerName());
        }
        System.out.print("Enter order number or ID to process> ");
        String input = sc.nextLine();
        String inputTrim = input == null ? "" : input.trim();

        Order selected = null;
        try {
            int idx = Integer.parseInt(inputTrim) - 1;
            if (idx >= 0 && idx < snapshot.size()) selected = snapshot.get(idx);
        } catch (NumberFormatException ignore) {
            for (Order o : snapshot) {
                String id = o.getOrderId();
                if (id.equalsIgnoreCase(inputTrim) || id.toLowerCase().startsWith(inputTrim.toLowerCase())) { selected = o; break; }
            }
        }

        if (selected == null) {
            System.out.println("Invalid selection.");
            return;
        }

        // Remove selected from queue
        List<Order> buffer = new ArrayList<>();
        int m = orderQueue.size();
        for (int i = 0; i < m; i++) {
            Order o = orderQueue.dequeue();
            if (!o.getOrderId().equalsIgnoreCase(selected.getOrderId())) buffer.add(o);
        }
        for (Order o : buffer) orderQueue.enqueue(o);

        // Process selected using same logic
        Order o = selected;

        // Advanced choice: Bubble or Selection
        System.out.println("Choose sorting algorithm:");
        System.out.println("1 Selection Sort");
        System.out.println("2 Bubble Sort");
        System.out.print("Choice> ");
        String choice = sc.nextLine().trim();
        Sorter.Stats s;
        if ("2".equals(choice)) {
            s = Sorter.bubbleSort(o.getItems());
            System.out.println("Sorted by Bubble Sort.");
        } else {
            s = Sorter.selectionSort(o.getItems());
            System.out.println("Sorted by Selection Sort.");
        }

        ArrayStack<BookItem> packingStack = new ArrayStack<>(Math.max(8, o.getItems().size()));
        for (BookItem item : o.getItems()) packingStack.push(item);
        System.out.println("Packing items (LIFO):");
        while (!packingStack.isEmpty()) System.out.println("  Packed -> " + packingStack.pop());

        o.setStatus("Processed");
        processedOrders.add(o);
        System.out.println("Processed order " + o.getOrderId());
        System.out.println("Comparisons: " + s.comparisons + " Time: " + s.timeNs + " ns");
    }

    private void processNextOrder(Scanner sc) {
        if (orderQueue.isEmpty()) {
            System.out.println("No orders in queue.");
            return;
        }
        Order o = orderQueue.dequeue();

        System.out.println("Choose sorting algorithm:");
        System.out.println("1 Selection Sort");
        System.out.println("2 Bubble Sort");
        System.out.print("Choice> ");
        String choice = sc.nextLine().trim();
        Sorter.Stats s;
        if ("2".equals(choice)) {
            s = Sorter.bubbleSort(o.getItems());
            System.out.println("Sorted by Bubble Sort.");
        } else {
            s = Sorter.selectionSort(o.getItems());
            System.out.println("Sorted by Selection Sort.");
        }

        ArrayStack<BookItem> packingStack = new ArrayStack<>(Math.max(8, o.getItems().size()));
        for (BookItem item : o.getItems()) packingStack.push(item);
        System.out.println("Packing items (LIFO):");
        while (!packingStack.isEmpty()) System.out.println("  Packed -> " + packingStack.pop());

        o.setStatus("Processed");
        processedOrders.add(o);
        System.out.println("Processed order " + o.getOrderId());
        System.out.println("Comparisons: " + s.comparisons + " Time: " + s.timeNs + " ns");
    }

    private void viewCustomerOrders() {
        System.out.println("\n-- Queued (Pending) Orders --");
        if (orderQueue.isEmpty()) System.out.println("(none)");
        else {
            int n = orderQueue.size();
            for (int i = 0; i < n; i++) {
                Order tmp = orderQueue.dequeue();
                System.out.println("ID="+tmp.getOrderId()+" | Customer="+tmp.getCustomerName()+" | Status="+tmp.getStatus());
                orderQueue.enqueue(tmp);
            }
        }
        System.out.println("\n-- Processed Orders --");
        if (processedOrders.isEmpty()) System.out.println("(none)");
        for (Order o : processedOrders) {
            System.out.println("ID="+o.getOrderId()+" | Customer="+o.getCustomerName()+" | Status="+o.getStatus());
        }
    }

    
    private void trackOrder(Scanner sc) {
        System.out.print("Enter orderId> ");
        String id = sc.nextLine();

        // Snapshot pending queue without disturbing order
        List<Order> pendingSnapshot = new ArrayList<>();
        int n = orderQueue.size();
        for (int i = 0; i < n; i++) {
            Order tmp = orderQueue.dequeue();
            pendingSnapshot.add(tmp);
            orderQueue.enqueue(tmp);
        }

        // Search pending first (Linear), then processed (Binary); show immediately when found
        Searcher.Stats sPending = new Searcher.Stats();
        int idxPending = Searcher.linearSearch(pendingSnapshot, id, sPending);
        if (idxPending >= 0) {
            System.out.println("\n-- Pending Order Found --");
            printOrderSummary(pendingSnapshot.get(idxPending));
            System.out.println("Search: Linear (Pending) | Comparisons: "+sPending.comparisons+" Time: "+sPending.timeNs+" ns");
            return;
        }

        // Processed: use Binary Search
        Searcher.Stats sProcessed = new Searcher.Stats();
        List<Order> sortedProcessed = new ArrayList<>(processedOrders);
        Collections.sort(sortedProcessed);
        int idxProcessed = Searcher.binarySearch(sortedProcessed, id, sProcessed);
        if (idxProcessed >= 0) {
            System.out.println("\n-- Processed Order Found --");
            printOrderSummary(sortedProcessed.get(idxProcessed));
            System.out.println("Search: Binary (Processed) | Comparisons: "+sProcessed.comparisons+" Time: "+sProcessed.timeNs+" ns");
            return;
        }

        System.out.println("Not found.");
    }

    private void printOrderSummary(Order o) {
        String name = o.getCustomerName();
        String address = o.getShippingAddress();
        String header = String.format("%-20s | %-25s | %-30s | %-20s | %-8s | %-8s | %-10s | %-10s",
            "Name", "Address", "Books", "Author", "Quantity", "Price", "Order Id", "Status");
        String sep = new String(new char[Math.min(150, header.length())]).replace('\0','-');
        System.out.println();
        System.out.println(header);
        System.out.println(sep);

        double total = 0.0;
        int totalItems = 0;
        for (BookItem item : o.getItems()) {
            double price = findPriceByTitle(item.getTitle());
            double line = price * item.getQuantity();
            total += line;
            totalItems += item.getQuantity();
            System.out.println(String.format("%-20s | %-25s | %-30s | %-20s | %-8d | $%-7.2f | %-10s | %-10s",
                trimTo(name,20), trimTo(address,25), trimTo(item.getTitle(),30), trimTo(item.getAuthor(),20), item.getQuantity(), line,
                o.getOrderId(), o.getStatus()));
        }
        System.out.println(sep);
        System.out.println(String.format("%-20s | %-25s | %-30s | %-20s | %-8s | $%-7.2f | %-10s | %-10s",
            "", "", "TOTAL", "", "", total, "", ""));
        if (totalItems > 2) {
            System.out.println("You have more than 2 items in this order.");
        }
    }

    private double findPriceByTitle(String title) {
        for (Book b : catalog) {
            if (b.getTitle().equals(title)) return b.getPrice();
        }
        return 0.0;
    }
}


