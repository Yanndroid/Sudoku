package de.dlyt.yanndroid.sudoku;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.dlyt.yanndroid.samsung.layout.ToolbarLayout;
import de.dlyt.yanndroid.sudoku.adapter.SolutionAdapter;
import de.dlyt.yanndroid.sudoku.utils.Game;

public class SolutionsActivity extends AppCompatActivity {

    private ArrayList<Integer[][]> solutions;
    private Context context;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solutions);

        ToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setNavigationOnClickListener(v -> onBackPressed());

        context = this;
        game = (Game) getIntent().getSerializableExtra("game");
        solutions = game.getSolutions();

        switch (solutions.size()) {
            case 1:
                toolbarLayout.setTitle("Solution");
                toolbarLayout.setSubtitle(null);
                break;
            case 101:
                toolbarLayout.setTitle("Solutions");
                toolbarLayout.setSubtitle("100+");
                Toast.makeText(context, R.string.found_100_plus_soultion, Toast.LENGTH_SHORT).show();
                break;
            default:
                toolbarLayout.setTitle("Solutions");
                toolbarLayout.setSubtitle(String.valueOf(solutions.size()));
        }

        RecyclerView solutions_recycler = findViewById(R.id.solutions_recycler);
        solutions_recycler.setLayoutManager(new LinearLayoutManager(this));
        solutions_recycler.setAdapter(new SolutionAdapter(context, game));
    }
}