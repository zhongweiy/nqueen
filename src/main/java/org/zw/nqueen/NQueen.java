package org.zw.nqueen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class NQueen {
    private static final double EPSILON = 0.0000001;
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
        List<Future<List<List<Integer>>>> futures = new ArrayList<>();
        for (int i = 0; i < N_THREAD; ++i) {
            int finalI = i;
            futures.add(executor.submit(() -> taskSplitNQueen(n, finalI)));
        }
        executor.shutdown();

        List<List<Integer>> solutions = new ArrayList<>();
        for (Future<List<List<Integer>>> f : futures) {
            try {
                solutions.addAll(f.get());
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
    private static List<List<Integer>> taskSplitNQueen(int n, int taskId) {
        List<List<Integer>> solution = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();

        backTracking(cur, n, solution, taskId);

        return solution;
    }

    private static void backTracking(List<Integer> cur,
                                     int n,
                                     List<List<Integer>> solution,
                                     int taskId) {
        if (cur.size() == n) {
            // Deep copy of cur list.
            solution.add(cur.stream().map(Integer::new).collect(toList()));
            return;
        }

        for (int i = 0; i < n; ++i) {
            // Only split the task to different thread at first back tracking
            // level.
            if ((cur.size() == 0 && (i % N_THREAD == taskId))
                    || cur.size() > 0) {
                cur.add(i);
                if (isValid(cur)) {
                    backTracking(cur, n, solution, taskId);
                }
                cur.remove(cur.size() - 1);
            }
        }
    }

    private static boolean isValid(List<Integer> cur) {
        return isVerticalValid(cur) &&
                isDiagonalValid(cur) &&
                isAnyAngleValid(cur);
    }

    private static boolean isVerticalValid(List<Integer> cur) {
        int last = cur.get(cur.size() - 1);
        for (int i = 0; i < cur.size() - 1; ++i) {
            if (last == cur.get(i)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isDiagonalValid(List<Integer> cur) {
        int lastRow = cur.size() - 1;
        int lastCol = cur.get(lastRow);
        for (int row = 0; row < cur.size() - 1; ++row) {
            int col = cur.get(row);

            if (Math.abs(row - lastRow) == Math.abs(col - lastCol)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether there exists 3 queens are in a straight line at ANY angle.
     * If exists, it is not valid and return false, otherwise return true.
     */
    private static boolean isAnyAngleValid(List<Integer> cur) {
        int row0 = cur.size() - 1;
        int col0 = cur.get(row0);

        for (int row1 = row0 - 1; row1 > 0; --row1) {
            double col1 = cur.get(row1);
            double angle1 = (col1 - col0) / (row1 - row0);

            for (int row2 = row1 - 1; row2 >= 0; --row2) {
                double col2 = cur.get(row2);
                double angle2 = (col2 - col0) / (row2 - row0);

                if (Math.abs(angle1 - angle2) < EPSILON) {
                    return false;
                }
            }
        }

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
                    sb.append("|\n");
                }
            }
        }
        System.out.print(sb.toString());
    }

}
