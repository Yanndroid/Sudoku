package de.dlyt.yanndroid.sudoku.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game implements Serializable {

    private Integer[][] grid;
    private ArrayList<Integer[][]> solutions;
    private boolean[][] preNumbers;

    private int length;
    private int size;

    private String name = "Sudoku";
    private int time = 0;
    private boolean finished = false;

    private Integer tries;
    private Boolean creating;

    public Game(Integer[][] grid) {
        this.grid = grid;
        this.length = grid.length;
        this.size = (int) Math.sqrt(length);

        this.preNumbers = new boolean[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                preNumbers[i][j] = grid[i][j] != null;
            }
        }

        solutions = new ArrayList<>();
        solveField(clone(grid), 0, 0);
    }

    public Game(int length) {
        this.length = length;
        this.size = (int) Math.sqrt(length);

        tries = 0;
        createSudoku(length);
        tries = null;
        this.grid = clone(grid);

        this.preNumbers = new boolean[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                preNumbers[i][j] = grid[i][j] != null;
            }
        }

        solutions = new ArrayList<>();
        solveField(clone(grid), 0, 0);
    }


    public Integer[][] getGrid() {
        return this.grid;
    }

    public ArrayList<Integer[][]> getSolutions() {
        return this.solutions;
    }

    public boolean[][] getPreNumbers() {
        return this.preNumbers;
    }

    public int getLength() {
        return length;
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Integer[][] clone(Integer[][] intArr) {
        return Arrays.stream(intArr).map(Integer[]::clone).toArray(Integer[][]::new);
    }

    /*Solving*/
    private void solveField(Integer[][] tmpGrid, int row, int column) {
        if (creating == null && solutions.size() > 100) return;
        if (column == tmpGrid.length) {
            column = 0;
            row++;
        }

        if (row == tmpGrid.length) {
            solutions.add(clone(tmpGrid));
            return;
        }

        if (tmpGrid[row][column] == null) {
            for (int n = 1; n <= tmpGrid.length; n++) {
                if (isValid(tmpGrid, n, row, column)) {
                    tmpGrid[row][column] = n;
                    solveField(tmpGrid, row, column + 1);
                }
            }
            tmpGrid[row][column] = null;
        } else solveField(tmpGrid, row, column + 1);
    }

    private boolean isValid(Integer[][] tmpGrid, Integer i, int row, int column) {
        for (Integer field : tmpGrid[row]) if (i == field) return false;
        for (Integer[] rows : tmpGrid) if (i == rows[column]) return false;
        int sRow = (row / size) * size;
        int sColumn = (column / size) * size;
        for (int r = sRow; r < sRow + size; r++) {
            for (int c = sColumn; c < sColumn + size; c++) if (i == tmpGrid[r][c]) return false;
        }
        return true;
    }


    /*Creating*/
    private void createSudoku(int length) {
        this.grid = new Integer[length][length];
        this.solutions = new ArrayList<>();


        for (int i = 0; i < (Math.pow(length, 2)) / 3; i++) addRandomNumber();
        creating = true;
        solveField(clone(grid), 0, 0);
        creating = null;
        if (solutions.isEmpty()) {
            createSudoku(length);
            return;
        }
        removedRandomNumber(clone(solutions.get(new Random().nextInt(solutions.size()))));
    }

    private void removedRandomNumber(Integer[][] tmp_grid) {
        this.grid = clone(tmp_grid);
        Random random = new Random();
        int row = random.nextInt(this.grid.length);
        int column = random.nextInt(this.grid.length);
        if (this.grid[row][column] != null) {
            this.grid[row][column] = null;
            this.solutions = new ArrayList<>();
            solveField(grid, 0, 0);
            if (solutions.size() == 1) {
                removedRandomNumber(grid);
            } else {
                tries++;
                this.grid = clone(tmp_grid);
                if (tries < 20) removedRandomNumber(grid);
            }
        } else {
            removedRandomNumber(this.grid);
        }
    }

    private void addRandomNumber() {
        Random random = new Random();
        int row = random.nextInt(length);
        int column = random.nextInt(length);
        int number = random.nextInt(length) + 1;
        if (this.grid[row][column] == null && isValid(this.grid, number, row, column)) {
            this.grid[row][column] = number;
        } else {
            addRandomNumber();
        }
    }


}
