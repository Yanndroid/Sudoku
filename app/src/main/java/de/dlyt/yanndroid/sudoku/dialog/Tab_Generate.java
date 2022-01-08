package de.dlyt.yanndroid.sudoku.dialog;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.view.SeekBar;
import de.dlyt.yanndroid.sudoku.R;
import de.dlyt.yanndroid.sudoku.game.Game;

public class Tab_Generate extends Fragment {

    private NewSudokuDialog.DialogListener dialogListener;
    private SeekBar difficulty_seekbar;

    public Tab_Generate(NewSudokuDialog.DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_new_sudoku_generate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        difficulty_seekbar = view.findViewById(R.id.dialog_difficulty_seekbar);
        difficulty_seekbar.setSeamless(true);
        difficulty_seekbar.setTickMark(getContext().getDrawable(R.drawable.seekbar_tick_mark));
        difficulty_seekbar.setMax(4);
        difficulty_seekbar.setProgress(difficulty_seekbar.getMax() / 2);

        view.findViewById(R.id.dialog_generate_4x4).setOnClickListener(v -> asyncNewGame(4, difficulty_seekbar.getProgress(), difficulty_seekbar.getMax()));
        view.findViewById(R.id.dialog_generate_9x9).setOnClickListener(v -> asyncNewGame(9, difficulty_seekbar.getProgress(), difficulty_seekbar.getMax()));
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncNewGame(int size, int difficulty, int max) {
        ProgressDialog mLoadingDialog = new ProgressDialog(getContext());
        mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_CIRCLE_ONLY);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();

        int diff = difficulty == max ? -1 : (int) (Math.pow(size, 2) * (difficulty * 0.35f / max + 0.4f));

        new AsyncTask<Void, Void, Game>() {
            @Override
            protected Game doInBackground(Void... voids) {
                return new Game(size, diff);
            }

            @Override
            protected void onPostExecute(Game game) {
                super.onPostExecute(game);
                getActivity().runOnUiThread(() -> {
                    dialogListener.onResult(game);
                    mLoadingDialog.dismiss();
                });
            }
        }.execute();
    }
}