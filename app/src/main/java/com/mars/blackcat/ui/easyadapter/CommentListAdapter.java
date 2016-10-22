package com.mars.blackcat.ui.easyadapter;

import android.content.Context;
import android.view.ViewGroup;

import com.mars.blackcat.R;
import com.mars.blackcat.base.Constant;
import com.mars.blackcat.bean.CommentList;
import com.mars.blackcat.manager.SettingManager;
import com.mars.blackcat.utils.FormatUtils;
import com.mars.blackcat.view.recyclerview.adapter.BaseViewHolder;
import com.mars.blackcat.view.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 帖子 评论、回复
 *
 * @author lfh.
 * @date 16/9/3.
 */
public class CommentListAdapter extends RecyclerArrayAdapter<CommentList.CommentsBean> {

    public CommentListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<CommentList.CommentsBean>(parent, R.layout.item_comment_list) {
            @Override
            public void setData(CommentList.CommentsBean item) {
                if (!SettingManager.getInstance().isNoneCover()) {
                    holder.setCircleImageUrl(R.id.ivBookCover, Constant.IMG_BASE_URL + item.author.avatar,
                            R.mipmap.avatar_default);
                } else {
                    holder.setImageResource(R.id.ivBookCover, R.mipmap.avatar_default);
                }

                holder.setText(R.id.tvBookTitle, item.author.nickname)
                        .setText(R.id.tvContent, item.content)
                        .setText(R.id.tvBookType, String.format(mContext.getString(R.string.book_detail_user_lv), item.author.lv))
                        .setText(R.id.tvFloor, String.format(mContext.getString(R.string.comment_floor), item.floor))
                        .setText(R.id.tvTime, FormatUtils.formatDate(item.created));

                if (item.replyTo == null) {
                    holder.setVisible(R.id.tvReplyNickName, false);
                    holder.setVisible(R.id.tvReplyFloor, false);
                } else {
                    holder.setText(R.id.tvReplyNickName, String.format(mContext.getString(R.string.comment_reply_nickname), item.replyTo.author.nickname))
                            .setText(R.id.tvReplyFloor, String.format(mContext.getString(R.string.comment_reply_floor), item.replyTo.floor));
                    holder.setVisible(R.id.tvReplyNickName, true);
                    holder.setVisible(R.id.tvReplyFloor, true);
                }
            }
        };
    }
}
