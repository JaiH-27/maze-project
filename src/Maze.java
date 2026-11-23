import java.util.ArrayList;
import java.util.List;

/**
 * The maze model, it's a 2D grid of cells the index of the grid is [row][col].
 * (0,0) is the top left corner and row goes downwards
 * y goes downwards
 */
public class Maze {
    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    public Maze(int rows, int cols) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Maze must be at least 1x1");
        }
        this.rows = rows;
        this.cols = cols;
        grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

   /** Gets the cell at this position, or null if it's outside the grid. */
    public Cell getCell(int r, int c) {
        if (!inBounds(r, c)) return null;
        return grid[r][c];
    }

    public Cell getStart() { return grid[0][0]; }
    public Cell getExit() { return grid[rows - 1][cols - 1]; }

   /** Gets the neighbouring cells inside the grid, walls don't matter here. */
    public List<Cell> getNeighbours(Cell cell) {
        List<Cell> list = new ArrayList<>();
        int r = cell.getRow();
        int c = cell.getCol();
        Cell[] candidates = {
            getCell(r - 1, c), getCell(r, c + 1), getCell(r + 1, c), getCell(r, c - 1)
        };
        for (Cell n : candidates) {
            if (n != null) list.add(n);
        }
        return list;
    }

    /** Neighbours not yet visited, used by the generator. */
    public List<Cell> getUnvisitedNeighbours(Cell cell) {
        List<Cell> list = new ArrayList<>();
        for (Cell n : getNeighbours(cell)) {
            if (!n.isVisited()) list.add(n);
        }
        return list;
    }

    /** Neighbours you can actually walk to (no wall in between), used by the solver. */
    public List<Cell> getOpenNeighbours(Cell cell) {
        List<Cell> list = new ArrayList<>();
        for (Cell n : getNeighbours(cell)) {
            if (canMove(cell, n)) list.add(n);
        }
        return list;
    }

    /** Removes the wall between two cells that are next to each other. */
    public void removeWallBetween(Cell a, Cell b) {
        int dr = b.getRow() - a.getRow();
        int dc = b.getCol() - a.getCol();
        if (dr == -1 && dc == 0) {        // b is above a
            a.setTopWall(false);
            b.setBottomWall(false);
        } else if (dr == 1 && dc == 0) {  // b is below a
            a.setBottomWall(false);
            b.setTopWall(false);
        } else if (dr == 0 && dc == 1) {  // b is right of a
            a.setRightWall(false);
            b.setLeftWall(false);
        } else if (dr == 0 && dc == -1) { // b is left of a
            a.setLeftWall(false);
            b.setRightWall(false);
        } else {
            throw new IllegalArgumentException("Cells are not adjacent: " + a + " " + b);
        }
    }

    /** True if a and b are aside by side and there is no wall between them. */
    public boolean canMove(Cell a, Cell b) {
        int dr = b.getRow() - a.getRow();
        int dc = b.getCol() - a.getCol();
        if (dr == -1 && dc == 0) return !a.hasTopWall();
        if (dr == 1 && dc == 0) return !a.hasBottomWall();
        if (dr == 0 && dc == 1) return !a.hasRightWall();
        if (dr == 0 && dc == -1) return !a.hasLeftWall();
        return false;
    }

    public void resetVisited() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].setVisited(false);
            }
        }
    }
}

