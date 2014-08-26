package to.carleva.soundcloud.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import to.carleva.soundcloud.R;
import to.carleva.soundcloud.remote.DataProvider;
import to.carleva.soundcloud.remote.DataProvider.UserUpdateObserver;
import to.carleva.soundcloud.types.User;

/**
 * Fragment class for the SoundCloud app 'User' section
 * 
 * @author Andrea Carlevato
 */
public class AccountFragment extends BaseFragment implements UserUpdateObserver {
    
    public static final int TITLE_STRING_ID = R.string.account_section;
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_section_account, container, false);
        return mRootView;
    }
    
    @Override
    public void onStart()   {
        super.onStart();
        DataProvider.INSTANCE.subscribeToUserUpdates(this);
    }
    
    @Override
    public void onStop()    {
        super.onStop();
        DataProvider.INSTANCE.unsubscribeToUserUpdates(this);
    }
    
    @Override
    public void onUserUpdate(final User user)   {
        
        // user name
        ((TextView) mRootView.findViewById(R.id.userNameValue)).setText(user.getUserName());
        // country
        ((TextView) mRootView.findViewById(R.id.countryValue)).setText(user.getCountry());
        // city
        ((TextView) mRootView.findViewById(R.id.cityValue)).setText(user.getCity());
        // user profile url
        ((TextView) mRootView.findViewById(R.id.urlValue)).setText(user.getProfileUrl());
        // user tracks number
        ((TextView) mRootView.findViewById(R.id.userTracksValue)).setText(Integer.toString(user.getTrackCount()));
        // number of playlists
        ((TextView) mRootView.findViewById(R.id.userPlayLitsCountValue)).setText(Integer.toString(user.getPlayListsCount()));
        // number of likes
        ((TextView) mRootView.findViewById(R.id.userFavoritesCountValue)).setText(Integer.toString(user.getFavoritesCount()));
        // is online
        String onlineText = null;
        TextView onlineValueTextView = (TextView) mRootView.findViewById(R.id.userIsOnlineValue);
        if (user.getIsOnline()) {
            onlineText = (String) getActivity().getResources().getString(R.string.online_now);
            onlineValueTextView.setTextColor(getActivity().getResources().getColor(R.color.green));
        } else {
            onlineText = (String) getActivity().getResources().getString(R.string.not_online_now);
            onlineValueTextView.setTextColor(getActivity().getResources().getColor(R.color.black));
        }
        onlineValueTextView.setText(onlineText);            
    }
}
