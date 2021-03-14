package org.zw.nqueen;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class NQueen {
    private static final double EPSILON = 0.0000001;
    private static final double NANO_PER_SECOND = 1_000_000_000.0;
    private static final int SOLUTION_PRINT_LIMIT = 10;

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
        double seconds = (double)elapsedTime / NANO_PER_SECOND;

        List<List<Integer>> firstNSolution = solution.stream()
                .limit(SOLUTION_PRINT_LIMIT)
                .collect(Collectors.toList());

        System.out.println(String.format("N=%s, " +
                        "There are %s solutions, Solutions are: %s\n" +
                        "Time (seconds):%s",
                n, solution.size(), firstNSolution, seconds));

        if (solution.size() > 0) {
            int i = 1;
            System.out.println(String.format("Solution(#%s):%s",
                    i, solution.get(i)));
            printQueens(solution.get(i));
        }
    }

    static List<List<Integer>> nQueen(Integer n) {
        List<List<Integer>> solution = new ArrayList<>();
        Stack<List<Integer>> stack = new Stack<>();
        stack.push(new ArrayList<>());

        while (!stack.empty()) {
            List<Integer> cur = stack.pop();
            if (cur.size() == n) {
                // Deep copy of cur list.
                solution.add(cur.stream().map(Integer::new).collect(toList()));
            } else {
                for (int i = 0; i < n; ++i) {
                    cur.add(i);
                    if (isValid(cur)) {
                        List<Integer> curClone = cur
                                .stream().map(Integer::new).collect(toList());
                        stack.push(curClone);
                    }
                    cur.remove(cur.size() - 1);
                }
            }
        }

        return solution;
    }

    private static void backTracking(List<Integer> cur,
                                     Integer n,
                                     List<List<Integer>> solution) {
        if (cur.size() == n) {
            // Deep copy of cur list.
            solution.add(cur.stream().map(Integer::new).collect(toList()));
            return;
        }

        for (int i = 0; i < n; ++i) {
            cur.add(i);
            if (isValid(cur)) {
                backTracking(cur, n, solution);
            }
            cur.remove(cur.size() - 1);
        }
    }

    private static boolean isValid(List<Integer> cur) {
        return isVerticalValid(cur) &&
                isDiagonalValid(cur) &&
                isAnyAngleValid(cur);
    }

    private static boolean isVerticalValid(List<Integer> cur) {
        // TODO check whether use hash map to get better performance.
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
