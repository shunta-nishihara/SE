// RoomInfo.java
import java.time.LocalDate;

public class RoomInfo {
    private final LocalDate date;
    private final int price; // この日の確定料金
    private int availability; // この日の空室数

    public RoomInfo(LocalDate date, int price, int initialAvailability) {
        this.date = date;
        this.price = price;
        this.availability = initialAvailability;
    }

    // --- Getters ---
    public LocalDate getDate() {
        return date;
    }

    public int getPrice() {
        return price;
    }

    public int getAvailability() {
        return availability;
    }

    // --- 在庫操作メソッド ---
    public void decreaseAvailability(int rooms) {
        if (this.availability >= rooms) {
            this.availability -= rooms;
        }
    }

    public void increaseAvailability(int rooms) {
        this.availability += rooms;
    }
}