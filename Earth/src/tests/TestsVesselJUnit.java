package tests;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import app.Vessel;

class TestsVesselJUnit extends Vessel {

	@Test
	public void test_transposition_matrix() {
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
		
		double[][] expected = new double [3][5];
		expected = transposition_matrix(5, H);
		
		double[][] actual = new double [3][5];
		
		actual[0][0] = 1;
		actual[0][1] = 4;
		actual[0][2] = 7;
		actual[0][3] = 10;
		actual[0][4] = 13;
		
		actual[1][0] = 2;
		actual[1][1] = 5;
		actual[1][2] = 8;
		actual[1][3] = 11;
		actual[1][4] = 14;
		
		actual[2][0] = 3;
		actual[2][1] = 6;
		actual[2][2] = 9;
		actual[2][3] = 12;
		actual[2][4] = 15;
		
		Assert.assertArrayEquals(expected, actual);
	}
	
	@Test
	public void test_multiplication_HtH() {
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
		
		double[][] expected = new double [3][3];
		expected = multiplication_HtH(5, H, Ht);
		
		double[][] actual = new double [3][3];
		
		actual[0][0] = 335;
		actual[0][1] = 370;
		actual[0][2] = 405;
		
		actual[1][0] = 370;
		actual[1][1] = 410;
		actual[1][2] = 450;
		
		actual[2][0] = 405;
		actual[2][1] = 450;
		actual[2][2] = 495;
		
		Assert.assertArrayEquals(expected, actual);
	}
	
	@Test
	public void test_inv_matrix() {
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
		
		double[][] expected = new double [3][3];
		expected = inv_matrix(H);
		
		double[][] actual = new double [3][3];
		double ch1 = -19;
		double ch2 = 12;
		double ch3 = 5;
		double ch4 = 3;

		actual[0][0] = ch1 / ch2;
		actual[0][1] = 0.5;
		actual[0][2] = 1 / ch2;
		
		actual[1][0] = ch3/ ch4;
		actual[1][1] = -1;
		actual[1][2] = 1 / ch4;
		
		actual[2][0] = -0.25;
		actual[2][1] = 0.5;
		actual[2][2] = -0.25;
		
		Assert.assertArrayEquals(expected, actual);
	}
	
	@Test
	public void test_multiplication_invHtHxHt() {
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
		
		double[][] expected = new double [3][5];
		expected = multiplication_invHtHxHt(5, H, Ht);
		
		double[][] actual = new double [3][5];

		actual[0][0] = 14;
		actual[0][1] = 32;
		actual[0][2] = 50;
		actual[0][3] = 68;
		actual[0][4] = 86;
		
		actual[1][0] = 32;
		actual[1][1] = 77;
		actual[1][2] = 122;
		actual[1][3] = 167;
		actual[1][4] = 212;
		
		actual[2][0] = 50;
		actual[2][1] = 122;
		actual[2][2] = 194;
		actual[2][3] = 266;
		actual[2][4] = 338;
		
		Assert.assertArrayEquals(expected, actual);
	}
	
	@Test
	public void test_multiplication_invHtHxHtxZ() {
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
		
		double[] expected = new double [3];
		expected = multiplication_invHtHxHtxZ(5, H, Z);
		
		double[] actual = new double [3];

		actual[0] = 135;
		actual[1] = 150;
		actual[2] = 165;
		
		Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}

}
