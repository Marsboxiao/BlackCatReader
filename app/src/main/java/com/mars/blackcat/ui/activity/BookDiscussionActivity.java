package com.mars.blackcat.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.mars.blackcat.R;
import com.mars.blackcat.base.BaseCommuniteActivity;
import com.mars.blackcat.component.AppComponent;
import com.mars.blackcat.ui.fragment.BookDiscussionFragment;
import com.mars.blackcat.view.SelectionLayout;

import java.util.List;

import butterknife.Bind;

/**
 * 综合讨论区
 */
public class BookDiscussionActivity extends BaseCommuniteActivity {

    private static final String INTENT_DIS = "isDis";

    public static void startActivity(Context context, boolean isDiscussion) {
        context.startActivity(new Intent(context, BookDiscussionActivity.class)
                .putExtra(INTENT_DIS, isDiscussion));
    }

    private boolean mIsDiscussion;

    @Bind(R.id.slOverall)
    SelectionLayout slOverall;

    @Override
    public int getLayoutId() {
        return R.layout.activity_community_book_discussion;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mIsDiscussion = getIntent().getBooleanExtra(INTENT_DIS, false);
        if (mIsDiscussion) {
            mCommonToolbar.setTitle("综合讨论区");
        } else {
            mCommonToolbar.setTitle("原创区");
        }
        mCommonToolbar.setNavigationIcon(R.mipmap.ab_back);
    }

    @Override
    public void initDatas() {
        super.initDatas();
    }

    @Override
    protected List<List<String>> getTabList() {
        return list1;
    }

    @Override
    public void configViews() {
        BookDiscussionFragment fragment = BookDiscussionFragment.newInstance(mIsDiscussion ? "ramble" : "original");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentCO, fragment).commit();
    }
}
