package de.dlyt.yanndroid.sudoku.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import de.dlyt.yanndroid.sudoku.MainActivity;
import de.dlyt.yanndroid.sudoku.R;
import de.dlyt.yanndroid.sudoku.utils.Game;
import de.dlyt.yanndroid.sudoku.utils.GameArrayList;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {
    private GameArrayList data;
    private Context context;

    public GamesAdapter(Context context, GameArrayList data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public GamesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(GamesAdapter.ViewHolder holder, int position) {

        final Game game = data.get(position);

        holder.item_text.setText(game.getName());
        //holder.item_icon.setImageDrawable(context.getDrawable(de.dlyt.yanndroid.samsung.R.drawable.ic_samsung_selected));

        holder.item_rename.setOnClickListener(v -> {

            EditText editText = new EditText(context);
            editText.setHint(R.string.name);

            FrameLayout container = new FrameLayout(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 52;
            params.rightMargin = 52;
            editText.setLayoutParams(params);
            container.addView(editText);

            new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogStyle))
                    .setTitle(game.getName())
                    .setView(container)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.rename, (dialog, which) -> {
                        CharSequence cSName = editText.getText();
                        String sName = cSName.length() != 0 ? cSName.toString() : game.getName();
                        game.setName(sName);
                        notifyItemChanged(position);
                        ((MainActivity) context).onNameChange(game, sName);
                    })
                    .show();
        });
        holder.item_delete.setOnClickListener(v -> {
            new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogStyle))
                    .setTitle(game.getName())
                    .setMessage(R.string.delete_sudoku)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, data.size());
                        ((MainActivity) context).onDeleteGame(game);
                    })
                    .show();
        });


        holder.itemView.setOnClickListener(v -> ((MainActivity) context).loadGame(position));

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView item_icon;
        private MaterialTextView item_text;
        private ImageView item_rename;
        private ImageView item_delete;


        public ViewHolder(View view) {
            super(view);
            item_text = view.findViewById(R.id.item_text);
            item_icon = view.findViewById(R.id.item_icon);
            item_rename = view.findViewById(R.id.item_rename);
            item_delete = view.findViewById(R.id.item_delete);


        }
    }
}
