package com.example.cookbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên Database và Phiên bản
    private static final String DATABASE_NAME = "Cookbook.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                "username TEXT UNIQUE NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "fullName TEXT, " +
                "avatar TEXT, " +
                "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";

        // 2. Tạo bảng Category
        String CREATE_CATEGORY_TABLE = "CREATE TABLE Category (" +
                "categoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoryName TEXT NOT NULL, " +
                "image TEXT" +
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
}