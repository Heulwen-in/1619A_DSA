## Online Bookstore - Order Processing System

This project demonstrates core data structures and algorithms to manage orders in an online bookstore:

- Queue for incoming orders (FIFO)
- Stack for packing items (LIFO)
- Sorting algorithms for order item organization (Selection and Bubble)
- Searching algorithms for order tracking (Linear and Binary)

### Project structure

```
Online-Bookstore/
  src/
    controller/        # UI & menus
    model/             # domain models
    ds/                # data structure implementations
    services/          # sorting/searching services
  out/                 # compiled .class output
  README.md
```

### How to build and run (Windows cmd)

```cmd
javac -encoding UTF-8 -d out src\model\*.java src\ds\*.java src\services\*.java src\controller\*.java
java -cp out controller.OrderProcessingSystem
```

### Using the program

1. Customer: view books, add to cart, place order, track order
2. Admin: view/create/edit/delete books, process next order or select a specific queued order by ID
3. Processing: select sorting algorithm (Selection or Bubble); packing demonstrated with stack (LIFO)
4. Tracking: enter Order ID; system searches Pending (Linear on queue snapshot) then Processed (Binary on sorted copy by id) and shows an order summary table with totals

Binary search sorts a copy of processed orders by `orderId` before searching (the original list order is preserved).

### Files of interest

- `ds/ArrayQueue.java`: Resizable circular array-based queue
- `ds/ArrayStack.java`: Resizable array-based stack
- `services/Sorter.java`: Selection and Bubble sort with stats
- `services/Searcher.java`: Linear and Binary search with stats
- `controller/OrderProcessingSystem.java`: Console UI tying everything together


