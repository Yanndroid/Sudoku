package de.dlyt.yanndroid.sudoku.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.textview.MaterialTextView;

import de.dlyt.yanndroid.sudoku.R;

public class SudokuItem extends LinearLayout {

    private MaterialTextView itemNumber;
    private View itemContainer;
    private int sqrt_size;
    private SharedPreferences sharedPreferences;

    public SudokuItem(Context context, int r, int c, int size) {
        super(context);

        sharedPreferences = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);

        this.sqrt_size = (int) Math.sqrt(size);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sudoku_item, this, true);

        itemNumber = findViewById(R.id.itemNumber);
        itemContainer = findViewById(R.id.itemContainer);


        if (r % sqrt_size == 0) {
            View dividerTop = findViewById(R.id.dividerTop);
            ViewGroup.LayoutParams paramsTop = dividerTop.getLayoutParams();
            paramsTop.height = 4;
            dividerTop.setLayoutParams(paramsTop);
        }

        if ((r + 1) % sqrt_size == 0) {
            View dividerBottom = findViewById(R.id.dividerBottom);
            ViewGroup.LayoutParams paramsBottom = dividerBottom.getLayoutParams();
            paramsBottom.height = 4;
            dividerBottom.setLayoutParams(paramsBottom);
        }

        if ((c + 1) % sqrt_size == 0) {
            View dividerRight = findViewById(R.id.dividerRight);
            ViewGroup.LayoutParams paramsRight = dividerRight.getLayoutParams();
            paramsRight.width = 4;
            dividerRight.setLayoutParams(paramsRight);
        }

        if (c % sqrt_size == 0) {
            View dividerLeft = findViewById(R.id.dividerLeft);
            ViewGroup.LayoutParams paramsLeft = dividerLeft.getLayoutParams();
            paramsLeft.width = 4;
            dividerLeft.setLayoutParams(paramsLeft);
        }

        if (sharedPreferences.getBoolean("gridColorSwitch", true)) {
            int rm = r % (sqrt_size * 2);
            int cm = c % (sqrt_size * 2);
            if ((rm >= sqrt_size && rm < sqrt_size * 2) != (cm >= sqrt_size && cm < sqrt_size * 2)) {
                itemContainer.setBackgroundColor(getResources().getColor(R.color.sesl_control_color_normal, context.getTheme()));
            }
        }

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }


    public void setNumber(Integer n, boolean isPreNumber) {
        itemNumber.setText(n == null ? null : String.valueOf(n));
        if (isPreNumber) {
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            itemNumber.setTextColor(typedValue.data);
        }
    }

}
