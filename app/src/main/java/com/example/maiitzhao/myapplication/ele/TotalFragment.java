package com.example.maiitzhao.myapplication.ele;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.MessageEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TotalFragment extends Fragment {

    private RecyclerView recyclerView;
    private int position;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_total, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        smartRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TotalAdapter adapter = new TotalAdapter(getContext(), position == 0);
        recyclerView.setAdapter(adapter);

        StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);

//        StickyRecyclerHeadersTouchListener touchListener =
//                new StickyRecyclerHeadersTouchListener(recyclerView, headersDecor);
//        touchListener.setOnHeaderClickListener(
//                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
//                    @Override
//                    public void onHeaderClick(View header, int position, long headerId) {
//                        CommonUtil.showToastShort( "Header position: " + position + ", id: " + headerId);
//                    }
//                });
//        recyclerView.addOnItemTouchListener(touchListener);
        smartRefreshLayout.setPrimaryColorsId(R.color.colorPrimary);//全局设置主题颜色
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                smartRefreshLayout.finishRefresh(1500);  //模拟数据刷新
            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                smartRefreshLayout.finishLoadmore(1500); //模拟加载更多
            }
        });
    }

    /**
     * 切换头部样式
     */
    public void changeHeader(RefreshHeader header) {
        if (smartRefreshLayout != null) {
            smartRefreshLayout.setRefreshHeader(header);
        }
    }

    /**
     * 切换脚部样式
     */
    public void changeFooter(RefreshFooter footer) {
        if (smartRefreshLayout != null) {
            smartRefreshLayout.setRefreshFooter(footer);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.CHANGE_STYPE) {
            Object data = event.getData();
            if (data instanceof RefreshHeader) {
                changeHeader((RefreshHeader) data);
            } else if (data instanceof RefreshFooter) {
                changeFooter((RefreshFooter) data);
            }
        }
    }
}
