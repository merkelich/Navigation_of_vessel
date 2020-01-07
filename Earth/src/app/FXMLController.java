package app;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import com.interactivemesh.jfx.importer.tds.TdsModelImporter;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class FXMLController implements Initializable {
	private Vessel vessel;
	private Satellite satellite1;
	private Satellite satellite2;
	private Satellite satellite3;
	private Satellite satellite4;
	
	private boolean first = true;
	private boolean first_satel = true;
	private boolean satel_two = false;
	private boolean satel_three = false;
	private boolean satel_four = false;
	private boolean move = false;
	private boolean observ_ = false;
	private boolean addVes = false;
	private boolean addSat = false;
	
	private boolean calculate = false;
	private boolean modeling = false;
	private boolean modeling_move = false;
	private int it = 0;
	private int it1 = 0;
	private int it2 = 0;
	private int it3 = 0;
	private int it4 = 0;
	
	private int time1 = 0, time2 = 0, time3 = 0, time4 = 0;
	private boolean timeFlag1 = false, timeFlag2 = false, timeFlag3 = false, timeFlag4 = false;
	private double last_value;
	private AnimationTimer timer;
	private double speedAngle;
	
	double[] result = new double[8];
	
	Group earth;
	Group world;
	Group satel;
	Group root;
	private final Sphere sphere = new Sphere(150);
	private Sphere start = new Sphere(5);
	private Sphere end = new Sphere(5);
	private Sphere test = new Sphere(5);
	
	private Group satel1 = new Group();
	private Group satel2 = new Group();
	private Group satel3 = new Group();
	private Group satel4 = new Group();
	
	private Label satLab1 = new Label();
	private Label satLab2 = new Label();
	private Label satLab3 = new Label();
	private Label satLab4 = new Label();
	
	ImageView imageView;
	
	private AmbientLight light = new AmbientLight();
	private final PointLight pointSun = new PointLight();
	
	private double anchorX, anchorY;
	private double anchorAngleX = 0;
	private double anchorAngleY = 0;
	private final DoubleProperty angleX = new SimpleDoubleProperty(0);
	private final DoubleProperty angleY = new SimpleDoubleProperty(0);
	
	@FXML
	private SubScene subScene;
	@FXML
	private Slider oncoming, speed_modeling, speed, amt_sat, h_sat1, h_sat2, h_sat3, h_sat4;
	@FXML
	private CheckBox shadows, sun, background, motion_checkbox, observ;
	@FXML
	private AnchorPane motion, sat2, sat3, sat4, dop_inf;
	@FXML
	private TextField f_of_start, l_of_start, f_of_end, l_of_end, f_sat1, l_sat1, f_sat2, l_sat2, f_sat3, l_sat3, f_sat4, l_sat4;
	@FXML
	private Label label_start, label_st_f, label_st_l, label_end_f, label_end_l, label_f1, label_l1, label_f2, label_l2, label_f3, label_l3, label_f4, label_l4, result_f, result_l;
	@FXML
	private Label freal, lreal, xreal, yreal, zreal, x, y, z, error, error1, iter, iter1, iter2, iter3, iter4, speed1, fst, lst, fe, le, pdop;
	@FXML
	private Button test1, calculate1;
	@FXML
	private Rectangle start_rect;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		vessel = new Vessel();
		prepareStart();
		prepareTest();
		prepareEnd();
		
		satellite1 = new Satellite();
		satellite2 = new Satellite();
		satellite3 = new Satellite();
		satellite4 = new Satellite();
		prepareSat1();
		prepareSat2();
		prepareSat3();
		prepareSat4();
		
		prepareAnimation();
		
	    Camera camera = new PerspectiveCamera(true);
	    camera.setNearClip(1);
	    camera.setFarClip(10000);
	    camera.translateZProperty().set(-1000);
	    
	    light.setColor(Color.WHITE);
	    pointSun.setColor(Color.WHITE);
	    pointSun.getTransforms().add(new Translate(0, 0, -1000));
	    
	    earth = new Group();
	    earth.getChildren().add(prepareEarth());
	    earth.setRotationAxis(Rotate.Y_AXIS);
	    
	    satel = new Group();
	    
	    world = new Group();
	    world.getChildren().addAll(earth, satel);
	    world.translateZProperty().bind(oncoming.valueProperty());
	    
	    root = new Group();
	    root.getChildren().add(prepareImageView());
	    root.getChildren().add(world);
	    
	    subScene.setRoot(root);
	    subScene.setFill(Color.SILVER);
	    subScene.setCamera(camera);

	    initMouseControl(world, subScene);
	}
	
	@FXML
	private void reset() {
		timer.stop();
		earth.rotateProperty().set(0);
		angleX.set(0);
	    angleY.set(0);
	    
	    oncoming.setValue(0);
	    
		if (background.isSelected()) {
			background.fire();
		}
		
		if (shadows.isSelected()) {
			shadows.fire();
		}
		
		if (sun.isSelected()) {
			sun.fire();
		}
		
		if (observ.isSelected()) {
			observ.fire();
		}
		
		reset_vessel();
		reset_sat();
		
		addVes = false;
		addSat = false;
		
		speed_modeling.setDisable(false);
		speed_modeling.setValue(1);
		calculate1.setDisable(true);
		test1.setDisable(false);
		modeling = false;
		modeling_move = false;
		calculate = false;
		timeFlag1 = false;
		timeFlag2 = false;
		timeFlag3 = false;
		timeFlag4 = false;
		time1 = 0;
		time2 = 0;
		time3 = 0;
		time4 = 0;
		it = 0;
		freal.setText("");
		lreal.setText("");
		
		result_f.setText("");
		result_l.setText("");
		
		xreal.setText("");
		yreal.setText("");
		zreal.setText("");
		
		x.setText("");
		y.setText("");
		z.setText("");
		
		error.setText("");
		error1.setText("");
		pdop.setText("");

		iter.setText("");
		iter1.setText("");
		iter2.setText("");
		iter3.setText("");
		iter4.setText("");
		
		
		speed1.setText("");
		fst.setText("");
		lst.setText("");
		fe.setText("");
		le.setText("");
		vessel.writeToFile(true, move);
	}
	
	@FXML
	private void reset_modeling() {
		timer.stop();
		earth.rotateProperty().set(0);
		angleX.set(0);
	    angleY.set(0);
	    
	    oncoming.setValue(0);
	    
		if (background.isSelected()) {
			background.fire();
		}
		
		if (shadows.isSelected()) {
			shadows.fire();
		}
		
		if (sun.isSelected()) {
			sun.fire();
		}
		
		if (observ.isSelected()) {
			observ.fire();
		}
		
		satellite1.calculateStartXYZ();
		satel1.getTransforms().clear();
		satel1.setTranslateX(satellite1.getX());
		satel1.setTranslateY(satellite1.getY());
		satel1.setTranslateZ(satellite1.getZ());
		
		if(satel_two) {
			satellite2.calculateStartXYZ();
			satel2.getTransforms().clear();
			satel2.setTranslateX(satellite2.getX());
			satel2.setTranslateY(satellite2.getY());
			satel2.setTranslateZ(satellite2.getZ());
		}
		
		if(satel_three) {
			satellite3.calculateStartXYZ();
			satel3.getTransforms().clear();
			satel3.setTranslateX(satellite3.getX());
			satel3.setTranslateY(satellite3.getY());
			satel3.setTranslateZ(satellite3.getZ());
		}
		
		if(satel_four) {
			satellite4.calculateStartXYZ();
			satel4.getTransforms().clear();
			satel4.setTranslateX(satellite4.getX());
			satel4.setTranslateY(satellite4.getY());
			satel4.setTranslateZ(satellite4.getZ());
		}
		
		vessel.setStartF(vessel.getStartF());
		vessel.setStartL(vessel.getStartL());
		test.getTransforms().clear();
		
		test.setTranslateX(vessel.getStartX());
		test.setTranslateY(vessel.getStartY());
		test.setTranslateZ(vessel.getStartZ());
		
		speed_modeling.setDisable(false);
		calculate1.setDisable(true);
		test1.setDisable(false);
		speed_modeling.setValue(1);
		modeling = false;
		modeling_move = false;
		calculate = false;
		timeFlag1 = false;
		timeFlag2 = false;
		timeFlag3 = false;
		timeFlag4 = false;
		time1 = 0;
		time2 = 0;
		time3 = 0;
		time4 = 0;
		it = 0;
		it1 = 0;
		it2 = 0;
		it3 = 0;
		it4 = 0;
		freal.setText("");
		lreal.setText("");
		
		result_f.setText("");
		result_l.setText("");
		
		xreal.setText("");
		yreal.setText("");
		zreal.setText("");
		
		x.setText("");
		y.setText("");
		z.setText("");
		
		error.setText("");
		error1.setText("");
		pdop.setText("");

		iter.setText("");
		iter1.setText("");
		iter2.setText("");
		iter3.setText("");
		iter4.setText("");
		vessel.writeToFile(true, move);
	}
	
	@FXML
	private void shadows() {
		if (shadows.isSelected()) {
			world.getChildren().add(light);
		}
		else {
			world.getChildren().remove(light);
		}
	}
	
	@FXML
	private void sun() {
		if (sun.isSelected()) {
			world.getChildren().add(pointSun);
		}
		else {
			world.getChildren().remove(pointSun);
		}
	}
	
	@FXML
	private void background() {
		if (background.isSelected()) {
			root.getChildren().remove(imageView);
			satLab1.setTextFill(Color.BLACK);
			satLab2.setTextFill(Color.BLACK);
			satLab3.setTextFill(Color.BLACK);
			satLab4.setTextFill(Color.BLACK);
		}
		else {
			root.getChildren().add(imageView);
			imageView.toBack();
			satLab1.setTextFill(Color.WHITE);
			satLab2.setTextFill(Color.WHITE);
			satLab3.setTextFill(Color.WHITE);
			satLab4.setTextFill(Color.WHITE);
		}
	}
	
	@FXML
	private void observ_on() {
		if (observ.isSelected()) {
			observ_ = true;
		}
		else {
			observ_ = false;
		}
	}
	
	private Node prepareEarth() {
		PhongMaterial earthMaterial = new PhongMaterial();
		earthMaterial.setDiffuseMap(new Image(getClass().getResourceAsStream("/resources/textures/earth-d.jpg")));
		earthMaterial.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/textures/earth-l.jpg")));
		earthMaterial.setSpecularMap(new Image(getClass().getResourceAsStream("/resources/textures/earth-s.jpg")));
		earthMaterial.setBumpMap(new Image(getClass().getResourceAsStream("/resources/textures/earth-n.jpg")));

		sphere.setRotationAxis(Rotate.Y_AXIS);
		sphere.setMaterial(earthMaterial);
		return sphere;
	}
	
	private Node prepareStart() {
		PhongMaterial startMaterial = new PhongMaterial();
		startMaterial.setDiffuseColor(Color.CHARTREUSE);
		start.setMaterial(startMaterial);
		return start;
	}
	
	private Node prepareTest() {
		PhongMaterial testMaterial = new PhongMaterial();
		testMaterial.setDiffuseColor(Color.GAINSBORO);
		test.setMaterial(testMaterial);
		return test;
	}
	
	private Node prepareEnd() {
		PhongMaterial endMaterial = new PhongMaterial();
		endMaterial.setDiffuseColor(Color.RED);
		end.setMaterial(endMaterial);
		return end;
	}
	
	private Node prepareSat1() {
		satLab1.setText("  Спутник №1");
		satLab1.setTextFill(Color.WHITE);
		satLab1.setFont(new Font("Cambria", 8));
		
	    TdsModelImporter tdsImporter = new TdsModelImporter();
	    tdsImporter.read(getClass().getResource("/resources/hst.3ds"));
	    Node[] tdsMesh = (Node[]) tdsImporter.getImport();
	    Map<String, PhongMaterial> mapTexs = tdsImporter.getNamedMaterials();
	    Iterator<String> it = mapTexs.keySet().iterator();
	    while (it.hasNext()) {
	          String key = it.next();
	          mapTexs.get(key).setDiffuseColor(Color.BLUE);
	    }
	    tdsImporter.close();
	    
	    for (int i = 0; i < tdsMesh.length; i++) {
	    	tdsMesh[i].setScaleX(0.025);
	    	tdsMesh[i].setScaleY(0.025);
	    	tdsMesh[i].setScaleZ(0.025);
	    	tdsMesh[i].getTransforms().add( new Rotate(90, Rotate.X_AXIS));
	    }
	    
	    satel1.getChildren().addAll(tdsMesh);
		
		satLab1.translateXProperty().bind(satel1.translateXProperty());
		satLab1.translateYProperty().bind(satel1.translateYProperty());
		satLab1.translateZProperty().bind(satel1.translateZProperty());
		return satel1;
	}
	
	private Node prepareSat2() {
		satLab2.setText("  Спутник №2");
		satLab2.setTextFill(Color.WHITE);
		satLab2.setFont(new Font("Cambria", 8));
		
	    TdsModelImporter tdsImporter = new TdsModelImporter();
	    tdsImporter.read(getClass().getResource("/resources/hst.3ds"));
	    Node[] tdsMesh = (Node[]) tdsImporter.getImport();
	    Map<String, PhongMaterial> mapTexs = tdsImporter.getNamedMaterials();
	    Iterator<String> it = mapTexs.keySet().iterator();
	    while (it.hasNext()) {
	          String key = it.next();
	          mapTexs.get(key).setDiffuseColor(Color.YELLOWGREEN);
	    }
	    tdsImporter.close();
	    
	    for (int i = 0; i < tdsMesh.length; i++) {
	    	tdsMesh[i].setScaleX(0.025);
	    	tdsMesh[i].setScaleY(0.025);
	    	tdsMesh[i].setScaleZ(0.025);
	    	tdsMesh[i].getTransforms().add( new Rotate(90, Rotate.X_AXIS));
	    }
	    
	    satel2.getChildren().addAll(tdsMesh);
		
		satLab2.translateXProperty().bind(satel2.translateXProperty());
		satLab2.translateYProperty().bind(satel2.translateYProperty());
		satLab2.translateZProperty().bind(satel2.translateZProperty());
		return satel2;
	}
	
	private Node prepareSat3() {
		satLab3.setText("  Спутник №3");
		satLab3.setTextFill(Color.WHITE);
		satLab3.setFont(new Font("Cambria", 8));
		
	    TdsModelImporter tdsImporter = new TdsModelImporter();
	    tdsImporter.read(getClass().getResource("/resources/hst.3ds"));
	    Node[] tdsMesh = (Node[]) tdsImporter.getImport();
	    Map<String, PhongMaterial> mapTexs = tdsImporter.getNamedMaterials();
	    Iterator<String> it = mapTexs.keySet().iterator();
	    while (it.hasNext()) {
	          String key = it.next();
	          mapTexs.get(key).setDiffuseColor(Color.ORANGE);
	    }
	    tdsImporter.close();
	    
	    for (int i = 0; i < tdsMesh.length; i++) {
	    	tdsMesh[i].setScaleX(0.025);
	    	tdsMesh[i].setScaleY(0.025);
	    	tdsMesh[i].setScaleZ(0.025);
	    	tdsMesh[i].getTransforms().add( new Rotate(90, Rotate.X_AXIS));
	    }
	    
	    satel3.getChildren().addAll(tdsMesh);

		satLab3.translateXProperty().bind(satel3.translateXProperty());
		satLab3.translateYProperty().bind(satel3.translateYProperty());
		satLab3.translateZProperty().bind(satel3.translateZProperty());
		return satel3;
	}
	
	private Node prepareSat4() {
		satLab4.setText("  Спутник №4");
		satLab4.setTextFill(Color.WHITE);
		satLab4.setFont(new Font("Cambria", 8));
		
	    TdsModelImporter tdsImporter = new TdsModelImporter();
	    tdsImporter.read(getClass().getResource("/resources/hst.3ds"));
	    Node[] tdsMesh = (Node[]) tdsImporter.getImport();
	    Map<String, PhongMaterial> mapTexs = tdsImporter.getNamedMaterials();
	    Iterator<String> it = mapTexs.keySet().iterator();
	    while (it.hasNext()) {
	          String key = it.next();
	          mapTexs.get(key).setDiffuseColor(Color.GOLD);
	    }
	    tdsImporter.close();
	    
	    for (int i = 0; i < tdsMesh.length; i++) {
	    	tdsMesh[i].setScaleX(0.025);
	    	tdsMesh[i].setScaleY(0.025);
	    	tdsMesh[i].setScaleZ(0.025);
	    	tdsMesh[i].getTransforms().add( new Rotate(90, Rotate.X_AXIS));
	    }
	    
	    satel4.getChildren().addAll(tdsMesh);

		satLab4.translateXProperty().bind(satel4.translateXProperty());
		satLab4.translateYProperty().bind(satel4.translateYProperty());
		satLab4.translateZProperty().bind(satel4.translateZProperty());
		return satel4;
	}
	
	private ImageView prepareImageView() {
		Image image = new Image(getClass().getResourceAsStream("/resources/textures/galaxy.jpg"));
		imageView = new ImageView(image);
		imageView.setPreserveRatio(true);
		imageView.getTransforms().add(new Translate(-image.getWidth() / 2, -image.getHeight() / 2, 1000));
		return imageView;
	}
	
	private void prepareAnimation() {
	    timer = new AnimationTimer() {
	        @Override
	        public void handle(long now) {
	        	double distance1;
	        	double distance2 = 7000;
	        	double distance3 = 7000;
	        	double distance4 = 7000;
	        	double distanceMax1;
	        	double distanceMax2;
	        	double distanceMax3;
	        	double distanceMax4;
	        	boolean check1;
	        	boolean check2 = false;
	        	boolean check3 = false;
	        	boolean check4 = false;
	        	boolean check;
	        	double speed;
	        	
	        	speed = speed_modeling.getValue();
	        	
	        	earth.rotateProperty().set(earth.getRotate() - (0.0000729 * 180 / Math.PI) * speed);
	        	
	        	if (move && addVes) {
	        		if (vessel.calculate_Motion(speedAngle * speed)) {
	        			timer.stop();
	        			test1.setDisable(true);
	        		}
	        		vessel.calculateXYZ();
	        		
	        		test.getTransforms().clear();
	        		test.setTranslateX(vessel.getX());
	        		test.setTranslateY(vessel.getY());
	        		test.setTranslateZ(vessel.getZ());
	        	}
	        	
	        	if (addSat) {
	        		satellite1.setF(satellite1.getF() + satellite1.getW() * speed);
	        		satellite1.calculateXYZ();
	        		satellite1.calculateLonEarth(earth.getRotate());
	        		satellite1.calculateXYZonEarth();
	        		
	        		satel1.getTransforms().clear();
	        		satel1.setTranslateX(satellite1.getX());
	        		satel1.setTranslateY(satellite1.getY());
	        		satel1.setTranslateZ(satellite1.getZ());
	        		
	        		if(satel_two) {
	        			satellite2.setF(satellite2.getF() + satellite2.getW() * speed);
	        			satellite2.calculateXYZ();
		        		satellite2.calculateLonEarth(earth.getRotate());
		        		satellite2.calculateXYZonEarth();
	        			
	        			satel2.getTransforms().clear();
	        			satel2.setTranslateX(satellite2.getX());
	        			satel2.setTranslateY(satellite2.getY());
	        			satel2.setTranslateZ(satellite2.getZ());
	        		}
	        		
	        		if(satel_three) {
	        			satellite3.setF(satellite3.getF() + satellite3.getW() * speed);
	        			satellite3.calculateXYZ();
		        		satellite3.calculateLonEarth(earth.getRotate());
		        		satellite3.calculateXYZonEarth();
	        			
	        			satel3.getTransforms().clear();
	        			satel3.setTranslateX(satellite3.getX());
	        			satel3.setTranslateY(satellite3.getY());
	        			satel3.setTranslateZ(satellite3.getZ());
	        		}
	        		
	        		if(satel_four) {
	        			satellite4.setF(satellite4.getF() + satellite4.getW() * speed);
	        			satellite4.calculateXYZ();
		        		satellite4.calculateLonEarth(earth.getRotate());
		        		satellite4.calculateXYZonEarth();
	        			
	        			satel4.getTransforms().clear();
	        			satel4.setTranslateX(satellite4.getX());
	        			satel4.setTranslateY(satellite4.getY());
	        			satel4.setTranslateZ(satellite4.getZ());
	        		}
	        	}

	        	if (addSat && addVes) {
	        		distance1 = vessel.distance(satellite1);
	        		distanceMax1 = satellite1.getDistanceMax();
	        		check1 = (distance1 <= distanceMax1);
	        		
	        		if(satel_two) {
	        			distance2 = vessel.distance(satellite2);
	        			distanceMax2 = satellite2.getDistanceMax();
	        			check2 = (distance2 <= distanceMax2);
	        		}
	        		
	        		if(satel_three) {
	        			distance3 = vessel.distance(satellite3);
	        			distanceMax3 = satellite3.getDistanceMax();
	        			check3 = (distance3 <= distanceMax3);
	        		}
	        		
	        		if(satel_four) {
	        			distance4 = vessel.distance(satellite4);
	        			distanceMax4 = satellite4.getDistanceMax();
	        			check4 = (distance4 <= distanceMax4);
	        		}
	        		
	        		check = check1 || check2 || check3 || check4;
	        		
	        		if(timeFlag1) {
	            		time1++;
	        		}
	        		if(timeFlag2) {
	            		time2++;
	        		}
	        		if(timeFlag3) {
	            		time3++;
	        		}
	        		if(timeFlag4) {
	            		time4++;
	        		}
	        		
	        		if ((move || observ_) && ((satel_two && satel_three && satel_four) || (satel_two && !satel_three && !satel_four))) {
	        			if (it >= 4) {
	        				modeling_move = true;
	        			}
    				}
	        		else if (it >= 3 && (move || observ_)) {
	        			modeling_move = true;
	        		}
	        		
	        		if (check && !modeling && !modeling_move) {
	        			if (!calculate) {
	        				last_value = speed;
	        				speed_modeling.setValue(1);
	        				speed_modeling.setDisable(true);
	        				calculate = true;
	        			}
	        			
	        			if((check1 && !satel_two && !satel_three && !satel_four) || (check2 && check1 && !satel_three && !satel_four) || (check2 && check1 && check3 && !satel_four) || (check2 && check1 && check3 && check4)) {
	        				if (!timeFlag1) {
	        					timeFlag1 = true;
		        				it++;
		        				it1++;
		        				vessel.matrix(satellite1, it, distance1);
	        				}
	        				else if (time1 == 30) {
		        				it++;
		        				it1++;
		        				time1 = 0;
		        				vessel.matrix(satellite1, it, distance1);
		        			}
	        			}
	        			
	        			if((check2 && check1 && !satel_three && !satel_four) || (check2 && check1 && check3 && !satel_four) || (check2 && check1 && check3 && check4)) {
	        				if (!timeFlag2) {
	        					timeFlag2 = true;
		        				it++;
		        				it2++;
		        				vessel.matrix(satellite2, it, distance2);
	        				}
	        				else if (time2 == 30) {
		        				it++;
		        				it2++;
		        				time2 = 0;
		        				vessel.matrix(satellite2, it, distance2);
		        			}
	        			}
	        			
	        			if((check2 && check1 && check3 && !satel_four) || (check2 && check1 && check3 && check4)) {
	        				if (!timeFlag3) {
	        					timeFlag3 = true;
		        				it++;
		        				it3++;
		        				vessel.matrix(satellite3, it, distance3);
	        				}
	        				else if (time3 == 30) {
		        				it++;
		        				it3++;
		        				time3 = 0;
		        				vessel.matrix(satellite3, it, distance3);
		        			}
	        			}
	        			
	        			if(check2 && check1 && check3 && check4) {
	        				if (!timeFlag4) {
	        					timeFlag4 = true;
		        				it++;
		        				it4++;
		        				vessel.matrix(satellite4, it, distance4);
	        				}
	        				else if (time4 == 30) {
		        				it++;
		        				it4++;
		        				time4 = 0;
		        				vessel.matrix(satellite4, it, distance4);
		        			}
	        			}
	        		}
	        		
	        		else if (!check1 && !check2 && !check3 && !check4 && calculate) {
	        			speed_modeling.setValue(last_value);
	        			speed_modeling.setDisable(false);
	        			modeling = true;
	        			calculate = false;
	        			timeFlag1 = false;
	        			timeFlag2 = false;
	        			timeFlag3 = false;
	        			timeFlag4 = false;
	        			time1 = 0;
	        			time2 = 0;
	        			time3 = 0;
	        			time4 = 0;
	        			if (it >= 3) {
	        				calculate1.setDisable(false);
		        			timer.stop();
	        			}
	        			else {
	        				test1.setDisable(false);
	        				modeling = false;
	        				modeling_move = false;
	        			}
	        		}
	        		else if (modeling_move) {
	        			modeling = true;
	        			test1.setDisable(true);
	        			timer.stop();
	        			calculate1.setDisable(false);
	        		}
	        	}
	        }
	    };
	}
	
	public static boolean isDouble(String x) throws NumberFormatException {
	    try {
	        Double.parseDouble(x);
	        return true;
	    } catch(Exception e) {
	        return false;
	    }
	}
	
	@FXML
	private void add_vessel() {
		boolean checkOnSymbol1;
		boolean checkOnSymbol2 = true;
		boolean checkOnSymbol3;
		boolean checkOnSymbol4 = true;
		boolean checkOnSymbol;
		
		boolean check;
		boolean check1;
		boolean check2 = false;
		boolean check3;
		boolean check4 = false;
		
		reset_modeling();
		label_st_f.setText("");
		label_st_l.setText("");
		label_end_f.setText("");
		label_end_l.setText("");
		
		checkOnSymbol1 = isDouble(f_of_start.getText());
		checkOnSymbol3 = isDouble(l_of_start.getText());
		
		if (move) {
			checkOnSymbol2 = isDouble(f_of_end.getText());
			checkOnSymbol4 = isDouble(l_of_end.getText());
		}
		checkOnSymbol = checkOnSymbol1 && checkOnSymbol2 && checkOnSymbol3 && checkOnSymbol4;
		
		if (!checkOnSymbol || (f_of_start.getText().equals(f_of_end.getText()) && l_of_start.getText().equals(l_of_end.getText()))) {
			if (f_of_start.getText().equals(f_of_end.getText()) && l_of_start.getText().equals(l_of_end.getText())) {
				label_st_f.setText("Совпадают координаты");
				label_st_l.setText("начала и конца движения");
				label_end_f.setText("Совпадают координаты");
				label_end_l.setText("начала и конца движения");
			}
			if(!checkOnSymbol1) {
				label_st_f.setText("Введите широту");
			}
			if (!checkOnSymbol3){
				label_st_l.setText("Введите долготу");
			}
			if(!checkOnSymbol2) {
				label_end_f.setText("Введите широту");
			}
			if(!checkOnSymbol4) {
				label_end_l.setText("Введите долготу");
			}
		}
		else {
			check1 = (Double.parseDouble(f_of_start.getText()) > 90) || (Double.parseDouble(f_of_start.getText()) < -90);
			check3 = (Double.parseDouble(l_of_start.getText()) > 180) || (Double.parseDouble(l_of_start.getText()) < -180);
			
			if (move) {
				check2 = (Double.parseDouble(f_of_end.getText()) > 90) || (Double.parseDouble(f_of_end.getText()) < -90);
				check4 = (Double.parseDouble(l_of_end.getText()) > 180) || (Double.parseDouble(l_of_end.getText()) < -180);
			}
			
			check = check1 || check2 || check3 || check4;
			if (check) {
				if(check1) {
					label_st_f.setText("-90 <= Широта <= 90");
				}
				if(check2) {
					label_end_f.setText("-90 <= Широта <= 90");
				}
				if(check3) {
					label_st_l.setText("-180 <= Долгота <= 180");
				}
				if(check4) {
					label_end_l.setText("-180 <= Долгота <= 180");
				}
			}
			else {
				vessel.setStartF(Double.parseDouble(f_of_start.getText()));
				vessel.setStartL(Double.parseDouble(l_of_start.getText()));
				vessel.calculateStartXYZ();
				vessel.calculateXYZ();
				
				if (move) {
					vessel.setEndF(Double.parseDouble(f_of_end.getText()));
					vessel.setEndL(Double.parseDouble(l_of_end.getText()));
					fst.setText("" + Double.parseDouble(f_of_start.getText()));
					lst.setText("" + Double.parseDouble(l_of_start.getText()));
					fe.setText("" + Double.parseDouble(f_of_end.getText()));
					le.setText("" + Double.parseDouble(l_of_end.getText()));
					vessel.calculateEndXYZ();
				}
				
				if(first) {
					test.setTranslateX(vessel.getStartX());
					test.setTranslateY(vessel.getStartY());
					test.setTranslateZ(vessel.getStartZ());
					
					earth.getChildren().add(test);
					if (move) {
						speedAngle = vessel.angleSpeed(speed.getValue());
						speed1.setText("" + speed.getValue() * 1852 / 3600  + " м/с");
						start.setTranslateX(vessel.getStartX());
						start.setTranslateY(vessel.getStartY());
						start.setTranslateZ(vessel.getStartZ());
						
						end.setTranslateX(vessel.getEndX());
						end.setTranslateY(vessel.getEndY());
						end.setTranslateZ(vessel.getEndZ());
						
						earth.getChildren().addAll(start, end);
					}
					first = false;
				}
				else {
					test.getTransforms().clear();
					test.setTranslateX(vessel.getStartX());
					test.setTranslateY(vessel.getStartY());
					test.setTranslateZ(vessel.getStartZ());
					if (move) {
						speedAngle = vessel.angleSpeed(speed.getValue());
						speed1.setText("" + speed.getValue() * 1852 / 3600 + " м/с");
						start.getTransforms().clear();
						start.setTranslateX(vessel.getStartX());
						start.setTranslateY(vessel.getStartY());
						start.setTranslateZ(vessel.getStartZ());
						
						end.getTransforms().clear();
						end.setTranslateX(vessel.getEndX());
						end.setTranslateY(vessel.getEndY());
						end.setTranslateZ(vessel.getEndZ());
					}
				}
			}
			addVes = true;
		}
	}
	
	@FXML
	private void test() {
	    timer.start();
	}
	
	@FXML
	private void stop() {
		timer.stop();
	}
	
	@FXML
	private void motion_checkbox() {
		if (motion_checkbox.isSelected()) {
			motion.setVisible(true);
			label_start.setText("Координаты начала движения судна");
			move = true;
			start_rect.setVisible(true);
			dop_inf.setVisible(true);
			reset_vessel();
		}
		else {
			motion.setVisible(false);
			label_start.setText("Координаты судна");
			move = false;
			start_rect.setVisible(false);
			dop_inf.setVisible(false);
			reset_vessel();
		}
	}
	
	@FXML
	private void reset_vessel() {
		timer.stop();
		
		reset_modeling();
		first = true;
		label_st_f.setText("");
		label_st_l.setText("");
		label_end_f.setText("");
		label_end_l.setText("");
		f_of_start.setText("");
		l_of_start.setText("");
		f_of_end.setText("");
		l_of_end.setText("");
		
		speed1.setText("");
		fst.setText("");
		lst.setText("");
		fe.setText("");
		le.setText("");
		
		test.getTransforms().clear();
		start.getTransforms().clear();
		end.getTransforms().clear();
		
		earth.getChildren().removeAll(test, start, end);
		
		addVes = false;
	}
	
	@FXML
	private void amount_of_sat() {
		switch ((int)amt_sat.getValue()) {
			case 1: sat2.setVisible(false);
					sat3.setVisible(false);
					sat4.setVisible(false);
					satel_two = false;
					satel_three = false;
					satel_four = false;
					reset_sat();
					break;
			case 2: sat2.setVisible(true);
					sat3.setVisible(false);
					sat4.setVisible(false);
					satel_two = true;
					satel_three = false;
					satel_four = false;
					reset_sat();
					break;
			case 3: sat2.setVisible(true);
					sat3.setVisible(true);
					sat4.setVisible(false);
					satel_two = true;
					satel_three = true;
					satel_four = false;
					reset_sat();
					break;
			case 4: sat2.setVisible(true);
					sat3.setVisible(true);
					sat4.setVisible(true);
					satel_two = true;
					satel_three = true;
					satel_four = true;
					reset_sat();
					break;
		}
	}
	
	@FXML
	private void add_sat() {
		boolean checkOnSymbol1;
		boolean checkOnSymbol2;
		boolean checkOnSymbol3 = true;
		boolean checkOnSymbol4 = true;
		boolean checkOnSymbol5 = true;
		boolean checkOnSymbol6 = true;
		boolean checkOnSymbol7 = true;
		boolean checkOnSymbol8 = true;
		boolean checkOnSymbol;
		
		boolean check;
		boolean check1;
		boolean check2;
		boolean check3;
		boolean check4;
		boolean check5;
		boolean check6;
		
		boolean checkCoord1;
		boolean checkCoord2;
		boolean checkCoord3 = false;
		boolean checkCoord4 = false;
		boolean checkCoord5 = false;
		boolean checkCoord6 = false;
		boolean checkCoord7 = false;
		boolean checkCoord8 = false;
		boolean checkCoord;
		
		reset_modeling();
		label_f1.setText("");
		label_l1.setText("");
		label_f2.setText("");
		label_l2.setText("");
		label_f3.setText("");
		label_l3.setText("");
		label_f4.setText("");
		label_l4.setText("");
		
		checkOnSymbol1 = isDouble(f_sat1.getText());
		checkOnSymbol2 = isDouble(l_sat1.getText());
		
		if (satel_two) {
			checkOnSymbol3 = isDouble(f_sat2.getText());
			checkOnSymbol4 = isDouble(l_sat2.getText());
		}
		if (satel_three) {
			checkOnSymbol5 = isDouble(f_sat3.getText());
			checkOnSymbol6 = isDouble(l_sat3.getText());
		}
		if (satel_four) {
			checkOnSymbol7 = isDouble(f_sat4.getText());
			checkOnSymbol8 = isDouble(l_sat4.getText());
		}
		checkOnSymbol = checkOnSymbol1 && checkOnSymbol2 && checkOnSymbol3 && checkOnSymbol4 && checkOnSymbol5 && checkOnSymbol6 && checkOnSymbol7 && checkOnSymbol8;
		
		check1 = f_sat1.getText().equals(f_sat2.getText()) && l_sat1.getText().equals(l_sat2.getText()) && satel_two;
		check2 = f_sat1.getText().equals(f_sat3.getText()) && l_sat1.getText().equals(l_sat3.getText()) && satel_three;
		check3 = f_sat1.getText().equals(f_sat4.getText()) && l_sat1.getText().equals(l_sat4.getText()) && satel_four;
		check4 = f_sat2.getText().equals(f_sat3.getText()) && l_sat2.getText().equals(l_sat3.getText()) && satel_two && satel_three;
		check5 = f_sat2.getText().equals(f_sat4.getText()) && l_sat2.getText().equals(l_sat4.getText()) && satel_two && satel_four;
		check6 = f_sat3.getText().equals(f_sat4.getText()) && l_sat3.getText().equals(l_sat4.getText()) && satel_three && satel_four;
		check = check1 || check2 || check3 || check4 || check5 || check6;
		
		if (!checkOnSymbol || check) {
			if (check1) {
				label_f1.setText("Совпадают координаты");
				label_l1.setText("у спутников");
				label_f2.setText("Совпадают координаты");
				label_l2.setText("у спутников");
			}
			if (check2) {
				label_f1.setText("Совпадают координаты");
				label_l1.setText("у спутников");
				label_f3.setText("Совпадают координаты");
				label_l3.setText("у спутников");
			}
			
			if (check3) {
				label_f1.setText("Совпадают координаты");
				label_l1.setText("у спутников");
				label_f4.setText("Совпадают координаты");
				label_l4.setText("у спутников");
			}
			
			if (check4) {
				label_f2.setText("Совпадают координаты");
				label_l2.setText("у спутников");
				label_f3.setText("Совпадают координаты");
				label_l3.setText("у спутников");
			}
			
			if (check5) {
				label_f2.setText("Совпадают координаты");
				label_l2.setText("у спутников");
				label_f4.setText("Совпадают координаты");
				label_l4.setText("у спутников");
			}
			
			if (check6) {
				label_f3.setText("Совпадают координаты");
				label_l3.setText("у спутников");
				label_f4.setText("Совпадают координаты");
				label_l4.setText("у спутников");
			}
			
			if(!checkOnSymbol1) {
				label_f1.setText("Введите широту");
			}
			
			if (!checkOnSymbol2){
				label_l1.setText("Введите долготу");
			}
			
			if (!checkOnSymbol3) {
				label_f2.setText("Введите широту");
			}
			
			if (!checkOnSymbol4) {
				label_l2.setText("Введите долготу");
			}
			
			if (!checkOnSymbol5) {
				label_f3.setText("Введите широту");
			}
			
			if (!checkOnSymbol6) {
				label_l3.setText("Введите долготу");
			}
			
			if (!checkOnSymbol7) {
				label_f4.setText("Введите широту");
			}
			
			if (!checkOnSymbol8) {
				label_l4.setText("Введите долготу");
			}
		}
		
		else {
			checkCoord1 = (Double.parseDouble(f_sat1.getText()) > 90) || (Double.parseDouble(f_sat1.getText()) < -90);
			checkCoord2 = (Double.parseDouble(l_sat1.getText()) > 180) || (Double.parseDouble(l_sat1.getText()) < -180);
			
			if (satel_two) {
				checkCoord3 = (Double.parseDouble(f_sat2.getText()) > 90) || (Double.parseDouble(f_sat2.getText()) < -90);
				checkCoord4 = (Double.parseDouble(l_sat2.getText()) > 180) || (Double.parseDouble(l_sat2.getText()) < -180);
			}
			
			if (satel_three) {
				checkCoord5 = (Double.parseDouble(f_sat3.getText()) > 90) || (Double.parseDouble(f_sat3.getText()) < -90);
				checkCoord6 = (Double.parseDouble(l_sat3.getText()) > 180) || (Double.parseDouble(l_sat3.getText()) < -180);
			}
			
			if (satel_four) {
				checkCoord7 = (Double.parseDouble(f_sat4.getText()) > 90) || (Double.parseDouble(f_sat4.getText()) < -90);
				checkCoord8 = (Double.parseDouble(l_sat4.getText()) > 180) || (Double.parseDouble(l_sat4.getText()) < -180);
			}
			
			checkCoord = checkCoord1 || checkCoord2 || checkCoord3 || checkCoord4 || checkCoord5 || checkCoord6 || checkCoord7 || checkCoord8;
			
			if (checkCoord) {
				if(checkCoord1) {
					label_f1.setText("-90 <= Широта <= 90");
				}
				if(checkCoord2) {
					label_l1.setText("-180 <= Долгота <= 180");
				}
				if(checkCoord3) {
					label_f2.setText("-90 <= Широта <= 90");
				}
				if(checkCoord4) {
					label_l2.setText("-180 <= Долгота <= 180");
				}
				if(checkCoord5) {
					label_f3.setText("-90 <= Широта <= 90");
				}
				if(checkCoord6) {
					label_l3.setText("-180 <= Долгота <= 180");
				}
				if(checkCoord7) {
					label_f4.setText("-90 <= Широта <= 90");
				}
				if(checkCoord8) {
					label_l4.setText("-180 <= Долгота <= 180");
				}
			}
			else {
				satellite1.setF(Double.parseDouble(f_sat1.getText()));
				satellite1.setStartF(Double.parseDouble(f_sat1.getText()));
				satellite1.setL(Double.parseDouble(l_sat1.getText()) - earth.getRotate());
				satellite1.setStartL(Double.parseDouble(l_sat1.getText()));
				satellite1.setHeight(h_sat1.getValue());
				satellite1.calculateXYZ();
				satellite1.calculateAngleRadioVision();
				
				if(satel_two) {
					satellite2.setF(Double.parseDouble(f_sat2.getText()));
					satellite2.setStartF(Double.parseDouble(f_sat2.getText()));
					satellite2.setL(Double.parseDouble(l_sat2.getText()) - earth.getRotate());
					satellite2.setStartL(Double.parseDouble(l_sat2.getText()));
					satellite2.setHeight(h_sat2.getValue());
					satellite2.calculateXYZ();
					satellite2.calculateAngleRadioVision();
				}
				
				if(satel_three) {
					satellite3.setF(Double.parseDouble(f_sat3.getText()));
					satellite3.setStartF(Double.parseDouble(f_sat3.getText()));
					satellite3.setL(Double.parseDouble(l_sat3.getText()) - earth.getRotate());
					satellite3.setStartL(Double.parseDouble(l_sat3.getText()));
					satellite3.setHeight(h_sat3.getValue());
					satellite3.calculateXYZ();
					satellite3.calculateAngleRadioVision();
				}
				
				if(satel_four) {
					satellite4.setF(Double.parseDouble(f_sat4.getText()));
					satellite4.setStartF(Double.parseDouble(f_sat4.getText()));
					satellite4.setL(Double.parseDouble(l_sat4.getText()) - earth.getRotate());
					satellite4.setStartL(Double.parseDouble(l_sat4.getText()));
					satellite4.setHeight(h_sat4.getValue());
					satellite4.calculateXYZ();
					satellite4.calculateAngleRadioVision();
				}
				
				if(first_satel) {
					satel1.setTranslateX(satellite1.getX());
					satel1.setTranslateY(satellite1.getY());
					satel1.setTranslateZ(satellite1.getZ());
					satel.getChildren().add(satel1);
					satel.getChildren().add(satLab1);
					
					if(satel_two) {
						satel2.setTranslateX(satellite2.getX());
						satel2.setTranslateY(satellite2.getY());
						satel2.setTranslateZ(satellite2.getZ());
						satel.getChildren().add(satel2);
						satel.getChildren().add(satLab2);
					}
					
					if(satel_three) {
						satel3.setTranslateX(satellite3.getX());
						satel3.setTranslateY(satellite3.getY());
						satel3.setTranslateZ(satellite3.getZ());
						satel.getChildren().add(satel3);
						satel.getChildren().add(satLab3);
					}
					
					if(satel_four) {
						satel4.setTranslateX(satellite4.getX());
						satel4.setTranslateY(satellite4.getY());
						satel4.setTranslateZ(satellite4.getZ());
						satel.getChildren().add(satel4);
						satel.getChildren().add(satLab4);
					}
					first_satel = false;
				}
				else {
					satel1.getTransforms().clear();
					satel1.setTranslateX(satellite1.getX());
					satel1.setTranslateY(satellite1.getY());
					satel1.setTranslateZ(satellite1.getZ());
					
					if(satel_two) {
						satel2.getTransforms().clear();
						satel2.setTranslateX(satellite2.getX());
						satel2.setTranslateY(satellite2.getY());
						satel2.setTranslateZ(satellite2.getZ());
					}
					
					if(satel_three) {
						satel3.getTransforms().clear();
						satel3.setTranslateX(satellite3.getX());
						satel3.setTranslateY(satellite3.getY());
						satel3.setTranslateZ(satellite3.getZ());
					}
					
					if(satel_four) {
						satel4.getTransforms().clear();
						satel4.setTranslateX(satellite4.getX());
						satel4.setTranslateY(satellite4.getY());
						satel4.setTranslateZ(satellite4.getZ());
					}
				}
			}
			addSat = true;
		}
	}
	
	@FXML
	private void reset_sat() {
		timer.stop();
		
		reset_modeling();
		first_satel = true;
		label_f1.setText("");
		label_l1.setText("");
		label_f2.setText("");
		label_l2.setText("");
		label_f3.setText("");
		label_l3.setText("");
		label_f4.setText("");
		label_l4.setText("");
		
		f_sat1.setText("");
		l_sat1.setText("");
		f_sat2.setText("");
		l_sat2.setText("");
		f_sat3.setText("");
		l_sat3.setText("");
		f_sat4.setText("");
		l_sat4.setText("");
		
		h_sat1.setValue(1000);
		h_sat2.setValue(1000);
		h_sat3.setValue(1000);
		h_sat4.setValue(1000);
		
		satel1.getTransforms().clear();
		satel2.getTransforms().clear();
		satel3.getTransforms().clear();
		satel4.getTransforms().clear();
		
		satel.getChildren().removeAll(satel1, satel2, satel3, satel4);
		satel.getChildren().removeAll(satLab1, satLab2, satLab3, satLab4);
		
		addSat = false;
	}
	
	@FXML
	private void calculate() {
		if(modeling) {
			result = vessel.calculate(it);
			freal.setText("" + vessel.getF());
			lreal.setText("" + vessel.getL());
			
			result_f.setText("" + result[0]);
			result_l.setText("" + result[1]);
			
			xreal.setText("" + vessel.getX() * 6400 / 150);
			yreal.setText("" + vessel.getY() * 6400 / 150);
			zreal.setText("" + vessel.getZ() * 6400 / 150);
			
			x.setText("" + result[2]);
			y.setText("" + result[3]);
			z.setText("" + result[4]);
			
			error.setText("" + result[5]);
			error1.setText("" + result[6]);
			
			pdop.setText("" + result[7]);

			iter.setText("" + it);
			iter1.setText("" + it1);
			iter2.setText("" + it2);
			iter3.setText("" + it3);
			iter4.setText("" + it4);
			modeling = false;
			modeling_move = false;
			test1.setDisable(false);
			it = 0;
			it1 = 0;
			it2 = 0;
			it3 = 0;
			it4 = 0;
			calculate1.setDisable(true);

			vessel.writeToFile(false, move);
		}
	}
	
	private void initMouseControl(Group group, SubScene scene) {
		Rotate xRotate;
		Rotate yRotate;
		group.getTransforms().addAll(
				xRotate = new Rotate(0, Rotate.X_AXIS),
				yRotate = new Rotate(0, Rotate.Y_AXIS)
				);
		xRotate.angleProperty().bind(angleX);
		yRotate.angleProperty().bind(angleY);

		scene.setOnMousePressed(event -> {
			anchorX = event.getSceneX();
		    anchorY = event.getSceneY();
		    anchorAngleX = angleX.get();
		    anchorAngleY = angleY.get();
		});

		scene.setOnMouseDragged(event -> {
			angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
		    angleY.set(anchorAngleY + anchorX - event.getSceneX());
		});    
	}
	  
}