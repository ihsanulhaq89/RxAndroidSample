package com.mac.rxandroidsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final int[] colorsArray = {R.color.blue, R.color.red, R.color.green};
    private final String B_DATA = "B_DATA";
    private final String B_COLOR = "B_COLOR";
    private final String B_COUNT = "B_COUNT";
    private static int count = 0;

    private Observable<Intent> broadCast;
    private TextView textview;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUIComponents();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                broadCast = createBroadCast();
            }
        });
    }

    private void initUIComponents() {
        textview = (TextView) findViewById(R.id.textview);
        button = (Button) findViewById(R.id.button);
    }

    public Observable<Intent> createBroadCast() {
        Observable observable = Observable.create(new Observable.OnSubscribe<Intent>() {
            @Override
            public void call(Subscriber<? super Intent> subscriber) {
                count++;
                Bundle bundle = new Bundle();
                bundle.putString(B_COUNT, count + " Broadcasts Received");
                bundle.putInt(B_COLOR, colorsArray[count % 3]);
                // bundle.putSerializable("tag", new Object());

                Intent intent = new Intent(B_DATA);
                intent.putExtras(bundle);

                subscriber.onNext(intent);

                // subscriber.onCompleted();
                // call it whenever you are done with the broadcast and dont need to call onNext again
            }
        });

        observable.subscribeOn(Schedulers.newThread());
        observable.observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(textBroadCastReceiver); // this will run the code written inside call()
        observable.subscribe(colorBroadCastReceiver); // this will run the code written inside call() again x2

        return observable;
    }

    private Subscriber<Intent> textBroadCastReceiver = new Subscriber<Intent>() {
        public void onCompleted() {
        }

        public void onError(Throwable e) {
        }

        public void onNext(Intent intent) {
            Bundle bundle = intent.getExtras();
            String countValue = bundle.getString(B_COUNT);
            textview.setText(countValue);
        }
    };

    private Subscriber<Intent> colorBroadCastReceiver = new Subscriber<Intent>() {
        public void onCompleted() {
        }

        public void onError(Throwable e) {
        }

        public void onNext(Intent intent) {
            Bundle bundle = intent.getExtras();
            int colorValue = bundle.getInt(B_COLOR);
            textview.setBackgroundColor(ContextCompat.getColor(MainActivity.this, colorValue));
        }
    };
}
