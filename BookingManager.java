// BookingManager.java (Đã thêm chức năng chọn dịch vụ)
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BookingManager extends JPanel {
    private JTextField customerIdField;
    private JTextField checkInField, checkOutField;
    private JComboBox<String> roomTypeCombo;
    private JComboBox<RoomItem> roomNumberCombo; // Dùng class RoomItem để lưu ID và Tên
    private JButton findRoomsBtn;

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn;

    // [MỚI] Components cho Dịch vụ
    private JComboBox<ServiceItem> serviceCombo;
    private JSpinner quantitySpinner;
    private JButton addServiceBtn, removeServiceBtn;
    private JTable bookingServicesTable;
    private DefaultTableModel bookingServicesModel;


    // Fonts và Colors (Tương tự CustomerManager)
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    
    private static final Color COLOR_ADD = new Color(40, 167, 69);
    // [ĐÃ SỬA] Đổi màu vàng sáng thành màu cam đậm hơn để dễ đọc chữ trắng
    private static final Color COLOR_UPDATE = new Color(255, 152, 0); 
    private static final Color COLOR_DELETE = new Color(220, 53, 69);
    private static final Color COLOR_REFRESH = new Color(23, 162, 184);
    private static final Color COLOR_SEARCH = new Color(0, 123, 255);

    /**
     * Class nội bộ để lưu ID và Số phòng trong JComboBox
     */
    private class RoomItem {
        // ... (existing code) ...
        int id;
        String roomNumber;

        RoomItem(int id, String roomNumber) {
            this.id = id;
            this.roomNumber = roomNumber;
        }

        @Override
        public String toString() {
            return roomNumber; // Đây là thứ sẽ hiển thị trong ComboBox
        }
    }

    /**
     * [MỚI] Class nội bộ để lưu ID và Tên Dịch vụ trong JComboBox
     */
    private class ServiceItem {
        int id;
        String name;
        double price;

        ServiceItem(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            // Hiển thị tên và giá
            return name + " (" + String.format("%,.0f", price) + " VND)";
        }
    }


    public BookingManager() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Tiêu đề (NORTH)
        // ... (existing code) ...
        JLabel title = new JLabel("QUẢN LÝ ĐẶT PHÒNG", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);
        
        // 2. Bảng (CENTER)
        // Cập nhật cột
        // ... (existing code) ...
        model = new DefaultTableModel(new String[]{"ID","Customer ID","Số phòng","Loại phòng","Check-in","Check-out","Days","Total"},0);
        table = new JTable(model);
        table.setFont(FONT_FIELD);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        // 3. Panel phía Nam (SOUTH) - Chứa cả Form Booking và Form Dịch vụ
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(Color.WHITE);

        // 3a. Form nhập liệu Booking (Phần trên của South)
        JPanel bookingFormPanel = createBookingFormPanel();
        southPanel.add(bookingFormPanel, BorderLayout.NORTH);

        // 3b. [MỚI] Form nhập liệu Dịch vụ (Phần giữa của South)
        JPanel servicePanel = createServicePanel();
        southPanel.add(servicePanel, BorderLayout.CENTER);
        
        // 3c. [MỚI] Panel nút CRUD (Phần dưới của South)
        JPanel crudButtonPanel = createCrudButtonPanel();
        southPanel.add(crudButtonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
        
        // Load data
        loadRoomTypes(); // Tải danh sách loại phòng
        loadServices(); // [MỚI] Tải danh sách dịch vụ
        refreshData();

        // Sự kiện
        findRoomsBtn.addActionListener(e -> updateAvailableRooms());
        addBtn.addActionListener(e -> addBooking());
        updateBtn.addActionListener(e -> updateBooking());
        deleteBtn.addActionListener(e -> deleteBooking());
        refreshBtn.addActionListener(e -> refreshData());

        // [MỚI] Sự kiện cho nút dịch vụ
        addServiceBtn.addActionListener(e -> addServiceToBookingTable());
        removeServiceBtn.addActionListener(e -> removeServiceFromBookingTable());

        // Sự kiện click bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    customerIdField.setText(model.getValueAt(row, 1).toString());
                    
                    // Lấy RoomItem từ bảng (đã được lưu khi refreshData)
                    RoomItem selectedRoom = (RoomItem) model.getValueAt(row, 2);
                    String roomType = model.getValueAt(row, 3).toString();
                    
                    // Format Date to String
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    checkInField.setText(sdf.format(model.getValueAt(row, 4)));
                    checkOutField.setText(sdf.format(model.getValueAt(row, 5)));

                    // Cập nhật ComboBox
                    roomTypeCombo.setSelectedItem(roomType);
                    
                    // Hiển thị phòng đang được chọn
                    roomNumberCombo.removeAllItems();
                    roomNumberCombo.addItem(selectedRoom);
                    roomNumberCombo.setSelectedItem(selectedRoom);

                    // [MỚI] Tải dịch vụ cho booking này
                    int bookingId = (int) model.getValueAt(row, 0);
                    loadBookingServices(bookingId);
                }
            }
        });
    }

    /**
     * Tạo panel form cho thông tin đặt phòng (khách, ngày, phòng)
     */
    private JPanel createBookingFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin đặt phòng",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Customer ID và Check-in
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Customer ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        customerIdField = createTextField(); formPanel.add(customerIdField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(createLabel("Check-in (yyyy-mm-dd):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        checkInField = createTextField(); formPanel.add(checkInField, gbc);

        // Hàng 2: Loại phòng và Check-out
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Loại phòng:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        roomTypeCombo = new JComboBox<>(); roomTypeCombo.setFont(FONT_FIELD); formPanel.add(roomTypeCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(createLabel("Check-out (yyyy-mm-dd):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        checkOutField = createTextField(); formPanel.add(checkOutField, gbc);

        // Hàng 3: Số phòng và Nút tìm
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("Số phòng (trống):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        roomNumberCombo = new JComboBox<>(); roomNumberCombo.setFont(FONT_FIELD); formPanel.add(roomNumberCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2;
        findRoomsBtn = createButton("Tìm phòng trống", COLOR_SEARCH);
        formPanel.add(findRoomsBtn, gbc);

        return formPanel;
    }

    /**
     * [MỚI] Tạo panel cho việc thêm/xóa dịch vụ
     */
    private JPanel createServicePanel() {
        JPanel servicePanel = new JPanel(new BorderLayout(10, 5));
        servicePanel.setBackground(Color.WHITE);
        servicePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dịch vụ đi kèm",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)
        ));

        // Panel Thêm Dịch Vụ (NORTH)
        JPanel addServiceForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addServiceForm.setBackground(Color.WHITE);
        
        serviceCombo = new JComboBox<>(); serviceCombo.setFont(FONT_FIELD);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(FONT_FIELD);
        addServiceBtn = createButton("Thêm Dịch Vụ", COLOR_ADD);

        addServiceForm.add(createLabel("Dịch vụ:"));
        addServiceForm.add(serviceCombo);
        addServiceForm.add(createLabel("Số lượng:"));
        addServiceForm.add(quantitySpinner);
        addServiceForm.add(addServiceBtn);

        servicePanel.add(addServiceForm, BorderLayout.NORTH);

        // Bảng Dịch Vụ Đã Chọn (CENTER)
        bookingServicesModel = new DefaultTableModel(new String[]{"ID Dịch Vụ", "Tên Dịch Vụ", "Số Lượng", "Đơn Giá"}, 0);
        bookingServicesTable = new JTable(bookingServicesModel);
        bookingServicesTable.setFont(FONT_FIELD);
        bookingServicesTable.setRowHeight(25);
        // Ẩn cột ID
        bookingServicesTable.removeColumn(bookingServicesTable.getColumnModel().getColumn(0));

        JScrollPane serviceScroll = new JScrollPane(bookingServicesTable);
        serviceScroll.getViewport().setBackground(Color.WHITE);
        serviceScroll.setPreferredSize(new Dimension(0, 100)); // Giới hạn chiều cao
        servicePanel.add(serviceScroll, BorderLayout.CENTER);

        // Panel Nút Xóa (SOUTH)
        JPanel removeServicePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        removeServicePanel.setBackground(Color.WHITE);
        removeServiceBtn = createButton("Xóa Dịch Vụ Đã Chọn", COLOR_DELETE);
        removeServicePanel.add(removeServiceBtn);
        servicePanel.add(removeServicePanel, BorderLayout.SOUTH);

        return servicePanel;
    }

    /**
     * [MỚI] Tạo panel cho các nút CRUD chính
     */
    private JPanel createCrudButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        addBtn = createButton("Thêm Booking", COLOR_ADD);
        updateBtn = createButton("Sửa Booking", COLOR_UPDATE);
        deleteBtn = createButton("Xóa Booking", COLOR_DELETE);
        refreshBtn = createButton("Làm mới", COLOR_REFRESH);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        return buttonPanel;
    }
    
    // Helper tạo components
    // ... (existing code) ...
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
    
    private void clearFields() {
        customerIdField.setText("");
        checkInField.setText("");
        checkOutField.setText("");
        if (roomTypeCombo.getItemCount() > 0) {
            roomTypeCombo.setSelectedIndex(0);
        }
        roomNumberCombo.removeAllItems();
        bookingServicesModel.setRowCount(0); // [MỚI] Xóa bảng dịch vụ
        if (serviceCombo.getItemCount() > 0) {
             serviceCombo.setSelectedIndex(0);
        }
        quantitySpinner.setValue(1);
        table.clearSelection();
    }

    // [MỚI] Tải các loại phòng
    // ... (existing code) ...
    private void loadRoomTypes() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT type FROM rooms")) {
            
            roomTypeCombo.removeAllItems();
            while (rs.next()) {
                roomTypeCombo.addItem(rs.getString("type"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // [MỚI] Tải danh sách dịch vụ vào ComboBox
    private void loadServices() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name, price FROM services")) {
            
            serviceCombo.removeAllItems();
            while (rs.next()) {
                serviceCombo.addItem(new ServiceItem(
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getDouble("price")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // [MỚI] Cập nhật danh sách phòng trống
    // ... (existing code) ...
    private void updateAvailableRooms() {
        String type = roomTypeCombo.getSelectedItem().toString();
        java.sql.Date sqlCheckIn, sqlCheckOut;

        try {
            // Chuyển đổi ngày
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date checkIn = sdf.parse(checkInField.getText());
            java.util.Date checkOut = sdf.parse(checkOutField.getText());
            sqlCheckIn = new java.sql.Date(checkIn.getTime());
            sqlCheckOut = new java.sql.Date(checkOut.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày nhập không hợp lệ. Vui lòng dùng định dạng yyyy-MM-dd.", "Lỗi ngày", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra xem có đang update không
        int currentBookingId = -1;
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            currentBookingId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        }

        roomNumberCombo.removeAllItems();
        
        try (Connection c = DBConnection.getConnection()) {
            // Câu SQL này tìm các phòng:
            // 1. Đúng loại (type)
            // 2. KHÔNG nằm trong bất kỳ booking nào
            //    mà khoảng thời gian (check_in, check_out) của booking đó
            //    CHỒNG LÊN khoảng thời gian đang chọn (sqlCheckIn, sqlCheckOut)
            // 3. TRỪ booking hiện tại đang chọn (nếu có)
            String sql = "SELECT id, room_number FROM rooms " +
                         "WHERE type = ? AND id NOT IN (" +
                         "  SELECT room_id FROM bookings " +
                         "  WHERE check_in < ? AND check_out > ? AND id != ?" +
                         ")";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, type);
            ps.setDate(2, sqlCheckOut); // check_in của booking < check_out mới
            ps.setDate(3, sqlCheckIn);  // check_out của booking > check_in mới
            ps.setInt(4, currentBookingId); // Bỏ qua booking đang sửa

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roomNumberCombo.addItem(new RoomItem(rs.getInt("id"), rs.getString("room_number")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // [MỚI] Thêm dịch vụ vào bảng tạm
    private void addServiceToBookingTable() {
        ServiceItem selectedService = (ServiceItem) serviceCombo.getSelectedItem();
        int quantity = (int) quantitySpinner.getValue();

        if (selectedService == null) {
            return;
        }

        // Thêm vào model của bảng dịch vụ
        bookingServicesModel.addRow(new Object[]{
            selectedService.id,
            selectedService.name,
            quantity,
            selectedService.price
        });
    }

    // [MỚI] Xóa dịch vụ khỏi bảng tạm
    private void removeServiceFromBookingTable() {
        int selectedRow = bookingServicesTable.getSelectedRow();
        if (selectedRow != -1) {
            bookingServicesModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dịch vụ để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    // [MỚI] Tải các dịch vụ đã đăng ký của một booking
    private void loadBookingServices(int bookingId) {
        bookingServicesModel.setRowCount(0); // Xóa bảng tạm
        try (Connection c = DBConnection.getConnection()) {
            String sql = "SELECT s.id, s.name, u.quantity, s.price " +
                         "FROM service_usage u JOIN services s ON u.service_id = s.id " +
                         "WHERE u.booking_id = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                bookingServicesModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // [MỚI] Lưu các dịch vụ trong bảng tạm vào CSDL (dùng cho Thêm và Sửa)
    private void saveServices(Connection c, int bookingId) throws SQLException {
        String sql = "INSERT INTO service_usage (booking_id, service_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < bookingServicesModel.getRowCount(); i++) {
                int serviceId = (int) bookingServicesModel.getValueAt(i, 0); // Lấy ID từ cột ẩn
                int quantity = (int) bookingServicesModel.getValueAt(i, 2);

                ps.setInt(1, bookingId);
                ps.setInt(2, serviceId);
                ps.setInt(3, quantity);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    // Các hàm logic (Đã cập nhật với Transaction)
    private void addBooking() {
        RoomItem selectedRoom = (RoomItem) roomNumberCombo.getSelectedItem();
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection c = null; // Khai báo bên ngoài để dùng trong finally
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false); // Bắt đầu Transaction

            // --- Bước 1: Thêm Booking ---
            String sql = "INSERT INTO bookings (customer_id, room_id, check_in, check_out, total_days, total_cost) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            int customerId = Integer.parseInt(customerIdField.getText());
            int roomId = selectedRoom.id; // Lấy ID từ RoomItem
            java.sql.Date checkIn = java.sql.Date.valueOf(checkInField.getText());
            java.sql.Date checkOut = java.sql.Date.valueOf(checkOutField.getText());

            long diff = checkOut.getTime() - checkIn.getTime();
            int days = (int)(diff/(1000*60*60*24));
            double pricePerNight = getRoomPrice(roomId); // Hàm này có thể cần Connection
            double total = days * pricePerNight;

            ps.setInt(1, customerId);
            ps.setInt(2, roomId);
            ps.setDate(3, checkIn);
            ps.setDate(4, checkOut);
            ps.setInt(5, days);
            ps.setDouble(6, total);
            ps.executeUpdate();

            // --- Bước 2: Lấy Booking ID vừa tạo ---
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newBookingId = generatedKeys.getInt(1);
                
                // --- Bước 3: Lưu Dịch Vụ ---
                saveServices(c, newBookingId);

                c.commit(); // Hoàn tất Transaction
                JOptionPane.showMessageDialog(this,"✅ Đã thêm booking và dịch vụ");
            } else {
                throw new SQLException("Tạo booking thất bại, không lấy được ID.");
            }

            refreshData();
            clearFields();
        } catch(Exception e){
            e.printStackTrace();
            try {
                if (c != null) c.rollback(); // Hoàn tác nếu lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (c != null) {
                    c.setAutoCommit(true);
                    c.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private double getRoomPrice(int roomId) {
        // ... (existing code) ...
        double price = 0;
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT price_per_night FROM rooms WHERE id=?");
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) price = rs.getDouble(1);
        } catch(Exception e){ e.printStackTrace();}
        return price;
    }

    private void updateBooking() {
        int row = table.getSelectedRow(); 
        if(row==-1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đặt phòng để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        RoomItem selectedRoom = (RoomItem) roomNumberCombo.getSelectedItem();
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Lỗi chọn phòng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int bookingId = Integer.parseInt(model.getValueAt(row,0).toString());
        
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false); // Bắt đầu Transaction

            // --- Bước 1: Cập nhật Booking ---
            String sql = "UPDATE bookings SET customer_id=?, room_id=?, check_in=?, check_out=?, total_days=?, total_cost=? WHERE id=?";
            PreparedStatement ps = c.prepareStatement(sql);
            
            int customerId = Integer.parseInt(customerIdField.getText());
            int roomId = selectedRoom.id; // Lấy ID từ RoomItem
            java.sql.Date checkIn = java.sql.Date.valueOf(checkInField.getText());
            java.sql.Date checkOut = java.sql.Date.valueOf(checkOutField.getText());
            long diff = checkOut.getTime()-checkIn.getTime();
            int days = (int)(diff/(1000*60*60*24));
            double total = days*getRoomPrice(roomId);

            ps.setInt(1,customerId); ps.setInt(2,roomId);
            ps.setDate(3,checkIn); ps.setDate(4,checkOut);
            ps.setInt(5,days); ps.setDouble(6,total); ps.setInt(7,bookingId);
            ps.executeUpdate();

            // --- Bước 2: Xóa Dịch Vụ Cũ ---
            PreparedStatement deletePs = c.prepareStatement("DELETE FROM service_usage WHERE booking_id = ?");
            deletePs.setInt(1, bookingId);
            deletePs.executeUpdate();

            // --- Bước 3: Thêm Dịch Vụ Mới ---
            saveServices(c, bookingId);

            c.commit(); // Hoàn tất Transaction
            JOptionPane.showMessageDialog(this, "✅ Cập nhật booking và dịch vụ thành công!");
            
            refreshData();
            clearFields();
        } catch(Exception e){ 
            e.printStackTrace();
             try {
                if (c != null) c.rollback(); // Hoàn tác nếu lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
             try {
                if (c != null) {
                    c.setAutoCommit(true);
                    c.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteBooking() {
        int row = table.getSelectedRow(); 
        if(row==-1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đặt phòng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Việc này sẽ xóa cả booking và các dịch vụ đã đăng ký. Bạn có chắc chắn?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        int bookingId = Integer.parseInt(model.getValueAt(row,0).toString());
        
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false); // Bắt đầu Transaction

            // --- Bước 1: Xóa Dịch Vụ ---
            PreparedStatement deleteServices = c.prepareStatement("DELETE FROM service_usage WHERE booking_id = ?");
            deleteServices.setInt(1, bookingId);
            deleteServices.executeUpdate();

            // --- Bước 2: Xóa Hóa Đơn (Nếu có) ---
            PreparedStatement deleteInvoices = c.prepareStatement("DELETE FROM invoices WHERE booking_id = ?");
            deleteInvoices.setInt(1, bookingId);
            deleteInvoices.executeUpdate();

            // --- Bước 3: Xóa Booking ---
            PreparedStatement deleteBooking = c.prepareStatement("DELETE FROM bookings WHERE id=?");
            deleteBooking.setInt(1, bookingId);
            deleteBooking.executeUpdate();

            c.commit(); // Hoàn tất Transaction
            JOptionPane.showMessageDialog(this, "✅ Đã xóa booking và các dữ liệu liên quan.");
            
            refreshData();
            clearFields();
        } catch(Exception e){ 
            e.printStackTrace();
            try {
                if (c != null) c.rollback(); // Hoàn tác nếu lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi xóa: " + e.getMessage() + "\n(Có thể do ràng buộc khóa ngoại chưa xử lý, ví dụ: Hóa đơn)", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
             try {
                if (c != null) {
                    c.setAutoCommit(true);
                    c.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshData() {
        // ... (existing code) ...
        try(Connection c = DBConnection.getConnection();
            Statement st = c.createStatement();
            // Cập nhật câu SQL để JOIN với bảng rooms
             ResultSet rs = st.executeQuery(
                 "SELECT b.id, b.customer_id, b.room_id, r.room_number, r.type, b.check_in, b.check_out, b.total_days, b.total_cost " +
                 "FROM bookings b JOIN rooms r ON b.room_id = r.id"
             )) {
            model.setRowCount(0);
            while(rs.next()) {
                // Thêm RoomItem vào cột "Số phòng"
                model.addRow(new Object[]{
                        rs.getInt("id"), 
                        rs.getInt("customer_id"), 
                        new RoomItem(rs.getInt("room_id"), rs.getString("room_number")), // Lưu object
                        rs.getString("type"),
                        rs.getDate("check_in"), 
                        rs.getDate("check_out"),
                        rs.getInt("total_days"), 
                        rs.getDouble("total_cost")
                });
            }
        } catch(Exception e){ e.printStackTrace();}
        clearFields();
    }
}

