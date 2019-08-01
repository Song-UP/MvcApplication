package com.song.songup.common_sdk.retrofit;

import android.content.Context;
import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;


import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * 要跟后端程序员约定格式为下：
 * BaseModel.java
 */
public class RxHelper {
    private static final int maxConnectCount = 3;

    /**
     * 对结果进行预处理
     * @param <T>
     * @return
     */
    private static <T>ObservableTransformer<BaseModel<T>, T> handleResult(){
        return new ObservableTransformer<BaseModel<T>, T>() {
            @Override
            public Observable<T> apply(@NonNull Observable<BaseModel<T>> tObservable) {
                return tObservable.flatMap(new Function<BaseModel<T>, Observable<T>>() {
                    @Override
                    public Observable<T> apply(@NonNull BaseModel<T> result) {
                        return getResult(result);
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
    /**
     * 在View中进行请求添加Lifecycle
     */
    public static <T>ObservableTransformer<BaseModel<T>, T> handleResult(View view){
        Context context = view.getContext();
        return handleResultContext(context);
    }
    /**
     * 在View中进行请求添加Lifecycle
     */
    public static <T>ObservableTransformer<BaseModel<T>, T> handleResultContext(Context context){
        if (context != null && context instanceof LifecycleProvider){
            return handleResult((LifecycleProvider) context);
        }else {
            return handleResult();
        }
    }
    /**
     * 对结果进行预处理->添加Lifecycle
     * @param <T>
     * @return
     */
    public static <T>ObservableTransformer<BaseModel<T>, T> handleResult(final LifecycleProvider lifecycleProvider){
        return new ObservableTransformer<BaseModel<T>, T>() {
            @Override
            public Observable<T> apply(@NonNull Observable<BaseModel<T>> tObservable) {
                return tObservable.flatMap(new Function<BaseModel<T>, Observable<T>>() {
                    @Override
                    public Observable<T> apply(@NonNull BaseModel<T> result) {
                        return getResult(result);
                    }
                }).compose(lifecycleProvider.<T>bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    private static <T>Observable<T> getResult(BaseModel<T> result){
        if (null != result.getSrv_time()) {
//           int differenceTime = (result.getSrv_time() - HeaderVerifyUtils.getSecondTimestamp(new Date()));
//           UserInfo.setDifferenceTime(differenceTime);
        }
        if (result.getStatus() == 1) {
            return createData(result.getData());
        } else {
            if(result.getStatus() == -1){
                //退出登录
//                UserInfo.setChatroom_block(1);
//                UserInfo.setToken("");
//                UserInfo.setAvatar("");
//                UserInfo.setRoomId("");
//                UserInfo.setAdmin("");
//
//                EventBus.getDefault().post(new LogOut());
            }

//            统一处理错误
            return Observable.error(new ServerException(result.getMsg()));
        }
    }

    public static <T>ObservableTransformer<BaseModel<T>, T> handleResultReConnection(final LifecycleProvider lifecycleProvider){
        return new ObservableTransformer<BaseModel<T>, T>() {
            int currentRetryCount = 0;
            @Override
            public Observable<T> apply(@NonNull Observable<BaseModel<T>> tObservable) {
                return tObservable.flatMap(new Function<BaseModel<T>, Observable<T>>() {
                    @Override
                    public Observable<T> apply(@NonNull BaseModel<T> result) {
                        return getResult(result);
                    }
                })
                        .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(Throwable throwable) {
                                        if (throwable instanceof IOException || ((HttpException)throwable).code() == 502){
                                            if (currentRetryCount < maxConnectCount){
                                                currentRetryCount++;
                                                long waitRetryTime = 1000 + currentRetryCount* 1000;
                                                return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);
                                            }else{
                                                return Observable.error(new Throwable(throwable));
                                            }
                                        } else{
                                            return Observable.error(throwable);
                                        }
                                    }
                                });
                            }
                        }).compose(lifecycleProvider.<T>bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());//observeOn(AndroidSchedulers.mainThread())表示在主线程消费事件，subscribeOn(Schedulers.io())表示事件在 I/O 操作中产生
            }
        };
    }

    public static <T>ObservableTransformer<BaseModel<T>, T> handleResultReConnection(){
        return new ObservableTransformer<BaseModel<T>, T>() {
            int currentRetryCount = 0;
            @Override
            public Observable<T> apply(@NonNull Observable<BaseModel<T>> tObservable) {
                return tObservable.flatMap(new Function<BaseModel<T>, Observable<T>>() {
                    @Override
                    public Observable<T> apply(@NonNull BaseModel<T> result) {
                        return getResult(result);
                    }
                })
                        .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(Throwable throwable) {
                                        if (throwable instanceof IOException || ((HttpException)throwable).code() == 502){
                                            if (currentRetryCount < maxConnectCount){
                                                currentRetryCount++;
                                                long waitRetryTime = 1000 + currentRetryCount* 1000;
                                                return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);
                                            }else{
                                                return Observable.error(new Throwable(throwable));
                                            }
                                        } else{
                                            return Observable.error(throwable);
                                        }
                                    }
                                });
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());//observeOn(AndroidSchedulers.mainThread())表示在主线程消费事件，subscribeOn(Schedulers.io())表示事件在 I/O 操作中产生
            }
        };
    }

    /**
     * 创建成功的数据
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Observable<T> createData(final T data) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> subscriber) {
                try {
                    if(data != null) {
                        subscriber.onNext(data);
                    }
                    subscriber.onComplete();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
