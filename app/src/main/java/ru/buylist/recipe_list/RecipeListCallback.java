package ru.buylist.recipe_list;

import java.util.List;

import ru.buylist.data.entity.Item;

public interface RecipeListCallback {

    public interface PatternListItemCallback {

        void onItemClick(Item item);

        void onDeleteButtonClick(Item item);

        void onEditButtonClick(Item item);
    }

    void onToMoveButtonClick();

    void onNewIngredientButtonClick();

    void onNewInstructionButtonClick();
}
