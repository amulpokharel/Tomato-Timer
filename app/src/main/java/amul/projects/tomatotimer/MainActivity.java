package amul.projects.tomatotimer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    //Binds
    @BindView(R.id.timer) TextView timer;
    @BindView(R.id.statusText) TextView status;
    @BindView(R.id.startBtn) Button startBtn;

    //private vars
    private static long BREAK_LENGTH = 3000L;
    private static long POMODORO_LENGTH = 3000L;
    private static Handler handler = new Handler();
    Time startTime;
    Time currentTime;
    Time endTime;
    int pomodoro_cycle = 1;
    boolean break_time = false;
    int notificationID = 1;
    android.support.v4.app.NotificationCompat.Builder nBuilder;
    NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nBuilder = new android.support.v4.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_clock)
                .setContentTitle("Pomodoro")
                .setContentText("00:00");

        startTime = new Time(0L);
        currentTime = new Time(0L);
        endTime = new Time(0L);
        Intent resultIntent = new Intent(this, MainActivity.class);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ButterKnife.bind(this);
    }

    //onclick method for start button. initializes + starts the timer
    @OnClick(R.id.startBtn)
    void start_timer(){
        //disable the button to avoid multiple threads
        startBtn.setEnabled(false);

        //set up times
        startTime.setToCurrent();
        if(break_time)
            endTime.setCurrentOffsetTime(BREAK_LENGTH);
        else
            endTime.setCurrentOffsetTime(POMODORO_LENGTH);
        currentTime.setTime(startTime);

        //set text for TextViews
        if(break_time){
            status.setText("Enjoy your break(#"+ pomodoro_cycle + ")!");
            timer.setText("00:03");
        }
        else {
            status.setText("Pomodoro #" + pomodoro_cycle + " in Progress");
            timer.setText("00:15");
        }



        //start thread
        handler.postDelayed(timerUpdateThread, 0L);
    }

    //onclick method for the stop button. ends the timer, updates messages accordingly
    @OnClick(R.id.stopBtn)
    void stop_timer(){

        //remove thread from callback, so it stops running
        handler.removeCallbacks(timerUpdateThread);

        //logic?
        if(currentTime.getTime_in_ms() <= 10) {
            if(pomodoro_cycle <= 4) {
                if(!break_time){
                    if (pomodoro_cycle == 4){
                        status.setText("Pomodoro completed!");
                        mNotificationManager.cancel(notificationID);
                        pomodoro_cycle = 1;
                        break_time = false;
                        startBtn.setEnabled(true);
                    }else {
                        break_time = true;
                        start_timer();
                    }
                }
                else{
                    break_time = false;
                    pomodoro_cycle++;
                    start_timer();
                    }
                }
        }
        else {
            status.setText("Aborted!");
            mNotificationManager.cancel(notificationID);
            pomodoro_cycle = 1;
            break_time = false;
            startBtn.setEnabled(true);
        }

    }

    //thread to handle timer
    private Runnable timerUpdateThread = new Runnable() {
        @Override
        public void run() {
            currentTime.subtractCurrentTime();

            //update the timer
            timer.setText(currentTime.toString());
            nBuilder.setContentText(currentTime.toString());
            if (break_time)
                nBuilder.setContentTitle("Break #" + pomodoro_cycle);
            else
                nBuilder.setContentTitle("Pomodoro #" + pomodoro_cycle);
            mNotificationManager.notify(notificationID, nBuilder.build());

            //stop timer when it's almost over
            if(currentTime.getTime_in_ms() <= 10) {
                stop_timer();
                return;
            }

            //keep running
            handler.postDelayed(this, 0L);
        }
    };
}
