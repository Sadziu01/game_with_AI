package pwr.sadowski.GUI;

import pwr.sadowski.operators.Host;
import pwr.sadowski.variables.OObject;

import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel {

    int scale = 5;

    private OObject[][] map = new OObject[100][150];

    public void setMap(OObject[][] map) {
        this.map = map;
    }


    public Dimension getPreferredSize() {
        return new Dimension(150 * scale,100 * scale);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(new Color(40,40,40));
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 150; x++) {
                if (map[y][x].getContent() == 'E') {
                    g.setColor(new Color(110,155,48));
                    g.fillRect(x * scale, y * scale, scale, scale);
                } else if (map[y][x].getContent() == 'R') {
                    g.setColor(new Color(133, 120, 148));
                    g.fillRect(x * scale, y * scale, scale, scale);
                }  else if (map[y][x].getContent() == 'T') {
                    g.setColor(new Color(148, 109, 52));
                    g.fillRect(x * scale, y * scale, scale, scale);
                } else if (map[y][x].getContent() == 'P') {
                    g.setColor(new Color(255, 255, 255));
                    g.fillRect(x * scale, y * scale, scale, scale);
                } else if (map[y][x].getContent() == ' ') {
                    g.setColor(new Color(209, 185, 146));
                    g.fillRect(x * scale, y * scale, scale, scale);
                }
            }
        }
        MyFrame.stats.update();
    }
}
