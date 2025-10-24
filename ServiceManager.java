// ServiceManager.java (Đã cập nhật, không còn là placeholder)
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ServiceManager extends JPanel {
    private JTextField nameField, priceField;
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

    public ServiceManager() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("QUẢN LÝ DỊCH VỤ", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Tên dịch vụ", "Giá (VND)"}, 0);
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
                BorderFactory.createEtchedBorder(), "Thông tin dịch vụ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)
        ));
        formPanel.setPreferredSize(new Dimension(350, 0)); // Set chiều rộng
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Tên dịch vụ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        nameField = createTextField(); formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Giá:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        priceField = createTextField(); formPanel.add(priceField, gbc);

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
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.EAST);

        refreshData();
        
        addBtn.addActionListener(e -> addService());
        updateBtn.addActionListener(e -> updateService());
        deleteBtn.addActionListener(e -> deleteService());
        refreshBtn.addActionListener(e -> refreshData());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    nameField.setText(model.getValueAt(row, 1).toString());
                    priceField.setText(model.getValueAt(row, 2).toString());
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
        priceField.setText("");
        table.clearSelection();
    }

    private void addService() {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("INSERT INTO services (name, price) VALUES (?, ?)");
            ps.setString(1, nameField.getText());
            ps.setDouble(2, Double.parseDouble(priceField.getText()));
            ps.executeUpdate();
            refreshData();
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá phải là một con số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void updateService() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("UPDATE services SET name=?, price=? WHERE id=?");
            ps.setString(1, nameField.getText());
            ps.setDouble(2, Double.parseDouble(priceField.getText()));
            ps.setInt(3, id);
            ps.executeUpdate();
            refreshData();
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá phải là một con số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void deleteService() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa dịch vụ này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM services WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            refreshData();
            clearFields();
        } catch (Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Không thể xóa dịch vụ đã được sử dụng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM services")) {
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getDouble("price")
                });
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        clearFields();
    }
}

