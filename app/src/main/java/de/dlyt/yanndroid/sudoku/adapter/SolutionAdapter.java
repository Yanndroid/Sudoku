package de.dlyt.yanndroid.sudoku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.dlyt.yanndroid.sudoku.R;
import de.dlyt.yanndroid.sudoku.utils.Game;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> {
    private ArrayList<Integer[][]> data;
    private Game game;
    private Context context;

    public SolutionAdapter(Context context, Game game) {
        this.data = game.getSolutions();
        this.game = game;
        this.context = context;
    }

    @Override
    public SolutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.solution_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(SolutionAdapter.ViewHolder holder, int position) {

        holder.sudokuView.setClipToOutline(true);
        holder.sudokuView.setNumColumns(game.getLength());
        holder.sudokuView.setAdapter(new SudokuAdapter(context, data.get(position), game.getPreNumbers(), true));

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private GridView sudokuView;

        public ViewHolder(View view) {
            super(view);
            sudokuView = view.findViewById(R.id.sudokuView);
        }
    }
}
