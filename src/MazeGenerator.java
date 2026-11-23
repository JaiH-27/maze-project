import java.util.List;
import java.util.Random;

/**
 * Makes the maze using randomised depth-first search (recursive backtracker),
 * using the Stack from the labs instead of recursion.
 *
 * It's written as step() so the GUI can call it once per timer tick and animate it.
 * Each step either moves into one new cell or backtracks one cell.
 */
public class MazeGenerator {
    private final Maze maze;
    private final Random random;
    private final StackReferenceBased stack = new StackReferenceBased();
    private Cell current;
    private boolean finished = false;

    public MazeGenerator(Maze maze, Random random) {
        this.maze = maze;
        this.random = random;
        maze.resetVisited();

        Cell start = maze.getStart();
        start.setVisited(true);
        stack.push(start);
        current = start;
    }

    /** Does one step of the generator. Returns false once the maze is finished. */
    public boolean step() {
        if (stack.isEmpty()) {
            finished = true;
            current = null;
            return false;
        }

        current = (Cell) stack.peek();
        List<Cell> options = maze.getUnvisitedNeighbours(current);

        if (options.isEmpty()) {
            // dead end, so backtrack
            stack.pop();
        } else {
            Cell next = options.get(random.nextInt(options.size()));
            maze.removeWallBetween(current, next);
            next.setVisited(true);
            stack.push(next);
            current = next;
        }
        return true;
    }

    /** Runs the whole thing with no animation (used for testing). */
    public void generateAll() {
        while (step()) { }
    }

    public Cell getCurrent() { return current; }
    public boolean isFinished() { return finished; }
}
