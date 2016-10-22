package com.mars.blackcat.manager;

import android.text.TextUtils;

import com.mars.blackcat.ReaderApplication;
import com.mars.blackcat.bean.Recommend;
import com.mars.blackcat.utils.ACache;
import com.mars.blackcat.utils.AppUtils;
import com.mars.blackcat.utils.FileUtils;
import com.mars.blackcat.utils.LogUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CollectionsManager {

    private volatile static CollectionsManager singleton;

    private CollectionsManager() {

    }

    public static CollectionsManager getInstance() {
        if (singleton == null) {
            synchronized (CollectionsManager.class) {
                if (singleton == null) {
                    singleton = new CollectionsManager();
                }
            }
        }
        return singleton;
    }

    /**
     * 获取收藏列表
     *
     * @return
     */
    public List<Recommend.RecommendBooks> getCollectionList() {
        List<Recommend.RecommendBooks> list = (ArrayList<Recommend.RecommendBooks>) ACache.get(
                ReaderApplication.getsInstance()).getAsObject("collection");
        return list == null ? null : list;
    }

    /**
     * 移除单个收藏
     *
     * @param bookId
     */
    public void remove(String bookId) {
        List<Recommend.RecommendBooks> list = getCollectionList();
        if (list == null) {
            return;
        }
        for (Recommend.RecommendBooks bean : list) {
            if (bean != null) {
                if (TextUtils.equals(bean._id, bookId)) {
                    list.remove(bean);
                    ACache.get(ReaderApplication.getsInstance()).put("collection", (Serializable) list);
                    break;
                }
            }
        }
    }

    /**
     * 是否已收藏
     *
     * @param bookId
     * @return
     */
    public boolean isCollected(String bookId) {
        List<Recommend.RecommendBooks> list = getCollectionList();
        if (list == null || list.isEmpty()) {
            return false;
        }
        for (Recommend.RecommendBooks bean : list) {
            if (bean._id.equals(bookId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 移除多个收藏
     *
     * @param removeList
     */
    public void removeSome(List<Recommend.RecommendBooks> removeList, boolean removeCache) {
        List<Recommend.RecommendBooks> list = getCollectionList();
        if (list == null) {
            return;
        }
        if (removeCache) {
            for (Recommend.RecommendBooks book : removeList) {
                try {
                    // 移除章节文件
                    FileUtils.deleteFileOrDirectory(FileUtils.getBookDir(book._id));
                    // 移除目录缓存
                    CacheManager.getInstance().removeTocList(AppUtils.getAppContext(), book._id);
                    // 移除阅读进度
                    SettingManager.getInstance().removeReadProgress(book._id);
                } catch (IOException e) {
                    LogUtils.e(e.toString());
                }
            }
        }
        list.removeAll(removeList);
        ACache.get(ReaderApplication.getsInstance()).put("collection", (Serializable) list);
    }

    /**
     * 加入收藏
     *
     * @param bean
     */
    public void add(Recommend.RecommendBooks bean) {
        List<Recommend.RecommendBooks> list = getCollectionList();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(bean);
        ACache.get(ReaderApplication.getsInstance()).put("collection", (Serializable) list);
    }

    /**
     * 置顶收藏
     *
     * @param bookId
     */
    public void top(String bookId) {
        List<Recommend.RecommendBooks> list = getCollectionList();
        if (list == null) {
            return;
        }
        for (Recommend.RecommendBooks bean : list) {
            if (bean != null) {
                if (TextUtils.equals(bean._id, bookId)) {
                    bean.isTop = true;
                    list.remove(bean);
                    list.add(0, bean);
                    ACache.get(ReaderApplication.getsInstance()).put("collection", (Serializable) list);
                    break;
                }
            }
        }
    }


}
