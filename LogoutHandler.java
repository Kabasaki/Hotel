// LogoutHandler.java (Không đổi)
import javax.swing.*;

public class LogoutHandler {

    public static void logout(JFrame currentFrame) {
        int confirm = JOptionPane.showConfirmDialog(
                currentFrame,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            currentFrame.dispose();
            SessionManager.clear();

            SwingUtilities.invokeLater(() -> {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            });
        }
    }
}
