import java.util.ArrayList;
import java.util.List;

public class WeatherVO {

	private String year;
	private List<String> tempData; // I have created list for temperature data from JAN to DEC
	private List<String> monthName; // I have created list for month name from JAN to DEC

	public WeatherVO() {
		tempData = new ArrayList<>();
		monthName = new ArrayList<>();
		monthName.add("JAN");
		monthName.add("FEB");
		monthName.add("MAR");
		monthName.add("APR");
		monthName.add("MAY");
		monthName.add("JUN");
		monthName.add("JUL");
		monthName.add("AUG");
		monthName.add("SEP");
		monthName.add("OCT");
		monthName.add("NOV");
		monthName.add("DEC");

	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setTempData(String value) {
		tempData.add(value);
	}

	public String getYear() {
		return year;
	}

	public String getTemp(int i) {
		return tempData.get(i);
	}

	public String getMonth(int i) {
		return monthName.get(i);
	}
}
