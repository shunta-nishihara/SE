import java.time.LocalDate;

public class Reservation {
    public enum ReservationStatus {
        BOOKED("予約済み"),
        CHECKED_IN("チェックイン済み");

        private final String displayName;
        ReservationStatus(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private String reservationNumber;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private int numberOfRooms;
    private ReservationStatus status;

    public Reservation(String reservationNumber, RoomType roomType, LocalDate checkIn, LocalDate checkOut, int guests, int rooms) {
        this.reservationNumber = reservationNumber;
        this.roomType = roomType;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.numberOfGuests = guests;
        this.numberOfRooms = rooms;
        this.status = ReservationStatus.BOOKED;
    }

    public String getReservationNumber() { return reservationNumber; }
    public RoomType getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public int getNumberOfRooms() { return numberOfRooms; }
    public ReservationStatus getStatus() { return status; }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getDetails() {
        return String.format("予約番号: %s\nステータス: %s\n部屋タイプ: %s (%d部屋)\n利用日: %s - %s\n利用人数: %d名",
                reservationNumber, status.getDisplayName(), roomType.getName(), numberOfRooms, checkInDate, checkOutDate, numberOfGuests);
    }
}