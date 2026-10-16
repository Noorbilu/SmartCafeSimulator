package model;

public class Item {
    private int itemId;
    private int categoryId;
    private String categoryName; // populated via JOIN for display
    private String itemName;
    private double price;
    private boolean isCombo;
    private boolean isAvailable;
    private int stock; // populated via JOIN for display (not a DB column here)

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isCombo() { return isCombo; }
    public void setCombo(boolean combo) { isCombo = combo; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}