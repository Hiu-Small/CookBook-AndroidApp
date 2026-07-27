# Implementation Plan - UI Overlap Fixes

Fix UI overlapping issues in the "Today's Pick" card on the Home screen and the ingredient items in the Detail screen.

## Proposed Changes

### UI Layer

#### [MODIFY] [fragment_home.xml](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-master/CookBook-AndroidApp-master/app/src/main/res/layout/fragment_home.xml)
- Constrain the `LinearLayout` containing `tvTodayPickTitle` to be to the left of the `btnSpin` button to prevent overlapping.
- Add `ellipsize="end"` and `maxLines="1"` to `tvTodayPickTitle` to handle extremely long names gracefully.

#### [MODIFY] [item_ingredient.xml](file:///C:/Users/Admin/Downloads/CookBook-AndroidApp-master/CookBook-AndroidApp-master/app/src/main/res/layout/item_ingredient.xml)
- Add `android:layout_toStartOf="@id/tvIngredientQuantity"` to `tvIngredientName` to prevent it from overlapping with the quantity badge.
- Add a margin between the name and the quantity.
- Add `ellipsize="end"` and `maxLines="1"` to `tvIngredientName`.

## Verification Plan

### Manual Verification
1.  **Home Screen**: Force a long title for Today's Pick (e.g., in `DatabaseHelper` or by finding a recipe with a long name). Verify the title doesn't go under the "Spin!" button.
2.  **Detail Screen**: Check a recipe with long ingredient names (e.g., "Hành tây & Hành lá", "Nước mắm, đường, dấm, tỏi, ớt"). Verify the names are truncated with "..." before hitting the quantity badge.
