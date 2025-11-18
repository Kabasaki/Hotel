import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JLabel message;
    private JButton loginBtn;
    private JButton registerBtn;

    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    public LoginForm() {
        setTitle("Đăng nhập hệ thống");
        setSize(650, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        mainPanel.add(title, gbc);

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

        loginBtn = new JButton("Đăng nhập");
        styleButton(loginBtn, PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginBtn, gbc);

        registerBtn = new JButton("Đăng ký");
        styleButton(registerBtn, SECONDARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(registerBtn, gbc);

        message = new JLabel("", SwingConstants.CENTER);
        message.setFont(FONT_LABEL);
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(message, gbc);
        
        add(mainPanel);

        loginBtn.addActionListener(e -> checkLogin());
        registerBtn.addActionListener(e -> openRegisterForm());
        
        passField.addActionListener(e -> loginBtn.doClick());
        userField.addActionListener(e -> passField.requestFocus());
    }

    private void styleButton(JButton button, Color background) {
        button.setFont(FONT_BUTTON);
        button.setBackground(background);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void checkLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            message.setText("Vui lòng nhập đầy đủ thông tin!");
            message.setForeground(ERROR_COLOR);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = "User"; 
                String roleSql = "SELECT r.role_name FROM employees e JOIN roles r ON e.role_id = r.id WHERE e.username = ?";
                PreparedStatement rolePs = conn.prepareStatement(roleSql);
                rolePs.setString(1, username);
                ResultSet roleRs = rolePs.executeQuery();
                if (roleRs.next()) {
                    role = roleRs.getString("role_name");
                }

                SessionManager.setUser(username, role);

                dispose();
                new MainApp().setVisible(true);
            } else {
                message.setText("❌ Sai tài khoản hoặc mật khẩu!");
                message.setForeground(ERROR_COLOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    private void openRegisterForm() {
        new RegisterForm().setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBConnection.initTables();
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}


