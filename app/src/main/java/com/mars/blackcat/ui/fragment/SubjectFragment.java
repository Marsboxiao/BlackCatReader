package com.mars.blackcat.ui.fragment;

import android.os.Bundle;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.mars.blackcat.R;
import com.mars.blackcat.base.BaseRVFragment;
import com.mars.blackcat.bean.BookLists;
import com.mars.blackcat.bean.other.TagEvent;
import com.mars.blackcat.component.AppComponent;
import com.mars.blackcat.component.DaggerFindComponent;
import com.mars.blackcat.manager.SettingManager;
import com.mars.blackcat.ui.activity.SubjectBookListDetailActivity;
import com.mars.blackcat.ui.contract.SubjectFragmentContract;
import com.mars.blackcat.ui.easyadapter.SubjectBookListAdapter;
import com.mars.blackcat.ui.presenter.SubjectFragmentPresenter;

import java.util.List;

/**
 * 主题书单
 *
 * @author yuyh.
 * @date 16/9/1.
 */
public class SubjectFragment extends BaseRVFragment<SubjectFragmentPresenter, BookLists.BookListsBean> implements SubjectFragmentContract.View {

    public final static String BUNDLE_TAG = "tag";
    public final static String BUNDLE_TAB = "tab";

    public String currendTag;
    public int currentTab;

    public String duration = "";
    public String sort = "";

    public static SubjectFragment newInstance(String tag, int tab) {
        SubjectFragment fragment = new SubjectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TAG, tag);
        bundle.putInt(BUNDLE_TAB, tab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.common_easy_recyclerview;
    }

    @Override
    public void initDatas() {
        RxBus.get().register(this);

        currentTab = getArguments().getInt(BUNDLE_TAB);
        switch (currentTab) {
            case 0:
                duration = "last-seven-days";
                sort = "collectorCount";
                break;
            case 1:
                duration = "all";
                sort = "created";
                break;
            case 2:
            default:
                duration = "all";
                sort = "collectorCount";
                break;
        }
    }

    @Override
    public void configViews() {
        initAdapter(SubjectBookListAdapter.class, true, true);
        onRefresh();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFindComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showBookList(List<BookLists.BookListsBean> bookLists, boolean isRefresh) {
        if (isRefresh) {
            mAdapter.clear();
            start = 0;
        }
        mAdapter.addAll(bookLists);
        start = start + bookLists.size();
    }

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Subscribe(thread= EventThread.MAIN_THREAD)
    public void initCategoryList(TagEvent event) {
        currendTag = event.tag;
        if (getUserVisibleHint()) {
            mPresenter.getBookLists(duration, sort, 0, limit, currendTag, SettingManager.getInstance().getUserChooseSex());
        }
    }

    @Override
    public void onDestroyView() {
        RxBus.get().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onItemClick(int position) {
        SubjectBookListDetailActivity.startActivity(activity, mAdapter.getItem(position));
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getBookLists(duration, sort, 0, limit, currendTag, SettingManager.getInstance().getUserChooseSex());
    }

    @Override
    public void onLoadMore() {
        mPresenter.getBookLists(duration, sort, start, limit, currendTag, SettingManager.getInstance().getUserChooseSex());
    }
}
