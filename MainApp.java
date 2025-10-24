// MainApp.java (Đã chỉnh sửa)
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class MainApp extends JFrame {
    private JButton logoutButton;
    private JLabel welcomeLabel;

    public MainApp() {
        setTitle("HỆ THỐNG QUẢN LÝ KHÁCH SẠN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Kích thước lớn hơn cho giao diện chính
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Thanh trên cùng (header)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(52, 58, 64)); // Màu nền tối
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Lấy tên người dùng từ SessionManager
        String currentUser = SessionManager.getUsername();
        String userRole = SessionManager.getRole();
        
        welcomeLabel = new JLabel("Xin chào, " + (currentUser != null ? currentUser : "Người dùng") + " (" + userRole + ")");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE); // Chữ trắng
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        // Nút đăng xuất (bên phải)
        logoutButton = new JButton("Đăng xuất");
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(220, 53, 69)); // Màu đỏ
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        logoutButton.addActionListener(e -> LogoutHandler.logout(this));

        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Nội dung chính với các Tab
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tab.setTabPlacement(JTabbedPane.LEFT); // Đặt tab bên trái cho hiện đại hơn
        tab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thêm các tab
        tab.add("Khách hàng", new CustomerManager());
        tab.add("Đặt phòng", new BookingManager());
        tab.add("Dịch vụ", new ServiceManager()); // Thêm file ServiceManager
        tab.add("Hóa đơn / Thống kê", new InvoiceManager());
        
        // Chỉ thêm tab Nhân sự nếu có quyền "admin" (phân biệt hoa thường)
        if ("Admin".equalsIgnoreCase(userRole)) {
            tab.add("Nhân sự", new EmployeeManager());
        }

        add(tab, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Đảm bảo L&F đã được set trong LoginForm
        SwingUtilities.invokeLater(() -> {
            // Chỉ chạy MainApp nếu đã đăng nhập (dành cho test)
            // Trong thực tế, luôn chạy từ LoginForm
            // new MainApp().setVisible(true);
            
            // Chạy LoginForm làm điểm bắt đầu
            LoginForm.main(args);
        });
    }
}
