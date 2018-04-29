package net.sentientturtle.nee.util;

/**
 * Small utility class to get MIME types from file extensions. _FAR FROM COMPLETE_.
 */
public class MIME {
    public static String getType(String extension) {
        switch (extension) {
            case ".css":
                return "text/css";
            case ".png":
                return "image/png";
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            default:
                throw new RuntimeException("MIME type for extension " + extension + " not known!");
        }
    }
}
