public class KisanHub implements IDownloadComplete
{

	private Weather fetchWeatherData;

	public static void main(String[] args)
	{
		KisanHub kh = new KisanHub();
		kh.fetchData();
	}

	public void fetchData() 
	{
		fetchWeatherData = new Weather(this);
		fetchWeatherData.parseHtml();
	}

	@Override
	public void onDownloadComplete() 
	{
		fetchWeatherData.readFiles();
	}

	@Override
	public void onDownloadFailed()
	{
		System.out.println("Failed to download files");
	}

	@Override
	public void onReadComplete() 
	{
		fetchWeatherData.deleteFiles();
	}

	
	

}
