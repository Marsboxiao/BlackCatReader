package com.mars.blackcat.ui.adapter;

import android.content.Context;
import android.view.View;

import com.mars.blackcat.R;
import com.mars.blackcat.bean.CategoryList;
import com.mars.blackcat.common.OnRvItemClickListener;
import com.mars.blackcat.view.easyadapter.recyclerview.EasyRVAdapter;
import com.mars.blackcat.view.easyadapter.recyclerview.EasyRVHolder;

import java.util.List;

/**
 * @author lfh.
 * @date 16/8/30.
 */
public class TopCategoryListAdapter extends EasyRVAdapter<CategoryList.MaleBean> {
    private OnRvItemClickListener itemClickListener;

    public TopCategoryListAdapter(Context context, List<CategoryList.MaleBean> list, OnRvItemClickListener listener) {
        super(context, list, R.layout.item_top_category_list);
        this.itemClickListener = listener;
    }

    @Override
    protected void onBindData(final EasyRVHolder holder, final int position, final CategoryList.MaleBean item) {
        holder.setText(R.id.tvName, item.name)
                .setText(R.id.tvBookCount, String.format(mContext.getString(R.string
                        .category_book_count), item.bookCount));

        holder.setOnItemViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.getItemView(), position, item);
            }
        });
    }

}
