package ru.rpuxa.desktop;

public class DesktopUtils {

    public static DesktopUtils INSTANCE = new DesktopUtils();

    public String getResource(String path) {
        return getClass().getClassLoader().getResource(path).getPath();
    }
}
