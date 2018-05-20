package com.mark.zumo.client.customer.view.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 18.
 */
public class MainBodyFragment extends Fragment {

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_body, container, false);
        ButterKnife.bind(this, view);
        inflateTabLayout();
        return view;
    }

    private void inflateTabLayout() {
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {

            }
        });
    }
}
