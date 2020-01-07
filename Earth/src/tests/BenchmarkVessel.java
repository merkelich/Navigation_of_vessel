package tests;


import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkVessel {
	
	public double[][] transposition_matrix(int it, double[][] H) {
		double[][] Ht = new double [3][it];
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < it; j++) {
				Ht[i][j] = 0;
			}
		}
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < it; j++) { //транспонированная матрица
				Ht[i][j] = H[j][i];
			}
		}
		
		return Ht;
	}
	
	public double[][] multiplication_HtH(int it, double[][] H, double[][] Ht) {
		double sum0 = 0; //перемножение транспонированной и нетранспонированной
		double sum1 = 0;
		double sum2 = 0;
		double[][] HtH = new double [3][3];
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				HtH[i][j] = 0;
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < it; j++) {
				sum0 += Ht[i][j] * H[j][0];
				sum1 += Ht[i][j] * H[j][1];
				sum2 += Ht[i][j] * H[j][2];
			}
			HtH[i][0] = sum0;
			HtH[i][1] = sum1;
			HtH[i][2] = sum2;
			sum0 = 0;
			sum1 = 0;
			sum2 = 0;
		}
		
		return HtH;
	}
	
	public double[][] inv_matrix(double[][] HtH) {
		double det;
		double det1;
		double det2;
		double det3;
		double det4;
		double det5;
		double det6;
		double[][] HtH_t = new double [3][3];
		
		double[][] inv_HtH = new double [3][3];
		
		HtH_t = transposition_matrix(3, HtH);
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				inv_HtH[i][j] = 0;
			}
		}
		
		det1 = HtH[1][1] * HtH[2][2] - HtH[2][1] * HtH[1][2];
		det2 = HtH[1][0] * HtH[2][2] - HtH[2][0] * HtH[1][2];
		det3 = HtH[1][0] * HtH[2][1] - HtH[2][0] * HtH[1][1];

		det4 = HtH[0][0] * det1;
		det5 = HtH[0][1] * det2;
		det6 = HtH[0][2] * det3;

		det = det4 - det5 + det6;

		inv_HtH[0][0] = (HtH_t[1][1] * HtH_t[2][2] - HtH_t[1][2] * HtH_t[2][1]) / (det); //нахождение обратной матрицы
		inv_HtH[0][1] = -(HtH_t[1][0] * HtH_t[2][2] - HtH_t[1][2] * HtH_t[2][0]) / (det);
		inv_HtH[0][2] = (HtH_t[1][0] * HtH_t[2][1] - HtH_t[1][1] * HtH_t[2][0]) / (det);

		inv_HtH[1][0] = -(HtH_t[0][1] * HtH_t[2][2] - HtH_t[0][2] * HtH_t[2][1]) / (det);
		inv_HtH[1][1] = (HtH_t[0][0] * HtH_t[2][2] - HtH_t[0][2] * HtH_t[2][0]) / (det);
		inv_HtH[1][2] = -(HtH_t[0][0] * HtH_t[2][1] - HtH_t[0][1] * HtH_t[2][0]) / (det);

		inv_HtH[2][0] = (HtH_t[0][1] * HtH_t[1][2] - HtH_t[0][2] * HtH_t[1][1]) / (det);
		inv_HtH[2][1] = -(HtH_t[0][0] * HtH_t[1][2] - HtH_t[0][2] * HtH_t[1][0]) / (det);
		inv_HtH[2][2] = (HtH_t[0][0] * HtH_t[1][1] - HtH_t[0][1] * HtH_t[1][0]) / (det);
		
		return inv_HtH;
	}
	
	public double[][] multiplication_invHtHxHt(int it, double[][] inv_HtH, double[][] Ht) {
		double[][] mult_inv_Ht = new double [3][it];
		
		for (int i = 0; i < 3; i++) { //обнуление массива
			for (int j = 0; j < it; j++) {
				mult_inv_Ht[i][j] = 0;
			}
		}

		for (int i = 0; i < 3; i++) { //умножение обратной на транспонированную
			for (int j = 0; j < it; j++) {
				mult_inv_Ht[i][j] = inv_HtH[i][0] * Ht[0][j] + inv_HtH[i][1] * Ht[1][j] + inv_HtH[i][2] * Ht[2][j];
			}
		}
		
		return mult_inv_Ht;
	}
	
	public double[] multiplication_invHtHxHtxZ(int it, double[][] mult_inv_Ht, double[] Z) {
		double[] X = new double [3]; //вектор поправок Х
		
		X[0] = 0;
		X[1] = 0;
		X[2] = 0;

		for (int i = 0; i < it; i++) { //умножение на невязку, получение вектора Х поправок к счислимым координатам
			X[0] += (mult_inv_Ht[0][i] * Z[i]);
			X[1] += (mult_inv_Ht[1][i] * Z[i]);
			X[2] += (mult_inv_Ht[2][i] * Z[i]);
		}
		
		return X;
	}
	
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void _1_emptinessTest() {
		
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double _2_cycleTest() {
		double a = 0;
		
		for(int i = 0; i < 100; i++) {
			a++;
		}
		
		return a;
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double _3_double_cycleTest() {
		double a = 0;
		
		for(int i = 0; i < 100; i++) {
			for(int j = 0; j < 100; j++) {
				a++;
			}
		}
		
		return a;
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double _5_logarithmTest() {
		double a = 100;
		double b;
		b = Math.log(a);
		return b;
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double _4_multiplicationTest() {
		double a = 100;
		double b = 100;
		double c;
		c = a * b;
		return c;
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double[][] transposition_matrixTest() {
		double[][] H = new double [5][3];
		
		H[0][0] = 1;
		H[0][1] = 2;
		H[0][2] = 3;
		
		H[1][0] = 4;
		H[1][1] = 5;
		H[1][2] = 6;
		
		H[2][0] = 7;
		H[2][1] = 8;
		H[2][2] = 9;
		
		H[3][0] = 10;
		H[3][1] = 11;
		H[3][2] = 12;
		
		H[4][0] = 13;
		H[4][1] = 14;
		H[4][2] = 15;
		
		return transposition_matrix(5, H);
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double[][] multiplication_HtHTest() {
		double[][] H = new double [5][3];
		
		H[0][0] = 1;
		H[0][1] = 2;
		H[0][2] = 3;
		
		H[1][0] = 4;
		H[1][1] = 5;
		H[1][2] = 6;
		
		H[2][0] = 7;
		H[2][1] = 8;
		H[2][2] = 9;
		
		H[3][0] = 10;
		H[3][1] = 11;
		H[3][2] = 12;
		
		H[4][0] = 13;
		H[4][1] = 14;
		H[4][2] = 15;
		
		double[][] Ht = new double [3][5];
		
		Ht[0][0] = 1;
		Ht[0][1] = 4;
		Ht[0][2] = 7;
		Ht[0][3] = 10;
		Ht[0][4] = 13;
		
		Ht[1][0] = 2;
		Ht[1][1] = 5;
		Ht[1][2] = 8;
		Ht[1][3] = 11;
		Ht[1][4] = 14;
		
		Ht[2][0] = 3;
		Ht[2][1] = 6;
		Ht[2][2] = 9;
		Ht[2][3] = 12;
		Ht[2][4] = 15;
		
		return multiplication_HtH(5, H, Ht);
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double[][] inv_matrixTest() {
		double[][] H = new double [3][3];
		
		H[0][0] = 1;
		H[0][1] = 2;
		H[0][2] = 3;
		
		H[1][0] = 4;
		H[1][1] = 5;
		H[1][2] = 8;
		
		H[2][0] = 7;
		H[2][1] = 8;
		H[2][2] = 9;
		
		return inv_matrix(H);
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double[][] multiplication_invHtHxHtTest() {
		double[][] H = new double [3][3];
		
		H[0][0] = 1;
		H[0][1] = 2;
		H[0][2] = 3;
		
		H[1][0] = 4;
		H[1][1] = 5;
		H[1][2] = 6;
		
		H[2][0] = 7;
		H[2][1] = 8;
		H[2][2] = 9;
		
		double[][] Ht = new double [3][5];
		
		Ht[0][0] = 1;
		Ht[0][1] = 4;
		Ht[0][2] = 7;
		Ht[0][3] = 10;
		Ht[0][4] = 13;
		
		Ht[1][0] = 2;
		Ht[1][1] = 5;
		Ht[1][2] = 8;
		Ht[1][3] = 11;
		Ht[1][4] = 14;
		
		Ht[2][0] = 3;
		Ht[2][1] = 6;
		Ht[2][2] = 9;
		Ht[2][3] = 12;
		Ht[2][4] = 15;
		
		return multiplication_invHtHxHt(5, H, Ht);
    }
	
	@Benchmark @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double[] multiplication_invHtHxHtxZTest() {
		double[][] H = new double [3][5];
		
		H[0][0] = 1;
		H[0][1] = 4;
		H[0][2] = 7;
		H[0][3] = 10;
		H[0][4] = 13;
		
		H[1][0] = 2;
		H[1][1] = 5;
		H[1][2] = 8;
		H[1][3] = 11;
		H[1][4] = 14;
		
		H[2][0] = 3;
		H[2][1] = 6;
		H[2][2] = 9;
		H[2][3] = 12;
		H[2][4] = 15;
		
		double[] Z = new double [5];
		Z[0] = 1;
		Z[1] = 2;
		Z[2] = 3;
		Z[3] = 4;
		Z[4] = 5;
		
		return multiplication_invHtHxHtxZ(5, H, Z);
    }
	
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(BenchmarkVessel.class.getSimpleName())
				.forks(1)
				.build();
		
		new Runner(opt).run();
	}
}
