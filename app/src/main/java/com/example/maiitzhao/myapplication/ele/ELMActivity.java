package com.example.maiitzhao.myapplication.ele;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.BaseActivity;

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
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setTitle("饿了么");
        setSupportActionBar(toolbar);
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
}
