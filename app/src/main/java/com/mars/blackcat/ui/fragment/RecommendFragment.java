package com.mars.blackcat.ui.fragment;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.mars.blackcat.R;
import com.mars.blackcat.base.BaseRVFragment;
import com.mars.blackcat.bean.BookToc;
import com.mars.blackcat.bean.Recommend;
import com.mars.blackcat.bean.other.DownloadMessage;
import com.mars.blackcat.bean.other.DownloadQueue;
import com.mars.blackcat.bean.other.RefreshCollectionListEvent;
import com.mars.blackcat.component.AppComponent;
import com.mars.blackcat.component.DaggerMainComponent;
import com.mars.blackcat.manager.CollectionsManager;
import com.mars.blackcat.service.DownloadBookService;
import com.mars.blackcat.ui.activity.BookDetailActivity;
import com.mars.blackcat.ui.activity.MainActivity;
import com.mars.blackcat.ui.activity.ReadActivity;
import com.mars.blackcat.ui.contract.RecommendContract;
import com.mars.blackcat.ui.easyadapter.RecommendAdapter;
import com.mars.blackcat.ui.presenter.RecommendPresenter;
import com.mars.blackcat.utils.ToastUtils;
import com.mars.blackcat.view.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecommendFragment extends BaseRVFragment<RecommendPresenter, Recommend
        .RecommendBooks> implements RecommendContract.View, RecyclerArrayAdapter
        .OnItemLongClickListener {

    @Bind(R.id.llBatchManagement)
    LinearLayout llBatchManagement;
    @Bind(R.id.tvSelectAll)
    TextView tvSelectAll;
    @Bind(R.id.tvDelete)
    TextView tvDelete;
//    @Bind(R.id.guanggao)
//    TextView tvTest;

    private boolean isHasCollections = false;
    private boolean isSelectAll = false;

    private List<BookToc.mixToc.Chapters> chaptersList = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_recommend;
    }

    @Override
    public void initDatas() {
        RxBus.get().register(this);
    }

    @Override
    public void configViews() {
        initAdapter(RecommendAdapter.class, true, false);
        mAdapter.setOnItemLongClickListener(this);
//        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
//            @Override
//            public View onCreateView(ViewGroup parent) {
//                View headerView =LayoutInflater.from(activity).inflate(R.layout.item_ad,parent,
// false);
//                return headerView;
//            }
//
//            @Override
//            public void onBindView(View headerView) {
//                ToastUtils.getToast("都告诉你了，我是个广告，还点我！！！", Toast.LENGTH_SHORT);
//            }
//        });
        mAdapter.addFooter(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                View footerView = LayoutInflater.from(activity).inflate(R.layout.foot_view_shelf,
                        parent, false);
                return footerView;
            }

            @Override
            public void onBindView(View headerView) {
                headerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) activity).setCurrentItem(2);
                    }
                });
            }
        });
        onRefresh();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showRecommendList(List<Recommend.RecommendBooks> list) {
        mAdapter.clear();
        mAdapter.addAll(list);
    }

    @Override
    public void showBookToc(String bookId, List<BookToc.mixToc.Chapters> list) {
        chaptersList.clear();
        chaptersList.addAll(list);
        DownloadBookService.post(new DownloadQueue(bookId, list, 1, list.size()));
        dismissDialog();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD)
    public void downloadMessage(final DownloadMessage msg) {
        mRecyclerView.setTipViewText(msg.message);
        if (msg.isComplete) {
            mRecyclerView.hideTipView(2200);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (isVisible(llBatchManagement)) //批量管理时，屏蔽点击事件
            return;
        ReadActivity.startActivity(activity, mAdapter.getItem(position));
    }

    @Override
    public boolean onItemLongClick(int position) {
        //没有收藏时，屏蔽长按事件，因为置顶和删除等功能不好实现
        //批量管理时，屏蔽长按事件
        if (!isHasCollections || isVisible(llBatchManagement)) return false;
        showLongClickDialog(position);
        return false;
    }

    /**
     * 显示长按对话框
     *
     * @param position
     */
    private void showLongClickDialog(final int position) {
        new AlertDialog.Builder(activity)
                .setTitle(mAdapter.getItem(position).title)
                .setItems(getResources().getStringArray(R.array.recommend_item_long_click_choice),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //置顶
                                        CollectionsManager.getInstance().top(mAdapter.getItem
                                                (position)._id);
                                        onRefresh();
                                        break;
                                    case 1:
                                        //书籍详情
                                        BookDetailActivity.startActivity(activity,
                                                mAdapter.getItem(position)._id);
                                        break;
                                    case 2:
                                        //移入养肥区
                                        ToastUtils.showToast("正在拼命开发中...");
                                        break;
                                    case 3:
                                        //缓存全本
                                        showDialog();
                                        mPresenter.getTocList(mAdapter.getItem(position)._id);
                                        break;
                                    case 4:
                                        //删除
                                        List<Recommend.RecommendBooks> removeList = new
                                                ArrayList<>();
                                        removeList.add(mAdapter.getItem(position));
                                        showDeleteCacheDialog(removeList);
                                        break;
                                    case 5:
                                        //批量管理
                                        showBatchManagementLayout();
                                        break;
                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(null, null)
                .create().show();
    }

    /**
     * 显示删除本地缓存对话框
     *
     * @param removeList
     */
    private void showDeleteCacheDialog(final List<Recommend.RecommendBooks> removeList) {
        final boolean selected[] = {true};
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.remove_selected_book))
                .setMultiChoiceItems(new String[]{activity.getString(R.string.delete_local_cache)
                        }, selected,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean
                                    isChecked) {
                                selected[0] = isChecked;
                            }
                        })
                .setPositiveButton(activity.getString(R.string.confirm), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new AsyncTask<String, String, String>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                showDialog();
                            }

                            @Override
                            protected String doInBackground(String... params) {
                                CollectionsManager.getInstance().removeSome(removeList,
                                        selected[0]);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                ToastUtils.showSingleToast("成功移除书籍");
                                for (Recommend.RecommendBooks bean : removeList) {
                                    mAdapter.remove(bean);
                                }
                                if (mAdapter.getCount() == 0) {
                                    //没有收藏时，刷新页面
                                    onRefresh();
                                }
                                if (isVisible(llBatchManagement)) {
                                    //批量管理完成后，隐藏批量管理布局并刷新页面
                                    goneBatchManagementAndRefreshUI();
                                }
                                hideDialog();
                            }
                        }.execute();

                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), null)
                .create().show();
    }

    /**
     * 隐藏批量管理布局并刷新页面
     */
    public void goneBatchManagementAndRefreshUI() {
        if (mAdapter == null) return;
        gone(llBatchManagement);
        for (Recommend.RecommendBooks bean :
                mAdapter.getAllData()) {
            bean.showCheckBox = false;
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 显示批量管理布局
     */
    private void showBatchManagementLayout() {
        visible(llBatchManagement);
        for (Recommend.RecommendBooks bean : mAdapter.getAllData()) {
            bean.showCheckBox = true;
        }
        mAdapter.notifyDataSetChanged();
    }


    @OnClick(R.id.tvSelectAll)
    public void selectAll() {
        isSelectAll = !isSelectAll;
        tvSelectAll.setText(isSelectAll ? activity.getString(R.string.cancel_selected_all) :
                activity.getString(R.string.selected_all));
        for (Recommend.RecommendBooks bean :
                mAdapter.getAllData()) {
            bean.isSeleted = isSelectAll;
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tvDelete)
    public void delete() {
        List<Recommend.RecommendBooks> removeList = new ArrayList<>();
        for (Recommend.RecommendBooks bean :
                mAdapter.getAllData()) {
            if (bean.isSeleted) {
                removeList.add(bean);
            }
        }
        if (removeList.isEmpty()) {
            ToastUtils.showToast(activity.getString(R.string.has_not_selected_delete_book));
        } else {
            showDeleteCacheDialog(removeList);
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        gone(llBatchManagement);
        List<Recommend.RecommendBooks> data = CollectionsManager.getInstance().getCollectionList();
        if (data != null && !data.isEmpty()) {
            //有收藏时，只显示收藏
            isHasCollections = true;
            mAdapter.clear();
            mAdapter.addAll(data);
            mRecyclerView.setRefreshing(false);
        } else {
            //没有收藏时，显示推荐
            isHasCollections = false;
            mPresenter.getRecommendList();
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD)
    public void RefreshCollectionList(RefreshCollectionListEvent event) {
        mRecyclerView.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void showError() {
        loaddingError();
        dismissDialog();
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint()) {
            goneBatchManagementAndRefreshUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //这样监听返回键有个缺点就是没有拦截Activity的返回监听，如果有更优方案可以改掉
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (isVisible(llBatchManagement)) {
                        goneBatchManagementAndRefreshUI();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
        ButterKnife.unbind(this);
    }

    private boolean isForeground() {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (MainActivity.class.getName().contains(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

    public static RecommendFragment getInstance(String title) {
        RecommendFragment sf = new RecommendFragment();
        return sf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
