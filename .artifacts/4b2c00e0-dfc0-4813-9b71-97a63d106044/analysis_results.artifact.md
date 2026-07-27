# Dự án CookBook - Phân tích hệ thống

Dưới đây là bản phân tích chi tiết về cấu trúc và chức năng của dự án CookBook.

## 1. Tổng quan dự án
- **Tên ứng dụng:** CookBook
- **Ngôn ngữ:** Java
- **Kiến trúc:** Truyền thống (Activities/Fragments với SQLite Helper)
- **Mục tiêu:** Ứng dụng quản lý công thức nấu ăn, danh sách mua sắm và lịch sử nấu nướng.

## 2. Cấu trúc dữ liệu (Database Schema)
Hệ thống sử dụng SQLite với các bảng chính sau:
- **User:** Lưu thông tin người dùng (email, mật khẩu, tên, avatar).
- **Category:** Phân loại món ăn (Món Việt, Món Ý, v.v.).
- **Recipe:** Thông tin chi tiết món ăn (tên, mô tả, ảnh, thời gian nấu, độ khó, calo, đánh giá).
- **Ingredient:** Các nguyên liệu cần thiết cho từng món ăn.
- **Step:** Các bước thực hiện món ăn.
- **Favorite:** Lưu danh sách món ăn yêu thích của người dùng.
- **GroceryList & GroceryItem:** Quản lý danh sách mua sắm, hỗ trợ gom nhóm theo món ăn.
- **CookHistory:** Lịch sử các món đã nấu.

## 3. Các thành phần chính của ứng dụng
### Activities
- **SplashActivity:** Màn hình chào khi khởi động ứng dụng.
- **AuthActivity:** Quản lý Đăng nhập/Đăng ký thông qua `LoginFragment` và `RegisterFragment`.
- **MainActivity:** Chứa Bottom Navigation điều hướng giữa các màn hình chính.
- **DetailActivity:** Hiển thị chi tiết một công thức nấu ăn.

### Fragments (Điều hướng chính)
- **HomeFragment:** Màn hình chính, hiện tại đang hiển thị một số món ăn nổi bật (đang ở mức tĩnh).
- **SearchFragment:** Tìm kiếm món ăn.
- **FavoritesFragment:** Hiển thị danh sách món ăn đã yêu thích.
- **GroceryListFragment:** Quản lý danh sách đi chợ, hỗ trợ check các món đã mua.
- **ProfileFragment:** Thông tin cá nhân người dùng.

## 4. Đặc điểm nổi bật
- **Quản lý Database tập trung:** `DatabaseHelper` xử lý toàn bộ logic truy vấn, từ auth đến grocery list.
- **Grocery List thông minh:** Hỗ trợ gom nhóm nguyên liệu cần mua theo món ăn hoặc các món mua thêm ngoài danh mục.
- **Dữ liệu mẫu:** Có sẵn cơ chế chèn dữ liệu mẫu (`insertSampleData`) để test ứng dụng ngay khi khởi tạo.

## 5. Đánh giá hiện trạng & Hướng phát triển
- **Ưu điểm:** Cấu trúc rõ ràng, logic database được đóng gói tốt trong `DatabaseHelper`.
- **Nhược điểm:**
    - Một số Fragment (`HomeFragment`) vẫn còn dữ liệu tĩnh (hardcoded).
    - Chưa sử dụng các thư viện hiện đại như Room, ViewModel hay LiveData (nếu muốn tối ưu hóa luồng dữ liệu).
    - Các Adapter có thể cần được tối ưu hóa hơn.

---
Bạn có muốn tôi đi sâu vào phân tích một file cụ thể nào không, hay muốn bắt đầu thực hiện một tính năng mới?