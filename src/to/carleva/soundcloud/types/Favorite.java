package to.carleva.soundcloud.types;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A class representing a Favorite item.
 * 
 * The class implements the Builder pattern, and currently supports only the SounCloud user 
 * favorite item JSON string as build option.
 * 
 * @see <a href="https://developers.soundcloud.com/docs/api/reference">
 *              https://developers.soundcloud.com/docs/api/reference</a>
 *  
 * @author Andrea Carlevato
 * 
 */
public class Favorite implements Serializable {

    private static final long serialVersionUID = 2126472291122776147L;
    
    private final int mId;
    private final String mType;
    private final String mTitle;
    private final String mDescription;
    private final String mGenre;
    private final int mReleaseYear;
    private final int mPlaybackCount;
    private final int mFavoritingsCount;
    private final String mAuthorUserName;

    /**
     * Creates and returns a new instance, by parsing the given JSON
     *      
     * @param jsonObject JSON string representing the favorite
     * @return a new Favorite instance, created from the supplied JSON
     * @throws JSONException if the parsing operation failed
     */
    public static Favorite buildFromJson(String jsonObject) throws JSONException {

        JSONObject jObj = new JSONObject(jsonObject);
        
        // resolve null json values resolved as "null"
        String kind = jObj.optString("kind", "");
        kind = eraseNullValue(kind);
        String title = jObj.optString("title", "");
        title = eraseNullValue(title);
        String description = jObj.optString("description", "");
        description = eraseNullValue(description);
        String genre = jObj.optString("genre", "");
        genre = eraseNullValue(genre);
        String author = jObj.getJSONObject("user").optString("username", "");
        author = eraseNullValue(author);
        
        return new Favorite(jObj.getInt("id"), kind, title, description, genre,
                jObj.optInt("release_year", -1), jObj.optInt("playback_count", -1),
                jObj.optInt("favoritings_count", -1), author);
    }

    /**
     * Getter for the favorite ID
     *      
     * @return this Favorite instance ID
     */
    public int getId() {
        return mId;
    }

    /**
     * Getter for the favorite type
     *      
     * @return this Favorite instance type. An empty String is returned if not available.
     */
    public String getType() {
        return mType;
    }

    /**
     * Getter for the favorite title
     *      
     * @return this Favorite instance title. An empty String is returned if not available.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Getter for the favorite description
     *      
     * @return this Favorite instance description. An empty String is returned if not available.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Getter for the Favorite genre
     *      
     * @return this Favorite instance genre. An empty String is returned if not available.
     */
    public String getGenre() {
        return mGenre;
    }

    /**
     * Getter for the favorite release year
     *      
     * @return this Favorite release year. -1 is returned if the information is not available.
     */
    public int getReleaseYear() {
        return mReleaseYear;
    }
    
    /**
     * Getter for the favorite author user name
     *      
     * @return this Favorite author user name. An empty String is returned if not available.
     */
    public String getAuthorUserName() {
        return mAuthorUserName;
    }

    /**
     * Getter for the favorite playback count.
     *      
     * @return this Favorite playback count. -1 is returned if the information is not available.
     */
    public int getPlaybackCount() {
        return mPlaybackCount;
    }
    
    /**
     * Getter for the favorite favoritings count.
     *      
     * @return this Favorite favoritings count. -1 is returned if the information is not available.
     */
    public int getFavoritingsCount() {
        return mFavoritingsCount;
    }
    
    /**     
     * @return true if the 2 Favorite instance are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
    	if (obj == null)
    		return false;
    	if (obj == this)
    		return true;
         if (!(obj instanceof Favorite))
             return false;

         Favorite rhs = (Favorite) obj;
         return new EqualsBuilder().
             append(mId, rhs.mId).
             append(mType, rhs.mType).
             append(mTitle, rhs.mTitle).
             append(mDescription, rhs.mDescription).
             append(mGenre, rhs.mGenre).
             append(mReleaseYear, rhs.mReleaseYear).
             append(mPlaybackCount, rhs.mPlaybackCount).
             append(mFavoritingsCount, rhs.mFavoritingsCount).
             append(mAuthorUserName, rhs.mAuthorUserName).
             isEquals();
    }

    /**     
     * @return an hash code for this Favorite instance
     */
    @Override
    public int hashCode() {
    	return new HashCodeBuilder(66, 18).
    		append(mId).
    		append(mType).
            append(mTitle).
            append(mDescription).
            append(mGenre).
            append(mReleaseYear).
            append(mReleaseYear).
            append(mPlaybackCount).
            append(mFavoritingsCount).
            append(mAuthorUserName).
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
    private Favorite(int id, String type, String title, String description, String genre,
            int releaseYear, int playbackCount, int favoritingsCount, String authorUserName) {
        mId = id;
        mType = type;
        mTitle = title;
        mDescription = description;
        mGenre = genre;
        mReleaseYear = releaseYear;
        mPlaybackCount = playbackCount;
        mFavoritingsCount = favoritingsCount;
        mAuthorUserName = authorUserName;
    }
}
