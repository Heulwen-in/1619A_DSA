package model;

import java.util.Objects;

public class BookItem {
    private String title;
    private String author;
    private int quantity;

    public BookItem(String title, String author, int quantity) {
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int q) { this.quantity = q; }

    @Override
    public String toString() {
        return String.format("%s by %s (x%d)", title, author, quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookItem)) return false;
        BookItem b = (BookItem) o;
        return Objects.equals(title, b.title) && Objects.equals(author, b.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
}


