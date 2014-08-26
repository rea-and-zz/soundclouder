package to.carleva.soundcloud.remote;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpException;

import android.util.Log;

/**
 * An utility class that wraps a simple HTTP GET call
 * 
 * @author Andrea Carlevato
 */
public class HttpUtils  {
	
	private static final String TAG = "HttpUtils";
	
    /**
     * Returns a string with the body of the HTTP GET response, for the given URL
     *      
     * @param url the URL of the resource to retrieve
     * @return a string containing the body of the response
     * @throws HttpException if the operation failed for any reason
     */
    public static String readFromUrl(final String url) throws HttpException {       

        try {
        	HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        	
        	urlConnection.addRequestProperty("Cache-Control", "no-cache");
        	urlConnection.addRequestProperty("Cache-Control", "max-age=0");
  
    		InputStream in = new BufferedInputStream(urlConnection.getInputStream());
    		StringBuilder sb = new StringBuilder();
            BufferedReader reader = 
                   new BufferedReader(new InputStreamReader(in), 65728);
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        	
            return sb.toString();
        } catch (IOException e) {
        	Log.e(TAG, "Error performing readFromUrl: " + e.toString());
            throw new HttpException();
        }
    }
    

}
