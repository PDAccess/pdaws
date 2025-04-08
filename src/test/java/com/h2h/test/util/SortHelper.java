package com.h2h.test.util;

public class SortHelper {
    private boolean sorted;
    private boolean unsorted;
    private boolean empty;

    public boolean isSorted() {
        return sorted;
    }

    public SortHelper setSorted(boolean sorted) {
        this.sorted = sorted;
        return this;
    }

    public boolean isUnsorted() {
        return unsorted;
    }

    public SortHelper setUnsorted(boolean unsorted) {
        this.unsorted = unsorted;
        return this;
    }

    public boolean isEmpty() {
        return empty;
    }

    public SortHelper setEmpty(boolean empty) {
        this.empty = empty;
        return this;
    }
}
