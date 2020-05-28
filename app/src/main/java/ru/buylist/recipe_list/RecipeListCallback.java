package ru.buylist.recipe_list;

public interface RecipeListCallback {

    public interface PatternListItemCallback {

        void onItemClick(Item item);

        void onDeleteButtonClick(Item item);

        void onEditButtonClick(Item item);
    }

    void onToMoveButtonClick();

    void onNewIngredientButtonClick();

    void onNewInstructionButtonClick();

    void onSaveInstructionButtonClick();
}
