package ru.buylist.utils;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;

public class FocusableEditText extends android.support.v7.widget.AppCompatEditText {
    public FocusableEditText(Context context) {
        super(context);
    }

    public FocusableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused) {
            InputMethodManager imm = (InputMethodManager)getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
    }
}
