import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom panel that draws the maze. It doesn't run any algorithms itself,
 * it just draws whatever state the maze, generator and solver are in.
 */
public class MazePanel extends JPanel {
    private static final Color UNVISITED = new Color(60, 22, 70);    // dark purple
    private static final Color CARVED = Color.WHITE;
    private static final Color CURRENT = new Color(170, 80, 255);   // bright violet
    private static final Color EXPLORED = new Color(220, 195, 240); // light lavender
    private static final Color PATH = new Color(200, 20, 60);       // crimson
    private static final Color START = new Color(120, 40, 170);     // deep purple
    private static final Color EXIT = new Color(120, 0, 30);        // dark crimson
    private static final Color WALL = new Color(35, 10, 40);        // near-black plum

    private Maze maze;
    private MazeGenerator generator;
    private MazeSolver solver;

    public MazePanel() {
        setPreferredSize(new Dimension(700, 700));
        setBackground(new Color(25, 8, 30));
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        this.generator = null;
        this.solver = null;
        repaint();
    }

    public void setGenerator(MazeGenerator g) { this.generator = g; }
    public void setSolver(MazeSolver s) { this.solver = s; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        Graphics2D g2 = (Graphics2D) g;
        int rows = maze.getRows();
        int cols = maze.getCols();

        // square cells that fit the panel, with a small margin
        int margin = 10;
        int cellSize = Math.max(1, Math.min((getWidth() - 2 * margin) / cols,
                                            (getHeight() - 2 * margin) / rows));
        int offsetX = (getWidth() - cellSize * cols) / 2;
        int offsetY = (getHeight() - cellSize * rows) / 2;

        Set<Cell> pathCells = new HashSet<>();
        if (solver != null && solver.isFound()) {
            pathCells.addAll(solver.getPath());
        }
        boolean solving = solver != null;

        // 1. fill cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);
                Color fill;
                if (pathCells.contains(cell)) {
                    fill = PATH;
                } else if (solving) {
                    fill = cell.isVisited() ? EXPLORED : CARVED;
                } else {
                    fill = cell.isVisited() ? CARVED : UNVISITED;
                }
                g2.setColor(fill);
                g2.fillRect(offsetX + c * cellSize, offsetY + r * cellSize, cellSize, cellSize);
            }
        }

        // start / exit markers (only once the maze is generated)
        if (generator == null || generator.isFinished()) {
            fillCell(g2, maze.getStart(), START, cellSize, offsetX, offsetY);
            fillCell(g2, maze.getExit(), EXIT, cellSize, offsetX, offsetY);
        }

        // highlight whichever cell is being worked on right now
        if (generator != null && !generator.isFinished() && generator.getCurrent() != null) {
            fillCell(g2, generator.getCurrent(), CURRENT, cellSize, offsetX, offsetY);
        }
            if (solver != null && !solver.isFinished() && solver.getCurrent() != null) {
            fillCell(g2, solver.getCurrent(), CURRENT, cellSize, offsetX, offsetY);
        }

        // 2. draw walls on top
        g2.setColor(WALL);
        g2.setStroke(new BasicStroke(Math.max(1, cellSize / 10f)));
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);
                int x = offsetX + c * cellSize;
                int y = offsetY + r * cellSize;
                if (cell.hasTopWall())    g2.drawLine(x, y, x + cellSize, y);
                if (cell.hasRightWall())  g2.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
                if (cell.hasBottomWall()) g2.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
                if (cell.hasLeftWall())   g2.drawLine(x, y, x, y + cellSize);
            }
        }
    }

        private void fillCell(Graphics2D g2, Cell cell, Color color, int size, int ox, int oy) {
        g2.setColor(color);
        g2.fillRect(ox + cell.getCol() * size, oy + cell.getRow() * size, size, size);
    }
}
