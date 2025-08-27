package model;

public class Book {
    private String title;
    private String author;
    private int stock;
    private double price;

    public Book(String title, String author, int stock, double price) {
        this.title = title;
        this.author = author;
        this.stock = stock;
        this.price = price;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getStock() { return stock; }
    public double getPrice() { return price; }
    public void reduceStock(int q) { stock -= q; }
    public void setPrice(double p) { this.price = p; }

    @Override
    public String toString() {
        return title + " by " + author + " - $" + String.format("%.2f", price) + " (Available: " + stock + ")";
    }
}


