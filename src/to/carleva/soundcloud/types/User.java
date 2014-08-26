package to.carleva.soundcloud.types;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;
import org.json.JSONException;

/**
 /**
 * A class representing a User.
 * 
 * The class implements the Builder pattern, and currently supports only the SounCloud user 
 * profile item JSON string as build option.
 * 
 * @see <a href="https://developers.soundcloud.com/docs/api/reference">
 *              https://developers.soundcloud.com/docs/api/reference</a>
 *  
 * @author Andrea Carlevato
 * 
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 2126472251122556147L;

    private final int mId;
    private final String mUserName;
    private final String mProfileUrl;
    private final String mFirstName;
    private final String mLastName;
    private final String mFullName;
    private final String mCountry;
    private final String mCity;
    private final String mWebSite;
    private final Boolean mIsOnline;
    private final String mPlan;
    private final int mTrackCount;
    private final int mPlayListsCount;
    private final int mFavoritesCount;
    private final int mFollowersCount;
    private final int mFollowingCount;

    /**
     * Creates and returns a new instance, by parsing the given JSON
     *      
     * @param jsonObject JSON string representing the user profile
     * @return a new User instance, created from the supplied JSON
     * @throws JSONException if the parsing operation failed
     */
    public static User buildFromJson(String jsonObject) throws JSONException     {

        JSONObject jObj = new JSONObject(jsonObject);
        
        // resolve null json values resolved as "null"
        String userName = jObj.optString("username", "");
        userName = eraseNullValue(userName);
        String profileUrl = jObj.optString("permalink_url", "");
        profileUrl = eraseNullValue(profileUrl);
        String firstName = jObj.optString("first_name", "");
        firstName = eraseNullValue(firstName);
        String lastName = jObj.optString("last_name", "");
        lastName = eraseNullValue(lastName);
        String fullName = jObj.optString("full_name", "");
        fullName = eraseNullValue(fullName);
        String country = jObj.optString("country", "");
        country = eraseNullValue(country);
        String city = jObj.optString("city", "");
        city = eraseNullValue(city);
        String website = jObj.optString("website", "");
        website = eraseNullValue(website);
        String plan = jObj.optString("plan", "");
        plan = eraseNullValue(plan);
        
        return new User(jObj.getInt("id"), userName, profileUrl, firstName, lastName,
        	fullName, country, city, website, jObj.optBoolean("online", false),
            plan, jObj.optInt("track_count", -1), jObj.optInt("playlist_count", -1),
            jObj.optInt("public_favorites_count", -1), jObj.optInt("followers_count", -1),
            jObj.optInt("followings_count", -1));
    }

    /**
     * Getter for the user ID.
     *      
     * @return this User instance ID.
     */
    public int getId() {
        return mId;
    }

    /**
     * Getter for the user name.
     *      
     * @return this User instance user name. An empty String is returned if not available.
     */
    public String getUserName() {
        return mUserName;
    }

    /**
     * Getter for the user profile URL.
     *      
     * @return this User instance us profile URL. An empty String is returned if not available.
     */
    public String getProfileUrl() {
        return mProfileUrl;
    }

    /**
     * Getter for the user first name.
     *      
     * @return this User instance first name. An empty String is returned if not available.
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Getter for the user last name.
     *      
     * @return this User instance last name. An empty String is returned if not available.
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Getter for the user full name.
     *      
     * @return this User instance full name. An empty String is returned if not available.
     */
    public String getFullName() {
        return mFullName;
    }

    /**
     * Getter for the user country name.
     *      
     * @return this User instance country name. An empty String is returned if not available.
     */
    public String getCountry() {
        return mCountry;
    }

    /**
     * Getter for the user city name.
     *      
     * @return this User instance city name. An empty String is returned if not available.
     */
    public String getCity() {
        return mCity;
    }

    /**
     * Getter for the user website URL.
     *      
     * @return this User instance website URL. An empty String is returned if not available.
     */
    public String getWebSite() {
        return mWebSite;
    }

    /**
     * Getter for the user first name.
     *      
     * @return this User instance first name. A default false value is returned if not available.
     */
    public Boolean getIsOnline() {
        return mIsOnline;
    }

    /**
     * Getter for the user plan name.
     *      
     * @return this User instance plan name. An empty String is returned if not available.
     */
    public String getPlan() {
        return mPlan;
    }

    /**
     * Getter for the user track count.
     *      
     * @return this User instance track count. -1 is returned if the information is not available.
     */
    public int getTrackCount() {
        return mTrackCount;
    }

    /**
     * Getter for the user playlists count.
     *      
     * @return this User instance playlists count. -1 is returned if the information is not available.
     */
    public int getPlayListsCount() {
        return mPlayListsCount;
    }

    /**
     * Getter for the user favorites count.
     *      
     * @return this User instance favorites count. -1 is returned if the information is not available.
     */
    public int getFavoritesCount() {
        return mFavoritesCount;
    }

    /**
     * Getter for the user followers count.
     *      
     * @return this User instance followers count. -1 is returned if the information is not available.
     */
    public int getFollowersCount() {
        return mFollowersCount;
    }

    /**
     * Getter for the user following count.
     *      
     * @return this User instance following count. -1 is returned if the information is not available.
     */
    public int getFollowingCount() {
        return mFollowingCount;
    }
    
    /**     
     * @return true if the 2 User instance are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
    	if (obj == null)
    		return false;
    	if (obj == this)
    		return true;
         if (!(obj instanceof User))
             return false;

         User rhs = (User) obj;
         return new EqualsBuilder().
             append(mId, rhs.mId).
             append(mUserName, rhs.mUserName).
             append(mProfileUrl, rhs.mProfileUrl).
             append(mFirstName, rhs.mFirstName).
             append(mLastName, rhs.mLastName).
             append(mFullName, rhs.mFullName).
             append(mCountry, rhs.mCountry).
             append(mCity, rhs.mCity).
             append(mWebSite, rhs.mWebSite).
             append(mIsOnline, rhs.mIsOnline).
             append(mPlan, rhs.mPlan).
             append(mTrackCount, rhs.mTrackCount).
             append(mPlayListsCount, rhs.mPlayListsCount).
             append(mFavoritesCount, rhs.mFavoritesCount).
             append(mFavoritesCount, rhs.mFavoritesCount).
             append(mFollowingCount, rhs.mFollowingCount).
             isEquals();
    }

    /**     
     * @return an hash code for this User instance
     */
    @Override
    public int hashCode() {
    	return new HashCodeBuilder(77, 32).
    		append(mId).
    		append(mUserName).
            append(mProfileUrl).
            append(mFirstName).
            append(mLastName).
            append(mFullName).
            append(mCountry).
            append(mCity).
            append(mWebSite).
            append(mIsOnline).
            append(mPlan).
            append(mTrackCount).
            append(mPlayListsCount).
            append(mFavoritesCount).
            append(mFavoritesCount).
            append(mFollowingCount).
            toHashCode();
    }
    
    /**   
     * Utility method to make sure the "null" strings from JSONObject are translated into ""
     *  
     * @param value original value
     * @return formatted value
     */
    static private String eraseNullValue(String value) {
    	 if (value.equals("null")) 
    		 value = "";
    	 
    	 return value;
    }
    
    /**
     * Builder pattern, ctor is private.
     */
    private User(int id, String userName, String profileUrl, String firstName, String lastName,
            String fullName, String country, String city, String webSite, Boolean isOnline,
            String plan, int trackCount, int playListsCount, int favoritesCount,
            int followersCount, int followingCount) {

       mId = id;
       mUserName = userName;
       mProfileUrl= profileUrl;
       mFirstName = firstName;
       mLastName = lastName;
       mFullName = fullName;
       mCountry = country;
       mCity = city;
       mWebSite = webSite;
       mIsOnline = isOnline;
       mPlan = plan;
       mTrackCount = trackCount;
       mPlayListsCount = playListsCount;
       mFavoritesCount = favoritesCount;
       mFollowersCount = followersCount;
       mFollowingCount = followingCount;
    }
}




