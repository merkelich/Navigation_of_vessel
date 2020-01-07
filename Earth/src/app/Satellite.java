package app;

public class Satellite {
	private double f; //широта спутника
	private double l; //долгота спутника
	private double height;
	private double l_on_Earth;
	
	private double st_f; //широта, с которой спутник начинает двигаться по орбите
	private double st_l; //долгота, с которой спутник начинает двигаться по орбите
	private double x; //х координата спутника в геоцентрических координатах
	private double y; //у координата спутника в геоцентрических координатах
	private double z; //z координата спутника в геоцентрических координатах
	
	private double x_on_Earth; //х координата спутника в геоцентрических координатах
	private double y_on_Earth; //у координата спутника в геоцентрических координатах
	private double z_on_Earth; //z координата спутника в геоцентрических координатах
	
	private double angleRadioVision; //угол радиовидимости
	private double distanceMax;
	private double speed; //первая космическая скорость
	private double w; //угловая скорость спутника
	
	public void calculateStartXYZ() {
		f = st_f;
		l = st_l;
		z = (150 + (150 * height ) / 6400) * Math.sin((90 - st_f) * Math.PI / 180) * Math.cos(st_l * Math.PI / 180);
		x = -(150 + (150 * height) / 6400)  * Math.sin((90 - st_f) * Math.PI / 180) * Math.sin(st_l * Math.PI / 180);
		y = -(150 + (150 * height) / 6400) * Math.cos((90 - st_f) * Math.PI / 180);
	}
	
	public void calculateXYZ() {
		z = (150 + (150 * height ) / 6400) * Math.sin((90 - f) * Math.PI / 180) * Math.cos(l * Math.PI / 180);
		x = -(150 + (150 * height) / 6400)  * Math.sin((90 - f) * Math.PI / 180) * Math.sin(l * Math.PI / 180);
		y = -(150 + (150 * height) / 6400) * Math.cos((90 - f) * Math.PI / 180);
	}
	
	public void calculateLonEarth(double angle) {
		l_on_Earth = l + angle;
		
		/**
		if (f > 360) {
			System.out.println("F: " + (f - 360));
		}
		else {
			System.out.println("F: " + f);
		}
		System.out.println("L: " + l_on_Earth);
		**/
	}
	
	public void calculateXYZonEarth() {
		z_on_Earth = (150 + (150 * height ) / 6400) * Math.sin((90 - f) * Math.PI / 180) * Math.cos(l_on_Earth * Math.PI / 180);
		x_on_Earth = -(150 + (150 * height) / 6400)  * Math.sin((90 - f) * Math.PI / 180) * Math.sin(l_on_Earth * Math.PI / 180);
		y_on_Earth = -(150 + (150 * height) / 6400) * Math.cos((90 - f) * Math.PI / 180);
	}
	
	public void calculateAngleRadioVision() {
		double orbit = 6400 + height;
		double delta = 5;
		double sina;
		
		sina = (6400 * Math.cos(delta * Math.PI / 180)) / orbit;
		angleRadioVision = 90 - delta - Math.asin(sina) * 180 / Math.PI;
		distanceMax = 6400 * Math.sin(angleRadioVision * Math.PI / 180) / sina;
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
	
	public double getX_on_Earth() {
		return x_on_Earth;
	}
	
	public double getY_on_Earth() {
		return y_on_Earth;
	}
	
	public double getZ_on_Earth() {
		return z_on_Earth;
	}
	
	public double getF() {
		return f;
	}
	
	public double getL() {
		return l;
	}
	
	public double getL_on_Earth() {
		return l_on_Earth;
	}
	
	public double getSt_F() {
		return st_f;
	}
	
	public double getSt_L() {
		return st_l;
	}
	
	public double getW() {
		double period;
		speed = Math.sqrt(6.6741 * 5.9722 * 10000000 / (6.4 + height / 1000)) / 1000;
		period = (Math.PI * 14800) / speed;
		w = (2 * Math.PI) / period * 180 / Math.PI;
		return w;
	}
	
	public double getDistanceMax() {
		return distanceMax;
	}
	
	public void setF(double f) {
		this.f = f;
	}
	
	public void setStartF(double f) {
		this.st_f = f;
	}
	
	public void setL(double l) {
		this.l = l;
	}
	
	public void setStartL(double l) {
		this.st_l = l;
	}
	
	public void setHeight(double h) {
		this.height = h;
	}
}
