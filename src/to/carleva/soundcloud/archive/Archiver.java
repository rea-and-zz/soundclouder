package to.carleva.soundcloud.archive;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * An utility class that allow clients to archive and un-archive objects and lists 
 * of objects, using regular files as backend storage.
 * 
 * @author Andrea Carlevato
 */
public class Archiver {
	
	private static final String TAG = "Archiver";
    
    /**
     * Save to disk a given object, to a file identified by given tag
     *      
     * @param obj a Serializable object that needs to be saved
     * @param tag the tag for this object
     * @param localFilesDir an instance of File pointing to desired output location
     * @throws ArchiveSaveException if the operation failed for any reason
     */
    public static void saveObject(final Serializable obj, final String tag, final File localFilesDir) 
            throws ArchiveSaveException {
        
        try {
            FileOutputStream fos = new FileOutputStream(new File(localFilesDir, tag + ".cache"));
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
            os.close();
        } catch (IOException e) {
        	Log.e(TAG, "Error archiving object: " + e.toString());
            throw new ArchiveSaveException();
        }
    }
    
    /**
     * Save to disk a given list of objects, to a file identified by given tag
     *      
     * @param list a List of Serializable objects that needs to be saved
     * @param tag the tag for this object
     * @param localFilesDir an instance of File pointing to desired output location
     * @throws ArchiveSaveException if the operation failed for any reason
     */
    public static void saveObjectList(final List<? extends Serializable> list, final String tag, final File localFilesDir) 
        throws ArchiveSaveException   {
        
        try {
            FileOutputStream fos  = new FileOutputStream(new File(localFilesDir, tag + ".cache"));              
            ObjectOutputStream os = new ObjectOutputStream(fos);
            for (Object obj : list) {
                os.writeObject(obj);
            }
            os.close();
        } catch (IOException e) {
        	Log.e(TAG, "Error archiving list of objects: " + e.toString());
            throw new ArchiveSaveException();
        }
    }
    
    /**
     * Load an object from a file dump, indicated by the given tag
     *       
     * @param tag the tag for this object
     * @param localFilesDir an instance of File pointing to desired output location
     * @return an instance of T restored from file
     * @throws ArchiveRestoreException if the operation failed for any reason
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T restoreObject(final String tag, final File localFilesDir)
        throws ArchiveRestoreException   {
        
        T restoredObj = null;
        try {
            File cacheFile = new File(localFilesDir, tag + ".cache");
            if (cacheFile.exists())  {
                FileInputStream fis = null;
                ObjectInputStream is = null;
                
                // read
                fis = new FileInputStream(cacheFile);
                is = new ObjectInputStream(fis);
                restoredObj = (T)is.readObject();
                is.close();
            }
        } catch (Exception e) {
        	Log.e(TAG, "Error restoring object: " + e.toString());
            throw new ArchiveRestoreException();
        }
        
        return restoredObj;
    }
    
    /**
     * Load an list objects from a file dump, indicated by the given tag
     *       
     * @param tag the tag for this list of objects
     * @param localFilesDir an instance of File pointing to desired output location
     * @return an instance of List<T> restored from file
     * @throws ArchiveRestoreException if the operation failed for any reason
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> restoreObjectList(final String tag, final File localFilesDir)
        throws ArchiveRestoreException   {
        
        List<T> restoredObjList = null;
        try {
                            
            File cacheFile = new File(localFilesDir, tag + ".cache");
            if (cacheFile.exists())  {
                List<T> list = new ArrayList<T>();
                FileInputStream fis = new FileInputStream(cacheFile);
                ObjectInputStream is = new ObjectInputStream(fis);
                try {
                    Object nextObject = null; 
                    while ((nextObject = is.readObject()) != null) {
                        list.add((T) nextObject);
                    }
                } catch (EOFException e) {
                    // stream was over, catched silently
                }
                
                is.close();
                restoredObjList = list;
            }
        } catch (Exception e) {
        	Log.e(TAG, "Error restoring list of objects: " + e.toString());
            throw new ArchiveRestoreException();
        }
        
        return restoredObjList;
    }
}
