package ml.withp.utility;

import java.io.InputStream;

public class Loader {
    public static InputStream resourceStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }
}
