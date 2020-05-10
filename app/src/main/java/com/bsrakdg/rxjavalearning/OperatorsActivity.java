package com.bsrakdg.rxjavalearning;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bsrakdg.rxjavalearning.models.Task;
import com.bsrakdg.rxjavalearning.utils.DataSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OperatorsActivity extends AppCompatActivity {

    private static final String TAG = "OperatorsActivity";

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO OPERATORS

        // TODO: Overview
        // Two way subsucribe on Observable :
        // 1. subsucribe with void
        // 2. subsucribe with return type Disposable

        // overview();

        // TODO Create Operator :
        // Two different ways to create an Observable using the Create() operator:
        // 1. Creating an Observable from a single object
        // 2. Creating an Observable from a list of objects

        // singleCreateOperator();
        // listCreateOperator();

        // TODO Just Operator
        // Makes only 1 emission
        // You can only pass a max of 10 objects to the just() operator.

        // justOperator();

        // TODO: Range Operator
        // rangeOperator();

        // TODO: Map Operator
        // Edit each item
        // mapOperator();

        // TODO: Repeat
        //repeatOperator();

        // TODO: TakeWhile
        // Add condition to emit
        // takeWhileOperator();

        // TODO Interval
        // intervalOperator();

        // TODO Timer
        timerOperator();
    }

    private void overview() {
        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTasksList()) // Observable list
                .subscribeOn(Schedulers.io()) // Where does work
                .filter(task -> { // Filter the task
                    Log.d(TAG, "test: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(5000); // It doesn't block UI
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return task.isComplete();
                })
                .observeOn(AndroidSchedulers.mainThread()); // Where does listen

        // 1. subsucribe with void
        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: called");
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext: " + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + task.getDescription());

                /*try {
                    Thread.sleep(5000);  // It blocks UI
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: called");
            }
        });

        // 2. subsucribe with return type Disposable
        disposable.add(taskObservable.subscribe(new Consumer<Task>() {
            @Override
            public void accept(Task task) throws Throwable {
                Log.d(TAG, "accept: ");
            }
        }));
    }

    private void singleCreateOperator() {
        // 1. Creating an Observable from a single object
        final Task task = new Task("Walk the dog", false, 3);

        Observable<Task> taskObservable = Observable
                .create((ObservableOnSubscribe<Task>) emitter -> {
                    // a unique process to the create operator
                    Log.d(TAG, "subscribe: worked");
                    if (!emitter.isDisposed()) {
                        emitter.onNext(task);
                        emitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext: " + task.toString());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: completed");
            }
        });
    }

    private void listCreateOperator() {

        // 2. Creating an Observable from a list of objects
        final List<Task> tasks = DataSource.createTasksList();

        Observable<Task> taskObservable = Observable
                .create((ObservableOnSubscribe<Task>) emitter -> {
                    // a unique process to the create operator
                    Log.d(TAG, "subscribe: worked");

                    for (Task itemTask : tasks) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(itemTask);
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext: " + task.toString());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: completed");
            }
        });
    }

    private void justOperator() {

        Observable<Integer[]> taskObservable = Observable
                .just(new Integer[]{1, 2, 3})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Integer[]>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer @NonNull [] integers) {
                Log.d(TAG, "onNext: " + integers.length);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    private void rangeOperator() {
        Observable<Integer> observable = Observable
                .range(0, 9)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    private void mapOperator() {
        Observable<Integer> observable = Observable
                .range(0, 9)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Throwable {
                        return integer * integer;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    };

    private void takeWhileOperator() {
        Observable<Integer> observable = Observable
                .range(0, 9)
                .map(integer -> {
                    Log.d(TAG, "apply: map " + integer );
                    return integer * integer;
                })
                .takeWhile(integer -> {
                    Log.d(TAG, "text: takeWhile " + integer);
                    return integer < 4;
                })
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    };

    private void repeatOperator() {
        Observable<Integer> observable = Observable
                .range(0, 9)
                .repeat(3)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    private void intervalOperator() {
        Observable<Long> intervalObservable = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .takeWhile(aLong -> aLong < 5)
                .subscribeOn(AndroidSchedulers.mainThread());

        intervalObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Long aLong) {
                Log.d(TAG, "onNext: " + aLong);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    private void timerOperator() {
        Observable<Long> timerObservable = Observable
                .timer(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .takeWhile(aLong -> aLong < 5)
                .subscribeOn(AndroidSchedulers.mainThread());

        timerObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Long aLong) {
                Log.d(TAG, "onNext: " + aLong);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear(); // Clear observables
        // disposable.dispose(); // hard clear
        // Example: You should clear disposable on View Model
    }
}
