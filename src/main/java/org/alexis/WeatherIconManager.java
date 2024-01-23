package org.alexis;

import java.io.File;

public class WeatherIconManager {
    private final File[] icons;

    public WeatherIconManager() {
        icons = new File("src/main/resources/condition-icons").listFiles(); //access condition-icons directory and returns an array
    }

    public String getFilePath(String iconName) { //methods that returns the path of an icon
        for (File icon : icons) { //iterates through the icons
            if (icon.getName().matches(iconName)) {
                return icon.getPath(); //returns path of the icon
            }
        }
        return "";
    }




}
