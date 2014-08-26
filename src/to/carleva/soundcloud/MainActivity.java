package to.carleva.soundcloud;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import to.carleva.soundcloud.fragments.AccountFragment;
import to.carleva.soundcloud.fragments.BaseFragment;
import to.carleva.soundcloud.fragments.FavoritesFragment;
import to.carleva.soundcloud.remote.Configuration;
import to.carleva.soundcloud.remote.DataProvider;

/**
 * Main activity class for the SoundCloud app 
 * 
 * @author Andrea Carlevato
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    
    AppSectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the adapter for the pager
        mSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), this);

        // setup action bar
        final ActionBar actionBar = getActionBar();
        
        // set navigation mode
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // disable home button
        actionBar.setHomeButtonEnabled(false);

        // configure the viewpager
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                
                actionBar.setSelectedNavigationItem(position);
            
                // set visibility to the fragment
                for (int i=0; i < mViewPager.getChildCount(); i++)  {
                    if (i != position)  {
                        BaseFragment appFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + i);
                        appFragment.setVisible(false);
                    }
                }
                BaseFragment appFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + position);
                appFragment.setVisible(true);
            }
        });

        // setup tabs
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
        
        // init data provider
        DataProvider.INSTANCE.init(this, new Configuration());     
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // kick off data update
        DataProvider.INSTANCE.start();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        // stop data update
        DataProvider.INSTANCE.stop();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // release DataProvider resources
        DataProvider.INSTANCE.release();
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        
        private Context mContext;
        
        public AppSectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    FavoritesFragment favoritesFragment = new FavoritesFragment();
                    favoritesFragment.setVisible(true);
                    return favoritesFragment;          
                case 1:
                    return new AccountFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)  {
                return (String) mContext.getResources().getString(FavoritesFragment.TITLE_STRING_ID);
            } else {
                return (String) mContext.getResources().getString(AccountFragment.TITLE_STRING_ID);
            }
        }
    }
}
