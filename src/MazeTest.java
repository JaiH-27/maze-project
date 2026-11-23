import java.util.List;
import java.util.Random;

/**
 * Runs the generator and solver (no GUI) on different maze sizes and checks:
 *  - every cell got visited by the generator
 *  - the maze is "perfect": exactly (rows*cols - 1) passages, so no loops
 *  - the solver finds a path from start to exit
 *  - every step on the path is a legal move (no walking through walls)
 */
public class MazeTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        int[][] sizes = {
            {1, 1}, {1, 10}, {10, 1}, {2, 2}, {10, 10}, {30, 30}, {50, 80}, {150, 150}
        };
        for (int[] s : sizes) {
            testSize(s[0], s[1]);
        }
        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
        if (failed > 0) System.exit(1);
    }

    private static void testSize(int rows, int cols) {
        String name = rows + "x" + cols;
        try {
            Maze maze = new Maze(rows, cols);
            MazeGenerator gen = new MazeGenerator(maze, new Random(42));
            gen.generateAll();

            check(name + " all cells visited", allVisited(maze));
            check(name + " perfect maze (passages = cells - 1)",
                  countPassages(maze) == rows * cols - 1);

            MazeSolver solver = new MazeSolver(maze);
            solver.solveAll();
            check(name + " path found", solver.isFound());

            List<Cell> path = solver.getPath();
            check(name + " path starts at start", path.get(0) == maze.getStart());
            check(name + " path ends at exit", path.get(path.size() - 1) == maze.getExit());
            check(name + " path respects walls", pathIsValid(maze, path));
            // shortest possible path is a straight-ish line
            check(name + " path length >= manhattan distance",
                  path.size() >= rows + cols - 1);
        } catch (Exception e) {
            check(name + " crashed: " + e, false);
        }
    }

    private static boolean allVisited(Maze maze) {
        for (int r = 0; r < maze.getRows(); r++)
            for (int c = 0; c < maze.getCols(); c++)
                if (!maze.getCell(r, c).isVisited()) return false;
        return true;
    }

    /** Counts open right and bottom walls so each passage is counted once. */
    private static int countPassages(Maze maze) {
        int count = 0;
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                if (c < maze.getCols() - 1 && !cell.hasRightWall()) count++;
                if (r < maze.getRows() - 1 && !cell.hasBottomWall()) count++;
            }
        }
        return count;
    }

    private static boolean pathIsValid(Maze maze, List<Cell> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            if (!maze.canMove(path.get(i), path.get(i + 1))) return false;
        }
        return true;
    }

    private static void check(String label, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + label);
        } else {
            failed++;
            System.out.println("FAIL  " + label);
        }
    }
}
