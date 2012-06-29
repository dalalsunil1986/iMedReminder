package com.cryptic.imed.util.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.cryptic.imed.R;

public class ViewUtils {
    /**
     * author: DougW, Nex
     * http://stackoverflow.com/questions/3495890/how-can-i-put-a-listview-into-a-scrollview-without-it-collapsing
     * http://nex-otaku-en.blogspot.com/2010/12/android-put-listview-in-scrollview.html
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void addListFragmentToLayout(FragmentActivity fragmentActivity, LinearLayout detailsContainer,
                                               Fragment listFragment, Fragment detailsFragment) {
        FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();

        if (detailsContainer != null) {     //dual pane
            fragmentTransaction.add(R.id.details_container, detailsFragment);
        }

        fragmentTransaction.add(R.id.list_container, listFragment);

        fragmentTransaction.commit();
    }
}
