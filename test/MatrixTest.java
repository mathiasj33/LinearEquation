import org.junit.Test;

import static org.junit.Assert.*;

public class MatrixTest {
    @Test
    public void testToValues() {
        String matrixString = "1 23\\2 9\\1 2";
        Matrix matrix = new Matrix(matrixString);
        assertEquals(1, matrix.get(0, 0), 1e-5);
        assertEquals(23, matrix.get(0, 1), 1e-5);
        assertEquals(2, matrix.get(1, 0), 1e-5);
        assertEquals(9, matrix.get(1, 1), 1e-5);
        assertEquals(1, matrix.get(2, 0), 1e-5);
        assertEquals(2, matrix.get(2, 1), 1e-5);
    }

    @Test
    public void testSwapRows() {
        Matrix matrix = new Matrix("1 23\\2 9\\1 2");
        matrix.swapRows(0, 1);
        assertEquals(new Matrix("2 9\\1 23\\1 2"), matrix);
        matrix.swapRows(0, 2);
        assertEquals(new Matrix("1 2\\1 23\\2 9"), matrix);
    }

    @Test
    public void testIdentity() {
        Matrix id = Matrix.identity(4);
        assertEquals(new Matrix("1 0 0 0\\0 1 0 0\\0 0 1 0\\0 0 0 1"), id);
    }

    @Test
    public void testPermutation() {
        Matrix perm = Matrix.permutation(3, 1, 2);
        assertEquals(new Matrix("1 0 0\\0 0 1\\0 1 0"), perm);
    }

    @Test
    public void testMul() {
        Matrix a = new Matrix("1 2\\3 4\\5 6");
        Matrix b = new Matrix("9 7 6\\10 1 0");
        assertEquals(new Matrix("29 9 6\\67 25 18\\105 41 30"), a.mul(b));
        assertEquals(new Matrix("26"), new Matrix("5 7").mul(new Matrix("1\\3")));
    }

    @Test
    public void testLU() {
        Matrix a = new Matrix("1 -1 1\\2 2 -1\\4 4 -8");
        Triple<Matrix, Matrix, Matrix> result = a.getLU();
        assertEquals(new Matrix("1 0 0\\0.25 1 0\\0.5 0 1"), result.a);
        assertEquals(new Matrix("4 4 -8\\0 -2 3\\0 0 3"), result.b);
        assertEquals(new Matrix("0 0 1\\1 0 0\\0 1 0"), result.c);
    }

    @Test
    public void testSolve() {
        Matrix a = new Matrix("1 0 1\\0 -3 1\\2 1 3");
        Matrix result = a.solveSystem(new Matrix("6\\7\\15"));
        assertEquals(new Matrix("2\\-1\\4"), result);

        a = new Matrix("2 5 -9 3\\5 6 -4 2\\3 -4 2 7\\11 7 4 -8");
        result = a.solveSystem(new Matrix("151\\103\\16\\-32"));
        assertEquals(new Matrix("3\\5\\-11\\7"), result);
    }
}
