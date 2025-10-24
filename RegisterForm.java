// RegisterForm.java (Đã chỉnh sửa với giao diện đẹp hơn)
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterForm extends JFrame {
    private JTextField userField;
    private JPasswordField passField, confirmPassField;
    private JLabel message;
    private JButton registerBtn;

    // Màu sắc và Fonts (Tương tự LoginForm)
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);


    public RegisterForm() {
        setTitle("Đăng ký tài khoản");
        setSize(450, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sử dụng GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel title = new JLabel("ĐĂNG KÝ", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(title, gbc);

        // Tài khoản
        JLabel userLabel = new JLabel("Tài khoản:");
        userLabel.setFont(FONT_LABEL);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(userLabel, gbc);

        userField = new JTextField(20);
        userField.setFont(FONT_FIELD);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(userField, gbc);

        // Mật khẩu
        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setFont(FONT_LABEL);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passLabel, gbc);

        passField = new JPasswordField();
        passField.setFont(FONT_FIELD);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(passField, gbc);

        // Xác nhận Mật khẩu
        JLabel confirmLabel = new JLabel("Xác nhận:");
        confirmLabel.setFont(FONT_LABEL);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(confirmLabel, gbc);

        confirmPassField = new JPasswordField();
        confirmPassField.setFont(FONT_FIELD);
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(confirmPassField, gbc);

        // Nút Đăng ký
        registerBtn = new JButton("Đăng ký");
        styleButton(registerBtn, PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(registerBtn, gbc);

        // Thông báo
        message = new JLabel("", SwingConstants.CENTER);
        message.setFont(FONT_LABEL);
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(message, gbc);
        
        add(mainPanel);

        registerBtn.addActionListener(e -> registerUser());
    }
    
    // Helper để tạo kiểu cho nút
    private void styleButton(JButton button, Color background) {
        button.setFont(FONT_BUTTON);
        button.setBackground(background);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void registerUser() {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        String confirm = new String(confirmPassField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            message.setText("Vui lòng nhập đầy đủ thông tin!");
            message.setForeground(ERROR_COLOR);
            return;
        }

        if (!password.equals(confirm)) {
            message.setText("❌ Mật khẩu không khớp!");
            message.setForeground(ERROR_COLOR);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT * FROM users WHERE username=?");
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                message.setText("❌ Tài khoản đã tồn tại!");
                message.setForeground(ERROR_COLOR);
                return;
            }

            PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            insert.setString(1, username);
            insert.setString(2, password);
            insert.executeUpdate();

            message.setText("✅ Đăng ký thành công!");
            message.setForeground(SUCCESS_COLOR);

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Lỗi: " + e.getMessage());
            message.setForeground(ERROR_COLOR);
        }
    }
}
