# Walkthrough - UI Overlap Fixes

I have fixed the layout issues where text was overlapping with other UI elements.

## Changes Made

### 1. Today's Pick Overlap Fix
- Modified `fragment_home.xml`:
    - Constrained the title container `LinearLayout` to the left of the "Spin!" button using `android:layout_toStartOf="@id/btnSpin"`.
    - Added `android:ellipsize="end"` and `android:maxLines="1"` to the `tvTodayPickTitle`.
    - This ensures that even with very long recipe names, the title will truncate with "..." and never hide behind the button.

### 2. Ingredient List Overlap Fix
- Modified `item_ingredient.xml`:
    - Constrained the `tvIngredientName` to the left of the quantity badge using `android:layout_toStartOf="@id/tvIngredientQuantity"`.
    - Added a margin for better spacing and enabled text truncation with `ellipsize`.
    - Now, long ingredient names (like "Hành tây & Hành lá") will be displayed clearly without bleeding into the quantity text.

## Verification Results

### Automated Tests
- Ran `gradlew app:assembleDebug`: **Build Successful**.

### Manual Verification
- **Today's Pick**: Verified that long names no longer overlap with the button.
- **Ingredients**: Verified that long ingredient names are now correctly truncated before reaching the quantity badge.
