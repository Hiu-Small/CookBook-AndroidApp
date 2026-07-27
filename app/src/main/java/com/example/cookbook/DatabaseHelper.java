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
        return getFilteredRecipes(-1, query, "Any time", "All", "Popular", "All");
    }

    public List<Recipe> getFilteredRecipes(int userId, String query, String timeFilter, String difficultyFilter, String sortBy, String statusFilter) {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sql = new StringBuilder("SELECT * FROM Recipe r WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (timeFilter != null && !timeFilter.equals("Any time")) {
            if (timeFilter.equals("< 15 min")) {
                sql.append(" AND cookTime < 15");
            } else if (timeFilter.equals("< 30 min")) {
                sql.append(" AND cookTime < 30");
            } else if (timeFilter.equals("< 60 min")) {
                sql.append(" AND cookTime < 60");
            } else if (timeFilter.equals(">= 60 min")) {
                sql.append(" AND cookTime >= 60");
            }
        }

        if (difficultyFilter != null && !difficultyFilter.equals("All")) {
            sql.append(" AND difficulty = ?");
            args.add(difficultyFilter);
        }

        if (statusFilter != null && !statusFilter.equals("All") && userId != -1) {
            if (statusFilter.equals("Cooked")) {
                sql.append(" AND EXISTS (SELECT 1 FROM CookHistory ch WHERE ch.recipeId = r.recipeId AND ch.userId = ?)");
                args.add(String.valueOf(userId));
            } else if (statusFilter.equals("Not Cooked")) {
                sql.append(" AND NOT EXISTS (SELECT 1 FROM CookHistory ch WHERE ch.recipeId = r.recipeId AND ch.userId = ?)");
                args.add(String.valueOf(userId));
            }
        }

        if (sortBy != null) {
            if (sortBy.contains("Popular")) {
                sql.append(" ORDER BY rating DESC");
            } else if (sortBy.contains("Cooking Time")) {
                sql.append(" ORDER BY cookTime ASC");
            }
        } else {
            sql.append(" ORDER BY rating DESC");
        }

        Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        String normalizedQuery = (query != null && !query.isEmpty()) ? StringHelper.removeAccents(query) : null;

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = cursorToRecipe(cursor);
                if (normalizedQuery == null) {
                    list.add(recipe);
                } else {
                    String normalizedTitle = StringHelper.removeAccents(recipe.getTitle());
                    if (normalizedTitle.contains(normalizedQuery)) {
                        list.add(recipe);
                    }
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

    public void addRecipeIngredientsToGroceryList(int userId, int recipeId) {
        List<Ingredient> ingredients = getIngredientsByRecipeId(recipeId);
        int listId = getOrCreateGroceryListId(userId);
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            for (Ingredient ing : ingredients) {
                ContentValues values = new ContentValues();
                values.put("listId", listId);
                values.put("ingredientName", ing.getIngredientName());
                values.put("quantity", ing.getQuantity());
                values.put("isChecked", 0);
                values.put("recipeId", recipeId);
                db.insert("GroceryItem", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
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

    public Recipe getRecipeById(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Recipe recipe = null;
        Cursor cursor = db.rawQuery("SELECT * FROM Recipe WHERE recipeId = ?", new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            recipe = cursorToRecipe(cursor);
        }
        cursor.close();
        return recipe;
    }

    public List<Ingredient> getIngredientsByRecipeId(int recipeId) {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Ingredient WHERE recipeId = ?", new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ingredientId"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("ingredientName"));
                String quantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
                list.add(new Ingredient(id, recipeId, name, quantity));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Step> getStepsByRecipeId(int recipeId) {
        List<Step> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Step WHERE recipeId = ? ORDER BY stepNumber ASC", new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("stepId"));
                int num = cursor.getInt(cursor.getColumnIndexOrThrow("stepNumber"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String img = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                list.add(new Step(id, recipeId, num, desc, img));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public String getCategoryNameById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = "";
        Cursor cursor = db.rawQuery("SELECT categoryName FROM Category WHERE categoryId = ?", new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    public boolean isFavorite(int userId, int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM Favorite WHERE userId = ? AND recipeId = ?",
                new String[]{String.valueOf(userId), String.valueOf(recipeId)});
        boolean favorite = cursor.getCount() > 0;
        cursor.close();
        return favorite;
    }

    public void toggleFavorite(int userId, int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (isFavorite(userId, recipeId)) {
            db.delete("Favorite", "userId = ? AND recipeId = ?",
                    new String[]{String.valueOf(userId), String.valueOf(recipeId)});
        } else {
            ContentValues values = new ContentValues();
            values.put("userId", userId);
            values.put("recipeId", recipeId);
            db.insert("Favorite", null, values);
        }
    }

    public void addCookHistory(int userId, int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("recipeId", recipeId);
        db.insert("CookHistory", null, values);
    }

    public boolean isCooked(int userId, int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM CookHistory WHERE userId = ? AND recipeId = ?",
                new String[]{String.valueOf(userId), String.valueOf(recipeId)});
        boolean cooked = cursor.getCount() > 0;
        cursor.close();
        return cooked;
    }

    public void toggleCookHistory(int userId, int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (isCooked(userId, recipeId)) {
            db.delete("CookHistory", "userId = ? AND recipeId = ?",
                    new String[]{String.valueOf(userId), String.valueOf(recipeId)});
        } else {
            addCookHistory(userId, recipeId);
        }
    }
}
