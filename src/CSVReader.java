import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CSVReader {
	
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

		reader.close();
	}

	public static void main(String args[]) throws Exception {
		File f = new File("cars.csv");
		ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();
		ArrayList<String> columnNames = new ArrayList<String>();
		CSVReader.readCSV(f, rows, columnNames);

		StringBuffer buffer = new StringBuffer();
//		for (int icol = 0; icol < columnNames.size(); icol++) {
//			buffer.append(columnNames.get(icol));
//			if ((icol + 1) < columnNames.size()) {
//				buffer.append(", ");
//			}
//		}
//		System.out.println(buffer.toString());

		//Keep track of the MAX and MIN for each column.
		int len = rows.get(0).size();
		Double[] max = new Double[len];
		Double[] min = new Double[len];
		for (int i = 0; i < len; i++) {
			max[i]=Double.MIN_VALUE;
			min[i]=Double.MAX_VALUE;
		}

		for (int irow = 0; irow < rows.size(); irow++) {
			ArrayList<Double> row = rows.get(irow);
			buffer = new StringBuffer();
			for (int ivalue = 0; ivalue < row.size(); ivalue++) {
				min[ivalue] = Math.min(min[ivalue], row.get(ivalue));
				max[ivalue] = Math.max(max[ivalue], row.get(ivalue));
			}
		}
		for (int i = 0; i < len; i++) {
			System.out.println(columnNames.get(i));
			System.out.print("MIN " + min[i].toString());
			System.out.println(" MAX " +max[i].toString());
		}
	}
}
