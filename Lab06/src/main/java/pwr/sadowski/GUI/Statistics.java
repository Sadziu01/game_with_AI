package pwr.sadowski.GUI;

import pwr.sadowski.operators.Host;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.util.Map;

public class Statistics extends JPanel {

    public JTable table;
    public DefaultTableModel tableModel;

    public Statistics() {
        String[] columns = {"Port", "Points"};
        this.tableModel = new DefaultTableModel(columns, 0);
        this.table = new JTable(tableModel);
        JScrollPane plane = new JScrollPane(table);
        add(plane);
    }

    protected void update() {
        tableModel.setRowCount(0);
        for (Map.Entry<Integer, Integer> entry : Host.uniquePorts.entrySet()) {
            tableModel.insertRow(tableModel.getRowCount(), new Object[] { entry.getKey(), entry.getValue()});
        }
    }

    protected void showStats(Statistics stats) {
        JFrame frame = new JFrame("stats");
        frame.add(stats);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        stats.update();
    }
}
