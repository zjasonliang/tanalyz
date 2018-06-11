package services.util.imgutil;

import javafx.scene.image.Image;

public interface ImageFilter {

    int[] filter(int imageWidth, int imageHeigth, int[] inPixels);

}
