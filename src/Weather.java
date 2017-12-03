import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datafetcher.FetchData;
import datafetcher.Utility;

public class Weather {

	/**
	 * This list is used to maintain order in which files url are getting from HTML.
	 * like for UK ==> TMAX ,TMIN ,TMEAN, SUNSHINE ,RAINFALL then goes for next row
	 * in table
	 */
	private List<String> fileList;
	private PrintWriter pw = null;
	private IDownloadComplete listener;

	public Weather(IDownloadComplete listener) {
		this.listener = listener;
	}

	/**
	 * This method first load url and then parse html using Jsoup library. We are
	 * using Executor service to download files parallel
	 */
	public void parseHtml() {
		int count = 0;
		fileList = new ArrayList<>();
		ExecutorService pool = Executors.newFixedThreadPool(Configuration.threadCount);
		try {

			Document doc = Jsoup.connect(Configuration.dataUrl).get();
			Element yearOrderedTable = doc.select("table.table").get(1);
			for (Element row : yearOrderedTable.select("tr")) {
				if (count <= Configuration.rowCount) {
					Elements tds = row.select("td");
					if (tds.size() > 0) {
						Elements links = tds.select("a[href]");
						for (int i = 0; i < Configuration.fieldCount; i++) {
							Element link = links.get(i);
							String downloadUrl = link.attr("href");
							/**
							 * I have taken file name from title tag of <a href>. ENGLAND__TMAX ,
							 * ENGLAND__TMIN etc
							 */
							String fileName = link.attr("title").replaceAll("Date", "").replaceAll(" ", "_");
							fileList.add(fileName);
							pool.submit(new FetchData(downloadUrl, fileName));

						}
					}

				}
				count++;
			}
			pool.shutdown();
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * once downloading is complete, We tell to main class to read files from files
		 * folder
		 */
		listener.onDownloadComplete();
	}

	/**
	 * This method is used to read files from files folder. We are readnig files in
	 * same order in which they appear in HTML table
	 */
	public void readFiles() {

		initPrintWriter();
		for (String fileName : fileList) {

			File f = new File(Utility.createMyFilesDirectory() + "/" + fileName);
			readIndividualFile(f);

		}
		pw.close();

		/**
		 * once reading is complete, We tell to main class to delete files from files
		 * folder
		 */
		listener.onReadComplete();
	}

	/**
	 * This method is used to read individual file and write data to csv file
	 * 
	 * @param f
	 *            this is file
	 */
	private void readIndividualFile(File f) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String line;
			String[] info = f.getName().split("__");

			while ((line = reader.readLine()) != null) {

				String[] words = line.split(" ");

				WeatherVO data = new WeatherVO();
				data.setYear(String.valueOf(words[0]));

				for (int i = 1; i < words.length; i++) {
					if (!words[i].equalsIgnoreCase("")) {
						data.setTempData(words[i]);
					}
				}

				for (int j = 0; j < 12; j++) {
					pw.write(info[0]);
					pw.write(',');
					if (info[1].equalsIgnoreCase("Tmax")) {
						pw.write("Max temp");
					} else if (info[1].equalsIgnoreCase("Tmin")) {
						pw.write("Min temp");
					}
					if (info[1].equalsIgnoreCase("Tmean")) {
						pw.write("Mean temp");
					}
					pw.write(',');

					pw.write(data.getYear());
					pw.write(',');
					pw.write(data.getMonth(j));
					pw.write(',');
					pw.write(data.getTemp(j));
					pw.write('\n');
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This is used to initialize Weather.csv file for writing data
	 */
	private void initPrintWriter() {
		try {
			pw = new PrintWriter(new File("weather.csv"));
			StringBuilder sb = new StringBuilder();
			sb.append("region_code");
			sb.append(',');
			sb.append("weather_param");
			sb.append(',');
			sb.append("year");
			sb.append(',');
			sb.append("key");
			sb.append(',');
			sb.append("value");
			sb.append('\n');
			pw.write(sb.toString());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * This is used to delete files from files folder after reading is complete
	 */
	public void deleteFiles() {
		File folder = new File(Utility.createMyFilesDirectory());
		if (folder.exists() && folder.listFiles().length > 0) {
			File[] listOfFiles = folder.listFiles();
			for (File f : listOfFiles) {
				f.delete();
			}
			folder.delete();
		}
	}
}
