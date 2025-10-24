// SessionManager.java (Đã chỉnh sửa)
public class SessionManager {
    // Giữ private static để đảm bảo tính toàn cục
    private static String username;
    private static String role;

    // Không cần hàm login() riêng, dùng setUser là đủ
    // public static void login(int id, String user, String userRole) { ... }

    // Đổi tên logout() thành clear() để rõ nghĩa
    public static void clear() {
        username = null;
        role = null;
    }

    public static boolean isLoggedIn() {
        return username != null;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }
    
    // Hàm này dường như không được dùng nhất quán, 
    // thống nhất dùng username và role
    // private static String currentUserEmail = null;
    // public static void setUser(String email) { ... }
    // public static String getUser() { ... }
    // public static void clearUser() { ... }


    // Hàm chính để set thông tin khi đăng nhập
    public static void setUser(String user, String userRole) {
        username = user;
        role = userRole;
    }
}
