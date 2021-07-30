package de.dlyt.yanndroid.sudoku.utils;

import java.util.ArrayList;

public class GameArrayList {
    private ArrayList<Game> games = new ArrayList<>();

    public Game get(int index) {
        return games.get(index);
    }

    public void set(int index, Game game) {
        this.games.set(index, game);
    }

    public void add(Game game) {
        this.games.add(game);
    }

    public int size() {
        return this.games.size();
    }

    public boolean isEmpty() {
        return games.isEmpty();
    }

    public void remove(int index) {
        games.remove(index);
    }

}
