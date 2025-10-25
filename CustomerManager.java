import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerManager extends JPanel {
    private JTextField nameField, phoneField, emailField, addrField, searchField;
    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, updateBtn, delBtn, refreshBtn, searchBtn;

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    
    private static final Color COLOR_ADD = new Color(40, 167, 69);
    private static final Color COLOR_UPDATE = new Color(255, 152, 0); 
    private static final Color COLOR_DELETE = new Color(220, 53, 69);
    private static final Color COLOR_REFRESH = new Color(23, 162, 184);
    private static final Color COLOR_SEARCH = new Color(0, 123, 255);

    public CustomerManager() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Tên KH", "SĐT", "Email", "Địa chỉ"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(model);
        table.setFont(FONT_FIELD);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new BorderLayout(10, 10));
        eastPanel.setBackground(Color.WHITE);
        eastPanel.setPreferredSize(new Dimension(350, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin khách hàng",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Tên KH (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        nameField = createTextField(); formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("SĐT (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        phoneField = createTextField(); formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        emailField = createTextField(); formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        addrField = createTextField(); formPanel.add(addrField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        addBtn = createButton("Thêm", COLOR_ADD);
        updateBtn = createButton("Sửa", COLOR_UPDATE);
        delBtn = createButton("Xóa", COLOR_DELETE);
        refreshBtn = createButton("Làm mới", COLOR_REFRESH);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(refreshBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        eastPanel.add(formPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Tìm kiếm",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)
        ));
        searchField = createTextField();
        searchBtn = createButton("Tìm", COLOR_SEARCH);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        
        eastPanel.add(searchPanel, BorderLayout.SOUTH);
        
        add(eastPanel, BorderLayout.EAST);

        refreshData();

        addBtn.addActionListener(e -> addCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        delBtn.addActionListener(e -> deleteCustomer());
        refreshBtn.addActionListener(e -> refreshData());
        searchBtn.addActionListener(e -> searchCustomer());
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    nameField.setText(model.getValueAt(row, 1).toString());
                    phoneField.setText(model.getValueAt(row, 2).toString());
                    emailField.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
                    addrField.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
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

    private void addCustomer() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addrField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên (*) và Số điện thoại (*) là bắt buộc.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, address.isEmpty() ? null : address);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "✅ Đã thêm khách hàng thành công!");
            refreshData();
            clearFields();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Lỗi: Số điện thoại hoặc Email đã tồn tại.", "Trùng lặp dữ liệu", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addrField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên (*) và Số điện thoại (*) là bắt buộc.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("UPDATE customers SET name=?, phone=?, email=?, address=? WHERE id=?");
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, address.isEmpty() ? null : address);
            ps.setInt(5, id);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "✅ Cập nhật khách hàng thành công!");
            refreshData();
            clearFields();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Lỗi: Số điện thoại hoặc Email đã tồn tại.", "Trùng lặp dữ liệu", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } catch (Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khách hàng này?\n(Lưu ý: Không thể xóa nếu khách hàng đã có booking)", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "✅ Đã xóa khách hàng thành công.");
            refreshData();
            clearFields();
        } catch (SQLException e) {
             if (e.getErrorCode() == 1451) { // Lỗi Foreign Key Constraint
                 JOptionPane.showMessageDialog(this, "Lỗi: Không thể xóa khách hàng đã có booking.", "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCustomer() {
        try (Connection c = DBConnection.getConnection()) {
            String key = "%" + searchField.getText().trim() + "%";
            PreparedStatement ps = c.prepareStatement("SELECT * FROM customers WHERE name LIKE ? OR phone LIKE ? OR email LIKE ?");
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
        } catch (Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers")) {
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
        } catch (Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lại dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        clearFields();
    }
    
    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addrField.setText("");
        searchField.setText("");
        table.clearSelection();
    }
}

