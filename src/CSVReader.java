import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CSVReader {

	static int rowsNum;
	static int columnsNum;
	int numBin = 20;
	static ArrayList<Double> mean = new ArrayList<Double>();
	static ArrayList<Double> deviation = new ArrayList<Double>();
	static ArrayList<Integer[]> binCounter = new ArrayList<Integer[]>();

	public static void readCSV(File f, ArrayList<ArrayList<Double>> rows, ArrayList<String> columnNames)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));

		String line = reader.readLine();

		int line_counter = 0;

		boolean skip_line = false;
		while (line != null) {
			if (line_counter == 0) {
				// The first line contains the column headers.
				int token_counter = 0;
				StringTokenizer st = new StringTokenizer(line);
				while (st.hasMoreTokens()) {
					String token = st.nextToken(",");
					columnNames.add(token.trim());
					token_counter++;
				}
				columnsNum = token_counter;
				line_counter++;
				line = reader.readLine();

				continue;
			}

			ArrayList<Double> row = new ArrayList<Double>();
			StringTokenizer st = new StringTokenizer(line);
			int token_counter = 0;

			skip_line = false;
			while (st.hasMoreTokens()) {
				String token = st.nextToken(",");
				try {
					double value = Double.parseDouble(token);
					row.add(value);
					token_counter++;
				} catch (NumberFormatException ex) {
					System.out.println("DataSet.readCSV(): NumberFormatException caught so skipping record. "
							+ ex.fillInStackTrace());
					skip_line = true;
					break;
				}
			}

			if (row.size() != columnNames.size()) {
				System.err.println(
						"Row ignored because it has " + (columnNames.size() - row.size()) + " column values missing.");
				skip_line = true;
			}

			if (!skip_line) {
				rows.add(row);
			}

			line_counter++;
			line = reader.readLine();
		}
		rowsNum = line_counter;
		reader.close();
	}

	public static void calMean(ArrayList<ArrayList<Double>> rows, int rowsNum) {
		Double[] sum = new Double[columnsNum];
		for (int i = 0; i < columnsNum; i++) {
			sum[i] = 0.0;
		}
		// System.out.print(columnsNum);

		for (ArrayList<Double> rowLine : rows) {
			for (int i = 0; i < columnsNum; i++) {
				sum[i] += rowLine.get(i).doubleValue();
			}
		}
		for (int i = 0; i < columnsNum; i++) {
			DecimalFormat df = new DecimalFormat("#.00");
			mean.add(Double.parseDouble(df.format(sum[i] / rowsNum)));
			// System.out.println(mean.get(i));
		}
	}

	public static void calDeviation(ArrayList<ArrayList<Double>> rows, int rowsNum) {
		Double[] sum = new Double[columnsNum];
		for (int i = 0; i < columnsNum; i++) {
			sum[i] = 0.0;
		}
		for (ArrayList<Double> rowLine : rows) {
			for (int i = 0; i < columnsNum; i++) {
				sum[i] += (rowLine.get(i).doubleValue() - mean.get(i).doubleValue())
						* (rowLine.get(i).doubleValue() - mean.get(i).doubleValue());
			}
		}
		for (int i = 0; i < columnsNum; i++) {
			DecimalFormat df = new DecimalFormat("#.00");
			deviation.add(Double.parseDouble(df.format(Math.sqrt(sum[i] / rowsNum))));
			// System.out.println(deviation.get(i));
		}
	}

	public static void countBin(ArrayList<ArrayList<Double>> rows, int rowsNum){

		int len = rows.get(0).size();
		Double[] max = new Double[len];
		Double[] min = new Double[len];
		for (int i = 0; i < len; i++) {
			max[i] = Double.MIN_VALUE;
			min[i] = Double.MAX_VALUE;
		}
		for (int irow = 0; irow < rows.size(); irow++) {
			ArrayList<Double> row = rows.get(irow);
			for (int ivalue = 0; ivalue < row.size(); ivalue++) {
				min[ivalue] = Math.min(min[ivalue], row.get(ivalue));
				max[ivalue] = Math.max(max[ivalue], row.get(ivalue));
			}
		}
		for(int i=0; i<columnsNum; i++ ){
			Integer[] binsum = new Integer[20];
			for(int j=0; j<20; j++){
				binsum[j] = 0;
			}
			for(ArrayList<Double> rowLine: rows){
				int area= (int) ((rowLine.get(i).doubleValue()- min[i]) / (max[i] - min[i]) * 20);
				if( area == 20) area = 19;
				binsum[area] += 1;
			}
			binCounter.add(binsum);
//			for(int k=0;k<20;k++){
//				System.out.println("Kth Bin contains for column" + i + "contains" + binsum[k] + " ");
//			}
		}
	}

	public static void main(String args[]) throws Exception {
		File f = new File("cars.csv");
		ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();
		ArrayList<String> columnNames = new ArrayList<String>();
		CSVReader.readCSV(f, rows, columnNames);
		CSVReader.calMean(rows, rowsNum);
		CSVReader.calDeviation(rows, rowsNum);
		CSVReader.countBin(rows, rowsNum);
		StringBuffer buffer = new StringBuffer();
		// for (int icol = 0; icol < columnNames.size(); icol++) {
		// buffer.append(columnNames.get(icol));
		// if ((icol + 1) < columnNames.size()) {
		// buffer.append(", ");
		// }
		// }
		// System.out.println(buffer.toString());

		// Keep track of the MAX and MIN for each column.
		int len = rows.get(0).size();
		Double[] max = new Double[len];
		Double[] min = new Double[len];
		for (int i = 0; i < len; i++) {
			max[i] = Double.MIN_VALUE;
			min[i] = Double.MAX_VALUE;
		}

		for (int irow = 0; irow < rows.size(); irow++) {
			ArrayList<Double> row = rows.get(irow);
			for (int ivalue = 0; ivalue < row.size(); ivalue++) {
				min[ivalue] = Math.min(min[ivalue], row.get(ivalue));
				max[ivalue] = Math.max(max[ivalue], row.get(ivalue));
			}
		}
		for (int i = 0; i < len; i++) {
			System.out.println(columnNames.get(i));
			System.out.print("MIN " + min[i].toString());
			System.out.println(" MAX " + max[i].toString());
		}
	}
}
