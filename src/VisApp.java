import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class VisApp implements ActionListener {
	private JFrame appFrame;
	private VisPanel visPanel;

	private ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();
	private ArrayList<String> columnNames = new ArrayList<String>();
	private Double[] max;
	private Double[] min;
	private String[] columnNamestring;
	private String[] columnNamestringZ;
	
	private Double[] maxminXY;

	private ArrayList<Double> mean = new ArrayList<Double>();
	private ArrayList<Double> deviation = new ArrayList<Double>();
	private ArrayList<Integer[]> binCounter = new ArrayList<Integer[]>();

	// By default, choose the first column to be x-axis, y-axis and z-axis
	private int x_index = 0;
	private int y_index = 0;
	private int z_index = 0;

	JComboBox<String> jbX;
	JComboBox<String> jbY;
	JComboBox<String> jbZ;

	private void CSVinitialize() throws Exception {
		File f = new File("cars.csv");
		CSVReader.readCSV(f, rows, columnNames);
		
		// Calculate the mean, deviation for each column
		// Calculate the bin counter for each column
		CSVReader.calMean(rows, CSVReader.rowsNum);
		CSVReader.calDeviation(rows, CSVReader.rowsNum);
		CSVReader.countBin(rows, CSVReader.rowsNum);
		mean = CSVReader.mean;
		deviation = CSVReader.deviation;
		binCounter = CSVReader.binCounter;
		
		
		StringBuffer buffer = new StringBuffer();

		int len = rows.get(0).size();
		max = new Double[len];
		min = new Double[len];
		for (int i = 0; i < len; i++) {
			max[i] = Double.MIN_VALUE;
			min[i] = Double.MAX_VALUE;
		}

		columnNamestring = new String[columnNames.size()];
		columnNamestringZ = new String[columnNames.size() + 1];
		for (int i = 0; i < columnNames.size(); i++) {
			columnNamestring[i] = columnNames.get(i);
			columnNamestringZ[i] = columnNames.get(i);
		}
		columnNamestringZ[columnNames.size()] = "Unset";

		for (int irow = 0; irow < rows.size(); irow++) {
			ArrayList<Double> row = rows.get(irow);
			buffer = new StringBuffer();
			for (int ivalue = 0; ivalue < row.size(); ivalue++) {
				min[ivalue] = Math.min(min[ivalue], row.get(ivalue));
				max[ivalue] = Math.max(max[ivalue], row.get(ivalue));
			}
		}
	}

	public VisApp() throws Exception {
		CSVinitialize();
		initialize();
		appFrame.setVisible(true);
	}

	private void initialize() {
		appFrame = new JFrame();
		appFrame.setTitle("Visualizion Project By Shiqi Zhong");
		appFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		appFrame.setBounds(100, 100, 1400, 700);

		initializePanel();
		initializeMenu();
	}

	private void initializeMenu() {
		JMenuBar menuBar = new JMenuBar();
		appFrame.setJMenuBar(menuBar);

		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		JMenuItem mi = new JMenuItem("Open CSV...", KeyEvent.VK_O);
		mi.addActionListener(this);
		mi.setActionCommand("open csv");
		mi.setEnabled(false);
		menu.add(mi);

		mi = new JMenuItem("Random Data", KeyEvent.VK_D);
		mi.addActionListener(this);
		mi.setActionCommand("random");
		mi.setEnabled(false);
		menu.add(mi);

		menu.addSeparator();

		mi = new JMenuItem("Exit", KeyEvent.VK_X);
		mi.addActionListener(this);
		mi.setActionCommand("exit");
		menu.add(mi);

		JMenu xmenu = new JMenu("x-axis");
		menuBar.add(xmenu);
		for (String columnName : columnNames) {
			mi = new JMenuItem(columnName, KeyEvent.VK_X);
			mi.addActionListener(this);
			mi.setActionCommand("SetX," + columnName);
			mi.setEnabled(true);
			xmenu.add(mi);
		}

		JMenu ymenu = new JMenu("y-axis");
		menuBar.add(ymenu);
		for (String columnName : columnNames) {
			mi = new JMenuItem(columnName, KeyEvent.VK_Y);
			mi.addActionListener(this);
			mi.setActionCommand("SetY," + columnName);
			mi.setEnabled(true);
			ymenu.add(mi);
		}

	}

	private void initializePanel() {
		visPanel = new VisPanel();
		visPanel.setBackground(Color.white);
		visPanel.setForeground(Color.darkGray);

		JPanel mainPanel = (JPanel) appFrame.getContentPane();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(visPanel, BorderLayout.CENTER);

		// Add two JComboBoxs for x and y axis and set up the action listeners
		jbX = new JComboBox<String>(columnNamestring);
		jbX.setEnabled(true);
		jbX.setBorder(BorderFactory.createTitledBorder("Choose X-axis"));

		ActionListener xListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				x_index = columnNames.indexOf(jbX.getSelectedItem());
				maxminXY = new Double[] { max[x_index],min[x_index],max[y_index],min[y_index] } ;

				float xValues[] = new float[rows.size()];
				float yValues[] = new float[rows.size()];
				float zValues[] = new float[rows.size()];
	
				for (int irow = 0; irow < rows.size(); irow++) {
					ArrayList<Double> row = rows.get(irow);
					xValues[irow] = row.get(x_index).floatValue();
					yValues[irow] = row.get(y_index).floatValue();
					if( z_index >= 0){
					zValues[irow] = (float) ((row.get(z_index) - min[z_index]) / (max[z_index] - min[z_index]));
					} else
						zValues[irow] = 1;

//					xValues[irow] = (float) ((row.get(x_index) - min[x_index]) / (max[x_index] - min[x_index]));
//					yValues[irow] = (float) ((row.get(y_index) - min[y_index]) / (max[y_index] - min[y_index]));
				}
//				visPanel.setData(xValues, yValues, columnNames.get(x_index), columnNames.get(y_index));
				visPanel.setData(xValues, yValues, zValues, columnNames.get(y_index), columnNames.get(x_index), maxminXY);
				Double[] meanDev= new Double[4];
				meanDev[0] = mean.get(x_index).doubleValue();
				meanDev[1] = mean.get(y_index).doubleValue();
				meanDev[2] = deviation.get(x_index).doubleValue();
				meanDev[3] = deviation.get(y_index).doubleValue();				
				visPanel.setStaticdata(meanDev, binCounter.get(x_index), binCounter.get(y_index));
			}
		};
		jbX.addActionListener(xListener);	
		mainPanel.add(jbX, BorderLayout.PAGE_START);

		jbY = new JComboBox<String>(columnNamestring);
		jbY.setEnabled(true);
		jbY.setBorder(BorderFactory.createTitledBorder("Choose Y-axis"));
		ActionListener yListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				y_index = columnNames.indexOf(jbY.getSelectedItem());
				maxminXY = new Double[] { max[x_index],min[x_index],max[y_index],min[y_index] } ;

				float xValues[] = new float[rows.size()];
				float yValues[] = new float[rows.size()];
				float zValues[] = new float[rows.size()];
				for (int irow = 0; irow < rows.size(); irow++) {
					ArrayList<Double> row = rows.get(irow);
					xValues[irow] = row.get(x_index).floatValue();
					yValues[irow] = row.get(y_index).floatValue();
					if( z_index >= 0){
					zValues[irow] = (float) ((row.get(z_index) - min[z_index]) / (max[z_index] - min[z_index]));
					} else
						zValues[irow] = 1;
				}
				visPanel.setData(xValues, yValues, zValues, columnNames.get(y_index), columnNames.get(x_index), maxminXY);
				Double[] meanDev= new Double[4];
				meanDev[0] = mean.get(x_index).doubleValue();
				meanDev[1] = mean.get(y_index).doubleValue();
				meanDev[2] = deviation.get(x_index).doubleValue();
				meanDev[3] = deviation.get(y_index).doubleValue();				
				visPanel.setStaticdata(meanDev, binCounter.get(x_index), binCounter.get(y_index));			
			}
		};
		jbY.addActionListener(yListener);		
		mainPanel.add(jbY, BorderLayout.PAGE_END);
		
		jbZ = new JComboBox<String>(columnNamestringZ);
		jbZ.setEnabled(true);
		jbZ.setBorder(BorderFactory.createTitledBorder("Choose Circle Radius"));
		
		ActionListener zListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				z_index = columnNames.indexOf(jbZ.getSelectedItem());
				maxminXY = new Double[] { max[x_index],min[x_index],max[y_index],min[y_index] } ;

				float xValues[] = new float[rows.size()];
				float yValues[] = new float[rows.size()];
				float zValues[] = new float[rows.size()];				
				for (int irow = 0; irow < rows.size(); irow++) {
					ArrayList<Double> row = rows.get(irow);
					xValues[irow] = row.get(x_index).floatValue();
					yValues[irow] = row.get(y_index).floatValue();
					if( z_index >= 0){
					zValues[irow] = (float) ((row.get(z_index) - min[z_index]) / (max[z_index] - min[z_index]));
					} else
						zValues[irow] = 1;
				}
				visPanel.setData(xValues, yValues, zValues, columnNames.get(y_index), columnNames.get(x_index), maxminXY);
				Double[] meanDev= new Double[4];
				meanDev[0] = mean.get(x_index).doubleValue();
				meanDev[1] = mean.get(y_index).doubleValue();
				meanDev[2] = deviation.get(x_index).doubleValue();
				meanDev[3] = deviation.get(y_index).doubleValue();				
				visPanel.setStaticdata(meanDev, binCounter.get(x_index), binCounter.get(y_index));
			}
		};
		jbZ.addActionListener(zListener);
		mainPanel.add(jbZ, BorderLayout.WEST);

	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VisApp app = new VisApp();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("random")) {
			// create some random data and populate the scatter plot panel
			Random rand = new Random();

			float xValues[] = new float[2000 + rand.nextInt(1000)];
			float yValues[] = new float[xValues.length];
			float zValues[] = new float[2000 + rand.nextInt(1000)];
			for (int i = 0; i < xValues.length; i++) {
				xValues[i] = rand.nextFloat() * 400.f;
				yValues[i] = rand.nextFloat() * 600.f;
			}

			visPanel.setData(xValues, yValues, zValues, columnNames.get(y_index), columnNames.get(x_index), maxminXY);
		} else if (event.getActionCommand().equals("exit")) {
			System.exit(0);
		} else {
			// Additionally， add the selecting function to the menu bar
			// When choose the column for the menu bar, update the JCombox
			String cmd = event.getActionCommand();
			String[] cmdParam = cmd.split(",");
//			for (String str : cmdParam) {
//				System.out.println(str);
//			}
			if (cmdParam[0].equals("SetX")) {
				x_index = columnNames.indexOf(cmdParam[1]);
				// Update the JCombox
				jbX.setSelectedItem(cmdParam[1]);
			} else{
				y_index = columnNames.indexOf(cmdParam[1]);
				// Update the JCombox
				jbY.setSelectedItem(cmdParam[1]);
			}

			maxminXY = new Double[] { max[x_index],min[x_index],max[y_index],min[y_index] } ;

			float xValues[] = new float[rows.size()];
			float yValues[] = new float[rows.size()];
			float zValues[] = new float[rows.size()];

			for (int irow = 0; irow < rows.size(); irow++) {
				ArrayList<Double> row = rows.get(irow);
				xValues[irow] = row.get(x_index).floatValue();
				yValues[irow] = row.get(y_index).floatValue();

				if( z_index >= 0){
				zValues[irow] = (float) ((row.get(z_index) - min[z_index]) / (max[z_index] - min[z_index]));
				} else
					zValues[irow] = 1;

//				xValues[irow] = (float) ((row.get(x_index) - min[x_index]) / (max[x_index] - min[x_index]));
				// System.out.println(xValues[irow]);
//				yValues[irow] = (float) ((row.get(y_index) - min[y_index]) / (max[y_index] - min[y_index]));
				// System.out.println(yValues[irow]);
			}
//			visPanel.setData(xValues, yValues, columnNames.get(x_index), columnNames.get(y_index));
			visPanel.setData(xValues, yValues, zValues, columnNames.get(y_index), columnNames.get(x_index), maxminXY);
		}
	}

}
