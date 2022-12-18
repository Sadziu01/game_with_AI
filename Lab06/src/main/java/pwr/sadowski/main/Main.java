/**
 * @author Bartosz Sadowski
 *gradlew build
 *gradlew jar
 *java.exe -p Lab06_pop.jar -m Lab/pwr.sadowski.main.Main
 */

package pwr.sadowski.main;

import pwr.sadowski.GUI.MyFrame;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MyFrame frame = new MyFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
