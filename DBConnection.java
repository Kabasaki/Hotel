import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASS = ""; 
    private static final String DB_NAME = "hotel_management";

    public static Connection getConnection() throws SQLException {
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(URL + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8", USER, PASS);
    }

    
    public static void initTables() {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {

            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(100) NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            String rolesTable = "CREATE TABLE IF NOT EXISTS roles (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "role_name VARCHAR(50) UNIQUE NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            String employeesTable = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(100)," +
                    "email VARCHAR(100)," +
                    "phone VARCHAR(20)," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(100) NOT NULL," +
                    "role_id INT," +
                    "FOREIGN KEY (role_id) REFERENCES roles(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            String customersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(100)," +
                    "phone VARCHAR(20)," +
                    "email VARCHAR(100)," +
                    "address VARCHAR(255)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            String roomsTable = "CREATE TABLE IF NOT EXISTS rooms (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "room_number VARCHAR(10) UNIQUE," +
                    "type VARCHAR(50)," +
                    "price_per_night DOUBLE," +
                    "status ENUM('Available','Booked') DEFAULT 'Available'" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            String bookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "customer_id INT," +
                    "room_id INT," +
                    "check_in DATE," +
                    "check_out DATE," +
                    "total_days INT," +
                    "total_cost DOUBLE," +
                    "FOREIGN KEY (customer_id) REFERENCES customers(id)," +
                    "FOREIGN KEY (room_id) REFERENCES rooms(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            String servicesTable = "CREATE TABLE IF NOT EXISTS services (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(100)," +
                    "price DOUBLE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            String serviceUsageTable = "CREATE TABLE IF NOT EXISTS service_usage (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "booking_id INT," +
                    "service_id INT," +
                    "quantity INT," +
                    "FOREIGN KEY (booking_id) REFERENCES bookings(id)," +
                    "FOREIGN KEY (service_id) REFERENCES services(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            String invoicesTable = "CREATE TABLE IF NOT EXISTS invoices (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "booking_id INT," +
                    "total_room DOUBLE," +
                    "total_service DOUBLE," +
                    "total_amount DOUBLE," +
                    "date_created DATE," +
                    "FOREIGN KEY (booking_id) REFERENCES bookings(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            
            st.executeUpdate(usersTable);
            st.executeUpdate(rolesTable); 
            st.executeUpdate(employeesTable); 
            st.executeUpdate(customersTable);
            st.executeUpdate(roomsTable);
            st.executeUpdate(bookingsTable);
            st.executeUpdate(servicesTable);
            st.executeUpdate(serviceUsageTable);
            st.executeUpdate(invoicesTable);

            st.executeUpdate("INSERT IGNORE INTO users (id, username, password) VALUES (1,'admin','123');");
            
            System.out.println("✅ Database và bảng đã được tạo (nếu chưa có).");
            
            seedData(st); 

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    private static void seedData(Statement st) throws SQLException {
        st.executeUpdate("INSERT IGNORE INTO roles (id, role_name) VALUES (1,'Admin');");
        st.executeUpdate("INSERT IGNORE INTO roles (id, role_name) VALUES (2,'Nhân viên');");

        st.executeUpdate("INSERT IGNORE INTO employees (id, name, email, phone, username, password, role_id) VALUES " +
                         "(1, 'Quản Trị Viên', 'admin@hotel.com', '0901234567', 'admin', '123', 1);"); // Đồng bộ với users(admin, 123)
        st.executeUpdate("INSERT IGNORE INTO employees (id, name, email, phone, username, password, role_id) VALUES " +
                         "(2, 'Nguyễn Văn A', 'vana@hotel.com', '0907654321', 'vana', '123', 2);");
        st.executeUpdate("INSERT IGNORE INTO users (id, username, password) VALUES (2,'vana','123');");


        st.executeUpdate("INSERT IGNORE INTO customers (id, name, phone, email, address) VALUES " +
                         "(1, 'Trần Thị B', '0912345678', 'btran@mail.com', 'Hà Nội');");
        st.executeUpdate("INSERT IGNORE INTO customers (id, name, phone, email, address) VALUES " +
                         "(2, 'Lê Văn C', '0987654321', 'c_le@mail.com', 'TP.HCM');");

        st.executeUpdate("INSERT IGNORE INTO rooms (id, room_number, type, price_per_night, status) VALUES " +
                         "(1, '101', 'Standard', 500000, 'Booked');");
        st.executeUpdate("INSERT IGNORE INTO rooms (id, room_number, type, price_per_night, status) VALUES " +
                         "(2, '102', 'Standard', 500000, 'Available');");
        st.executeUpdate("INSERT IGNORE INTO rooms (id, room_number, type, price_per_night, status) VALUES " +
                         "(3, '201', 'Deluxe', 800000, 'Available');");

        st.executeUpdate("INSERT IGNORE INTO services (id, name, price) VALUES " +
                         "(1, 'Giặt là', 50000);");
        st.executeUpdate("INSERT IGNORE INTO services (id, name, price) VALUES " +
                         "(2, 'Ăn sáng tại phòng', 80000);");
                         
        st.executeUpdate("INSERT IGNORE INTO bookings (id, customer_id, room_id, check_in, check_out, total_days, total_cost) VALUES " +
                         "(1, 1, 1, '2025-10-25', '2025-10-28', 3, 1500000.0);");
                         
        System.out.println("✅ Đã chèn dữ liệu mẫu thành công.");
    }
}
