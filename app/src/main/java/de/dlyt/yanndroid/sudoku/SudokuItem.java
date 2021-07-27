package de.dlyt.yanndroid.sudoku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.textview.MaterialTextView;

public class SudokuItem extends LinearLayout {

    private MaterialTextView itemNumber;

    public SudokuItem(Context context, int r, int c) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sudoku_item, this, true);

        itemNumber = findViewById(R.id.itemNumber);


        if (r % 3 == 0) {
            View dividerTop = findViewById(R.id.dividerTop);
            ViewGroup.LayoutParams paramsTop = dividerTop.getLayoutParams();
            paramsTop.height = 2;
            dividerTop.setLayoutParams(paramsTop);
        }

        if (r % 3 == 2) {
            View dividerBottom = findViewById(R.id.dividerBottom);
            ViewGroup.LayoutParams paramsBottom = dividerBottom.getLayoutParams();
            paramsBottom.height = 2;
            dividerBottom.setLayoutParams(paramsBottom);
        }

        if (c % 3 == 2) {
            View dividerRight = findViewById(R.id.dividerRight);
            ViewGroup.LayoutParams paramsRight = dividerRight.getLayoutParams();
            paramsRight.width = 2;
            dividerRight.setLayoutParams(paramsRight);
        }

        if (c % 3 == 0) {
            View dividerLeft = findViewById(R.id.dividerLeft);
            ViewGroup.LayoutParams paramsLeft = dividerLeft.getLayoutParams();
            paramsLeft.width = 2;
            dividerLeft.setLayoutParams(paramsLeft);
        }


    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }


    public void setText(Integer n, boolean isPreNumber) {
        itemNumber.setText(n == null ? null : String.valueOf(n));
        if (isPreNumber) itemNumber.setTextColor(getResources().getColor(R.color.primary_color, getContext().getTheme()));
    }


}
