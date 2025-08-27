package services;

import java.util.List;
import model.BookItem;

public class Sorter {
    public static class Stats {
        public long comparisons = 0;
        public long swaps = 0;
        public long timeNs = 0;
    }

    public static Stats selectionSort(List<BookItem> list) {
        Stats s = new Stats();
        long start = System.nanoTime();
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                s.comparisons++;
                if (list.get(j).getTitle().compareToIgnoreCase(list.get(minIdx).getTitle()) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                BookItem tmp = list.get(i);
                list.set(i, list.get(minIdx));
                list.set(minIdx, tmp);
                s.swaps++;
            }
        }
        s.timeNs = System.nanoTime() - start;
        return s;
    }

    // Bubble sort by title (optimized early exit)
    public static Stats bubbleSort(List<BookItem> list) {
        Stats s = new Stats();
        long start = System.nanoTime();
        int n = list.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                s.comparisons++;
                if (list.get(j).getTitle().compareToIgnoreCase(list.get(j + 1).getTitle()) > 0) {
                    BookItem tmp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, tmp);
                    s.swaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        s.timeNs = System.nanoTime() - start;
        return s;
    }
}


