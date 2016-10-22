package com.mars.blackcat.ui.adapter;

import android.content.Context;

import com.mars.blackcat.R;
import com.mars.blackcat.view.easyadapter.abslistview.EasyLVAdapter;
import com.mars.blackcat.view.easyadapter.abslistview.EasyLVHolder;

import java.util.List;

/**
 * @author yuyh.
 * @date 16/8/6.
 */
public class AutoCompleteAdapter extends EasyLVAdapter<String> {

    public AutoCompleteAdapter(Context context, List<String> list) {
        super(context, list, R.layout.item_auto_complete_list);
    }

    @Override
    public void convert(EasyLVHolder holder, int position, String s) {
        holder.setText(R.id.tvAutoCompleteItem, s);
    }
}
