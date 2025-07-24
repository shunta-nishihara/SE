import javax.swing.*;
import java.awt.*;

public class StaffFrame extends JFrame {
    private HotelSystem hotelSystem;

    public StaffFrame(HotelSystem hotelSystem) {
        this.hotelSystem = hotelSystem;
        setTitle("ホテルスタッフ用インターフェース");
        // ▼▼▼ ウィンドウを閉じる動作を変更 ▼▼▼
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // ▲▲▲ 変更ここまで ▲▲▲
        setSize(500, 400);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("チェックイン", createCheckInPanel());
        tabbedPane.addTab("チェックアウト", createCheckoutPanel());

        add(tabbedPane);
        setLocation(650, 100);
    }
    
    private JPanel createCheckInPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField reservationField = new JTextField(10);
        JButton findButton = new JButton("予約を調査");
        JTextArea resultArea = new JTextArea();
        JButton checkInButton = new JButton("チェックインを実行する");

        resultArea.setEditable(false);
        checkInButton.setEnabled(false);

        topPanel.add(new JLabel("予約番号:"));
        topPanel.add(reservationField);
        topPanel.add(findButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(checkInButton, BorderLayout.SOUTH);
        
        findButton.addActionListener(e -> {
            String resNum = reservationField.getText();
            Reservation res = hotelSystem.findReservation(resNum);
            if (res != null) {
                resultArea.setText(res.getDetails());
                if (res.getStatus() == Reservation.ReservationStatus.BOOKED) {
                    checkInButton.setEnabled(true);
                } else {
                    checkInButton.setEnabled(false);
                }
            } else {
                resultArea.setText("エラー: 予約が見つかりません。");
                checkInButton.setEnabled(false);
            }
        });

        checkInButton.addActionListener(e -> {
            String resNum = reservationField.getText();
            hotelSystem.processCheckIn(resNum);
            
            Reservation updatedRes = hotelSystem.findReservation(resNum);
            if (updatedRes != null) {
                resultArea.setText(updatedRes.getDetails() + "\n\nチェックイン処理が完了しました。");
            } else {
                resultArea.setText("チェックイン処理が完了しました。");
            }
            checkInButton.setEnabled(false);
        });

        return panel;
    }

    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField reservationField = new JTextField(10);
        JButton checkoutButton = new JButton("チェックアウト");
        JTextArea resultArea = new JTextArea("スタッフは【手作業で】部屋番号から予約番号を調べ、ここに入力します。");
        resultArea.setEditable(false);
        
        topPanel.add(new JLabel("予約番号:"));
        topPanel.add(reservationField);
        topPanel.add(checkoutButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        
        checkoutButton.addActionListener(e -> {
            String resNum = reservationField.getText();
            String result = hotelSystem.processCheckout(resNum);
            resultArea.setText(result);
        });
        return panel;
    }
}