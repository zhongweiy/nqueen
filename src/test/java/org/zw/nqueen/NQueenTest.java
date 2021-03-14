package org.zw.nqueen;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class NQueenTest {

    @Test
    public void nQueen1() {
        assertEquals(1, NQueen.nQueen(1).size());
    }

    @Test
    public void nQueen2() {
        assertEquals(0, NQueen.nQueen(2).size());
    }

    @Test
    public void nQueen4() {
        List<List<Integer>> solution = NQueen.nQueen(4);

        assertEquals(2, solution.size());

        List<Integer> expectSolution0 = Arrays.asList(1, 3, 0, 2);
        List<Integer> expectSolution1 = Arrays.asList(2, 0, 3, 1);

        assertEquals(expectSolution0, solution.get(0));
        assertEquals(expectSolution1, solution.get(1));
    }

    @Test
    public void nQueen8() {
        List<List<Integer>> solution = NQueen.nQueen(8);

        assertEquals(8, solution.size());

        List<Integer> expectSolution1 = Arrays.asList(2, 5, 7, 1, 3, 0, 6, 4);

        assertEquals(expectSolution1, solution.get(1));
    }
}