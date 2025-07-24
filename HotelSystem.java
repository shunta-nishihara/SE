import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HotelSystem {
    public Map<String, RoomType> roomTypes = new HashMap<>();
    public Map<String, Reservation> reservations = new HashMap<>();

    public HotelSystem() {
        LocalDate today = LocalDate.now();
        roomTypes.put("スタンダード", new RoomType("スタンダード", 4, 30000, 45000, 15, today, 500));
        roomTypes.put("スイート", new RoomType("スイート", 6, 150000, 200000, 5, today, 500));
    }
    
    public Reservation findReservation(String reservationNumber) {
        return reservations.get(reservationNumber);
    }
    
    public String getReservationDetailsForCheckIn(String reservationNumber) {
        Reservation res = findReservation(reservationNumber);
        return (res != null) ? res.getDetails() : "エラー: 予約が見つかりません。";
    }

    public int getRoomCapacity(String typeName) {
        RoomType type = roomTypes.get(typeName);
        return (type != null) ? type.getCapacity() : 0;
    }
    
    public long calculateTotalPrice(String typeName, LocalDate checkIn, LocalDate checkOut, int numberOfRooms) {
        RoomType type = roomTypes.get(typeName);
        if (type == null || !checkIn.isBefore(checkOut) || numberOfRooms <= 0) {
            return 0;
        }
        long singleRoomPrice = checkIn.datesUntil(checkOut)
                                      .mapToLong(date -> type.calculatePriceForDate(date))
                                      .sum();
        return singleRoomPrice * numberOfRooms;
    }

    public String createReservation(String typeName, LocalDate checkIn, LocalDate checkOut, int guests, int numberOfRooms) {
        RoomType type = roomTypes.get(typeName);
        type.decreaseAvailability(checkIn, checkOut, numberOfRooms);
        String reservationNumber = UUID.randomUUID().toString().substring(0, 6);
        Reservation newReservation = new Reservation(reservationNumber, type, checkIn, checkOut, guests, numberOfRooms);
        reservations.put(reservationNumber, newReservation);
        return "予約が完了しました。\n予約番号: " + reservationNumber;
    }
    
    public String processCheckIn(String reservationNumber) {
        Reservation res = findReservation(reservationNumber);
        if (res == null) {
            return "エラー: 予約が見つかりません。";
        }
        if (res.getStatus() == Reservation.ReservationStatus.CHECKED_IN) {
            return "エラー: この予約はすでにチェックイン済みです。";
        }
        res.setStatus(Reservation.ReservationStatus.CHECKED_IN);
        return "予約番号: " + reservationNumber + " のチェックイン処理が完了しました。";
    }
    
    public String processCheckout(String reservationNumber) {
        Reservation res = reservations.get(reservationNumber);
        if (res == null) return "エラー: 予約が見つかりません。";
        
        // ▼▼▼ ステータス確認のロジックを追加 ▼▼▼
        if (res.getStatus() != Reservation.ReservationStatus.CHECKED_IN) {
            return "エラー: この予約はまだチェックインされていません。";
        }
        // ▲▲▲ 追加ここまで ▲▲▲
        
        long totalPrice = calculateTotalPrice(
            res.getRoomType().getName(),
            res.getCheckInDate(),
            res.getCheckOutDate(),
            res.getNumberOfRooms()
        );
        
        reservations.remove(reservationNumber);
        
        return String.format("料金合計: %,d 円\nチェックアウト処理が完了し、予約情報は削除されました。", totalPrice);
    }

    public String cancelReservation(String reservationNumber) {
        Reservation res = findReservation(reservationNumber);
        if (res == null) {
            return "エラー: その予約番号は見つかりません。";
        }
        if (res.getStatus() != Reservation.ReservationStatus.BOOKED) {
            return "エラー: チェックイン済みの予約はキャンセルできません。";
        }

        RoomType type = res.getRoomType();
        type.increaseAvailability(res.getCheckInDate(), res.getCheckOutDate(), res.getNumberOfRooms());
        reservations.remove(reservationNumber);
        return "予約番号: " + reservationNumber + " の予約をキャンセルしました。";
    }
}