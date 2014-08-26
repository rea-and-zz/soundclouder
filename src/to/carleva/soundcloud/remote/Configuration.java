package to.carleva.soundcloud.remote;

import to.carleva.soundcloud.archive.ArchiveSaveException;

/**
 * A class to wrap the configuration set for the DataProvider
 * 
 * @author Andrea Carlevato
 */
public class Configuration {
	
	boolean mCacheDataEnabled = true;
	boolean mStopOnNoConnection = true;
	String mApiHostingServer = "http://api.soundcloud.com";
	
    /**
     * @returns true if this configuration will allow caching of data on disk. Default is true.
     */
	public boolean getCacheDataEnabled() {
		return mCacheDataEnabled;
	}
	
    /**
     * Sets if this configuration will allow caching of data on disk. Default is true.
     * 
     * @param enabled true if caching will be allowed.
     */
	public Configuration setCacheDataEnabled(boolean enabled) {
		mCacheDataEnabled = enabled;
		return this;
	}
	
    /**
     * @returns true if this configuration will allow the update activity to pause
     * when no connectivity is detected. Default is true.
     */
	public boolean getStopOnNoConnection() {
		return mStopOnNoConnection;
	}
	
    /**
     * Sets if this configuration will allow the update activity to pause when no 
     * connectivity is found. Default is true. Default is true.
     * 
     * @param enabled true if activity will pause when no connectivity is detected
     */
	public Configuration setStopOnNoConnection(boolean enabled) {
		mStopOnNoConnection = enabled;
		return this;
	}
	
    /**
     * @returns the hostname of the target server for this configuration
     */
	public String getApiHostingServer() {
		return mApiHostingServer;
	}
	
	/**
     * Sets the hostname of the service provider
     * 
     * @param apiHostingServer the url of the hostname
     */
	public Configuration setApiHostingServer(String apiHostingServer) {
		mApiHostingServer = apiHostingServer;
		return this;
	}	
}