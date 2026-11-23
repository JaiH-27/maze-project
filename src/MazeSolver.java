import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Solves the maze using BFS and the lab Queue, from top-left to bottom-right.
 * BFS always finds the shortest path. parent[][] remembers where each cell
 * was reached from, so the path can be traced back from the exit.
 */

public class MazeSolver {
    private final Maze maze;
    private final QueueArrayBased queue = new QueueArrayBased();
    private final Cell[][] parent;
    private List<Cell> path = new ArrayList<>();
    private boolean finished = false;
    private boolean found = false;
    private Cell current;

    public MazeSolver(Maze maze) {
        this.maze = maze;
        parent = new Cell[maze.getRows()][maze.getCols()];
        maze.resetVisited();

        Cell start = maze.getStart();
        start.setVisited(true);
        queue.enqueue(start);
    }

    /** Processes one cell from the queue. Returns false when finished. */
    public boolean step() {
        if (finished) return false;

        if (queue.isEmpty()) {
            // should not happen in a perfect maze, but handle it anyway
            finished = true;
            current = null;
            return false;
        }

        current = (Cell) queue.dequeue();

        if (current == maze.getExit()) {
            found = true;
            finished = true;
            buildPath();
            return false;
        }

        for (Cell next : maze.getOpenNeighbours(current)) {
            if (!next.isVisited()) {
                next.setVisited(true);
                parent[next.getRow()][next.getCol()] = current;
                queue.enqueue(next);
            }
        }
        return true;
    }

    /** Walks back from the exit using parent[][] to build the path. */
    private void buildPath() {
        path = new ArrayList<>();
        Cell c = maze.getExit();
        while (c != null) {
            path.add(c);
            c = parent[c.getRow()][c.getCol()];
        }
        Collections.reverse(path); // start -> exit
    }

    /** Runs the whole search with no animation (used for testing). */
    public void solveAll() {
        while (step()) { }
    }

    public List<Cell> getPath() { return path; }
    public boolean isFinished() { return finished; }
    public boolean isFound() { return found; }
    public Cell getCurrent() { return current; }
}
