# Bổ sung Danh mục "Tất cả" và Nâng cấp Tìm kiếm

Kế hoạch này nhằm thêm mục "Tất cả" vào danh sách danh mục và cải thiện trải nghiệm điều hướng khi xem toàn bộ món ăn hoặc tìm kiếm trống.

## Proposed Changes

### Layouts

#### [MODIFY] [fragment_home.xml](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/res/layout/fragment_home.xml)
- Gán ID `android:id="@+id/tvSeeAll"` cho TextView "See all".

### Home Module

#### [MODIFY] [HomeFragment.java](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/java/com/example/cookbook/HomeFragment.java)
- **Danh mục "Tất cả"**: Trong hàm `loadCategories`, chèn thêm một đối tượng `Category` giả (ID = -1, Tên = "Tất cả") vào đầu danh sách.
- **Xử lý Click Danh mục**: Nếu chọn "Tất cả", gọi `loadPopularRecipes()` để hiện các món phổ biến không lọc.
- **Nút See All**: Thiết lập sự kiện click để chuyển sang `SearchFragment` với cờ hiệu yêu cầu hiển thị toàn bộ món ăn.
- **Tìm kiếm trống**: Cập nhật sự kiện click `btnSearch` để nếu ô nhập trống vẫn chuyển sang trang Search.

### Search Module

#### [MODIFY] [SearchFragment.java](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/java/com/example/cookbook/SearchFragment.java)
- Cập nhật hàm `performSearch(String query)`: Nếu `query` rỗng, ứng dụng sẽ lấy toàn bộ danh sách món ăn từ Database được sắp xếp theo độ phổ biến (Rating) thay vì hiện 0 kết quả.

## Verification Plan

### Manual Verification
1.  **Kiểm tra danh mục**: Mở Home, xác nhận có mục "Tất cả" đầu tiên. Nhấn vào các mục khác để lọc, sau đó nhấn "Tất cả" để xem lại danh sách đầy đủ (tối đa 4 món).
2.  **Kiểm tra See all**: Nhấn "See all" ở phần Popular Recipes, xác nhận ứng dụng chuyển sang trang Search và hiển thị toàn bộ món ăn trong DB, sắp xếp theo rating.
3.  **Kiểm tra Tìm kiếm trống**: Để trống ô tìm kiếm ở Home và nhấn nút tìm kiếm, xác nhận ứng dụng chuyển sang trang Search và hiển thị toàn bộ món ăn.
