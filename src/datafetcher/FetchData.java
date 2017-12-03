package datafetcher;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class FetchData implements Runnable
{
	
	private String fileUrl, fileName;

	/** 
	 * 
	 * @param fileUrl url of text file to download( http url from html)
	 * @param fileName name of file 
	 */
    public FetchData(String fileUrl, String fileName)
    {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
    }
    
    @Override
    public void run()
    {
        downloadFile(fileUrl, fileName);
    }
	
    /**
     * This method is used to read file from server. 
     * WE are reading file from server and storing to our end
     * @param fileUrl
     * @param fileName
     */
	public void downloadFile(String fileUrl, String fileName) 
	{
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream =null;
		BufferedReader bufferedReader = null;
		FileWriter writer  = null;
		try {
				//System.out.println(fileUrl +"  "+fileName);
				url = new URL(fileUrl);
				httpURLConnection = (HttpURLConnection)url.openConnection();
				httpURLConnection.connect();
				int status= httpURLConnection.getResponseCode();
				boolean redirect = false;
				
				/**
				 * normally, 3xx is redirect
				 * This logic is because when i was clicking on Href http url found in <td> tag 
				 * it is redirecting to https url
				 */
				
				if (status != HttpURLConnection.HTTP_OK)
				{
					if (status == HttpURLConnection.HTTP_MOVED_TEMP|| status == HttpURLConnection.HTTP_MOVED_PERM|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
				}

				
				if (redirect)
				{
					String newUrl = httpURLConnection.getHeaderField("Location");
					httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
				}
				
				inputStream = httpURLConnection.getInputStream();
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            
	            /**
	             * This login to used to skip first 8 lines from text files
	             */
	            for(int j = 1 ; j <=8;j++)
	            {
	            	bufferedReader.readLine();
	            }
	            
	            String res = null;
	            /**
	             * at run time we are creating folder named files to store files
	             */
	            writer =  new FileWriter(Utility.createMyFilesDirectory()+"/"+fileName);
	            while ((res = bufferedReader.readLine()) != null)
	            {
	                writer.write(res);
	                writer.write("\n");
	            }
	            writer.close();
	            bufferedReader.close();
	            inputStream.close();
				httpURLConnection.disconnect();
			}
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		catch (IOException io) 
		{
			io.printStackTrace();
		}
		finally
		{
			try 
			{
				writer.close();
				bufferedReader.close();
	            inputStream.close();
				httpURLConnection.disconnect();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
            
		}
		
	}

	
}
