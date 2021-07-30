package de.dlyt.yanndroid.sudoku.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.material.textview.MaterialTextView;

import de.dlyt.yanndroid.sudoku.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SudokuAdapter extends BaseAdapter {

    private Context context;
    private Integer[][] grid;
    private boolean[][] preNumbers;
    private boolean locked;

    public SudokuAdapter(Context context, Game game) {
        this(context, game.getGrid(), game.getPreNumbers(), false);
    }

    public SudokuAdapter(Context context, Integer[][] grid, boolean[][] preNumbers) {
        this(context, grid, preNumbers, false);
    }

    public SudokuAdapter(Context context, Integer[][] grid, boolean[][] preNumbers, boolean locked) {
        this.context = context;
        this.grid = grid;
        this.preNumbers = preNumbers;
        this.locked = locked;
    }

    public SudokuAdapter getNew() {
        return new SudokuAdapter(context, grid, preNumbers, locked);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SudokuItem sudokuItem;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        int r = position / grid.length;
        int c = position % grid.length;

        if (convertView == null) {
            sudokuItem = new SudokuItem(context, r, c, grid.length);
        } else {
            sudokuItem = (SudokuItem) convertView;
        }

        boolean preNumber = preNumbers[r][c];

        sudokuItem.setNumber(grid[r][c], preNumber);
        if (!preNumber && !locked) {
            sudokuItem.setOnClickListener(v -> showPopup(sudokuItem, r, c));
            sudokuItem.setOnLongClickListener(v -> {
                sudokuItem.setNumber(null, false);
                grid[r][c] = null;
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                return true;
            });
        }

        return sudokuItem;
    }


    private void showPopup(SudokuItem view, int row, int column) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.Base_Animation_AppCompat_MenuPopup);

        GridLayout popupGrid = popupView.findViewById(R.id.popupGrid);
        popupGrid.setColumnCount((int) Math.sqrt(grid.length));
        popupGrid.setRowCount((int) Math.sqrt(grid.length));
        for (int i = 1; i <= grid.length; i++) {
            MaterialTextView materialTextView = new MaterialTextView(context);

            materialTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            materialTextView.setPadding(28, 4, 28, 4);

            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            materialTextView.setBackgroundResource(outValue.resourceId);

            materialTextView.setTextSize(24);
            materialTextView.setTypeface(Typeface.DEFAULT_BOLD);

            materialTextView.setText(String.valueOf(i));
            materialTextView.setOnClickListener(v -> {
                Integer value = Integer.valueOf(((MaterialTextView) v).getText().toString());
                view.setNumber(value, false);
                grid[row][column] = value;
                popupWindow.dismiss();
            });

            popupGrid.addView(materialTextView);
        }

        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        popupWindow.showAtLocation(view,
                Gravity.TOP | Gravity.START,
                rect.left - popupView.getMeasuredWidth() / 2 + rect.width() / 2,
                rect.bottom - popupView.getMeasuredHeight() / 2 - rect.height() / 2);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager.LayoutParams wmlp = (WindowManager.LayoutParams) container.getLayoutParams();
        wmlp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wmlp.dimAmount = 0.3f;
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).updateViewLayout(container, wmlp);
    }


    @Override
    public int getCount() {
        return grid.length * grid.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}