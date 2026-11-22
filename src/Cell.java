/**
 * A single cell in the maze grid
 * Every cell starts with all four walls up. The generator knocks walls down
 * to create passages. The visited flag is reused by both the generator and the solver
 * (the maze resets it between the two)
 */
public class Cell {
    private final int row;
    private final int col;

    // true = wall is stilll standing
    private boolean topWall = true;
    private boolean rightWall = true;
    private boolean bottomWall = true;
    private boolean leftWall = true;

    private boolean visited = false;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public boolean hasTopWall() { return topWall; }
    public boolean hasRightWall() { return rightWall; }
    public boolean hasBottomWall() { return bottomWall; }
    public boolean hasLeftWall() { return leftWall; }

    public void setTopWall(boolean b) { topWall = b; }
    public void setRightWall(boolean b) { rightWall = b; }
    public void setBottomWall(boolean b) { bottomWall = b; }
    public void setLeftWall(boolean b) { leftWall = b; }

    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }


    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}