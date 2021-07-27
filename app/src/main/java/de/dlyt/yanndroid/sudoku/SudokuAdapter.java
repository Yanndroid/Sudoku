package de.dlyt.yanndroid.sudoku;

import android.content.Context;
import android.graphics.Rect;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.material.textview.MaterialTextView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SudokuAdapter extends BaseAdapter {

    private Context context;
    private Integer[][] grid;

    public SudokuAdapter(Context context, Integer[][] grid) {
        this.context = context;
        this.grid = grid;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SudokuItem sudokuItem;

        int r = position / 9;
        int c = position % 9;

        if (convertView == null) {
            sudokuItem = new SudokuItem(context, r, c);
        } else {
            sudokuItem = (SudokuItem) convertView;
        }

        boolean preNumber = grid[r][c] != null;

        sudokuItem.setText(grid[r][c], preNumber);
        if (!preNumber) {
            sudokuItem.setOnClickListener(v -> showPopup(sudokuItem));
            sudokuItem.setOnLongClickListener(v -> {
                sudokuItem.setText(null, false);
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                return true;
            });
        }

        return sudokuItem;
    }


    public void showPopup(SudokuItem view) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.Base_Animation_AppCompat_MenuPopup);

        int[] textViewIDs = {R.id.popupButton1, R.id.popupButton2, R.id.popupButton3, R.id.popupButton4, R.id.popupButton5, R.id.popupButton6, R.id.popupButton7, R.id.popupButton8, R.id.popupButton9};
        for (int id : textViewIDs) {
            ((MaterialTextView) popupView.findViewById(id)).setOnClickListener(v -> {
                view.setText(Integer.valueOf(((MaterialTextView) v).getText().toString()), false);
                popupWindow.dismiss();
            });
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

}
