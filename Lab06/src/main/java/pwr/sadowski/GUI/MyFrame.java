package pwr.sadowski.GUI;

import pwr.sadowski.operators.Host;
import pwr.sadowski.operators.Player;

import java.awt.*;

import java.awt.Color;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.io.IOException;


public class MyFrame extends JFrame {

	private JPanel contentPane;
	JPanel menuPanel = new JPanel();
	public static MyPanel hostPanel = new MyPanel();
	public static MyPanel playerPanel = new MyPanel();

	CardLayout cl = new CardLayout();
	JButton hostButton = new JButton("HostApp");
	JButton playerButton = new JButton("PlayerApp");


	Host host;
	Player player;


	public static Statistics stats = new Statistics();

	public static JLabel lblPoints;
	private final JLabel lblPortHost = new JLabel("Port:");
	private final JLabel lblPortPlayer = new JLabel("Port:");
	private final JTextField hostPortListen = new JTextField();
	private final JTextField playerConnectWithHost = new JTextField();
	private final JToggleButton btnListenHost = new JToggleButton("Listen");
	private final JToggleButton btnConnectWithHost = new JToggleButton("Connect");
	private final JButton statsButton = new JButton("Stats");

	/**
	 * Create the frame.
	 */
	public MyFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 775, 640);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(cl);


		menuPanel.setBounds(10, 10, 414, 240);
		contentPane.add("1", menuPanel);
		menuPanel.setBackground(new Color(70, 163, 255));
		menuPanel.setLayout(null);

		//Host
		hostButton.setFont(new Font("Tahoma", Font.PLAIN, 17));
		hostButton.setBounds(232, 284, 308, 40);
		menuPanel.add(hostButton);

		hostButton.addActionListener(e -> {
			cl.show(contentPane, "2");

			host = new Host();
		});


		hostPanel.setBounds(10, 11, 414, 239);
		contentPane.add("2", hostPanel);
		hostPanel.setBackground(Color.GREEN);
		hostPanel.setLayout(null);


		lblPortHost.setForeground(new Color(255, 255, 255));
		lblPortHost.setBounds(233, 533, 46, 14);
		hostPanel.add(lblPortHost);

		hostPortListen.setText("");
		hostPortListen.setBounds(270, 530, 86, 20);
		hostPortListen.setColumns(10);
		hostPanel.add(hostPortListen);


		btnListenHost.setBounds(406, 529, 89, 23);

		btnListenHost.addActionListener(e -> {
			if(btnListenHost.isSelected()){
				try {
					host.setPort(Integer.parseInt(hostPortListen.getText()));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				host.start();
			}
		});

		hostPanel.add(btnListenHost);

		//Player
		playerButton.setFont(new Font("Tahoma", Font.PLAIN, 17));
		playerButton.setBounds(232, 360, 308, 40);
		menuPanel.add(playerButton);

		playerButton.addActionListener(e -> {
			cl.show(contentPane, "3");
			player = new Player();
		});


		playerPanel.setBounds(10, 11, 414, 239);
		contentPane.add("3", playerPanel);
		playerPanel.setBackground(Color.YELLOW);
		playerPanel.setLayout(null);

		lblPortPlayer.setForeground(new Color(255, 255, 255));
		lblPortPlayer.setBounds(233, 533, 46, 14);
		playerPanel.add(lblPortPlayer);


		playerConnectWithHost.setText("");
		playerConnectWithHost.setBounds(270, 530, 86, 20);
		playerConnectWithHost.setColumns(10);
		playerPanel.add(playerConnectWithHost);

		btnConnectWithHost.setBounds(406, 529, 89, 23);

		btnConnectWithHost.addActionListener(e -> {
			if(btnConnectWithHost.isSelected()){
				try {
					player.setPortToSend(Integer.parseInt(playerConnectWithHost.getText()));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}

			player.start();
		});

		playerPanel.add(btnConnectWithHost);

		lblPoints = new JLabel("POINTS: ");
		lblPoints.setForeground(new Color(255, 128, 0));
		lblPoints.setFont(new Font("Tahoma", Font.BOLD, 29));
		lblPoints.setBounds(21, 501, 182, 63);
		playerPanel.add(lblPoints);


		statsButton.setBounds(606, 529, 89, 23);
		hostPanel.add(statsButton);

		statsButton.addActionListener(e -> {
			if(btnListenHost.isSelected()){
				stats.showStats(stats);
			}
		});



		cl.show(contentPane, "1");
	}
}
