import javax.swing.SwingUtilities;

public class MainLauncher {
    public static void main(String[] args) {
        HotelSystem sharedSystem = new HotelSystem();
        
        SwingUtilities.invokeLater(() -> {
            CustomerFrame customerFrame = new CustomerFrame(sharedSystem);
            customerFrame.setVisible(true);
            
            StaffFrame staffFrame = new StaffFrame(sharedSystem);
            staffFrame.setVisible(true);
        });
    }
}