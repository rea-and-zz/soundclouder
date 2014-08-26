package to.carleva.soundcloud.fragments;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import to.carleva.soundcloud.R;
import to.carleva.soundcloud.remote.DataProvider;
import to.carleva.soundcloud.remote.DataProvider.FavoritesUpdateObserver;
import to.carleva.soundcloud.types.Favorite;

/**
 * Fragment class for the SoundCloud app 'Favorites' section
 * 
 * @author Andrea Carlevato
 */
public class FavoritesFragment extends BaseFragment implements FavoritesUpdateObserver  {
    
    /**
     * Custom ArrayAdapter class to populate the Favorites list
     * 
     */
    public class FavoritesArrayAdapter extends ArrayAdapter<Favorite> {
        
        private final Context mContext;
        private final List<Favorite> mValues;
    
        public FavoritesArrayAdapter(Context context, List<Favorite> values) {
            super(context, R.layout.favorite_row, values);
            mContext = context;
            mValues = values;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            // fill row items
            View rowView = inflater.inflate(R.layout.favorite_row, parent, false);
            // title
            TextView titleLabelView = (TextView) rowView.findViewById(R.id.titleLabel);
            titleLabelView.setText(formatValueFourUi(mValues.get(position).getTitle()));
            // author
            TextView authorLabelView = (TextView) rowView.findViewById(R.id.authorLabel);
            authorLabelView.setText(formatValueFourUi(mValues.get(position).getAuthorUserName()));
            // genre/s
            TextView genreLabelView = (TextView) rowView.findViewById(R.id.genreLabel);
            genreLabelView.setText(formatValueFourUi(mValues.get(position).getGenre()));
            // playback count
            TextView countsLabelView = (TextView) rowView.findViewById(R.id.countsLabel);
            countsLabelView.setText("Views: "
                    + formatValueFourUi(mValues.get(position).getPlaybackCount())
                    + "     Likes: " 
                    + formatValueFourUi(mValues.get(position).getFavoritingsCount()));
            
            return rowView;
        } 
    }
    
    public static final int TITLE_STRING_ID = R.string.favorites_section;
    private View mRootView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_section_favorites, container, false);
        return mRootView;
    }
    
    @Override
    public void onStart()   {
        super.onStart();
        DataProvider.INSTANCE.subscribeToFavoritesUpdates(this);
    }
    
    @Override
    public void onStop()    {
        super.onStop();
        DataProvider.INSTANCE.unsubscribeToFavoritesUpdates(this);
    }
    
    @Override
    public void onFavoritesUpdate(final List<Favorite> favs)    {  
        // populate the list
        ListView lv = (ListView) mRootView.findViewById(R.id.favList);
        FavoritesArrayAdapter arrayAdapter = new FavoritesArrayAdapter(getActivity(), favs );
        lv.setAdapter(arrayAdapter); 
    }
}
