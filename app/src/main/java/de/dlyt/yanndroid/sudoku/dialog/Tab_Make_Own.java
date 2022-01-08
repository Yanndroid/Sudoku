package de.dlyt.yanndroid.sudoku.dialog;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.GridLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.sudoku.R;
import de.dlyt.yanndroid.sudoku.adapter.SudokuViewAdapter;
import de.dlyt.yanndroid.sudoku.game.Game;

public class Tab_Make_Own extends Fragment {

    private NewSudokuDialog.DialogListener dialogListener;
    private Button dialog_picker_done;

    public Tab_Make_Own(NewSudokuDialog.DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_new_sudoku_make_own, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog mLoadingDialog = new ProgressDialog(getContext());
        mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_CIRCLE_ONLY);
        mLoadingDialog.setCancelable(false);

        Game game = new Game(9);

        RecyclerView game_recycler = view.findViewById(R.id.game_recycler);
        game_recycler.setLayoutManager(new GridLayoutManager(getContext(), game.getSize()));
        game_recycler.setAdapter(new SudokuViewAdapter(getContext(), game, false));
        game_recycler.setClipToOutline(true);

        dialog_picker_done = view.findViewById(R.id.dialog_picker_done);
        dialog_picker_done.setOnClickListener(v -> {

            mLoadingDialog.show();
            new AsyncTask<Void, Void, Object>() {
                @Override
                protected Object doInBackground(Void... voids) {
                    return game.makeGameFromEdit();
                }

                @Override
                protected void onPostExecute(Object o) {
                    if (o instanceof Integer)
                        Toast.makeText(getContext(), (int) o == 0 ? R.string.no_solution : R.string.multiple_solutions, Toast.LENGTH_SHORT).show();
                    else dialogListener.onResult((Game) o);
                    mLoadingDialog.dismiss();
                }
            }.execute();
        });

    }
}