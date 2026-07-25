# Hiển thị dữ liệu động cho Trang chủ và tính năng Spin

Kế hoạch này sẽ thực hiện việc nạp dữ liệu từ Database cho các danh mục, món ăn phổ biến và tính năng "Today's Pick" (chọn món ngẫu nhiên) trên trang Home.

## User Review Required

> [!IMPORTANT]
> - Tôi sẽ thay đổi cấu trúc trang Home từ các thẻ tĩnh sang sử dụng `RecyclerView` để hiển thị danh sách linh hoạt hơn.
> - Tính năng "Spin" sẽ lấy một món ngẫu nhiên từ toàn bộ kho dữ liệu món ăn trong Database.

## Proposed Changes

### Database

#### [MODIFY] [DatabaseHelper.java](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/java/com/example/cookbook/DatabaseHelper.java)
- Thêm `getAllCategories()`: Lấy toàn bộ danh mục món ăn.
- Thêm `getPopularRecipes(int limit)`: Lấy các món ăn có đánh giá cao nhất.
- Thêm `getRandomRecipe()`: Lấy ngẫu nhiên 1 món ăn.

### Layouts

#### [NEW] [item_category.xml](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/res/layout/item_category.xml)
- Tạo layout cho từng nút danh mục (ví dụ: Món Âu, Dessert).

#### [MODIFY] [fragment_home.xml](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/res/layout/fragment_home.xml)
- Bổ sung ID cho phần Today's Pick: `tvTodayPickTitle`, `tvTodayPickTime`.
- Thay thế các `LinearLayout` tĩnh bằng `RecyclerView` cho:
    - Danh mục (`rvCategories`).
    - Món ăn phổ biến (`rvPopularRecipes`).

### Adapters

#### [NEW] [CategoryAdapter.java](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/java/com/example/cookbook/CategoryAdapter.java)
- Adapter để hiển thị danh sách danh mục.

#### [RENAME/MODIFY] [FavoritesAdapter.java](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/java/com/example/cookbook/FavoritesAdapter.java) -> `RecipeAdapter.java`
- Đổi tên thành `RecipeAdapter` để dùng chung cho cả trang Home, Search và Favorites.

### Home Module

#### [MODIFY] [HomeFragment.java](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-ai-cha-cha/CookBook-AndroidApp-ai-cha-cha/app/src/main/java/com/example/cookbook/HomeFragment.java)
- Khởi tạo và gán Adapter cho các RecyclerView.
- Viết logic `loadTodayPick()` để hiển thị món ngẫu nhiên khi vào trang.
- Bắt sự kiện nút `btnSpin` để gọi lại `loadTodayPick()`.

## Verification Plan

### Manual Verification
1.  Mở ứng dụng, kiểm tra xem danh sách danh mục (Món Việt, Món Ý, ...) có hiện đúng như trong `DatabaseHelper.insertSampleData` không.
2.  Kiểm tra phần "Popular Recipes" có hiện đúng Phở Bò, Pasta Carbonara, Bánh Chocolate từ DB không.
3.  Bấm nút **Spin! ✨** nhiều lần để xác nhận món ăn ở banner Today's Pick được thay đổi ngẫu nhiên.
