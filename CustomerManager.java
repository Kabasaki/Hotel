// CustomerManager.java (Đã chỉnh sửa với layout mới)
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

    // Fonts và Colors
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    
    private static final Color COLOR_ADD = new Color(40, 167, 69);
    private static final Color COLOR_UPDATE = new Color(255, 193, 7);
    private static final Color COLOR_DELETE = new Color(220, 53, 69);
    private static final Color COLOR_REFRESH = new Color(23, 162, 184);
    private static final Color COLOR_SEARCH = new Color(0, 123, 255);

    public CustomerManager() {
        // Sử dụng BorderLayout
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Tiêu đề (NORTH)
        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // 2. Bảng dữ liệu (CENTER)
        model = new DefaultTableModel(new String[]{"ID", "Tên KH", "SĐT", "Email", "Địa chỉ"}, 0);
        table = new JTable(model);
        table.setFont(FONT_FIELD);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        // 3. Panel Form và Chức năng (EAST)
        JPanel eastPanel = new JPanel(new BorderLayout(10, 10));
        eastPanel.setBackground(Color.WHITE);
        eastPanel.setPreferredSize(new Dimension(350, 0)); // Set chiều rộng ưu tiên

        // 3a. Form nhập liệu
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
        formPanel.add(createLabel("Tên KH:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        nameField = createTextField(); formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("SĐT:"), gbc);
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
        
        // Nút CRUD
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

        // 3b. Panel Tìm kiếm
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

        // Load dữ liệu
        refreshData();

        // Thêm sự kiện
        addBtn.addActionListener(e -> addCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        delBtn.addActionListener(e -> deleteCustomer());
        refreshBtn.addActionListener(e -> refreshData());
        searchBtn.addActionListener(e -> searchCustomer());
        
        // Sự kiện click vào bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    nameField.setText(model.getValueAt(row, 1).toString());
                    phoneField.setText(model.getValueAt(row, 2).toString());
                    emailField.setText(model.getValueAt(row, 3).toString());
                    addrField.setText(model.getValueAt(row, 4).toString());
                }
            }
        });
    }
    
    // Helper tạo components
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

        // [ĐÃ SỬA] Thêm 2 dòng này để đảm bảo màu nền và chữ hiển thị đúng
        // trên các Look & Feel khác nhau (như Windows)
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    // Các hàm xử lý logic (giữ nguyên)
    private void addCustomer() {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)");
            ps.setString(1, nameField.getText());
            ps.setString(2, phoneField.getText());
            ps.setString(3, emailField.getText());
            ps.setString(4, addrField.getText());
            ps.executeUpdate();
            refreshData();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("UPDATE customers SET name=?, phone=?, email=?, address=? WHERE id=?");
            ps.setString(1, nameField.getText());
            ps.setString(2, phoneField.getText());
            ps.setString(3, emailField.getText());
            ps.setString(4, addrField.getText());
            ps.setInt(5, id);
            ps.executeUpdate();
            refreshData();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            refreshData();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void searchCustomer() {
        try (Connection c = DBConnection.getConnection()) {
            String key = "%" + searchField.getText() + "%";
            PreparedStatement ps = c.prepareStatement("SELECT * FROM customers WHERE name LIKE ? OR phone LIKE ? OR email LIKE ?");
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshData() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers")) {
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
        } catch (Exception e) { e.printStackTrace(); }
        clearFields();
    }
    
    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addrField.setText("");
        table.clearSelection();
    }
}
