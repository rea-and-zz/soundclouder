package to.carleva.soundcloud.fragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import to.carleva.soundcloud.R;
import to.carleva.soundcloud.remote.DataProvider.Error;
import to.carleva.soundcloud.remote.DataProvider.UpdateObserver;

/**
 * Base fragment class for the SoundCloud app 
 * 
 * @author Andrea Carlevato
 */
public abstract class BaseFragment extends Fragment implements UpdateObserver   {
    
    private boolean mIsVisible = false;
    private boolean mWaitingFirstUpdate = true;
    private ProgressDialog mProgressDialog;
    
    @Override
    public void onStart()   {
        super.onStart();
        // if the fragment is visible and no data was presented yet (by this instance), show a spinner
        if (this.getVisible() && mWaitingFirstUpdate)   {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle((String) getActivity().getResources().getString(R.string.loading_spinner_title));
            mProgressDialog.setMessage((String) getActivity().getResources().getString(R.string.loading_spinner_text));
            mProgressDialog.show();
        }
    }
    
    @Override
    public void onUpdated() {
        mWaitingFirstUpdate = false;
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    
    @Override
    public void onError(final Error error)  {
        if (getVisible())  {
            Toast.makeText(getActivity(), getTextforError(error), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onStopped() {
    }
    
    /**
     * Mark this fragment as visible in the page viewer
     *      
     * @param isVisible boolean indicating if the current fragment is visible or not 
     */
    public void setVisible(final boolean isVisible) {
        mIsVisible = isVisible;
    }
    
    /**
     * Formats a SoundCloud data item value into a format that is friendly for the app UI.
     *
     * @param item a generic SoundCloud data item to be formatted.
     * @return the formatted String which can be used in the app UI.
     */
    protected <T> String formatValueFourUi(T item)  {
        String stringValue = item.toString();
        return (stringValue.isEmpty() || stringValue.equals("-1")) ? 
                (String) getActivity().getResources().getString(R.string.not_available) :
                    stringValue;
    }
    
    /**
     * Getter for visible state of the fragment. 
     *
     * @return boolean indicating if the fragment is visible (true) or not (false).
     */
    private Boolean getVisible()    {
        return mIsVisible;
    }
    
    /**
     * Formats an error message for the UI, based on the receiver error type.
     *
     * @param error an instance of the Error class.
     * @return the formatted String which can be presented by the UI.
     */
    private String getTextforError(final Error error)   {
        int errorMsgId;
        switch (error)  {
            case NO_INTERNET_CONNECTION:
                errorMsgId = R.string.no_connectivity_error_msg;
                break;
            case UNABLE_TO_CONNECT:
                errorMsgId = R.string.connection_error_msg;
                break;
            case DATA_PARSING_ERROR:
                errorMsgId = R.string.format_error_msg;
                break;
            case INTERNAL_ERROR:
                errorMsgId = R.string.internal_error_msg;
                break;
            default:
                errorMsgId = R.string.connection_error_msg;
                break;
        }
        
        return (String) getActivity().getResources().getString(errorMsgId) +
                    " " + (String) getActivity().getResources().getString(R.string.we_ll_try_again_soon);
    }
}
