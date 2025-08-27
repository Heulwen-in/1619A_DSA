package services;

import java.util.List;
import model.Order;

public class Searcher {
    public static class Stats {
        public long comparisons = 0;
        public long timeNs = 0;
    }

    public static int linearSearch(List<Order> list, String orderId, Stats s) {
        long start = System.nanoTime();
        for (int i = 0; i < list.size(); i++) {
            s.comparisons++;
            if (list.get(i).getOrderId().equals(orderId)) {
                s.timeNs = System.nanoTime() - start;
                return i;
            }
        }
        s.timeNs = System.nanoTime() - start;
        return -1;
    }

    public static int binarySearch(List<Order> list, String orderId, Stats s) {
        long start = System.nanoTime();
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            s.comparisons++;
            int cmp = list.get(mid).getOrderId().compareTo(orderId);
            if (cmp == 0) {
                s.timeNs = System.nanoTime() - start;
                return mid;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        s.timeNs = System.nanoTime() - start;
        return -1;
    }
}


