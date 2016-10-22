package com.mars.blackcat.ui.presenter;

import com.mars.blackcat.api.BookApi;
import com.mars.blackcat.base.RxPresenter;
import com.mars.blackcat.bean.BookReview;
import com.mars.blackcat.bean.CommentList;
import com.mars.blackcat.ui.contract.BookReviewDetailContract;
import com.mars.blackcat.utils.LogUtils;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yuyh.
 * @date 16/9/2.
 */
public class BookReviewDetailPresenter extends RxPresenter<BookReviewDetailContract.View> implements BookReviewDetailContract.Presenter {

    private BookApi bookApi;

    @Inject
    public BookReviewDetailPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookReviewDetail(String id) {
        Subscription rxSubscription = bookApi.getBookReviewDetail(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookReview>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getBookReviewDetail:" + e.toString());
                    }

                    @Override
                    public void onNext(BookReview data) {
                        mView.showBookReviewDetail(data);
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void getBestComments(String bookReviewId) {
        Subscription rxSubscription = bookApi.getBestComments(bookReviewId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getBestComments:" + e.toString());
                    }

                    @Override
                    public void onNext(CommentList list) {
                        mView.showBestComments(list);
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void getBookReviewComments(String bookReviewId, int start, int limit) {
        Subscription rxSubscription = bookApi.getBookReviewComments(bookReviewId, start + "", limit + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getBookReviewComments:" + e.toString());
                    }

                    @Override
                    public void onNext(CommentList list) {
                        mView.showBookReviewComments(list);
                    }
                });
        addSubscrebe(rxSubscription);
    }

}
