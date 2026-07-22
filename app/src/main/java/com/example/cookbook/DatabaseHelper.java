package com.example.cookbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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
        // 1. DỮ LIỆU BẢNG USER (1 Người dùng)
        // ==========================================
        db.execSQL("INSERT INTO User (userId, email, password, fullName, avatar) " +
                "VALUES (1, 'user@gmail.com', '123456', 'Nguyễn Văn A', 'avatar_default');");
        db.execSQL("INSERT INTO User (userId, email, password, fullName, avatar) " +
                "VALUES (2, 'nth303@gmail.com', '123456', 'Nguyễn Trung Hiếu', 'avatar_default');");
        db.execSQL("INSERT INTO User (userId, email, password, fullName, avatar) " +
                "VALUES (3, 'ntd11@gmail.com', '123456', 'Nguyễn Tuấn Đạt', 'avatar_default');");

        // ==========================================
        // 2. DỮ LIỆU BẢNG CATEGORY (4 Danh mục)
        // ==========================================
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (1, 'Món Việt');");
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (2, 'Món Ý');");
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (3, 'Món Hàn');");
        db.execSQL("INSERT INTO Category (categoryId, categoryName) VALUES (4, 'Tráng miệng');");

        // ==========================================
        // 3. DỮ LIỆU BẢNG RECIPE (3 Món ăn)
        // ==========================================
        // Món 1: Phở Bò Hà Nội (5.0 sao)
        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (1, 1, 'Phở Bò Hà Nội', 'Món phở truyền thống hương vị đậm đà thơm ngon.', 'img_pho_bo', 60, 'Medium', 4, 450, 5.0);");

        // Món 2: Pasta Carbonara (4.8 sao)
        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (2, 2, 'Pasta Carbonara', 'Mỳ Ý sốt kem béo ngậy chuẩn vị Ý.', 'img_pasta', 25, 'Medium', 2, 520, 4.8);");

        // Món 3: Bánh Chocolate Lava (4.9 sao)
        db.execSQL("INSERT INTO Recipe (recipeId, categoryId, title, description, image, cookTime, difficulty, servings, calories, rating) " +
                "VALUES (3, 4, 'Chocolate Lava Cake', 'Bánh nướng sô-cô-la chảy ngọt ngào.', 'img_lava_cake', 22, 'Hard', 2, 380, 4.9);");

        // ==========================================
        // 4. DỮ LIỆU BẢNG INGREDIENT (Nguyên liệu)
        // ==========================================
        // Nguyên liệu cho Phở Bò (recipeId = 1)
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Xương ống bò', '1.5 kg');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Nạm bò', '300 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Bánh phở tươi', '500 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (1, 'Hành tây', '1 củ');");

        // Nguyên liệu cho Pasta Carbonara (recipeId = 2)
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (2, 'Mỳ Spaghetti', '200 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (2, 'Thịt hun khói (Bacon)', '150 g');");
        db.execSQL("INSERT INTO Ingredient (recipeId, ingredientName, quantity) VALUES (2, 'Phô mai Parmigiano', '50 g');");

        // ==========================================
        // 5. DỮ LIỆU BẢNG STEP (Các bước nấu)
        // ==========================================
        // Các bước cho Phở Bò (recipeId = 1)
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (1, 1, 'Rửa sạch xương bò và chần qua nước sôi 5 phút.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (1, 2, 'Ninh xương trong 2-3 tiếng cùng hành nướng và gừng.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (1, 3, 'Trần bánh phở, xếp thịt bò lát mỏng lên trên và múc nước dùng.', NULL);");

        // Các bước cho Pasta (recipeId = 2)
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (2, 1, 'Luộc mỳ spaghetti trong nước sôi có muối khoảng 8-10 phút.', NULL);");
        db.execSQL("INSERT INTO Step (recipeId, stepNumber, description, image) VALUES (2, 2, 'Rán giòn thịt bacon trên chảo nóng.', NULL);");

        // ==========================================
        // 6. DỮ LIỆU BẢNG FAVORITE (Yêu thích)
        // ==========================================
        // User 1 thích Phở Bò và Pasta
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (1, 1);");
        db.execSQL("INSERT INTO Favorite (userId, recipeId) VALUES (1, 2);");

        // ==========================================
        // 7. DỮ LIỆU BẢNG GROCERYLIST (Danh sách mua sắm)
        // ==========================================
        db.execSQL("INSERT INTO GroceryList (listId, userId, title) VALUES (1, 1, 'Danh sách đi chợ tuần này');");

        // ==========================================
        // 8. DỮ LIỆU BẢNG GROCERYITEM (Món cần mua)
        // ==========================================
        // List 1 chứa các món (isChecked: 0 = chưa mua, 1 = đã mua)
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (1, 'Nạm bò', '300 g', 1, 1);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (1, 'Bánh phở tươi', '200 g', 0, 1);");
        db.execSQL("INSERT INTO GroceryItem (listId, ingredientName, quantity, isChecked, recipeId) VALUES (1, 'Mỳ Spaghetti', '200 g', 0, 2);");

        // ==========================================
        // 9. DỮ LIỆU BẢNG COOKHISTORY (Lịch sử đã nấu)
        // ==========================================
        db.execSQL("INSERT INTO CookHistory (userId, recipeId) VALUES (1, 1);");
        db.execSQL("INSERT INTO CookHistory (userId, recipeId) VALUES (1, 2);");
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
        String query = "SELECT r.recipeId, r.title, r.cookTime, r.difficulty, r.rating, r.image " +
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
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow("difficulty"));
                double rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                // Đưa dữ liệu vào danh sách
                Recipe recipe = new Recipe();
                recipe.setRecipeId(id);
                recipe.setTitle(title);
                recipe.setCookTime(cookTime);
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
}