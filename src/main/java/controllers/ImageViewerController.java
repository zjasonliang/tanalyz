package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.DataModel;

public class ImageViewerController {
    @FXML private ImageView imageView;

    private DataModel model;

    public void initialize() {
        imageView.setImage(model.wordCloud);
    }

    void initDataModel(DataModel model) {
        this.model = model;
    }
}
