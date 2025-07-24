import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class CustomerFrame extends JFrame {
    private HotelSystem hotelSystem;

    public CustomerFrame(HotelSystem hotelSystem) {
        this.hotelSystem = hotelSystem;
        setTitle("お客様用インターフェース");
        // ▼▼▼ ウィンドウを閉じる動作を変更 ▼▼▼
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // ▲▲▲ 変更ここまで ▲▲▲
        setSize(500, 400);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("ホテル予約", createReservationPanel());
        /* tabbedPane.addTab("予約確認・キャンセル", createConfirmationPanel()); */

        add(tabbedPane);
        setLocation(100, 100);
    }

    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        
        String[] roomTypeOptions = {"スタンダード", "スイート"};
        JComboBox<String> typeComboBox = new JComboBox<>(roomTypeOptions);
        
        // ▼▼▼ カレンダーをテキスト入力に戻す ▼▼▼
        JTextField checkInField = new JTextField(LocalDate.now().toString());
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(1).toString());
        // ▲▲▲ 変更ここまで ▲▲▲
        
        JTextField guestsField = new JTextField("1");
        JTextField roomsField = new JTextField("1");
        JButton reserveButton = new JButton("部屋を予約する");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        panel.add(new JLabel("部屋タイプ:"));
        panel.add(typeComboBox);
        panel.add(new JLabel("チェックイン日 (YYYY-MM-DD):"));
        panel.add(checkInField); // ▼▼▼ 変更 ▼▼▼
        panel.add(new JLabel("チェックアウト日 (YYYY-MM-DD):"));
        panel.add(checkOutField); // ▼▼▼ 変更 ▼▼▼
        panel.add(new JLabel("利用人数:"));
        panel.add(guestsField);
        panel.add(new JLabel("部屋数:"));
        panel.add(roomsField);
        panel.add(reserveButton);
        panel.add(new JScrollPane(resultArea));

        reserveButton.addActionListener(e -> {
            try {
                String type = (String) typeComboBox.getSelectedItem();

                // ▼▼▼ テキストから日付を取得するように戻す ▼▼▼
                LocalDate checkIn = LocalDate.parse(checkInField.getText());
                LocalDate checkOut = LocalDate.parse(checkOutField.getText());
                // ▲▲▲ 変更ここまで ▲▲▲

                int guests = Integer.parseInt(guestsField.getText());
                int requestedRooms = Integer.parseInt(roomsField.getText());

                long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
                if (nights <= 0) {
                    resultArea.setText("エラー: チェックアウト日はチェックイン日より後にしてください。");
                    return;
                }

                int capacityPerRoom = hotelSystem.getRoomCapacity(type);
                if (capacityPerRoom > 0 && guests > (capacityPerRoom * requestedRooms)) {
                    resultArea.setText(String.format(
                        "エラー: 人数が収容可能人数（%d名）を超えています。\n部屋数を増やすか、人数を減らしてください。",
                        capacityPerRoom * requestedRooms
                    ));
                    return;
                }


                RoomType type_of_room = hotelSystem.roomTypes.get(type);
                if (type_of_room == null) {
                    resultArea.setText("エラー: その部屋タイプは存在しません。");
                    return;
                }
                if (!type_of_room.hasAvailability(checkIn, checkOut, requestedRooms)) {
                    resultArea.setText(String.format("エラー: 指定された期間に%d部屋の空きがありません。", requestedRooms));
                    return;
                }

                long totalPrice = hotelSystem.calculateTotalPrice(type, checkIn, checkOut, requestedRooms);
                
                String confirmationMessage = String.format(
                    "以下の内容で予約します。よろしいですか？\n\n" +
                    "部屋タイプ: %s\n" +
                    "部屋数: %d部屋\n" +
                    "利用日: %s から %d 泊\n" +
                    "利用人数: %d 名\n" +
                    "料金合計: %,d 円",
                    type, requestedRooms, checkIn, nights, guests, totalPrice
                );

                int choice = JOptionPane.showConfirmDialog(
                    this, confirmationMessage, "予約内容の確認",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE
                );

                if (choice == JOptionPane.OK_OPTION) {
                    String result = hotelSystem.createReservation(type, checkIn, checkOut, guests, requestedRooms);
                    resultArea.setText(result);
                } else {
                    resultArea.setText("予約はキャンセルされました。");
                }

            } catch (DateTimeParseException | NumberFormatException ex) {
                resultArea.setText("エラー: 入力形式が正しくありません。");
            }
        });
        return panel;
    }

    /* private JPanel createConfirmationPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField reservationField = new JTextField(10);
        JButton findButton = new JButton("予約を検索");
        JTextArea resultArea = new JTextArea();
        JButton cancelButton = new JButton("この予約をキャンセル");
        
        resultArea.setEditable(false);
        cancelButton.setEnabled(false);

        topPanel.add(new JLabel("予約番号:"));
        topPanel.add(reservationField);
        topPanel.add(findButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.SOUTH);

        findButton.addActionListener(e -> {
            String resNum = reservationField.getText();
            Reservation res = hotelSystem.findReservation(resNum);
            
            if (res != null) {
                long totalPrice = hotelSystem.calculateTotalPrice(
                    res.getRoomType().getName(), res.getCheckInDate(), res.getCheckOutDate(), res.getNumberOfRooms()
                );
                String details = res.getDetails() + String.format("\n料金合計: %,d 円", totalPrice);
                resultArea.setText(details);
                
                if (res.getStatus() == Reservation.ReservationStatus.BOOKED) {
                    cancelButton.setEnabled(true);
                } else {
                    cancelButton.setEnabled(false);
                }
            } else {
                resultArea.setText("エラー: 予約が見つかりません。");
                cancelButton.setEnabled(false);
            }
        });

        cancelButton.addActionListener(e -> {
            String resNum = reservationField.getText();
            int choice = JOptionPane.showConfirmDialog(
                this, "本当にこの予約をキャンセルしますか？\nこの操作は元に戻せません。", "キャンセル確認",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE
            );
            
            if (choice == JOptionPane.OK_OPTION) {
                String result = hotelSystem.cancelReservation(resNum);
                resultArea.setText(result);
                reservationField.setText("");
                cancelButton.setEnabled(false);
            }
        });
        
        return panel;
    } */
}