package model;

import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import services.nlp.Text;

import java.awt.*;

/**
 * DataModel objects store the data shared among different views (screens).
 */
public class DataModel {
    public Text currentlyLoadedText;
    public WebEngine webEngine;
    public Image wordCloud;
    public Prechecked prechecked;
}
