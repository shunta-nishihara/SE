// RoomType.java
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class RoomType {
    // --- 部屋の種別としての情報 (テンプレート) ---
    private final String name;
    private final int capacity;
    private final int normalPrice;
    private final int busyPrice;
    
    // --- 日ごとの具体的な部屋情報(RoomInfo)を管理 ---
    private final Map<LocalDate, RoomInfo> dailyRoomInfo = new HashMap<>();

    public RoomType(String name, int capacity, int normalPrice, int busyPrice, int initialStock, LocalDate startDate, int days) {
        this.name = name;
        this.capacity = capacity;
        this.normalPrice = normalPrice;
        this.busyPrice = busyPrice;
        
        // 指定された期間のRoomInfoオブジェクトを生成してMapに格納
        Stream.iterate(startDate, date -> date.plusDays(1))
              .limit(days)
              .forEach(date -> {
                  int priceForDate = calculatePriceForDate(date); // その日の価格を計算
                  dailyRoomInfo.put(date, new RoomInfo(date, priceForDate, initialStock));
              });
    }

    // --- メソッド ---
    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    // 予約可能かチェックするロジック (対象日のRoomInfoに問い合わせる)
    public boolean hasAvailability(LocalDate checkIn, LocalDate checkOut, int requiredRooms) {
        return checkIn.isBefore(checkOut) &&
               checkIn.datesUntil(checkOut)
                      .allMatch(date -> {
                          RoomInfo info = dailyRoomInfo.get(date);
                          return info != null && info.getAvailability() >= requiredRooms;
                      });
    }

    // 在庫を減らす (対象日のRoomInfoに依頼する)
    public void decreaseAvailability(LocalDate checkIn, LocalDate checkOut, int roomsToBook) {
        checkIn.datesUntil(checkOut)
               .map(dailyRoomInfo::get)
               .filter(info -> info != null)
               .forEach(info -> info.decreaseAvailability(roomsToBook));
    }

    // 在庫を増やす (対象日のRoomInfoに依頼する)
    public void increaseAvailability(LocalDate checkIn, LocalDate checkOut, int roomsToRestore) {
        checkIn.datesUntil(checkOut)
               .map(dailyRoomInfo::get)
               .filter(info -> info != null)
               .forEach(info -> info.increaseAvailability(roomsToRestore));
    }

    // 特定の日の価格を計算する内部的なメソッド
    public int calculatePriceForDate(LocalDate date) {
        int month = date.getMonthValue();
        // 7月か8月は繁忙期価格
        if (month == 7 || month == 8) {
            return busyPrice;
        }
        return normalPrice;
    }
}