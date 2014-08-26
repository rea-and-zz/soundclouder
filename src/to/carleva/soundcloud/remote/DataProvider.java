package to.carleva.soundcloud.remote;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import to.carleva.soundcloud.archive.Archiver;
import to.carleva.soundcloud.archive.ArchiveException;
import to.carleva.soundcloud.archive.ArchiveRestoreException;
import to.carleva.soundcloud.archive.ArchiveSaveException;
import to.carleva.soundcloud.types.Favorite;
import to.carleva.soundcloud.types.User;


/**
 * DataProvider is the singleton class which allows its client (UI) to retrieve information 
 * about a SoundCloud user.
 * 
 * The class implement the Observer design pattern. DataProvider allows observers to subscribe to different types
 * of update streams (currently user profile and user favorites). Suppled data is retrieved by means of the
 * SoundCloud public API.
 * 
 * Please note that, as per requirements, DataProvider takes care of completing updates for all streams
 * supported, before notifying its observers.
 * 
 * DataProvider implements a caching strategy, to allow fast-recovery of previously retrieve data. This is
 * used to supply observer with the most recent version of the data, when no updated content is already
 * available from the online back-end.
 * 
 * DataProvider listens for connectivity status of the host device, and implements a logic which stops its
 * update loop when no connectivity is available, and immediately resume it when connectivity is restored.  
 * 
 * DataProvider also notifies its observer with different categories of error which may arise during
 * its update tasks. Current DataProvider behavior is, on error, to allow a back-off period and then continue 
 * with its regular update cycle.
 * 
 * Note that DataProvider will always notify its observer on the main thread. The class is entirely 
 * thread safe.
 * 
 * @author Andrea Carlevato
 */
public class DataProvider {
    
    /**
     * The base observer interface.
     * 
     * Defines methods to be notified of DataProvider update stream
     *   - being stopped
     *   - raising an error
     *   - successfully completing an update cycle
     */
    public interface UpdateObserver {
        void onUpdated();
        void onError(final Error error);
        void onStopped();
    }
    
    /**
     * The user profiles data updates observer interface.
     * 
     */
    public interface UserUpdateObserver extends UpdateObserver  {
        void onUserUpdate(final User user);
    }
    
    /**
     * The user favorites data updates observer interface.
     */
    public interface FavoritesUpdateObserver extends UpdateObserver {
        void onFavoritesUpdate(final List<Favorite> favorites);
    }
    
    /**
     * Enumeration for the different categories of error which DataProvider's observers can receive.
     */
    public enum Error   {
        NO_INTERNET_CONNECTION,
        UNABLE_TO_CONNECT,
        DATA_PARSING_ERROR,
        INTERNAL_ERROR
    }
    
    public static final DataProvider INSTANCE = new DataProvider();
    
    private static final String SERVICE_USER_RESOURCE 
        = "/users/reaand.json?client_id=b6d489be193bd1fcb3a22d76d6e5ce0f";
    private static final String SERVICE_FAVORITES_RESOURCE 
        = "/users/reaand/favorites.json?client_id=b6d489be193bd1fcb3a22d76d6e5ce0f";
    private static final int UPDATE_PERIOD_SECS = 60;
	private static final String TAG = "DataProvider";
    
    private final Set<UserUpdateObserver> userObservers = new HashSet<UserUpdateObserver>();
    private final Set<FavoritesUpdateObserver> favoritesObservers = new HashSet<FavoritesUpdateObserver>();
    private final Object mNetworkMonitorOject = new Object();
    private boolean mIsRunning;
    private boolean mNetworkIsAvailable = true;
    private User mUser;
    private List<Favorite> mFavorites;
    private Context mContext;
    private Configuration mConfiguration;
    
