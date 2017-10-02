import java.util.Arrays;

public class Matrix {
    private float[][] values;

    public Matrix(String matrix) {
        this.values = toValues(matrix);
    }

    public static Matrix identity(int size) {
        float[][] id = new float[size][size];
        for (int i = 0; i < size; i++) {
            id[i][i] = 1;
        }
        return new Matrix(id);
    }

    public static Matrix permutation(int size, int i, int j) {
        Matrix id = Matrix.identity(size);
        id.swapRows(i, j);
        return id;
    }

    private Matrix(float[][] values) {
        this.values = values;
    }

    private float[][] toValues(String matrix) {
        String[] rows = matrix.split("\\\\");
        float[][] arr = new float[rows.length][rows[0].split(" ").length];
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            String[] columns = row.split(" ");
            for (int j = 0; j < columns.length; j++) {
                arr[i][j] = Float.valueOf(columns[j]);
            }
        }
        return arr;
    }

    public Matrix solveSystem(Matrix b) {
        Triple<Matrix, Matrix, Matrix> decomp = getLU();
        return solveSystem(decomp.a, decomp.b, decomp.c, b);
    }

    public Matrix solveSystem(Matrix lower, Matrix upper, Matrix perm, Matrix b) {
        Matrix y = lower.solveLower(perm.mul(b));
        Matrix x = upper.solveUpper(y);
        return x;
    }

    public Matrix solveLower(Matrix b) {
        float[][] result = new float[getNumRows()][1];
        for (int i = 0; i < getNumRows(); i++) {
            float tmp = b.get(i, 0);
            for(int j = 0; j < i + 1; j++) {
                if(j == i) {
                    tmp /= get(i, j);
                } else {
                    tmp -= get(i, j) * result[j][0];
                }
            }
            result[i][0] = tmp;
        }
        return new Matrix(result);
    }

    public Matrix solveUpper(Matrix b) {
        float[][] result = new float[getNumRows()][1];
        for (int i = getNumRows() - 1; i >= 0; i--) {
            float tmp = b.get(i, 0);
            for(int j = getNumColumns() - 1; j >= i; j--) {
                if(j == i) {
                    tmp /= get(i, j);
                } else {
                    tmp -= get(i, j) * result[j][0];
                }
            }
            result[i][0] = tmp;
        }
        return new Matrix(result);
    }

    public Matrix mul(Matrix other) {
        float[][] result = new float[getNumRows()][other.getNumColumns()];
        for (int i = 0; i < getNumRows(); i++) {
            for (int j = 0; j < other.getNumColumns(); j++) {
                int sum = 0;
                for (int k = 0; k < getNumColumns(); k++) {
                    sum += get(i, k) * other.get(k, j);
                }
                result[i][j] = sum;
            }
        }
        return new Matrix(result);
    }

    public Matrix mask(Matrix mask) {
        float[][] result = new float[getNumRows()][getNumColumns()];
        for (int i = 0; i < getNumRows(); i++) {
            for (int j = 0; j < getNumColumns(); j++) {
                result[i][j] = mask.get(i, j) == 1 ? get(i, j) : 0;
            }
        }
        return new Matrix(result);
    }

    public Matrix add(Matrix other) {
        float[][] result = new float[getNumRows()][getNumColumns()];
        for (int i = 0; i < getNumRows(); i++) {
            for (int j = 0; j < getNumColumns(); j++) {
                result[i][j] = get(i, j) + other.get(i, j);
            }
        }
        return new Matrix(result);
    }

    public Triple<Matrix, Matrix, Matrix> getLU() {
        Matrix copy = copy();
        Matrix perm = copy.decompose(0);

        Matrix lower = copy.mask(getLowerMask()).add(Matrix.identity(getNumRows()));
        Matrix upper = copy.mask(getUpperMask());
        return new Triple<>(lower, upper, perm);
    }

    private Matrix decompose(int index) {
        if (index == getNumRows() - 1) return Matrix.identity(getNumRows());
        int largestRowIndex = getIndexOfAbsoluteMaximum(index, index);
        swapRows(index, largestRowIndex);
        Matrix perm = Matrix.permutation(getNumRows(), index, largestRowIndex);

        float alpha = get(index, index);
        for (int i = index + 1; i < getNumRows(); i++) {
            float div = get(i, index) / alpha;
            set(i, index, div);
            for(int j = index + 1; j < getNumColumns(); j++) {
                float value = get(i, j) - div * get(index, j);
                set(i, j, value);
            }
        }

        Matrix newPerm = decompose(index + 1);
        return newPerm.mul(perm);
    }

    private Matrix getLowerMask() {
        float[][] result = new float[getNumRows()][getNumColumns()];
        for (int i = 0; i < getNumRows(); i++) {
            for (int j = 0; j < i; j++) {
                result[i][j] = 1;
            }
        }
        return new Matrix(result);
    }

    private Matrix getUpperMask() {
        float[][] result = new float[getNumRows()][getNumColumns()];
        for (int i = 0; i < getNumRows(); i++) {
            for (int j = i; j < getNumColumns(); j++) {
                result[i][j] = 1;
            }
        }
        return new Matrix(result);
    }

    private int getIndexOfAbsoluteMaximum(int column, int from) {
        int maxIndex = from;
        for (int i = from; i < getNumRows(); i++) {
            if (Math.abs(get(i, column)) > Math.abs(get(maxIndex, column))) maxIndex = i;
        }
        return maxIndex;
    }

    public void swapRows(int i, int j) {
        float[] tmp = values[i];
        values[i] = values[j];
        values[j] = tmp;
    }

    public Matrix copy() {
        return new Matrix(deepCopy(values));
    }

    public float get(int i, int j) {
        return values[i][j];
    }

    public void set(int i, int j, float v) {
        values[i][j] = v;
    }

    public int getNumRows() {
        return values.length;
    }

    public int getNumColumns() {
        return values[0].length;
    }

    private static float[][] deepCopy(float[][] original) {
        if (original == null) {
            return null;
        }

        final float[][] result = new float[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "values=" + Arrays.deepToString(values) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Matrix matrix = (Matrix) o;

        for (int i = 0; i < getNumRows(); i++) {
            for (int j = 0; j < getNumColumns(); j++) {
                if(Math.abs(get(i, j) - matrix.get(i, j)) > 1e-5) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(values);
    }
}
