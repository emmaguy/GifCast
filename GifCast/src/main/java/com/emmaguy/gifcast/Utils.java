package com.emmaguy.gifcast;

public class Utils {
    public static boolean isGif(String pictureFileName) {
        return pictureFileName.endsWith(".gif");
    }

    public static boolean isImage(String pictureFileName) {
        return pictureFileName.endsWith(".png")
                || pictureFileName.endsWith(".gif")
                || pictureFileName.endsWith(".jpg")
                || pictureFileName.endsWith(".jpeg");
    }
}
