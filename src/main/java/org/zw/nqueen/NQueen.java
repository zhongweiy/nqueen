package org.zw.nqueen;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class NQueen {
    private static final double NANO_PER_SECOND = 1_000_000_000.0;
    private static final int SOLUTION_PRINT_LIMIT = 10;
    private static final int EXAMPLE_SOLUTION = 1;
    private static final int N_THREAD = Runtime.getRuntime()
            .availableProcessors();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("USAGE: nqueen n");
            System.exit(1);
        }

        Integer n = null;

        try {
            n = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Argument" + args[0] + " must be an integer.");
            System.exit(1);
        }

        long start = System.nanoTime();
        List<List<Integer>> solution = nQueen(n);
        long elapsedTime = System.nanoTime() - start;
        double elapsedSeconds = (double)elapsedTime / NANO_PER_SECOND;

        List<List<Integer>> firstNSolution = solution.stream()
                .limit(SOLUTION_PRINT_LIMIT)
                .collect(Collectors.toList());

        System.out.println(String.format("N=%s. There are %s solutions.\n" +
                        "The first %s Solutions are: %s",
                n, solution.size(), firstNSolution.size(), firstNSolution));

        if (solution.size() > EXAMPLE_SOLUTION) {
            System.out.println(String.format("Solution(#%s): %s",
                    EXAMPLE_SOLUTION, solution.get(EXAMPLE_SOLUTION)));
            printQueens(solution.get(EXAMPLE_SOLUTION));
        }

        System.out.println(String.format("Runtime (seconds): %s by %s threads",
                elapsedSeconds, N_THREAD));
    }

    static List<List<Integer>> nQueen(int n) {
        // Split computation into multi-thread to run.
        ExecutorService executor = Executors.newFixedThreadPool(N_THREAD);
        List<Future<List<int[]>>> futures = new ArrayList<>();
        for (int i = 0; i < N_THREAD; ++i) {
            int finalI = i;
            futures.add(executor.submit(() -> taskSplitNQueen(n, finalI)));
        }
        executor.shutdown();

        List<List<Integer>> solutions = new ArrayList<>();
        for (Future<List<int[]>> f : futures) {
            try {
                List<int[]> solution = f.get();
                // List<Integer> is easy to print. So convert int[] to
                // List<Integer>.
                for (int[] arr : solution) {
                    List<Integer> list = Arrays.stream(arr).boxed()
                            .collect(Collectors.toList());
                    solutions.add(list);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                System.err.println("Exception happens in task execution.");
            }
        }
        return solutions;
    }

    /**
     * Task is split into multi-thread on the first level of backtracking.
     */
    private static List<int[]> taskSplitNQueen(int n, int taskId) {
        List<int[]> solution = new ArrayList<>();
        State cur = new State(n);

        backTracking(cur, n, solution, taskId);

        return solution;
    }

    /**
     * State class represents the backtracking status. It was a simple
     * List<Integer> at beginning. But to optimize vertical validation,
     * {@link State#occupied} boolean array is introduced. To optimize any
     * angle validation, {@link State#angles} is introduced.
     */
    private static class State {
        // Store the board. positions[3] = 5 means there is a queen on row(3)
        // and col(5).
        int[] positions;
        // Store information for vertical validation. occupied[3] means there is
        // a queen on col(3) on any row.
        boolean[] occupied;
        // Current backtracking row.
        int row;
        // The size of board.
        int n;
        // Store all angles for each queen between other previous rows' queens.
        List<Set<Double>> angles;

        State(int n) {
            this.n = n;
            positions = new int[n];
            occupied = new boolean[n];
            angles = new ArrayList<>();
            row = -1;

            for (int i = 0; i < n; ++i) {
                positions[i] = -1;
                occupied[i] = false;
            }
        }
    }

    private static void backTracking(State cur,
                                     int n,
                                     List<int[]> solution,
                                     int taskId) {
        if (cur.row == n - 1) {
            // Deep copy of cur list.
            int[] curCopy = new int[n];
            System.arraycopy(cur.positions, 0, curCopy, 0, n);
            solution.add(curCopy);
            return;
        }

        for (int i = 0; i < n; ++i) {
            // Only split the task to different thread at first back tracking
            // level.
            if ((cur.row == -1 && (i % N_THREAD == taskId))
                    || cur.row >= 0) {
                cur.row++;
                cur.positions[cur.row] = i;
                if (isVerticalValid(cur.occupied, i)) {
                    cur.occupied[i] = true;
                    if (isDiagonalValid(cur)) {
                        if (isAnyAngleValid(cur)) {
                            backTracking(cur, n, solution, taskId);
                            // Backtrack to previous angles.
                            cur.angles.remove(cur.angles.size() - 1);
                        }
                    }
                    // Backtrack to previous occupied.
                    cur.occupied[i] = false;
                }
                // Backtrack to previous positions.
                cur.positions[cur.row] = -1;
                cur.row--;
            }
        }
    }

    private static boolean isVerticalValid(boolean[] occupied, int col) {
        return !occupied[col];
    }

    private static boolean isDiagonalValid(State cur) {
        int curRow = cur.row;
        int curCol = cur.positions[curRow];
        for (int row = 0; row < curRow; ++row) {
            int col = cur.positions[row];

            if (Math.abs(row - curRow) == Math.abs(col - curCol)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether there exists 3 queens are in a straight line at ANY angle.
     * If exists, it is not valid and return false, otherwise return true.
     */
    private static boolean isAnyAngleValid(State cur) {
        int curRow = cur.row;
        int curCol = cur.positions[curRow];
        Set<Double> newAngles = new HashSet<>();

        for (int rowPrev = curRow - 1; rowPrev >= 0; --rowPrev) {
            int colPrev = cur.positions[rowPrev];
            double angle = ((double)colPrev - curCol) / (rowPrev - curRow);
            if (cur.angles.get(rowPrev).contains(angle)) {
                return false;
            }
            newAngles.add(angle);
        }

        cur.angles.add(newAngles);

        return true;
    }

    private static void printQueens(List<Integer> queens) {
        StringBuilder sb = new StringBuilder();
        int n = queens.size();

        for (Integer col : queens) {
            for (int j = 0; j < n; ++j) {
                if (j == 0) {
                    sb.append("|");
                }
                if (j == col) {
                    sb.append("*,");
                } else {
                    sb.append(" ,");
                }
                if (j == n - 1) {
                    sb.setLength(sb.length() - 1);
                    sb.append("|\n");
                }
            }
        }
        System.out.print(sb.toString());
    }

}
