package com.mars.blackcat.ui.easyadapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mars.blackcat.R;
import com.mars.blackcat.base.Constant;
import com.mars.blackcat.bean.Recommend;
import com.mars.blackcat.manager.SettingManager;
import com.mars.blackcat.view.recyclerview.adapter.BaseViewHolder;
import com.mars.blackcat.view.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * @author yuyh.
 * @date 2016/9/7.
 */
public class RecommendAdapter extends RecyclerArrayAdapter<Recommend.RecommendBooks> {

    public RecommendAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<Recommend.RecommendBooks>(parent, R.layout.item_recommend_list) {
            @Override
            public void setData(final Recommend.RecommendBooks item) {
                super.setData(item);
                if (!SettingManager.getInstance().isNoneCover()) {
                    holder.setRoundImageUrl(R.id.ivRecommendCover, Constant.IMG_BASE_URL + item.cover,
                            R.mipmap.cover_default);
                } else {
                    holder.setImageResource(R.id.ivRecommendCover, R.mipmap.cover_default);
                }

                holder.setText(R.id.tvRecommendTitle, item.title)
                        .setText(R.id.tvRecommendShort, item.lastChapter)
                        .setVisible(R.id.ivTopLabel, item.isTop)
                        .setVisible(R.id.ckBoxSelect, item.showCheckBox);

                CheckBox ckBoxSelect = holder.getView(R.id.ckBoxSelect);
                ckBoxSelect.setChecked(item.isSeleted);
                ckBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            item.isSeleted = true;
                        } else {
                            item.isSeleted = false;
                        }
                    }
                });
            }
        };
    }

}
