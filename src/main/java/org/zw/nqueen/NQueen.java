package org.zw.nqueen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

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
        State(int n) {
            this.n = n;
            positions = new int[n];
            occupied = new boolean[n];
            row = -1;

            for (int i = 0; i < n; ++i) {
                positions[i] = -1;
                occupied[i] = false;
            }
        }

        State copy() {
            State c = new State(positions.length);
            c.row = row;
            c.n = n;
            System.arraycopy(positions, 0, c.positions, 0, n);
            System.arraycopy(occupied, 0, c.occupied, 0, n);
            return c;
        }
    }

    static List<List<Integer>> nQueen(Integer n) {
        List<int[]> solution = new ArrayList<>();
        Stack<State> stack = new Stack<>();
        stack.push(new State(n));

        while (!stack.empty()) {
            State cur = stack.pop();
            if (cur.row == n - 1) {
                // Deep copy of cur list.
                int[] positionsCopy = new int[n];
                System.arraycopy(cur.positions, 0, positionsCopy, 0, n);
                solution.add(positionsCopy);
            } else {
                for (int i = 0; i < n; ++i) {
                    cur.row++;
                    cur.positions[cur.row] = i;
                    if (isVerticalValid(cur.occupied, i)) {
                        cur.occupied[i] = true;
                        if (isDiagonalValid(cur) && isAnyAngleValid(cur)) {
                            stack.push(cur.copy());
                        }
                        // Backtracking.
                        cur.occupied[i] = false;
                    }
                    // Backtracking.
                    cur.positions[cur.row] = -1;
                    cur.row--;

                }
            }
        }

        return solution.stream().map(NQueen::toList)
                .collect(Collectors.toList());
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
        int row0 = cur.row;
        int col0 = cur.positions[row0];

        for (int row1 = row0 - 1; row1 > 0; --row1) {
            double col1 = cur.positions[row1];
            double angle1 = (col1 - col0) / (row1 - row0);

            for (int row2 = row1 - 1; row2 >= 0; --row2) {
                double col2 = cur.positions[row2];

                if ((col0 < col1 && col1 < col2)
                        || (col0 > col1 && col1 > col2)) {
                    double angle2 = (col2 - col0) / (row2 - row0);

                    if (Math.abs(angle1 - angle2) < EPSILON) {
                        return false;
                    }
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

    static private List<Integer> toList(int[] array) {
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }
}
