package com.example.aiiage.myhandlethread.RxJavaDemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.aiiage.myhandlethread.R;
import com.tencent.bugly.crashreport.crash.e;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;

public class RxJavaDemoActivity extends Activity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java_demo);
        /*创建一个Observable map()*/
       io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
              emitter.onNext(1);
              emitter.onNext(2);
              emitter.onNext(3);
            }
        }).subscribe(new io.reactivex.Observer<Integer>() {
           @Override
           public void onSubscribe(Disposable d) {
               Log.d(TAG,"This value is "+"subscribe");
           }

           @Override
           public void onNext(Integer integer) {
               Log.d(TAG,"This value is "+integer);
           }

           @Override
           public void onError(Throwable e) {
               Log.d(TAG,"This value is "+"error");
           }

           @Override
           public void onComplete() {
               Log.d(TAG,"This value is "+"complete");
           }
       });
       /*map转换*/
        io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "This is return " +integer;//指定变换的格式
            }
        }).subscribe(new io.reactivex.functions.Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "This is a accept :" + s);
            }
        });
        /*zip合并*/
        io.reactivex.Observable.zip(getStringObservable(), getIntegerObservable(), new io.reactivex.functions.BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s+integer;
        }
        }).subscribe(new io.reactivex.functions.Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "zip : accept : " + s + "\n");
            }
        });
        /*Observable的concat的连接*/
        io.reactivex.Observable.concat(io.reactivex.Observable.just(1,2,3), io.reactivex.Observable.just(4,5,6,7))
                .subscribe(new io.reactivex.functions.Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG,"concat :"+integer+ "\n");
                    }
                });
        /*flatMap:一 ->多 -> 一*/
        io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list=new ArrayList<>();
                for (int i=0;i<3;i++){
                    list.add("I am a value "+ integer);
                }
                int delayTime =(int) (1 +Math.random()*10);
                return io.reactivex.Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.functions.Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Log.e(TAG, "flatMap : accept : " + s + "\n");
                        }
                    });
        /*distinct去重*/
        io.reactivex.Observable.just(1,2,2,2,3,4,5,4,5).distinct()
                .subscribe(new io.reactivex.functions.Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG,"distinct: "+integer+"\n");
                    }
                });
        /*Filter 过滤器*/
        io.reactivex.Observable.just(45,123,32,-89,11,2)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer>=10;
                    }
                }).subscribe(new io.reactivex.functions.Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG,"Filter :"+integer);
            }
        });
        /*buffer 按skip分成大侠不超过count的buffer，然后生成一个Observable*/
        io.reactivex.Observable.just(1,2,3,4,5).buffer(2,2)
                .subscribe(new io.reactivex.functions.Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.e(TAG, "buffer size : " + integers.size() + "\n");
                        Log.e(TAG, "buffer value : " );
                        for (Integer i:integers){
                            Log.e(TAG,i+""+"\n");
                        }
                    }
                });
        /*timer 定时任务*/
        io.reactivex.Observable.timer(2,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())// // timer 默认在新线程，所以需要切换回主线程
                .subscribe(new io.reactivex.functions.Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG,"Timer: "+aLong+"at"+"" + "\n");
                    }
                });
        /*doOnNext 在获取数据前执行别的操作*/
        io.reactivex.Observable.just("wang","yan","qin")
                .doOnNext(new io.reactivex.functions.Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG,"doOnNext保存 "+s+"\n");
                    }
                }).subscribe(new io.reactivex.functions.Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "doOnNext :" + s + "\n");
            }
        });
        /*skip 跳过这个数开始接收*/
        io.reactivex.Observable.just("Wang","a","yan","qin")
                .skip(2)
                .subscribe(new io.reactivex.functions.Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG,"skip : "+s +"\n");
                    }
                });
        /*Single 只会接收一个参数*/
        Single.just(1)
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e(TAG, "single doOnNext :" + integer + "\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "single : onError : "+e.getMessage()+"\n");
                    }
                });
        /*debounce 去除发送频率过快的顶*/
        io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                // send events with simulated time wait
                emitter.onNext(1); // skip
                Thread.sleep(400);
                emitter.onNext(2); // deliver
                Thread.sleep(505);
                emitter.onNext(3); // skip
                Thread.sleep(100);
                emitter.onNext(4); // deliver
                Thread.sleep(605);
                emitter.onNext(5); // deliver
                Thread.sleep(510);
                emitter.onComplete();
            }
        }).debounce(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG,"debounce :" + integer + "\n");
                    }
                });
        /*defer */
        io.reactivex.Observable observable = io.reactivex.Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return io.reactivex.Observable.just(1, 2, 3);
            }
        });
        observable.subscribe(new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer o) {
                Log.e(TAG, "defer : " + o + "\n");

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "defer : onError : " + e.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "defer : onComplete\n");
            }
        });

        io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                //
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).subscribe(new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "defer1 : " + integer + "\n");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "defer1 : onComplete\n");
            }
        });
        /*last*/
        io.reactivex.Observable.just(1, 2, 3)
                .last(1)
                .subscribe(new io.reactivex.functions.Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "last : " + integer + "\n");
                    }
                });
        /*merge*/
        io.reactivex.Observable.merge(io.reactivex.Observable.just(1, 2), io.reactivex.Observable.just(3, 4, 5))
                .subscribe(new io.reactivex.functions.Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: merge :" + integer + "\n" );
                    }
                });
        /*reduce */
        io.reactivex.Observable.just(1,3,4).scan(new io.reactivex.functions.BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer+integer2;
            }
        }).subscribe(new io.reactivex.functions.Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: reduce : " + integer + "\n");
            }
        });
        /*windows:*/
        io.reactivex.Observable.interval(1,TimeUnit.SECONDS)
                .take(15)//最多接收15个
                .window(3,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<io.reactivex.Observable<Long>>() {
                    @Override
                    public void accept(io.reactivex.Observable<Long> longObservable) throws Exception {
                        longObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new io.reactivex.functions.Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        Log.e(TAG, "Next:" + aLong + "\n");
                                    }
                                });
                    }
                });
    }

    /*zip 1*/
    private io.reactivex.Observable<String> getStringObservable(){

        return io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                if (! emitter.isDisposed()){
                    emitter.onNext("A");
                    Log.d(TAG,"String emit: A \n");
                    emitter.onNext("B");
                    Log.d(TAG,"String emit: B \n");
                    emitter.onNext("C");
                    Log.d(TAG,"String emit: C \n");
                }
            }
        });
    }
    /*zip2*/
    private io.reactivex.Observable<Integer> getIntegerObservable(){
      return io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
          @Override
          public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
              if (!emitter.isDisposed()){
                  emitter.onNext(1);
                  Log.d(TAG, "Integer emit : 1 \n");
                  emitter.onNext(2);
                  Log.d(TAG, "Integer emit : 2 \n");
                  emitter.onNext(3);
                  Log.d(TAG, "Integer emit : 3 \n");
                  emitter.onNext(4);
                  Log.d(TAG, "Integer emit : 4 \n");
                  emitter.onNext(5);
                  Log.d(TAG, "Integer emit : 5 \n");
              }
          }
      });
    }
}
