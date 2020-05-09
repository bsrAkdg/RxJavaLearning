package com.bsrakdg.rxjavalearning;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bsrakdg.rxjavalearning.models.Task;
import com.bsrakdg.rxjavalearning.utils.DataSource;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);

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

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: called");
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

    }
}
