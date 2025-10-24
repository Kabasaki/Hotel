// LogoutHandler.java (Không đổi)
import javax.swing.*;

public class LogoutHandler {

    // Phương thức tĩnh để đăng xuất
    public static void logout(JFrame currentFrame) {
        int confirm = JOptionPane.showConfirmDialog(
                currentFrame,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Đóng cửa sổ hiện tại (MainApp)
            currentFrame.dispose();

            // Xóa thông tin người dùng 
            // THAY ĐỔI: Gọi hàm clear() của SessionManager
            SessionManager.clear();

            // Mở lại màn hình đăng nhập
            SwingUtilities.invokeLater(() -> {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            });
        }
    }
}
