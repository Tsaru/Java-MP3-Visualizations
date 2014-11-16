package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class AnimatedCircle extends Visualization {
	private Pane canvas;
	//Thread t;
	
	
	public AnimatedCircle(int freqMax, Pane pane) {
		canvas = new Pane();
		canvas.setStyle("-fx-background-color: black;");
	    canvas.setPrefSize(200,200);
	    pane.getChildren().add(canvas);
		final Circle circleTimeline = new Circle(100, 50, 30);
		circleTimeline.setFill(Color.TEAL);
		final Circle circle = new Circle (30, 30, 30);
		circle.setFill(Color.TEAL);
		canvas.getChildren().addAll(circle);
		final Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);
		final KeyValue kv = new KeyValue(circleTimeline.centerXProperty(), 300, Interpolator.EASE_BOTH);
		final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
		timeline.getKeyFrames().add(kf);
		timeline.play();
		Path path = new Path();
			path.getElements().add(new MoveTo(20,20));
			path.getElements().add(new CubicCurveTo(380, 0, 380, 120, 200, 120));
			path.getElements().add(new CubicCurveTo(0, 120, 0, 240, 380, 240));
		PathTransition pathTransition = new PathTransition();
			pathTransition.setDuration(Duration.millis(4000));
			pathTransition.setPath(path);
			pathTransition.setNode(circle);
			pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
			pathTransition.setAutoReverse(true);
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(4000), circle);
	    	fadeTransition.setFromValue(0.8f);
	        fadeTransition.setToValue(0.1f);
	        fadeTransition.setAutoReverse(true);
	    ParallelTransition parallelTransition = new ParallelTransition();
	        parallelTransition.getChildren().addAll(
	                fadeTransition,
	                pathTransition
	        );
	        parallelTransition.setCycleCount(Timeline.INDEFINITE);
	        parallelTransition.play();
	    //t = new Thread(this, "Animated Thing");
	    //t.start();
	}  // end AnimatedSquare

	@Override
	public void run() {
	}

	@Override
	public void Update(double timestamp, double duration, float[] magnitudes,
			float[] phases) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Node getNode() {
		return canvas;
	}

	@Override
	protected Object call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}