    /**
     * Initialize the DataProvider. This must be invoked before start.
     * 
     * @param context the current context
     * @param configuration a Configuration instance that wraps settings for DataProvider
     */
    synchronized public void init(final Context context, Configuration configuration)  {
    	mContext = context;
    	mConfiguration = configuration;
    	if (mConfiguration.getStopOnNoConnection()) {
    		context.registerReceiver(mConnReceiver, 
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    	}
    }
    
    /**
     * Release DataProvider resources. This allows cleanup on termination or on other app life-cycle points.
     * 
     */
    synchronized public void release() {
    	stop();
    	mContext.unregisterReceiver(mConnReceiver);
    	mContext = null;
        mConnReceiver = null;
    }
    
    /**
     * Start the update async loop, which will result in observers being regularly notified with 
     * updated data (or errors).
     */
    synchronized public void start()    {
        mIsRunning = true;
        startUpdates();
    }
    
    /**
     * Getter for the running state of the DataProvider
     * 
     * @return true if DataProvider is running, false otherwise.
     */
    synchronized public boolean isRunning() {
        return mIsRunning;
    }
    
    /**
     * Stop the update async loop.
     */
    synchronized public void stop() {
        mIsRunning = false;
    }
    
    /**
     * Subscribe the given user profile updates observer.
     * 
     * Note that DataProvider will always notify its observer on the main thread.
     * 
     * @param observer an observer of user profile updates
     */
    synchronized public void subscribeToUserUpdates(final UserUpdateObserver observer) {
        if (mUser != null)  {
            // if available, most recent user data are notified right away
            observer.onUserUpdate(mUser);
        }
        userObservers.add(observer);
    }
    
    /**
     * Subscribe the given user favorites updates observer.
     * 
     * Note that DataProvider will always notify its observer on the main thread.
     * 
     * @param observer an observer of user favorites updates
     */
    synchronized public void subscribeToFavoritesUpdates(final FavoritesUpdateObserver observer) {
        if (mFavorites != null) {
            // if available, most recent favorites data are notified right away
            observer.onFavoritesUpdate(mFavorites);
        }
        favoritesObservers.add(observer);
    }
    
    /**
     * Unsubscribe the given user profile updates observer.
     * 
     * @param observer an observer of user profile updates
     */
    synchronized public void unsubscribeToUserUpdates(final UserUpdateObserver observer) {
        userObservers.remove(observer);
    }
    
    /**
     * Unsubscribe the given user favorites updates observer.
     * 
     * @param observer an observer of user favorites updates
     */
    synchronized public void unsubscribeToFavoritesUpdates(final FavoritesUpdateObserver observer) {
        favoritesObservers.remove(observer);
    }
    
    /**
     * Asynchronously triggers the begging of the update loop.
     */
    private void startUpdates() {
        
        new Thread(new Runnable() {
            
            @Override
            public void run()  {
                
                // this is an outer looper, to catch errors, forward them, are re-schedule the inner loop (with delay)
                boolean startsAfterError = false;
                while (isRunning()) {
                    try {
                        // run the data update loop
                        runUnchecked(startsAfterError);
                    } catch (HttpException e) {
                        // An error retrieving the resource from remote server
                    	Log.e(TAG, "UNABLE_TO_CONNECT error being raised");
                        notifyError(Error.UNABLE_TO_CONNECT);
                        startsAfterError = true;
                    } catch (ArchiveException e) {
                        // An error archiving/de-archiving data
                    	Log.e(TAG, "INTERNAL_ERROR error being raised");
                        notifyError(Error.INTERNAL_ERROR);
                        startsAfterError = true;
                    } catch (JSONException e) {
                        // An error parsing responses to actual objects
                    	Log.e(TAG, "DATA_PARSING_ERROR error being raised");
                        notifyError(Error.DATA_PARSING_ERROR);
                        startsAfterError = true;
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        notifyError(Error.INTERNAL_ERROR);
                        startsAfterError = true;
                    }
                    // on error, the inner update loop is re-scheduled with a delay
                }
            }
            
            private void runUnchecked(boolean afterError) 
                    throws InterruptedException, HttpException, ArchiveException, JSONException {
                
                // if previous cycle terminated for an error, we wait the usual period before re-trying
                if (afterError) {
                    Thread.sleep(UPDATE_PERIOD_SECS * 1000);
                }
                
                // try to restore the users and favorites from cache
                if (mConfiguration.getCacheDataEnabled() && restoreState() && !afterError) {
                    // cached data has been restored, notify observers right away with this
                    // network updates will follow (if possible)
                    notifyUpdate();
                }
                
                while (isRunning()) {
                    
                    // check availability of connectivity
                    synchronized(mNetworkMonitorOject) {
                        if (!mNetworkIsAvailable)   {
                            // an error due to lack of connectivity is notified
                            // on the first run, on after a transition available -> unavailable
                            notifyError(Error.NO_INTERNET_CONNECTION);
                            // wait for connectivity to get available
                            mNetworkMonitorOject.wait();
                        }
                    }
                    
                    // setup a 2-thread executor, user profile and favorites jobs will be submitted on it
                    ExecutorService executor = Executors.newFixedThreadPool(2);
                    Future<User> resultUser = executor.submit(new Callable<User>() {            
                        @Override
                        public User call() throws HttpException, JSONException {
                            
                            // get user data
                            String jsonResponse = HttpUtils.readFromUrl(mConfiguration.getApiHostingServer() 
                            		+ SERVICE_USER_RESOURCE);
                            
                            // parse the json
                            User newUser = User.buildFromJson(jsonResponse);

                            return newUser;
                        }   
                    });
                    Future<List<Favorite>> resultFavs = executor.submit(new Callable<List<Favorite>>() {            
                        @Override
                        public List<Favorite> call() throws HttpException, JSONException {

                            // get favorites data
                            String jsonResponse = HttpUtils.readFromUrl(mConfiguration.getApiHostingServer() 
                                    		+ SERVICE_FAVORITES_RESOURCE);
                            // parse the json
                            JSONArray favoritesJson = new JSONArray(jsonResponse);
                            List<Favorite> newFavorites = new ArrayList<Favorite>();
                            for (int i=0;i<favoritesJson.length();i++){                        
                                JSONObject e = favoritesJson.getJSONObject(i);
                                newFavorites.add(Favorite.buildFromJson(e.toString()));
                            }

                            return newFavorites;
                        }   
                    });
                    
                    try {
                        mUser = resultUser.get();
                        mFavorites = resultFavs.get();
                    } catch (ExecutionException e) {
                        // re-throw the cause as original exception, to allow error forwarding 
                        try {
                        	Log.e(TAG, "Error getting Futures value: " + e.toString());
                            throw e.getCause();
                        } catch (HttpException e2) {
                            throw e2;
                        } catch (JSONException e2) {
                            throw e2;
                        } catch (Throwable t) {
                            // no other exceptions are thrown by Collables, we can silent this
                        }
                    }
                    
                    // all update task are done, if enabled, save data on cache
                    if (mConfiguration.getCacheDataEnabled()) 	{
                    	saveState();
                    }
                    
                    // notify observers
                	Log.e(TAG, "Notifying");

                    notifyUpdate();

                    // pause until it's time for next cycle
                    Thread.sleep(UPDATE_PERIOD_SECS * 1000);
                }
            }
        }           
        ).start();
    }
    
    /**
     * Notify observers with relevant updates. 
     * 
     * Note that all observers notifications are posted on the main thread.
     */
    synchronized private void notifyUpdate() {
    	
    	new Handler(Looper.getMainLooper()).post(new Runnable() {		
            @Override
            public void run() {
                // same object can subscribe as both user and favs, we want to send one single notification
                Set<UpdateObserver> dispatched = new HashSet<UpdateObserver>();
                for (UserUpdateObserver observer : userObservers ) {
                    observer.onUpdated();
                    observer.onUserUpdate(mUser);
                    dispatched.add(observer);
                }
                for (FavoritesUpdateObserver observer : favoritesObservers ) {
                    if (!dispatched.contains(observer)) {
                        observer.onUpdated();
                        dispatched.add(observer);
                    }
                    observer.onFavoritesUpdate(mFavorites);
                }
            }
        });
    }
    
    /**
     * Notify observers about an occurred error.
     * 
     * Note that all observers notifications are posted on the main thread.
     * 
     * @param error the error that occurred
     */
    synchronized private void notifyError(final Error error) {
    	
    	new Handler(Looper.getMainLooper()).post(new Runnable() {		
            @Override
            public void run() {
		        // same object can subscribe as both user and favs, we want to send one single notification
		        Set<UpdateObserver> dispatched = new HashSet<UpdateObserver>();
		        for (UserUpdateObserver observer : userObservers ) {
		            observer.onError(error);
		            dispatched.add(observer);
		        }   
		        for (FavoritesUpdateObserver observer : favoritesObservers ) {
		            if (!dispatched.contains(observer)) {
		                observer.onError(error);
		                dispatched.add(observer);
		            }
		        }
            }
    	});
    }
    
    /**
     * Archive current user data, if available.
     */
    private void saveState() throws ArchiveSaveException {      
        if (mUser != null && mFavorites != null)  {           
            Archiver.saveObject(mUser, "user", mContext.getFilesDir());
            Archiver.saveObjectList(mFavorites, "favorites", mContext.getFilesDir());
        }
    }
    
    /**
     * Un-archive saved user data, if possible
     * 
     * @return true if unarchiving completed successfully, false otherwise.
     */
    private boolean restoreState() throws ArchiveRestoreException {
        mUser = Archiver.restoreObject("user", mContext.getFilesDir());
        mFavorites = Archiver.restoreObjectList("favorites", mContext.getFilesDir());
        return mUser != null && mFavorites != null;
    }
    
    /**
     * A broadcast received to listed to changes to connectivity status.
     */
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            synchronized(mNetworkMonitorOject) {
                mNetworkIsAvailable = !noConnectivity;
                if (mNetworkIsAvailable)    {
                    mNetworkMonitorOject.notifyAll();
                }
            }
        }
    };
    
    /**
     * A Singleton, ctor is private.
     */
    private DataProvider() {
    }
}
