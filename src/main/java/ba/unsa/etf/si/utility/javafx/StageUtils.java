package ba.unsa.etf.si.utility.javafx;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class StageUtils {

    private StageUtils() {}

    public static Rectangle2D getScreenBounds() {
        return Screen.getPrimary().getBounds();
    }

    public static void centerStage(Stage stage, int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height);
        Rectangle2D primScreenBounds = getScreenBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public static void setStage(Stage stage, String title, boolean resizable, StageStyle stageStyle, Modality modality) {
        stage.setTitle(title);
        stage.setResizable(resizable);
        stage.initStyle(stageStyle);
        if(modality != null) stage.initModality(modality);
    }

    public static void setStageDimensions(Stage stage) {
        Rectangle2D bounds = getScreenBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }

    public static Timeline setAnimation(Parent root, EventHandler<ActionEvent> handler) {
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(handler);
        return timeline;
    }
}
