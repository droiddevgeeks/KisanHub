import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Configuration
{
	/**
	 * this variable is used for decide number of threads for downloading files
	 * we are setting this variable from config.properties files
	 */
	public static int threadCount; 
	
	/**
	 * this variable is used for decide number of cities data like UK , ENGLAND etc( number of rows from table to read) to read
	 * we are setting this variable from config.properties files
	 */
	public static int rowCount;
	
	/**
	 * this variable is used for decide number of column data like TMAX ,TMIN ,TMEAN etc( number of column from table to read) to read
	 * we are setting this variable from config.properties files
	 */
	public static int fieldCount;
	
	public static final String dataUrl = "https://www.metoffice.gov.uk/climate/uk/summaries/datasets#yearOrdered";
	
	static
	{
		File configFile = new File("config.properties");
		try 
		{
			    FileReader reader = new FileReader(configFile);
			    Properties props = new Properties();
			    props.load(reader);
			 
			    threadCount = Integer.valueOf(props.getProperty("threadcount"));
			    rowCount = Integer.valueOf(props.getProperty("rowcount"));
			    fieldCount = Integer.valueOf(props.getProperty("columncount"));
			    reader.close();
		}
		catch (FileNotFoundException ex) 
		{    
			ex.printStackTrace();
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
	}
	
	
}
