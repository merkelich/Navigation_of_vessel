package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Vessel {
	private double f; //широта судна
	private double l; //долгота судна
	
	private double f_start; //широта судна
	private double l_start; //долгота судна
	
	private double f_end; //широта судна
	private double l_end; //долгота судна
	
	private double x; //х координата судна в геоцентрических координатах
	private double y; //у координата судна в геоцентрических координатах
	private double z; //z координата судна в геоцентрических координатах
	
	private double x_start; //х координата судна в геоцентрических координатах
	private double y_start; //у координата судна в геоцентрических координатах
	private double z_start; //z координата судна в геоцентрических координатах
	
	private double x_end; //х координата судна в геоцентрических координатах
	private double y_end; //у координата судна в геоцентрических координатах
	private double z_end; //z координата судна в геоцентрических координатах
	
	private double num_f; //счислимая широта судна
	private double num_l; //счислимая долгота судна
	
	private double num_x; //счислимая х координата судна
	private double num_y; //счислимая у координата судна
	private double num_z; //счислимая z координата судна
	
	private double error = 0.1;
	
	private double speedKm;
	private double angleSp;
	
	private double[][] H_test = new double [200][3]; //матрица H для вычисления поправок
	private double[][] PDOP_test = new double [200][3]; //матрица H для вычисления поправок
	private double[] Z_test = new double [200]; //столбец невязки
	
	private double[][] H; //матрица H для вычисления поправок
	private double[][] PDOP_m; //матрица H для вычисления поправок
	private double[] Z; //столбец невязки
	private double[] coord = new double [8];
	
	public void calculateStartXYZ() {
		z_start = 150 * Math.sin((90 - f_start) * Math.PI / 180) * Math.cos(l_start * Math.PI / 180);
		x_start = -150 * Math.sin((90 - f_start) * Math.PI / 180) * Math.sin(l_start * Math.PI / 180);
		y_start = -150 * Math.cos((90 - f_start) * Math.PI / 180);
	}
	
	public void calculateEndXYZ() {
		z_end = 150 * Math.sin((90 - f_end) * Math.PI / 180) * Math.cos(l_end * Math.PI / 180);
		x_end = -150 * Math.sin((90 - f_end) * Math.PI / 180) * Math.sin(l_end * Math.PI / 180);
		y_end = -150 * Math.cos((90 - f_end) * Math.PI / 180);
	}
	
	public void calculateXYZ() {
		z = 150 * Math.sin((90 - f) * Math.PI / 180) * Math.cos(l * Math.PI / 180);
		x = -150 * Math.sin((90 - f) * Math.PI / 180) * Math.sin(l * Math.PI / 180);
		y = -150 * Math.cos((90 - f) * Math.PI / 180);
		
		num_z = 150 * Math.sin((90 - num_f) * Math.PI / 180) * Math.cos(num_l * Math.PI / 180) * 6400 / 150;
		num_x = -150 * Math.sin((90 - num_f) * Math.PI / 180) * Math.sin(num_l * Math.PI / 180) * 6400 / 150;
		num_y = -150 * Math.cos((90 - num_f) * Math.PI / 180) * 6400 / 150;
	}
	
	protected double[][] transposition_matrix(int it, double[][] H) {
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
	
	protected double[][] multiplication_HtH(int it, double[][] H, double[][] Ht) {
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
	
	protected double[][] inv_matrix(double[][] HtH) {
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
	
	protected double[][] multiplication_invHtHxHt(int it, double[][] inv_HtH, double[][] Ht) {
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
	
	protected double[] multiplication_invHtHxHtxZ(int it, double[][] mult_inv_Ht, double[] Z) {
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
	
	public double[] calculate(int it) {
		
		double[][] Ht = new double [3][it];
		double[][] HtH = new double [3][3];
		double[][] inv_HtH = new double [3][3];
		
		double[][] HtPDOP = new double [3][it];
		double[][] HtHPDOP = new double [3][3];
		double[][] inv_HtHPDOP = new double [3][3];
		
		double[][] mult_inv_Ht = new double [3][it];
		double[] X = new double [3]; //вектор поправок Х
		
		double gamma;
		double value;
		double arccos;
		double radius1;
		double radius2;
		double traceMatrix;
		double PDOP;
		
		H = new double[it][3];
		PDOP_m = new double[it][3];
		Z = new double[it];
		
		for (int i = 0; i < it; i++) {
			H[i][0] = H_test[i][0];
			H[i][1] = H_test[i][1];
			H[i][2] = H_test[i][2];
			Z[i] = Z_test[i];
		}
		
		Ht = transposition_matrix(it, H);

		HtH = multiplication_HtH(it, H, Ht);

		inv_HtH = inv_matrix(HtH);

		mult_inv_Ht = multiplication_invHtHxHt(it, inv_HtH, Ht);
		
		X = multiplication_invHtHxHtxZ(it, mult_inv_Ht, Z);
		
		coord[2] = num_x + X[0];
		coord[3] = num_y + X[1];
		coord[4] = num_z + X[2];
		
		coord[0] = 90 - Math.atan2(Math.sqrt(Math.pow(coord[4], 2) + Math.pow(coord[2], 2)), -(coord[3])) * 180 / Math.PI;
		coord[1] = Math.atan2(-coord[2], coord[4]) * 180 / Math.PI;
		
		radius1 = Math.sqrt(Math.pow(coord[2], 2) + Math.pow(coord[3], 2) + Math.pow(coord[4], 2));
		radius2 = Math.sqrt(Math.pow((x * 6400 / 150), 2) + Math.pow((y * 6400 / 150), 2) + Math.pow((z * 6400 / 150), 2));
		gamma = Math.acos((coord[2]*(x * 6400 / 150) + coord[3] * (y * 6400 / 150) + coord[4] * (z * 6400 / 150)) / (radius1 * radius2)) * 180 / Math.PI;
		coord[6] = (2 * Math.PI * 6400 * gamma) / 360 * 1000;
		
		value = Math.sin(f * Math.PI / 180) * Math.sin(coord[0] * Math.PI / 180) + Math.cos(f * Math.PI / 180) * Math.cos(coord[0] * Math.PI / 180) * Math.cos((coord[1] - l) * Math.PI / 180);
		arccos  =  Math.acos(value) * 180 / Math.PI;
		coord[5] = (2 * Math.PI * 6400000 / 360) * arccos;
		
		error = arccos;
		
		for (int i = 0; i < it; i++) {
			PDOP_m[i][0] = PDOP_test[i][0];
			PDOP_m[i][1] = PDOP_test[i][1];
			PDOP_m[i][2] = PDOP_test[i][2];
		}
		
		
		HtPDOP = transposition_matrix(it, PDOP_m);

		HtHPDOP = multiplication_HtH(it, PDOP_m, HtPDOP);

		inv_HtHPDOP = inv_matrix(HtHPDOP);
		
		traceMatrix = inv_HtHPDOP[0][0] + inv_HtHPDOP[1][1] + inv_HtHPDOP[2][2];
		PDOP = Math.sqrt(Math.abs(traceMatrix));
		
		num_f = coord[0];
		num_l = coord[1];
		
		coord[7] = PDOP;
		
		return coord;
	}
	
	public double angleSpeed(double speed) {
		double period;
		speedKm = speed * 1.852 / 3600;
		period = (Math.PI * 12800) / speedKm;
		angleSp = (2 * Math.PI) / period * 180 / Math.PI;
		return angleSp;
	}
	
	public boolean calculate_Motion(double angleSp) {
		double angleA;
		double angleB;
		double angleC;
		
		double value1;
		double value2;
		double value3;
		
		double alpha;
		double delta_l;
		double delta_f;
		
		if ((f < f_end + 2 * angleSp && f > f_end - 2 * angleSp) && (l < l_end + 2 * angleSp && l > l_end - 2 * angleSp)) {
			return true;
		}
		else {
			if (f == f_end && (0 < l && l < 90) && l_end < 0) {
				l = l - angleSp;
			}
			else if (f == f_end && (0 <= l_end && l_end < 90) && l < 0) {
				l = l + angleSp;
			}
			else if (f == f_end && (90 < l && l <= 180) && l_end < 0) {
				l = l + angleSp;
				if (l > 180) {
					l = l - 360;
				}
			}
			else if (f == f_end && (90 < l_end && l_end <= 180) && l < 0) {
				l = l - angleSp;
				if (l < -180) {
					l = l + 360;
				}
			}
			else if (f == f_end && 0 < l && 0 < l_end && l_end < l) {
				l = l - angleSp;
			}
			else if (f == f_end && 0 < l && 0 < l_end && l_end > l) {
				l = l + angleSp;
			}
			else if (f == f_end && 0 > l && 0 > l_end && l_end > l) {
				l = l + angleSp;
			}
			else if (f == f_end && 0 > l && 0 > l_end && l_end < l) {
				l = l - angleSp;
			}
			else if (f == f_end && (l == 0 && l_end == 180) || (l_end == 0 && l == 180) || (l_end == 0 && l == -180) || (l == 0 && l_end == -180)) {
				l = l + angleSp;
				if (l > 180) {
					l = l - 360;
				}
			}
			else if (l == l_end && f > f_end) {
				f = f - angleSp;
			}
			else if (l == l_end && f < f_end) {
				f = f + angleSp;
			}
			else {
				value1 = Math.sin(f_end * Math.PI / 180) * Math.sin(f_end * Math.PI / 180) + Math.cos(f_end * Math.PI / 180) * Math.cos(f_end * Math.PI / 180) * Math.cos((l - l_end) * Math.PI / 180);
				value2 = Math.sin(f_end * Math.PI / 180) * Math.sin(f * Math.PI / 180) + Math.cos(f_end * Math.PI / 180) * Math.cos(f * Math.PI / 180) * Math.cos((l - l_end) * Math.PI / 180);
				value3 = Math.sin(f_end * Math.PI / 180) * Math.sin(f * Math.PI / 180) + Math.cos(f_end * Math.PI / 180) * Math.cos(f * Math.PI / 180) * Math.cos(0);
				
				angleA  =  Math.acos(value1) * 180 / Math.PI;
				angleB  =  Math.acos(value2) * 180 / Math.PI;
				angleC  =  Math.acos(value3) * 180 / Math.PI;
				
				alpha = Math.acos((Math.cos(angleA * Math.PI / 180) - Math.cos(angleB * Math.PI / 180) * Math.cos(angleC * Math.PI / 180)) / (Math.sin(angleB * Math.PI / 180) * Math.sin(angleC * Math.PI / 180))) * 180 / Math.PI;
			
				delta_l = angleSp * Math.sin(alpha * Math.PI / 180);
				
				if ((0 <= l && l < 90) && l_end < 0) {
					l = l - delta_l;
				}
				else if ((0 <= l_end && l_end < 90) && l < 0) {
					l = l + delta_l;
				}
				else if ((90 < l && l <= 180) && l_end < 0) {
					l = l + delta_l;
					if (l > 180) {
						l = l - 360;
					}
				}
				else if ((90 < l_end && l_end <= 180) && l < 0) {
					l = l - delta_l;
					if (l < -180) {
						l = l + 360;
					}
				}
				else if (0 < l && 0 < l_end && l_end < l) {
					l = l - delta_l;
				}
				else if (0 < l && 0 < l_end && l_end > l) {
					l = l + delta_l;
				}
				else if (0 > l && 0 > l_end && l_end > l) {
					l = l + delta_l;
				}
				else if ((l == 0 && l_end == 180) || (l_end == 0 && l == 180) || (l_end == 0 && l == -180) || (l == 0 && l_end == -180)) {
					l = l + angleSp;
					if (l > 180) {
						l = l - 360;
					}
				}
				else {
					l = l - delta_l;
				}
				
				delta_f = angleSp * Math.cos(alpha * Math.PI / 180);
			
				if (f_end > f) {
					f = f + delta_f; 
				}
				else {
					f = f - delta_f; 
				}
			}
			
			num_f = f + error;
			num_l = l - error;
			error = error + 0.000001;
			return false;
		}
	}
	
	public double distance(Satellite sat) {
		double s; //определение расстояния между спутником и судном
		double delta_x; //разница координат по х между спутником и судном
		double delta_y; //разница координат по y между спутником и судном
		double delta_z; //разница координат по z между спутником и судном
		
		delta_x = sat.getX_on_Earth() - x;
		delta_y = sat.getY_on_Earth() - y;
		delta_z = sat.getZ_on_Earth() - z;

		s = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2) + Math.pow(delta_z, 2)) * 6400 / 150;
		return s;
	}
	
	public void matrix(Satellite sat, int it, double s) {
		double num_s; //определение расстояния между спутником и судном(по счислимым координатам)
		double num_delta_x; //разница координат по х между спутником и судном(счислимые координаты)
		double num_delta_y; //разница координат по y между спутником и судном(счислимые координаты)
		double num_delta_z; //разница координат по z между спутником и судном(счислимые координаты)
		double sx; //производная расстояния по x
		double sy; //производная расстояния по y
		double sz; //производная расстояния по z
		
		double delta_x; //разница координат по х между спутником и судном
		double delta_y; //разница координат по y между спутником и судном
		double delta_z; //разница координат по z между спутником и судном
		double sxPDOP; //производная расстояния по x
		double syPDOP; //производная расстояния по y
		double szPDOP; //производная расстояния по z

		num_delta_x = sat.getX_on_Earth() * 6400 / 150 - num_x;
		num_delta_y = sat.getY_on_Earth() * 6400 / 150 - num_y;
		num_delta_z = sat.getZ_on_Earth() * 6400 / 150 - num_z;
		
		delta_x = (sat.getX_on_Earth() - x) * 6400 / 150;
		delta_y = (sat.getY_on_Earth() - y) * 6400 / 150;
		delta_z = (sat.getZ_on_Earth() - z) * 6400 / 150;

		num_s = Math.sqrt(Math.pow(num_delta_x, 2) + Math.pow(num_delta_y, 2) + Math.pow(num_delta_z, 2));
		
		Z_test[it - 1] = num_s - s;
		
		sx = num_delta_x / num_s;
		sxPDOP = delta_x / s;
		
		H_test[it - 1][0] = sx;
		PDOP_test[it - 1][0] = sxPDOP;
		
		sy = num_delta_y / num_s;
		syPDOP = delta_y / s;
		
		H_test[it - 1][1] = sy;
		PDOP_test[it - 1][1] = syPDOP;
		
		sz = num_delta_z / num_s;
		szPDOP = delta_z / s;
		
		H_test[it - 1][2] = sz;
		PDOP_test[it - 1][2] = szPDOP;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double getStartX() {
		return x_start;
	}
	
	public double getStartY() {
		return y_start;
	}
	
	public double getStartZ() {
		return z_start;
	}
	
	public double getEndX() {
		return x_end;
	}
	
	public double getEndY() {
		return y_end;
	}
	
	public double getEndZ() {
		return z_end;
	}
	
	public double getStartF() {
		return f_start;
	}
	
	public double getStartL() {
		return l_start;
	}
	
	public void setStartF(double f) {
		this.f_start = f;
		this.f = f;
		this.num_f = f + 0.1;
	}
	
	public void setStartL(double l) {
		this.l_start = l;
		this.l = l;
		this.num_l = l - 0.1;
	}
	
	public void setEndF(double f) {
		this.f_end = f;
	}
	
	public void setEndL(double l) {
		this.l_end = l;
	}
	
	public void setF(double f) {
		this.f = f;
	}
	
	public void setL(double l) {
		this.l = l;
	}
	
	public double getF() {
		return f;
	}
	
	public double getL() {
		return l;
	}
	
	public void writeToFile(boolean delete, boolean move) {
		if (!delete) { 
			try(FileWriter writer = new FileWriter("coords.txt", true)) {
        	
				writer.write(coord[0] + "  " + coord[1] + "\r\n");
				writer.flush();
			}
			catch(IOException ex) {
				System.out.println(ex.getMessage());
			} 
			
			if(move) {
				try(FileWriter writer = new FileWriter("move.txt", true)) {
					writer.write(f + "  " + l + "\r\n");
					writer.flush();
				}
				catch(IOException ex) {
					System.out.println(ex.getMessage());
				} 
			}
		}
		else {
			File file  = new File("coords.txt");
			if(file.exists()) {
				file.delete();
			}
			File file2  = new File("move.txt");
			if(file2.exists()) {
				file2.delete();
			}
		}
	}
}
