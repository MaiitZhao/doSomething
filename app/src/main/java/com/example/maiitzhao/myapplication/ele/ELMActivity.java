package com.example.maiitzhao.myapplication.ele;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.BaseActivity;
import com.example.maiitzhao.myapplication.base.MessageEvent;
import com.scwang.smartrefresh.header.FlyRefreshHeader;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * 类描述：仿饿了吗首界面
 * 创建人：zpxiang
 * 创建时间：2019/1/9
 * 修改人：
 * 修改时间：
 */
public class ELMActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private String[] titles = {"全部", "卤味", "麻辣小吃", "精品蛋糕", "水果", "咖啡", "奶茶果汁", "面包", "牛奶"};

    @Override
    protected int initContentView() {
        return R.layout.activity_elm;
    }

    @Override
    protected void initView() {
        initToolbar();

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    toolbar.setTitle("急了么");
                } else {
                    toolbar.setTitle(tab.getText());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setTitle("急了么");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_header_style, menu);
        return true;
    }

    @Override
    @Subscribe
    public boolean onOptionsItemSelected(MenuItem item) {
        MessageEvent event = new MessageEvent();
        event.setType(MessageEvent.CHANGE_STYPE);
        switch (item.getItemId()) {
            case R.id.type_header1:
                event.setData(new ClassicsHeader(this));
                break;
            case R.id.type_header2:
                event.setData(new BezierRadarHeader(this));
                break;
            case R.id.type_header3:
                event.setData(new FlyRefreshHeader(this));
                break;
            case R.id.type_footer4:
                event.setData(new TaurusHeader(this));
                break;

            case R.id.type_footer1:
                event.setData(new ClassicsFooter(this).setDrawableSize(20));
                break;
            case R.id.type_footer2:
                event.setData(new BallPulseFooter(this));
                break;
        }

        EventBus.getDefault().post(event);
        return true;
    }

    class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TotalFragment fragment = new TotalFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
