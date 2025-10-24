// InvoiceManager.java (Đã chỉnh sửa với layout mới)
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class InvoiceManager extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton generateBtn, refreshBtn;
    
    // Fonts và Colors (Tương tự)
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    
    private static final Color COLOR_GENERATE = new Color(0, 123, 255);
    private static final Color COLOR_REFRESH = new Color(23, 162, 184);

    public InvoiceManager() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Tiêu đề (NORTH)
        JLabel title = new JLabel("HÓA ĐƠN & THỐNG KÊ", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // 2. Bảng (CENTER)
        model = new DefaultTableModel(new String[]{"ID", "BookingID", "Tổng phòng", "Tổng dịch vụ", "Tổng hóa đơn", "Ngày tạo"}, 0);
        table = new JTable(model);
        table.setFont(FONT_FIELD);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        // 3. Panel Nút (SOUTH)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        generateBtn = createButton("Tạo hóa đơn từ đặt phòng", COLOR_GENERATE);
        refreshBtn = createButton("Làm mới", COLOR_REFRESH);
        
        buttonPanel.add(generateBtn);
        buttonPanel.add(refreshBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        refreshData();

        // Sự kiện
        generateBtn.addActionListener(e -> generateInvoices());
        refreshBtn.addActionListener(e -> refreshData());
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
    // Các hàm logic (giữ nguyên)
    private void generateInvoices() {
        try (Connection c = DBConnection.getConnection()) {
            Statement st = c.createStatement();
            // Chỉ lấy các booking CHƯA có trong bảng invoices
            ResultSet bookings = st.executeQuery(
                "SELECT * FROM bookings b WHERE NOT EXISTS (SELECT 1 FROM invoices i WHERE i.booking_id = b.id)"
            );
            
            int count = 0;
            while (bookings.next()) {
                int bookingId = bookings.getInt("id");
                double roomTotal = bookings.getDouble("total_cost");

                PreparedStatement ps = c.prepareStatement(
                        "SELECT SUM(s.price*u.quantity) AS total_service FROM service_usage u JOIN services s ON u.service_id=s.id WHERE booking_id=?");
                ps.setInt(1, bookingId);
                ResultSet rs = ps.executeQuery();
                double serviceTotal = rs.next() ? rs.getDouble("total_service") : 0;

                double totalAmount = roomTotal + serviceTotal;

                // Không cần check nữa vì câu SQL đã lọc
                PreparedStatement insert = c.prepareStatement(
                        "INSERT INTO invoices (booking_id, total_room, total_service, total_amount, date_created) VALUES (?,?,?,?,?)");
                insert.setInt(1, bookingId);
                insert.setDouble(2, roomTotal);
                insert.setDouble(3, serviceTotal);
                insert.setDouble(4, totalAmount);
                insert.setDate(5, Date.valueOf(LocalDate.now()));
                insert.executeUpdate();
                count++;
            }
            
            if (count > 0) {
                JOptionPane.showMessageDialog(this, "✅ Đã tạo " + count + " hóa đơn mới!");
            } else {
                JOptionPane.showMessageDialog(this, "Không có đặt phòng mới nào để tạo hóa đơn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            
            refreshData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshData() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM invoices")) {
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("total_room"),
                        rs.getDouble("total_service"),
                        rs.getDouble("total_amount"),
                        rs.getDate("date_created")
                });
        } catch (Exception e) { e.printStackTrace(); }
    }
}
