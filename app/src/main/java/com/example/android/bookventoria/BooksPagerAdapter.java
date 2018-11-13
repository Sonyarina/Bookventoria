package com.example.android.bookventoria;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * {@link BooksPagerAdapter} is a {@link FragmentPagerAdapter}. According to superclass javadoc:
 * Implementation of PagerAdapter that represents each page as a {@link Fragment} represents each
 * page as a {@link Fragment} that is persistently kept in the fragment manager as long as the
 * user can return to the page. Tips and guidance received from Udacity lessons, particularly the
 * miwok app lesson.
 */
public class BooksPagerAdapter extends FragmentPagerAdapter {

    /* Tag for the log messages */
    private static final String LOG_TAG = BooksPagerAdapter.class.getSimpleName();
    /**
     * The listener and interface below is used to attach addOnBackStackChangedListener listener
     * to the FragmentManager to be notified when the user presses the up or back button. This
     * feature is not really implemented at the moment but will potentially be needed in future
     * versions of this app.
     * Guidance found at article: Learn how to use the OnBackStackChangedListener to get the current Fragment
     * https://why-android.com/2016/03/29/learn-how-to-use-the-onbackstackchangedlistener
     */
    public MyOnBackStackChangedListener listener;

    /**
     * Context of the app
     */
    private Context mContext;

    /**
     * Required public constructor for BooksPagerAdapter
     *
     * @param context is the context of the app
     * @param fm      is the fragment manager that will keep each fragment's state in the adapter
     *                across swipes.
     */
    public BooksPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

        if (context instanceof MyOnBackStackChangedListener) {
            listener = (MyOnBackStackChangedListener) context;
        }
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position The position passed in from view pager
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new BookListFragment();
        } else if (position == 1) {
            return new GenresListFragment();
        } else {
            return new SuppliersFragment();
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;
    }

    /**
     * From Superclass doc: This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.bottom_nav_books);
        } else if (position == 1) {
            return mContext.getString(R.string.bottom_nav_genres);
        } else {
            return mContext.getString(R.string.bottom_nav_suppliers);
        }
    }

    /**
     * This interface is used to attach addOnBackStackChangedListener listener
     * to the FragmentManager to be notified when the user presses the up or back button.
     * Guidance found at article: Learn how to use the OnBackStackChangedListener to get the current Fragment
     * https://why-android.com/2016/03/29/learn-how-to-use-the-onbackstackchangedlistener
     */
    public interface MyOnBackStackChangedListener {
        void fragmentChanged();
    }
}
