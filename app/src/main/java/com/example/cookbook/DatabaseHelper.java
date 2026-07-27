package com.example.cookbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên Database và Phiên bản
    private static final String DATABASE_NAME = "Cookbook.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper instance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Bật tính năng ràng buộc Khóa Ngoại (Foreign Key) trong SQLite
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng User
        String CREATE_USER_TABLE = "CREATE TABLE User (" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "fullName TEXT, " +
                "avatar TEXT " +
                ");";

        // 2. Tạo bảng Category
        String CREATE_CATEGORY_TABLE = "CREATE TABLE Category (" +
                "categoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoryName TEXT NOT NULL" +
                ");";

        // 3. Tạo bảng Recipe
        String CREATE_RECIPE_TABLE = "CREATE TABLE Recipe (" +
                "recipeId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoryId INTEGER, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "image TEXT, " +
                "cookTime INTEGER, " +
                "difficulty TEXT, " +
                "servings INTEGER, " +
                "calories INTEGER, " +
                "rating REAL DEFAULT 0.0 CHECK (rating >= 0.0 AND rating <= 5.0), " +
                "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (categoryId) REFERENCES Category(categoryId) ON DELETE SET NULL" +
                ");";

        // 4. Tạo bảng Ingredient
        String CREATE_INGREDIENT_TABLE = "CREATE TABLE Ingredient (" +
                "ingredientId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipeId INTEGER, " +
                "ingredientName TEXT NOT NULL, " +
                "quantity TEXT, " +
                "FOREIGN KEY (recipeId) REFERENCES Recipe(recipeId) ON DELETE CASCADE" +
                ");";

        // 5. Tạo bảng Step
        String CREATE_STEP_TABLE = "CREATE TABLE Step (" +
                "stepId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipeId INTEGER, " +
                "stepNumber INTEGER NOT NULL, " +
                "description TEXT NOT NULL, " +
                "image TEXT, " +
                "FOREIGN KEY (recipeId) REFERENCES Recipe(recipeId) ON DELETE CASCADE" +
                ");";

        // 6. Tạo bảng Favorite (Khóa chính kết hợp userId + recipeId)
        String CREATE_FAVORITE_TABLE = "CREATE TABLE Favorite (" +
                "userId INTEGER, " +
                "recipeId INTEGER, " +
                "favoriteAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (userId, recipeId), " +
                "FOREIGN KEY (userId) REFERENCES User(userId) ON DELETE CASCADE, " +
                "FOREIGN KEY (recipeId) REFERENCES Recipe(recipeId) ON DELETE CASCADE" +
                ");";

        // 7. Tạo bảng GroceryList
        String CREATE_GROCERY_LIST_TABLE = "CREATE TABLE GroceryList (" +
                "listId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "title TEXT NOT NULL, " +
                "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (userId) REFERENCES User(userId) ON DELETE CASCADE" +
                ");";

        // 8. Tạo bảng GroceryItem (isChecked lưu dưới dạng INTEGER: 0 = false, 1 = true)
        String CREATE_GROCERY_ITEM_TABLE = "CREATE TABLE GroceryItem (" +
                "itemId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "listId INTEGER, " +
                "ingredientName TEXT NOT NULL, " +
                "quantity TEXT, " +
                "isChecked INTEGER DEFAULT 0, " +
                "recipeId INTEGER, " +
                "FOREIGN KEY (listId) REFERENCES GroceryList(listId) ON DELETE CASCADE, " +
                "FOREIGN KEY (recipeId) REFERENCES Recipe(recipeId) ON DELETE SET NULL" +
                ");";

        // 9. Tạo bảng CookHistory
        String CREATE_COOK_HISTORY_TABLE = "CREATE TABLE CookHistory (" +
                "historyId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "recipeId INTEGER, " +
                "cookedDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (userId) REFERENCES User(userId) ON DELETE CASCADE, " +
                "FOREIGN KEY (recipeId) REFERENCES Recipe(recipeId) ON DELETE CASCADE" +
                ");";

        // Thực thi các câu lệnh tạo bảng
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_INGREDIENT_TABLE);
        db.execSQL(CREATE_STEP_TABLE);
        db.execSQL(CREATE_FAVORITE_TABLE);
        db.execSQL(CREATE_GROCERY_LIST_TABLE);
        db.execSQL(CREATE_GROCERY_ITEM_TABLE);
        db.execSQL(CREATE_COOK_HISTORY_TABLE);

        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại khi nâng cấp VERSION
        db.execSQL("DROP TABLE IF EXISTS CookHistory");
        db.execSQL("DROP TABLE IF EXISTS GroceryItem");
        db.execSQL("DROP TABLE IF EXISTS GroceryList");
        db.execSQL("DROP TABLE IF EXISTS Favorite");
        db.execSQL("DROP TABLE IF EXISTS Step");
        db.execSQL("DROP TABLE IF EXISTS Ingredient");
        db.execSQL("DROP TABLE IF EXISTS Recipe");
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS User");

        // Tạo lại bảng mới
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // ==========================================
        // 1. DỮ LIỆU BẢNG USER (4 Người dùng)
        // ==========================================
        db.execSQL("INSERT INTO User (userId, email, password, fullName, avatar) " +
                "VALUES (1, 'user@gmail.com', '123456', 'Nguyễn Văn An', 'avatar_1');");
        db.execSQL("INSERT INTO User (userId, email, password, fullName, avatar) " +
                "VALUES (2, 'nth303@gmail.com', '123456', 'Nguyễn Trung Hiếu', 'avatar_2');");
        db.execSQL("INSERT INTO User (userId, email, password, fullName, avatar) " +
                "VALUES (3, 'ntd11@gmail.com', '123456', 'Nguyễn Tuấn Đạt', 'avatar_3');");
        db.execSQL("INSERT INTO User (userId, email, password, fullName, avatar) " +
                "VALUES (4, 'maitt@gmail.com', '123456', 'Trần Thị Mai', 'avatar_4');");

        // ==========================================
        // 2. DỮ LIỆU BẢNG CATEGORY (6 Danh mục)
        // ==========================================
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (1, 'Món Việt');");
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (2, 'Món Ý');");
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (3, 'Món Hàn');");
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (4, 'Món Nhật');");
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (5, 'Healthy & Eat Clean');");
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (6, 'Tráng miệng');");

        // ==========================================
        // 3. DỮ LIỆU BẢNG RECIPE (8 Món ăn thực tế)
        // ==========================================
        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (1, 1, 'Phở Bò Hà Nội', 'Món phở truyền thống đậm đà chuẩn vị Bắc với nước dùng ngọt thanh từ xương ống ninh kỹ.', 'img_pho_bo', 90, 'Medium', 4, 480, 4.9);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (2, 1, 'Bún Chả Hà Nội', 'Thịt nướng than hoa thơm nức mũi ăn kèm nước mắm đu đủ chua ngọt, dưa góp và bún tươi.', 'img_bun_cha', 45, 'Medium', 3, 520, 4.8);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (3, 2, 'Spaghetti Carbonara', 'Mỳ Ý béo ngậy với sốt lòng đỏ trứng gà, phô mai Parmesan bào mịn và thịt hun khói giòn rụm.', 'img_pasta', 20, 'Easy', 2, 540, 4.7);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (4, 3, 'Cơm Trộn Bibimbap', 'Món cơm trộn Hàn Quốc rực rỡ sắc màu với rau củ xào, thịt bò đậm vị, trứng ốp la và sốt Gochujang.', 'img_bibimbap', 30, 'Easy', 2, 460, 4.6);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (5, 4, 'Mỳ Ramen Tonkotsu', 'Mỳ Ramen Nhật Bản chuẩn vị với nước dùng béo bùi ninh từ xương heo, ăn kèm thịt xá xíu Chashu và trứng lòng đào.', 'img_ramen', 60, 'Hard', 2, 620, 4.9);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (6, 5, 'Salad Ức Gà Sốt Chanh Dây', 'Món ăn kiêng thanh mát, ức gà áp chảo mềm mọng kết hợp rau xà lách tươi giòn và sốt chanh dây chua ngọt.', 'img_salad_chicken', 15, 'Easy', 1, 290, 4.5);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (7, 6, 'Chocolate Lava Cake', 'Bánh nướng sô-cô-la ngọt ngào với lớp vỏ xốp mềm và phần nhân sô-cô-la sóng sánh chảy mịn bên trong.', 'img_lava_cake', 25, 'Medium', 2, 380, 4.8);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (8, 1, 'Bánh Mỳ Chảo Bò Né', 'Bò né xèo xèo trên chảo nóng cùng trứng ốp la, pate béo ngậy, xúc xích và ăn kèm bánh mì giòn rụm.', 'img_bo_ne', 15, 'Easy', 1, 510, 4.7);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (9, 1, 'Bánh Xèo Miền Tây', 'Bánh xèo vàng ươm, giòn rụm với nhân tôm thịt, giá đỗ, cuốn cùng rau cải xanh và nước mắm chua ngọt.', 'img_banh_xeo', 40, 'Medium', 4, 450, 4.8);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (10, 1, 'Gỏi Cuốn Tôm Thịt', 'Món cuốn thanh mát với tôm luộc đỏ au, thịt ba chỉ béo ngậy, bún tươi và các loại rau thơm, chấm tương đậu phộng.', 'img_goi_cuon', 20, 'Easy', 2, 300, 4.9);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (11, 2, 'Pizza Margherita', 'Pizza truyền thống nước Ý với đế bánh mỏng giòn, sốt cà chua tươi, phô mai Mozzarella béo ngậy và lá húng quế.', 'img_pizza', 60, 'Hard', 2, 600, 4.7);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (12, 2, 'Risotto Nấm', 'Cơm Ý Risotto nấu chậm ngấm vị ngọt từ nước dùng gà, kết hợp cùng nấm hương thơm lừng và phô mai Parmesan.', 'img_risotto', 35, 'Medium', 2, 480, 4.6);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (13, 3, 'Tteokbokki Bánh Gạo Cay', 'Bánh gạo dẻo mịn ngập trong nước sốt cay nồng đặc trưng Hàn Quốc, ăn kèm chả cá và trứng cút.', 'img_tteokbokki', 25, 'Easy', 2, 400, 4.8);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (14, 3, 'Canh Kim Chi Thịt Heo', 'Canh kim chi chua cay đưa cơm, kết hợp cùng thịt ba chỉ heo mềm béo và đậu hũ non thanh mát.', 'img_canh_kimchi', 30, 'Easy', 3, 350, 4.7);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (15, 4, 'Sushi Cá Hồi (Nigiri)', 'Cơm giấm Nhật Bản nắm tay dẻo thơm, bên trên là lát cá hồi sống tươi rói béo ngậy.', 'img_sushi', 45, 'Medium', 2, 320, 4.9);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (16, 4, 'Cơm Cà ri Nhật Bản', 'Sốt cà ri sánh mịn, đậm đà vị hoa quả ngọt dịu, nấu cùng khoai tây, cà rốt và thịt bò.', 'img_curry', 40, 'Medium', 3, 550, 4.8);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (17, 5, 'Cá Hồi Áp Chảo Măng Tây', 'Bữa tối Eat Clean hoàn hảo với phi-lê cá hồi giàu Omega-3 áp chảo xém vàng, ăn kèm măng tây xanh mướt.', 'img_salmon_asparagus', 20, 'Easy', 1, 350, 4.9);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (18, 5, 'Smoothie Bowl Trái Cây', 'Bát sinh tố đặc xay từ quả mọng đông lạnh, trang trí với hạt chia, yến mạch và dừa khô rất tốt cho sức khỏe.', 'img_smoothie_bowl', 10, 'Easy', 1, 250, 4.7);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (19, 6, 'Bánh Tiramisu Ý', 'Món tráng miệng nổi tiếng với lớp bánh quy đẫm vị cà phê hòa quyện cùng kem Mascarpone béo ngậy.', 'img_tiramisu', 40, 'Medium', 4, 420, 4.9);");

        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (20, 6, 'Chè Khúc Bạch', 'Chè thanh mát giải nhiệt mùa hè với những viên khúc bạch dai mềm béo mùi phô mai, nước đường phèn và hạnh nhân lát.', 'img_che_khuc_bach', 120, 'Medium', 4, 300, 4.8);");

        // ==========================================
        // 4. DỮ LIỆU BẢNG INGREDIENT (Nguyên liệu chi tiết)
        // ==========================================
        // --- Phở Bò (recipeId = 1) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Xương ống bò', '1.5 kg');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Thịt thăn bò (Tái)', '300 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Nạm bò', '300 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Bánh phở tươi', '600 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Hành tây & Hành lá', '2 củ / 100g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Gừng, hoa hồi, thảo quả, quế', 'Mỗi loại một ít');");

        // --- Bún Chả (recipeId = 2) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (2, 'Thịt ba chỉ thái mỏng', '400 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (2, 'Thịt nạc vai băm nhỏ', '300 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (2, 'Bún tươi', '500 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (2, 'Đu đủ xanh & Cà rốt', '1/2 củ mỗi loại');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (2, 'Nước mắm, đường, dấm, tỏi, ớt', 'Gia vị pha nước chấm');");

        // --- Spaghetti Carbonara (recipeId = 3) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (3, 'Mỳ Spaghetti', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (3, 'Thịt ba chỉ hun khói (Bacon)', '120 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (3, 'Lòng đỏ trứng gà', '3 quả');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (3, 'Phô mai Parmesan bào', '50 g');");

        // --- Cơm Trộn Bibimbap (recipeId = 4) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (4, 'Cơm trắng dẻo', '2 bát');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (4, 'Thịt bò thái mỏng', '150 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (4, 'Rau bina, giá đỗ, cà rốt', '100g mỗi loại');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (4, 'Nấm đông cô thái sợi', '50 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (4, 'Sốt ớt Hàn Quốc (Gochujang)', '2 muỗng canh');");

        // --- Mỳ Ramen Tonkotsu (recipeId = 5) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (5, 'Mỳ Ramen tươi', '2 vắt');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (5, 'Xương heo ninh nước dùng', '1 kg');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (5, 'Thịt cuộn xá xíu (Chashu)', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (5, 'Trứng ngâm tương lòng đào', '2 quả');");

        // --- Salad Ức Gà (recipeId = 6) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (6, 'Ức gà tươi', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (6, 'Xà lách thuỷ canh', '100 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (6, 'Cà chua bi & Dưa leo', '6 quả / 1 củ');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (6, 'Nước cốt chanh dây & Mật tịnh', '3 muỗng canh');");

        // --- Chocolate Lava Cake (recipeId = 7) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (7, 'Sô-cô-la đen 70%', '100 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (7, 'Bơ lạt Anchor', '80 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (7, 'Trứng gà nguyên quả', '2 quả');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (7, 'Bột mì đa dụng', '30 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (7, 'Đường bột', '40 g');");

        // --- Bánh Mỳ Chảo Bò Né (recipeId = 8) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (8, 'Thịt thăn bò thái lát', '150 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (8, 'Trứng gà', '1 quả');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (8, 'Pate gan heo', '30 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (8, 'Bánh mì giòn', '1 ổ');");

        // --- Bánh Xèo (9) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (9, 'Bột bánh xèo pha sẵn', '250 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (9, 'Tôm sú nhỏ & Thịt ba chỉ', '200 g mỗi loại');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (9, 'Giá đỗ & Hành lá', '100 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (9, 'Rau cải xanh, xà lách, rau thơm', 'Ăn kèm');");

        // --- Gỏi Cuốn (10) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (10, 'Bánh tráng dẻo', '1 tệp');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (10, 'Tôm sú & Thịt ba chỉ luộc', '150 g mỗi loại');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (10, 'Bún tươi', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (10, 'Hẹ, xà lách, rau mùi', '1 mớ nhỏ');");

        // --- Pizza Margherita (11) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (11, 'Bột mì (làm đế bánh)', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (11, 'Sốt cà chua xay nhuyễn', '100 ml');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (11, 'Phô mai Mozzarella tươi', '150 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (11, 'Lá húng quế tươi (Basil)', '1 vài lá');");

        // --- Risotto Nấm (12) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (12, 'Gạo Arborio (Cơm Ý)', '150 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (12, 'Nấm đùi gà & Nấm mỡ', '100 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (12, 'Nước dùng gà', '500 ml');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (12, 'Phô mai Parmesan & Bơ lạt', '30 g');");

        // --- Tteokbokki (13) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (13, 'Bánh gạo Hàn Quốc', '300 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (13, 'Chả cá Hàn Quốc (Odeng)', '2 miếng');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (13, 'Tương ớt Gochujang', '2 muỗng');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (13, 'Trứng cút luộc', '5 quả');");

        // --- Canh Kim Chi (14) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (14, 'Kim chi cải thảo chua', '250 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (14, 'Thịt ba chỉ heo', '150 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (14, 'Đậu hũ non', '1 hộp');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (14, 'Hành baro, tỏi băm', '1 ít');");

        // --- Sushi Cá Hồi (15) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (15, 'Gạo Nhật (Gạo Sushi)', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (15, 'Phi-lê cá hồi tươi sống', '150 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (15, 'Giấm gạo, đường, muối', 'Pha nước trộn cơm');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (15, 'Nước tương & Mù tạt Wasabi', 'Ăn kèm');");

        // --- Cơm Cà ri (16) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (16, 'Viên cà ri Nhật Bản', '2 viên');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (16, 'Thịt bò (hoặc thịt heo)', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (16, 'Khoai tây & Cà rốt', '1 củ mỗi loại');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (16, 'Cơm trắng', '3 bát');");

        // --- Cá Hồi Măng Tây (17) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (17, 'Phi-lê cá hồi tươi', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (17, 'Măng tây xanh', '10 ngọn');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (17, 'Muối, tiêu đen, dầu olive', 'Gia vị ướp');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (17, 'Chanh vàng & Bơ lạt', 'Làm sốt');");

        // --- Smoothie Bowl (18) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (18, 'Chuối đông lạnh', '1 quả');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (18, 'Quả mọng mix (Dâu, việt quất)', '100 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (18, 'Sữa hạnh nhân không đường', '50 ml');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (18, 'Hạt chia, Granola, dừa sấy', 'Topping');");

        // --- Tiramisu (19) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (19, 'Phô mai Mascarpone', '250 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (19, 'Kem tươi Whipping cream', '200 ml');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (19, 'Bánh quy Sâm-panh (Ladyfinger)', '1 gói');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (19, 'Cà phê Espresso & Bột cacao', '1 tách nhỏ');");

        // --- Chè Khúc Bạch (20) ---
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (20, 'Sữa tươi & Whipping cream', '200 ml mỗi loại');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (20, 'Gelatin bột', '15 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (20, 'Đường phèn, lá dứa', 'Nấu nước chè');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (20, 'Hạnh nhân lát rang chín', '20 g');");

        // ==========================================
        // 5. DỮ LIỆU BẢNG STEP (Các bước hướng dẫn chi tiết)
        // ==========================================
        // --- Phở Bò (recipeId = 1) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (1, 1, 'Chần xương bò trong nước sôi có gừng đập dập khoảng 5 phút để loại bỏ hoàn toàn bọt bẩn và mùi hôi.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (1, 2, 'Nướng xém vỏ hành tây, gừng, hoa hồi, quế, thảo quả trên bếp than hoặc nồi chiên không dầu cho thơm phức.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (1, 3, 'Cho xương bò cùng các gia vị vừa nướng vào nồi lớn, đổ 3 lít nước và hầm lửa nhỏ trong 1.5 - 2 tiếng.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (1, 4, 'Trần nhanh bánh phở qua nước sôi rồi xếp vào tô. Đặt thịt thăn bò thái mỏng và hành lá xắt nhỏ lên trên.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (1, 5, 'Chan nước dùng đang sôi sùng sục trực tiếp lên tô phở để thịt bò tái chín tới. Thưởng thức cùng chanh, ớt tươi.', NULL);");

        // --- Bún Chả Hà Nội (recipeId = 2) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (2, 1, 'Ướp thịt ba chỉ và thịt băm riêng biệt với hành khô, nước mắm, đường hoa xá, dầu hàu trong 30 phút.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (2, 2, 'Làm dưa góp: Đu đủ, cà rốt thái mỏng bóp muối rửa sạch, ngâm dấm đường 20 phút cho giòn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (2, 3, 'Vo viên thịt băm và xếp thịt miếng lên vỉ, nướng trên bếp than hoa đến khi vàng xém hai mặt.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (2, 4, 'Pha nước chấm: Nước ấm, nước mắm, dấm, đường theo tỷ lệ 4:1:1:1. Thêm chả nướng, dưa góp, tỏi ớt băm và dùng kèm bún tươi.', NULL);");

        // --- Spaghetti Carbonara (recipeId = 3) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (3, 1, 'Luộc mỳ Spaghetti trong nồi nước sôi có nêm 1 chút muối khoảng 8 - 10 phút cho mỳ chín tới (Al dente).', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (3, 2, 'Cắt thịt bacon thành miếng nhỏ, gián giòn trên chảo lửa vừa cho tiết bớt mỡ béo.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (3, 3, 'Đánh tan lòng đỏ trứng gà cùng phô mai Parmesan bào mịn và tiêu đen xay trong bát tô lớn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (3, 4, 'Tắt bếp chảo mỳ, trút nhanh mỳ và bát trứng phô mai vào đảo đều tay để sức nóng tự nhiên làm chín trứng tạo độ béo mịn mượt.', NULL);");

        // --- Cơm Trộn Bibimbap (recipeId = 4) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (4, 1, 'Sơ chế rau bina, giá đỗ chần qua nước sôi. Cà rốt và nấm thái sợi rồi xào sơ với ít dầu tỏi.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (4, 2, 'Thịt bò thái mỏng ướp xì dầu, tỏi băm, dầu mè rồi xào chín tới trên lửa lớn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (4, 3, 'Chiên 1 quả trứng gà sao cho lòng đỏ còn ngô ngố (lòng đào).', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (4, 4, 'Xếp cơm vào bát đá nóng, rải đều các loại rau củ, thịt bò xung quanh, đặt trứng lên giữa và thêm 2 muỗng sốt Gochujang rồi trộn đều.', NULL);");

        // --- Mỳ Ramen Tonkotsu (recipeId = 5) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (5, 1, 'Ninh xương heo trên lửa lớn khoảng 4 - 6 tiếng cho đến khi nước đục màu trắng sữa béo bùi.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (5, 2, 'Luộc mỳ Ramen trong nước sôi khoảng 2 - 3 phút rồi vớt ra xóc róc nước.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (5, 3, 'Xếp mỳ vào tô, múc nước dùng hầm xương heo nóng chao lên mỳ.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (5, 4, 'Trình bày lát thịt Chashu áp chảo, nửa quả trứng lòng đào ngâm tương, rong biển khô và măng ngâm lên bề mặt.', NULL);");

        // --- Salad Ức Gà (recipeId = 6) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (6, 1, 'Ức gà rửa sạch, ướp chút muối, tiêu xay và dầu olive trong 10 phút.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (6, 2, 'Áp chảo ức gà mỗi mặt 4 - 5 phút cho chín vàng mọng nước, sau đó thái lát mỏng vừa ăn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (6, 3, 'Nấu nước cốt chanh dây với ít mật tịnh trên lửa nhỏ đến khi sốt hơi sệt lại.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (6, 4, 'Xếp xà lách tươi, cà chua bi chẻ đôi ra đĩa, đặt ức gà lên trên và rưới sốt chanh dây lên thưởng thức.', NULL);");

        // --- Chocolate Lava Cake (recipeId = 7) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (7, 1, 'Đun chảy sô-cô-la đen cùng bơ lạt cách thủy cho đến khi hỗn hợp mịn màng đồng nhất.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (7, 2, 'Đánh tan trứng gà với đường bột, sau đó trút hỗn hợp sô-cô-la đun chảy vào khuấy nhẹ tay.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (7, 3, 'Rây bột mì vào hỗn hợp trộn đều. Rót bột vào khuôn nướng đã thoa bơ mịn chống dính.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (7, 4, 'Nướng bánh ở nhiệt độ 200°C trong chính xác 10 - 12 phút để vỏ bên ngoài chín xốp mà nhân bên trong vẫn tan chảy.', NULL);");

        // --- Bánh Mỳ Chảo Bò Né (recipeId = 8) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (8, 1, 'Ướp thịt thăn bò thái lát với tỏi băm, dầu hàu, tiêu xay và ít bột năng cho thịt mềm mịn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (8, 2, 'Làm nóng chảo gang, cho 1 muỗng bơ lạt vào đun chảy thơm phức.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (8, 3, 'Cho thịt bò vào chảo đảo nhanh, đập thêm 1 quả trứng gà ốp la và rải góc pate béo ngậy.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (8, 4, 'Rắc hành tây thái mỏng, tiêu xay lên trên, bế chảo sôi xèo xèo ra bàn và chấm ăn cùng bánh mì giòn.', NULL);");

        // --- Bánh Xèo (9) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (9, 1, 'Pha bột bánh xèo với nước cốt dừa, bột nghệ và hành lá cắt nhỏ, để nghỉ 20 phút.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (9, 2, 'Xào sơ tôm, thịt ba chỉ với chút gia vị cho ngấm.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (9, 3, 'Tráng một lớp bột mỏng lên chảo nóng nhiều dầu, cho nhân tôm thịt và giá đỗ vào giữa, đậy vung 2 phút.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (9, 4, 'Mở vung chờ viền bánh giòn thì gập đôi lại. Thưởng thức nóng cùng rau sống và nước mắm chua ngọt.', NULL);");

        // --- Gỏi Cuốn (10) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (10, 1, 'Thịt ba chỉ luộc chín, thái lát mỏng. Tôm luộc chín, bóc vỏ, chẻ đôi sống lưng.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (10, 2, 'Rửa sạch xà lách, rau thơm và hẹ, để ráo nước.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (10, 3, 'Nhúng bánh tráng qua nước ấm cho mềm, trải phẳng ra đĩa.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (10, 4, 'Xếp rau sống, bún, thịt lợn vào gập 2 mép. Đặt tôm đỏ ra mặt ngoài, thêm cọng hẹ rồi cuộn chặt tay.', NULL);");

        // --- Pizza Margherita (11) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (11, 1, 'Nhồi bột mì với men nở, dầu olive và nước ấm. Ủ bột khoảng 1 tiếng cho nở gấp đôi.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (11, 2, 'Cán mỏng bột thành hình tròn để làm đế pizza, châm vài lỗ nhỏ trên mặt đế.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (11, 3, 'Phết đều sốt cà chua lên mặt bánh, xếp các lát phô mai Mozzarella lên trên.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (11, 4, 'Nướng ở 250 độ C trong 10-12 phút. Lấy ra trang trí thêm lá húng quế tươi và thưởng thức.', NULL);");

        // --- Risotto Nấm (12) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (12, 1, 'Áp chảo nấm với bơ và tỏi cho xém vàng rồi trút ra bát riêng.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (12, 2, 'Cho gạo Ý vào chảo đảo qua với dầu olive. Từ từ thêm từng muỗng nước dùng gà nóng vào đun nhỏ lửa.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (12, 3, 'Vừa đun vừa đảo đều tay đến khi hạt gạo ngậm đủ nước, nở bung và chín mềm (khoảng 20 phút).', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (12, 4, 'Tắt bếp, trộn nấm đã xào, bơ lạt và phô mai Parmesan vào để tạo độ béo ngậy, dẻo sánh.', NULL);");

        // --- Tteokbokki (13) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (13, 1, 'Ngâm bánh gạo trong nước ấm 10 phút cho mềm nếu dùng bánh gạo lạnh.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (13, 2, 'Đun sôi 400ml nước, cho tương ớt Gochujang, đường, xì dầu vào khuấy tan thành nước sốt.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (13, 3, 'Thêm bánh gạo, chả cá cắt miếng và hành tây vào nấu trên lửa vừa, đảo liên tục để không dính đáy nồi.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (13, 4, 'Khi nước sốt sệt lại, cho trứng cút luộc và hành lá vào đảo đều rồi tắt bếp.', NULL);");

        // --- Canh Kim Chi (14) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (14, 1, 'Thái thịt ba chỉ thành miếng vừa ăn, xào săn với chút dầu mè và hành tỏi.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (14, 2, 'Cho kim chi chua vào xào cùng thịt khoảng 3 phút để tiết ra màu đỏ đẹp mắt.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (14, 3, 'Đổ nước lọc hoặc nước vo gạo vào đun sôi 15 phút cho thịt mềm và nước canh đậm vị.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (14, 4, 'Thêm đậu hũ non thái vuông và hành baro thái xéo vào, đun sôi bùng lại rồi tắt bếp.', NULL);");

        // --- Sushi Cá Hồi (15) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (15, 1, 'Nấu chín gạo Nhật, xới tơi rồi trộn đều khi còn nóng với hỗn hợp giấm, đường, muối.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (15, 2, 'Dùng dao mỏng, bén thái phi-lê cá hồi thành từng lát mỏng, to bản vừa ăn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (15, 3, 'Làm ướt tay, vo một nắm cơm nhỏ thành hình bầu dục (khoảng 20g cơm).', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (15, 4, 'Chấm 1 xíu Wasabi lên mặt dưới lát cá hồi rồi đặt miếng cá lên nắm cơm, vuốt nhẹ cho dính vào nhau.', NULL);");

        // --- Cơm Cà ri (16) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (16, 1, 'Khoai tây, cà rốt gọt vỏ thái hạt lựu lớn. Thịt bò thái miếng vừa ăn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (16, 2, 'Xào sơ thịt bò rồi cho khoai tây, cà rốt vào đảo cùng.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (16, 3, 'Đổ nước ngập nguyên liệu, ninh sôi lửa nhỏ khoảng 20 phút cho mềm mềm.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (16, 4, 'Tắt lửa, bẻ viên cà ri cho vào nồi khuấy cho tan hoàn toàn, bật lại lửa liu riu cho sốt sánh đặc là xong.', NULL);");

        // --- Cá Hồi Măng Tây (17) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (17, 1, 'Thấm khô cá hồi, ướp với chút muối, tiêu đen và dầu olive trong 10 phút.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (17, 2, 'Măng tây cắt bỏ gốc già, luộc sơ qua nước sôi 1 phút rồi vớt ra ngâm nước đá cho giòn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (17, 3, 'Áp chảo cá hồi mặt da trước khoảng 3 phút cho xém giòn, lật mặt lại áp chảo thêm 2 phút là chín tới.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (17, 4, 'Dùng cùng chiếc chảo đó, cho xíu bơ lạt vào xào nhanh măng tây. Bày ra đĩa vắt thêm chút chanh vàng.', NULL);");

        // --- Smoothie Bowl (18) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (18, 1, 'Bảo quản chuối và quả mọng trong ngăn đá tủ lạnh ít nhất 4 tiếng trước khi làm.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (18, 2, 'Cho trái cây đông lạnh cùng sữa hạnh nhân vào máy xay sinh tố, xay nhấp thả đến khi nhuyễn đặc như kem.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (18, 3, 'Đổ hỗn hợp Smoothie đặc sánh ra bát tô tròn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (18, 4, 'Xếp các loại topping: hạt chia, Granola, dừa khô, trái cây tươi cắt lát lên bề mặt thật đẹp mắt.', NULL);");

        // --- Tiramisu (19) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (19, 1, 'Dùng máy đánh trứng đánh bông kem tươi (Whipping cream) với đường.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (19, 2, 'Tán nhuyễn phô mai Mascarpone, trộn nhẹ nhàng (fold) cùng kem tươi đã đánh bông.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (19, 3, 'Nhúng thật nhanh (1 giây) bánh quy Sâm-panh qua nước cà phê Espresso rồi xếp kín đáy khuôn.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (19, 4, 'Phủ 1 lớp kem lên trên, lặp lại thêm 1 lớp bánh 1 lớp kem nữa. Cất tủ lạnh 4-6 tiếng, khi ăn rây bột cacao lên mặt.', NULL);");

        // --- Chè Khúc Bạch (20) ---
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (20, 1, 'Ngâm bột Gelatin với chút nước cho nở mềm.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (20, 2, 'Đun nóng nhẹ (không sôi) hỗn hợp sữa tươi, Whipping cream và chút đường. Cho Gelatin vào khuấy tan.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (20, 3, 'Đổ hỗn hợp ra khuôn vuông, cất tủ lạnh 3-4 tiếng cho đông cứng lại rồi dùng dao lượn sóng cắt miếng vuông nhỏ.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (20, 4, 'Nấu nước đường phèn với lá dứa cho thơm, để nguội. Khi ăn múc nước đường, khúc bạch, thêm đá và rắc hạnh nhân lát.', NULL);");

        // ==========================================
        // 6. DỮ LIỆU BẢNG FAVORITE (Tất cả các User)
        // ==========================================
        // --- User 1 (An) ---
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (1, 1);");
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (1, 2);");
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (1, 3);");
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (1, 6);");
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (1, 8);");

        // --- User 2 (Hiếu) ---
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (2, 4);");
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (2, 5);");

        // --- User 3 (Đạt) ---
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (3, 3);");
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (3, 7);");

        // --- User 4 (Mai) ---
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (4, 6);");
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (4, 4);");

        // ==========================================
        // 7. DỮ LIỆU BẢNG GROCERYLIST
        // ==========================================
        db.execSQL("INSERT INTO GroceryList (listId, userId, title) VALUES (1, 1, 'Danh sách đi chợ của An');");
        db.execSQL("INSERT INTO GroceryList (listId, userId, title) VALUES (2, 2, 'Mua đồ làm món Hàn & Nhật');");
        db.execSQL("INSERT INTO GroceryList (listId, userId, title) VALUES (3, 3, 'Đồ làm bánh cuối tuần');");
        db.execSQL("INSERT INTO GroceryList (listId, userId, title) VALUES (4, 4, 'Thực đơn Eat Clean tuần này');");

        // ==========================================
        // 8. DỮ LIỆU BẢNG GROCERYITEM
        // ==========================================
        // --- USER 1 (An - listId = 1) ---
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (1, 'Thịt thăn bò (Tái)', '300 g', 1, 1);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (1, 'Bánh phở tươi', '600 g', 0, 1);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (1, 'Mỳ Spaghetti', '200 g', 0, 3);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (1, 'Ức gà tươi', '200 g', 1, 6);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (1, 'Sữa tươi không đường', '1 hộp', 0, NULL);");

        // --- USER 2 (Hiếu - listId = 2) ---
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (2, 'Thịt bò thái mỏng', '200 g', 0, 4);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (2, 'Sốt ớt Hàn Quốc (Gochujang)', '1 hũ', 1, 4);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (2, 'Thịt cuộn xá xíu (Chashu)', '250 g', 0, 5);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (2, 'Mỳ Ramen tươi', '2 vắt', 0, 5);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (2, 'Kim chi cải thảo', '500 g', 1, NULL);");

        // --- USER 3 (Đạt - listId = 3) ---
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (3, 'Sô-cô-la đen 70%', '200 g', 0, 7);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (3, 'Bơ lạt Anchor', '100 g', 1, 7);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (3, 'Trứng gà nguyên quả', '1 vỉ 10 quả', 0, 7);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (3, 'Bột mì đa dụng', '500 g', 0, NULL);");

        // --- USER 4 (Mai - listId = 4) ---
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (4, 'Ức gà tươi', '500 g', 0, 6);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (4, 'Xà lách thuỷ canh', '200 g', 1, 6);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (4, 'Cà chua bi & Dưa leo', '250 g', 0, 6);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (4, 'Yến mạch cán mỏng', '1 túi 500g', 0, NULL);");

        // ==========================================
        // 9. DỮ LIỆU BẢNG COOKHISTORY
        // ==========================================
        db.execSQL("INSERT INTO CookHistory (userId, recipeId) VALUES (1, 1);");
        db.execSQL("INSERT INTO CookHistory (userId, recipeId) VALUES (1, 3);");
        db.execSQL("INSERT INTO CookHistory (userId, recipeId) VALUES (1, 8);");
        db.execSQL("INSERT INTO CookHistory (userId, recipeId) VALUES (2, 4);");
        db.execSQL("INSERT INTO CookHistory (userId, recipeId) VALUES (3, 7);");
        db.execSQL("INSERT INTO CookHistory (userId, recipeId) VALUES (4, 6);");
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT userId FROM User WHERE email = ? AND password = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{email, password});

        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userId FROM User WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public long registerUser(String email, String password, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        values.put("fullName", fullName);
        return db.insert("User", null, values);
    }

    public List<Recipe> getFavoriteRecipes(int userId, String orderBy) {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Câu lệnh JOIN giữa bảng Recipe và Favorite
        String query = "SELECT r.recipeId, r.title, r.cookTime, r.calories, r.difficulty, r.rating, r.image " +
                "FROM Recipe r " +
                "INNER JOIN Favorite f ON r.recipeId = f.recipeId " +
                "WHERE f.userId = ? " +
                "ORDER BY " + orderBy;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("recipeId"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                int cookTime = cursor.getInt(cursor.getColumnIndexOrThrow("cookTime"));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow("calories"));
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow("difficulty"));
                double rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                // Đưa dữ liệu vào danh sách
                Recipe recipe = new Recipe();
                recipe.setRecipeId(id);
                recipe.setTitle(title);
                recipe.setCookTime(cookTime);
                recipe.setCalories(calories);
                recipe.setDifficulty(difficulty);
                recipe.setRating(rating);
                recipe.setImage(image);

                list.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int getUserId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT userId FROM User WHERE email = ? AND password = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.rawQuery(sql, selectionArgs);
        int userId = -1; // Mặc định -1 là không tìm thấy

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
        }

        cursor.close(); // Luôn nhớ đóng cursor
        return userId;
    }

    public String getUserFullName(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fullName = "User";
        Cursor cursor = db.rawQuery("SELECT fullName FROM User WHERE userId = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
        }
        cursor.close();
        return fullName;
    }

    // Lấy toàn bộ danh mục
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Category", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("categoryId"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("categoryName"));
                list.add(new Category(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Lấy danh sách món ăn phổ biến (Rating cao)
    public List<Recipe> getPopularRecipes(int limit) {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Recipe ORDER BY rating DESC LIMIT ?", new String[]{String.valueOf(limit)});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToRecipe(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Lấy món ăn phổ biến theo danh mục
    public List<Recipe> getPopularRecipesByCategory(int categoryId, int limit) {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Recipe WHERE categoryId = ? ORDER BY rating DESC LIMIT ?", 
                new String[]{String.valueOf(categoryId), String.valueOf(limit)});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToRecipe(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Lấy 1 món ăn ngẫu nhiên cho Today's Pick
    public Recipe getRandomRecipe() {
        SQLiteDatabase db = this.getReadableDatabase();
        Recipe recipe = null;
        Cursor cursor = db.rawQuery("SELECT * FROM Recipe ORDER BY RANDOM() LIMIT 1", null);
        if (cursor.moveToFirst()) {
            recipe = cursorToRecipe(cursor);
        }
        cursor.close();
        return recipe;
    }

    // Hàm phụ để chuyển đổi Cursor sang đối tượng Recipe
    private Recipe cursorToRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(cursor.getInt(cursor.getColumnIndexOrThrow("recipeId")));
        recipe.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")));
        recipe.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        recipe.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        recipe.setImage(cursor.getString(cursor.getColumnIndexOrThrow("image")));
        recipe.setCookTime(cursor.getInt(cursor.getColumnIndexOrThrow("cookTime")));
        recipe.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow("difficulty")));
        recipe.setServings(cursor.getInt(cursor.getColumnIndexOrThrow("servings")));
        recipe.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow("calories")));
        recipe.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow("rating")));
        recipe.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
        return recipe;
    }

    public List<Recipe> searchRecipes(String query) {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Lấy toàn bộ danh sách để lọc không dấu bằng Java
        Cursor cursor = db.rawQuery("SELECT * FROM Recipe", null);
        String normalizedQuery = StringHelper.removeAccents(query);

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = cursorToRecipe(cursor);
                String normalizedTitle = StringHelper.removeAccents(recipe.getTitle());
                String normalizedDesc = StringHelper.removeAccents(recipe.getDescription());

                if (normalizedTitle.contains(normalizedQuery) || normalizedDesc.contains(normalizedQuery)) {
                    list.add(recipe);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 1. Lấy hoặc tạo listId cho User
    public int getOrCreateGroceryListId(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int listId = -1;

        Cursor cursor = db.rawQuery("SELECT listId FROM GroceryList WHERE userId = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            listId = cursor.getInt(cursor.getColumnIndexOrThrow("listId"));
        } else {
            ContentValues values = new ContentValues();
            values.put("userId", userId);
            values.put("title", "My Grocery List");
            listId = (int) db.insert("GroceryList", null, values);
        }
        cursor.close();
        return listId;
    }

    // 2. Lấy danh sách nguyên liệu đã gom nhóm theo Tên Món Ăn (Recipe)
    public List<GrocerySection> getGroupedGroceryList(int userId) {
        List<GrocerySection> sectionList = new ArrayList<>();
        int listId = getOrCreateGroceryListId(userId);
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT gi.*, r.title AS recipeTitle " +
                "FROM GroceryItem gi " +
                "LEFT JOIN Recipe r ON gi.recipeId = r.recipeId " +
                "WHERE gi.listId = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(listId)});
        Map<String, List<GroceryItem>> mapItems = new LinkedHashMap<>();
        Map<String, Integer> mapRecipeIds = new LinkedHashMap<>();

        if (cursor.moveToFirst()) {
            do {
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("itemId"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("ingredientName"));
                String qty = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
                int isCheckedInt = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked"));

                Integer recipeId = null;
                if (!cursor.isNull(cursor.getColumnIndexOrThrow("recipeId"))) {
                    recipeId = cursor.getInt(cursor.getColumnIndexOrThrow("recipeId"));
                }

                String recipeTitle = cursor.getString(cursor.getColumnIndexOrThrow("recipeTitle"));
                if (recipeTitle == null || recipeTitle.isEmpty()) {
                    recipeTitle = "Món mua thêm";
                } else {
                    recipeTitle = "🟠  " + recipeTitle;
                }

                GroceryItem item = new GroceryItem(itemId, listId, name, qty, isCheckedInt == 1, recipeId);

                if (!mapItems.containsKey(recipeTitle)) {
                    mapItems.put(recipeTitle, new ArrayList<>());
                    mapRecipeIds.put(recipeTitle, recipeId);
                }
                mapItems.get(recipeTitle).add(item);

            } while (cursor.moveToNext());
        }
        cursor.close();

        for (Map.Entry<String, List<GroceryItem>> entry : mapItems.entrySet()) {
            sectionList.add(new GrocerySection(entry.getKey(), mapRecipeIds.get(entry.getKey()), entry.getValue()));
        }

        return sectionList;
    }

    // 3. Cập nhật trạng thái CheckBox
    public void updateGroceryItemCheck(int itemId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isChecked", isChecked ? 1 : 0);
        db.update("GroceryItem", values, "itemId = ?", new String[]{String.valueOf(itemId)});
    }

    // 4. Xóa tất cả các mục đã tick chọn (Clear Completed)
    public void clearCompletedGroceryItems(int userId) {
        int listId = getOrCreateGroceryListId(userId);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("GroceryItem", "listId = ? AND isChecked = 1", new String[]{String.valueOf(listId)});
    }

    // 5. Thêm một mục mới thủ công (Add Item)
    public boolean addGroceryItem(int userId, String name, String quantity) {
        int listId = getOrCreateGroceryListId(userId);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("listId", listId);
        values.put("ingredientName", name);
        values.put("quantity", quantity);
        values.put("isChecked", 0);

        return db.insert("GroceryItem", null, values) != -1;
    }

    // 2. Xóa 1 nguyên liệu duy nhất (Bấm nút dấu trừ)
    public boolean deleteGroceryItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("GroceryItem", "itemId = ?", new String[]{String.valueOf(itemId)}) > 0;
    }

    // 3. Xóa cả 1 Món Ăn (Bấm nút dấu X ở header món)
    public void deleteGrocerySection(int userId, Integer recipeId) {
        int listId = getOrCreateGroceryListId(userId);
        SQLiteDatabase db = this.getWritableDatabase();

        if (recipeId != null) {
            db.delete("GroceryItem", "listId = ? AND recipeId = ?",
                    new String[]{String.valueOf(listId), String.valueOf(recipeId)});
        } else {
            // Trường hợp "Món mua thêm" (recipeId IS NULL)
            db.delete("GroceryItem", "listId = ? AND recipeId IS NULL",
                    new String[]{String.valueOf(listId)});
        }
    }

    // 4. Xóa toàn bộ danh sách (Bấm nút Clear All List)
    public void clearAllGroceryList(int userId) {
        int listId = getOrCreateGroceryListId(userId);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("GroceryItem", "listId = ?", new String[]{String.valueOf(listId)});
    }

    // 1. Lấy đếm số món đã nấu (Cooked)
    public int getCookedRecipesCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Thay "CookedRecipe" hoặc điều kiện câu SQL theo đúng bảng của bạn
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CookHistory WHERE userId = ?", new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // 2. Lấy đếm số món đã lưu yêu thích (Favorites)
    public int getFavoritesSavedCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Favorite WHERE userId = ?", new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // 3. Lấy thông tin User hiện tại (Full Name, Email, Password)
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE userId = ?", new String[]{String.valueOf(userId)});
        User user = null;
        if (cursor.moveToFirst()) {
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            user = new User(userId, email,password, fullName, "");
        }
        cursor.close();
        return user;
    }

    // 4. Cập nhật Thông tin cá nhân (Full Name & Email)
    public boolean updateUserProfile(int userId, String fullName, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullName", fullName);
        values.put("email", email);
        return db.update("User", values, "userId = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    // 5. Cập nhật Mật khẩu mới
    public boolean updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        return db.update("User", values, "userId = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean removeFavorite(int userId, int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("Favorite", "userId = ? AND recipeId = ?",
                new String[]{String.valueOf(userId), String.valueOf(recipeId)});
        return rowsDeleted > 0;
    }

    // 1. Kiểm tra xem user hiện tại đã thả tim món này chưa?
    public boolean isFavorite(int userId, int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery(
                "SELECT * FROM Favorite WHERE userId = ? AND recipeId = ?",
                new String[]{String.valueOf(userId), String.valueOf(recipeId)}
        );
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 2. Hàm Toggle: Đã thích thì Xóa, Chưa thích thì Thêm
    public boolean toggleFavorite(int userId, int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (isFavorite(userId, recipeId)) {
            // Đã thả tim -> Xóa khỏi danh sách yêu thích
            db.delete("Favorite", "userId = ? AND recipeId = ?",
                    new String[]{String.valueOf(userId), String.valueOf(recipeId)});
            return false; // Trả về false nghĩa là trạng thái hiện tại: Chưa thích (Trắng)
        } else {
            // Chưa thả tim -> Thêm vào bảng Favorite
            android.content.ContentValues values = new android.content.ContentValues();
            values.put("userId", userId);
            values.put("recipeId", recipeId);
            db.insert("Favorite", null, values);
            return true; // Trả về true nghĩa là trạng thái hiện tại: Đã thích (Vàng)
        }
    }
}