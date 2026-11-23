import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Random;

/**
 * Main window. Has controls for the maze size and animation speed, and uses a
 * javax.swing.Timer to call step() on the generator/solver repeatedly.
 * Using a Swing Timer (not Thread.sleep) keeps the GUI responsive because
 * everything runs on the event dispatch thread.
 */
public class MazeApp extends JFrame {
    private final MazePanel panel = new MazePanel();
    private final JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 150, 1));
    private final JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 150, 1));
    private final JSlider speedSlider = new JSlider(0, 100, 80);
    private final JButton generateBtn = new JButton("Generate");
    private final JButton solveBtn = new JButton("Solve");
    private final JLabel status = new JLabel("Pick a size and press Generate");

    private Maze maze;
    private MazeGenerator generator;
    private MazeSolver solver;
    private Timer timer;

    public MazeApp() {
        super("Maze Generator & Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controls = new JPanel();
        controls.add(new JLabel("Rows:"));
        controls.add(rowsSpinner);
        controls.add(new JLabel("Cols:"));
        controls.add(colsSpinner);
        controls.add(new JLabel("Speed:"));
        controls.add(speedSlider);
        controls.add(generateBtn);
        controls.add(solveBtn);

        solveBtn.setEnabled(false);
        generateBtn.addActionListener(e -> startGenerating());
        solveBtn.addActionListener(e -> startSolving());
        speedSlider.addChangeListener(e -> {
            if (timer != null) timer.setDelay(getDelay());
        });

        add(controls, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /** Slider at 100 = 0ms delay, slider at 0 = 200ms delay. */
    private int getDelay() {
        return (100 - speedSlider.getValue()) * 2;
    }

    /** At full speed do several steps per tick so big mazes don't take forever. */
    private int stepsPerTick() {
        return speedSlider.getValue() == 100 ? 50 : 1;
    }

    private void stopTimer() {
        if (timer != null) timer.stop();
    }

    private void startGenerating() {
        stopTimer();
        int rows = (Integer) rowsSpinner.getValue();
        int cols = (Integer) colsSpinner.getValue();

        maze = new Maze(rows, cols);
        generator = new MazeGenerator(maze, new Random());
        solver = null;
        panel.setMaze(maze);
        panel.setGenerator(generator);
        solveBtn.setEnabled(false);
        status.setText("Generating " + rows + " x " + cols + "...");

        timer = new Timer(getDelay(), e -> {
            boolean running = true;
            for (int i = 0; i < stepsPerTick() && running; i++) {
                running = generator.step();
            }
            panel.repaint();
            if (!running) {
                timer.stop();
                status.setText("Maze generated. Press Solve.");
                solveBtn.setEnabled(true);
            }
        });
        timer.start();
    }

    private void startSolving() {
        if (maze == null || generator == null || !generator.isFinished()) return;
        stopTimer();

        solver = new MazeSolver(maze);
        panel.setSolver(solver);
        solveBtn.setEnabled(false);
        status.setText("Solving with BFS...");

        timer = new Timer(getDelay(), e -> {
            boolean running = true;
            for (int i = 0; i < stepsPerTick() && running; i++) {
                running = solver.step();
            }
            panel.repaint();
            if (!running) {
                timer.stop();
                if (solver.isFound()) {
                    status.setText("Solved! Shortest path length: " + solver.getPath().size() + " cells");
                } else {
                    status.setText("No path found");
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeApp().setVisible(true));
    }
}
