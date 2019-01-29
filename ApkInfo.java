public class ApkInfo {
	
	public String appPath;
	public Application app;
	
	private String response;
	private String uuid;
	private ZipFile zipFile;
	
	public ApkInfo(String appPath, Application app) throws Exception
	{
		this.appPath = appPath;
		this.app = app;
		zipFile = new ZipFile(appPath);
		response = CommandLineUtils.ExecuteCommand("aapt dump badging " + appPath); // you can write your own util to execute shell command and get response.
	}
	
	/**
	 * This method must be called when all operations are finished.
	 * @return 
	 */
	public Boolean closeZip()
	{
		try {

			zipFile.close();
		} catch (Exception e) {

			Logger.exception(e);
			return false;
		}
		return true;
	}
	
	/**
	 * This method will return bundle id of the given apk file.
	 * @return
	 */
	public String getBundleIdFromApk()
	{
		if(response == null || response.length() == 0)
		{
			Logger.error("aapt command was failed for file at path: " + appPath);
			return null;
		}

		String retVal = null;
		if(response != null && response.length() > 0) {

			retVal = getAppBundleId();
		} else {
			
			Logger.error("aapt command was failed for file at path: " + appPath);
		}
		return retVal;
	}
	
	/**
	 * This method will return sdkVersion of the given apk file.
	 * @return
	 */
	public String getSdkVersionFromApk()
	{
		if(response == null || response.length() == 0)
		{
			Logger.error("aapt command was failed for file at path: " + appPath);
			return null;
		}

		String retVal = null;
		if(response != null && response.length() > 0) {

			retVal = getAppSdkVersion();
		} else {
			
			Logger.error("aapt command was failed for file at path: " + appPath);
		}
		return retVal;
	}
	
	/**
	 * This method will return icon path of the fiven apk file.
	 * @return
	 */
	public String getAppIconPathFromApk()
	{
		String retVal = null;
		if(response != null && response.length() > 0) {

			retVal = getAppIconPath();
		} else {
			
			Logger.error("aapt command was failed for file at path: " + appPath);
		}
		return retVal;
	}
	
	/**
	 * This method will extract given icon from given path
	 * and save in db as bytes.
	 * @param appIconPath
	 * @return
	 */
	public String getAppIconFromApk(String appIconPath)
	{
		String retVal = null;
		this.uuid = UUID.nameUUIDFromBytes(app.bundleId.getBytes()).toString();
    
			if(zipFile == null) {

				Logger.error("zip handle to apk file at path: " + appPath + " is null.");
				return null;
			}

			String iconFileName = getFileNameFromPath(appIconPath);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {

				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {

					String fName = entry.getName();
					if(fName.contains(iconFileName))
					{
						try	{

							saveIcon(entry);
							retVal = this.uuid;
							break;

						} catch (Exception e) {

							Logger.exception(e);
						}
					}
				}	
			}
		
		return retVal;
	}
	
	/**
	 * This method will extract file name from given file path
	 * @param appIconPath
	 * @return filename
	 */
	private String getFileNameFromPath(String appIconPath)
	{
		String[] pathEntries = appIconPath.split("/");
		int noOfEntriesInPath = pathEntries.length;
		return pathEntries[noOfEntriesInPath-1];
	}
	
	/**
	 * This method will parse aatp response and extract bundle id of the app.
	 * @return app bundle id
	 */
	private String getAppBundleId()
	{
		String retVal = null;
		try {
			
			String bundleId = null;
			int start = response.indexOf("name='") + "name='".length();
			int end = response.indexOf("'", start);
			bundleId = response.substring(start, end);

			if(bundleId != null)
				return bundleId;
	
		} catch (Exception e) {

			Logger.exception(e);
		}
		return retVal;
	}
	
	/**
	 * This method will parse aatp response and extract sdkVersion of the app.
	 * @return app sdkVersion
	 */
	private String getAppSdkVersion()
	{
		String retVal = null;
		try {
			
			String sdkVersion = null;
			int start = response.indexOf("sdkVersion:'") + "sdkVersion:'".length();
			int end = response.indexOf("'", start);
			sdkVersion = response.substring(start, end);

			if(sdkVersion != null)
				return sdkVersion;
	
		} catch (Exception e) {

			Logger.exception(e);
		}
		return retVal;
	}
	
	/**
	 * This method will parse aatp response and extract icon path of the app.
	 * @return icon path
	 */
	private String getAppIconPath()
	{
		String retVal = null;
		try {
			
			String iconUrl = null;
			int start = response.indexOf("application-icon") + "application-icon".length();
			int end = response.indexOf(".png'") + ".png".length();
			if (start > "application-icon".length() && end > ".png".length()) {
				
				String tmp = response.substring(start, end);
				start = tmp.indexOf(":'") + ":'".length();
				end = tmp.length();
				iconUrl = tmp.substring(start, end);
			}

			if(iconUrl != null)
				retVal = iconUrl;

		} catch (Exception e) {

			Logger.exception(e);
		}

		return retVal;
	}
}
