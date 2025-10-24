import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class EmployeeManager extends JPanel {
    private JTextField nameField, emailField, phoneField, usernameField;
    private JPasswordField passField;
    private JComboBox<String> roleCombo;
    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn;
    
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    
    private static final Color COLOR_ADD = new Color(40, 167, 69);
    private static final Color COLOR_UPDATE = new Color(255, 193, 7);
    private static final Color COLOR_DELETE = new Color(220, 53, 69);
    private static final Color COLOR_REFRESH = new Color(23, 162, 184);

    public EmployeeManager() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("QUẢN LÝ NHÂN SỰ", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID","Tên","Email","SĐT","Username","Vai trò"},0);
        table = new JTable(model);
        table.setFont(FONT_FIELD);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin nhân viên",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)
        ));
        formPanel.setPreferredSize(new Dimension(350, 0)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Tên:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        nameField = createTextField(); formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        emailField = createTextField(); formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("SĐT:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        phoneField = createTextField(); formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        usernameField = createTextField(); formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        passField = new JPasswordField(); passField.setFont(FONT_FIELD); formPanel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createLabel("Vai trò:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        roleCombo = new JComboBox<>(); roleCombo.setFont(FONT_FIELD); formPanel.add(roleCombo, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        addBtn = createButton("Thêm", COLOR_ADD);
        updateBtn = createButton("Sửa", COLOR_UPDATE);
        deleteBtn = createButton("Xóa", COLOR_DELETE);
        refreshBtn = createButton("Làm mới", COLOR_REFRESH);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.EAST);

        loadRoles();
        refreshData();
        
        addBtn.addActionListener(e -> addEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());
        refreshBtn.addActionListener(e -> refreshData());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    nameField.setText(model.getValueAt(row, 1).toString());
                    emailField.setText(model.getValueAt(row, 2).toString());
                    phoneField.setText(model.getValueAt(row, 3).toString());
                    usernameField.setText(model.getValueAt(row, 4).toString());
                    roleCombo.setSelectedItem(model.getValueAt(row, 5).toString());
                    passField.setText(""); // Không hiển thị mật khẩu cũ
                }
            }
        });
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(15);
        field.setFont(FONT_FIELD);
        return field;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }
    
    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        usernameField.setText("");
        passField.setText("");
        roleCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void loadRoles() {
        try(Connection c = DBConnection.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT role_name FROM roles")) {
            roleCombo.removeAllItems();
            while(rs.next()) roleCombo.addItem(rs.getString("role_name"));
        } catch(Exception e){ e.printStackTrace();}
    }

    private void addEmployee() {
        String username = usernameField.getText();
        String password = new String(passField.getPassword());
        
        if (username.isEmpty() || password.isEmpty() || nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên, Username và Password không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try(Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false); 

            String role = roleCombo.getSelectedItem().toString();
            PreparedStatement ps1 = c.prepareStatement("SELECT id FROM roles WHERE role_name=?");
            ps1.setString(1, role);
            ResultSet rs = ps1.executeQuery();
            int roleId = rs.next() ? rs.getInt(1) : 1;

            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO employees (name,email,phone,username,password,role_id) VALUES (?,?,?,?,?,?)");
            ps.setString(1, nameField.getText());
            ps.setString(2, emailField.getText());
            ps.setString(3, phoneField.getText());
            ps.setString(4, username);
            ps.setString(5, password); 
            ps.setInt(6, roleId);
            ps.executeUpdate();

            PreparedStatement psUsers = c.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            psUsers.setString(1, username);
            psUsers.setString(2, password);
            psUsers.executeUpdate();

            c.commit(); 
            JOptionPane.showMessageDialog(this,"✅ Đã thêm nhân viên và tạo tài khoản đăng nhập.");
            refreshData();
            clearFields();
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Lỗi: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        int row = table.getSelectedRow(); 
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String oldUsername = model.getValueAt(row, 4).toString(); 

        String newUsername = usernameField.getText();
        String newPassword = new String(passField.getPassword());
        
        if (newUsername.isEmpty() || nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên và Username không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.isEmpty()) {
             try(Connection c = DBConnection.getConnection()) {
                PreparedStatement ps = c.prepareStatement("SELECT password FROM employees WHERE id=?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) newPassword = rs.getString("password");
             } catch(Exception e) { e.printStackTrace(); }
        }

        try(Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);

            String role = roleCombo.getSelectedItem().toString();
            PreparedStatement ps1 = c.prepareStatement("SELECT id FROM roles WHERE role_name=?");
            ps1.setString(1, role);
            ResultSet rs = ps1.executeQuery();
            int roleId = rs.next() ? rs.getInt(1) : 1;

            PreparedStatement ps = c.prepareStatement(
                    "UPDATE employees SET name=?,email=?,phone=?,username=?,password=?,role_id=? WHERE id=?");
            ps.setString(1, nameField.getText());
            ps.setString(2, emailField.getText());
            ps.setString(3, phoneField.getText());
            ps.setString(4, newUsername);
            ps.setString(5, newPassword);
            ps.setInt(6, roleId);
            ps.setInt(7, id);
            ps.executeUpdate();

            PreparedStatement psUsers = c.prepareStatement("UPDATE users SET username=?, password=? WHERE username=?");
            psUsers.setString(1, newUsername);
            psUsers.setString(2, newPassword);
            psUsers.setString(3, oldUsername);
            psUsers.executeUpdate();
            
            c.commit();
            JOptionPane.showMessageDialog(this, "✅ Cập nhật thành công!");
            refreshData();
            clearFields();
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow(); 
        if(row == -1) {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String username = model.getValueAt(row, 4).toString();

        try(Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);

            PreparedStatement ps = c.prepareStatement("DELETE FROM employees WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

            PreparedStatement psUsers = c.prepareStatement("DELETE FROM users WHERE username=?");
            psUsers.setString(1, username);
            psUsers.executeUpdate();

            c.commit();
            JOptionPane.showMessageDialog(this, "✅ Đã xóa nhân viên!");
            refreshData();
            clearFields();
        } catch(Exception e){ e.printStackTrace(); }
    }
    
    private void refreshData() {
        try(Connection c = DBConnection.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT e.id,e.name,e.email,e.phone,e.username,r.role_name FROM employees e JOIN roles r ON e.role_id=r.id")) {
            model.setRowCount(0);
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
        } catch(Exception e){ e.printStackTrace();}
        clearFields();
    }
}
