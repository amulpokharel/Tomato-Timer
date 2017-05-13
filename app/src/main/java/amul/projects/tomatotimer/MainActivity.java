package amul.projects.tomatotimer;

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
    private static long BREAK_LENGTH = 300000L;
    private static long POMODORO_LENGTH = 15000L;
    private static Handler handler = new Handler();
    long startTime = 0L;
    long currentTime = 0L;
    long endTime = 0L;
    int pomodoro_cycle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    //onclick method for start button. initializes + starts the timer
    @OnClick(R.id.startBtn)
    void start_timer(){
        //disable the button to avoid multiple threads
        startBtn.setEnabled(false);

        //set up times
        startTime = SystemClock.uptimeMillis();
        endTime = startTime + POMODORO_LENGTH;
        currentTime = startTime;

        //increment the cycle
        pomodoro_cycle++;

        //set text for TextViews
        status.setText("Pomodoro in Progress");
        timer.setText("25:00");

        //start thread
        handler.postDelayed(timerUpdateThread, 0L);
    }

    //onclick method for the stop button. ends the timer, updates messages accordingly
    @OnClick(R.id.stopBtn)
    void stop_timer(){

        //remove thread from callback, so it stops running
        handler.removeCallbacks(timerUpdateThread);

        //logic?
        if(currentTime <= 10) {
            status.setText("Completed!");
        }
        else
            status.setText("Aborted!");

    }

    //thread to handle timer
    private Runnable timerUpdateThread = new Runnable() {
        @Override
        public void run() {
            currentTime = endTime - SystemClock.uptimeMillis();

            int seconds = (int) (currentTime/1000);
            int minutes = seconds/60;
            seconds = seconds%60; //so seconds are within range of 0 to 59s

            //update the timer
            timer.setText(String.format("%02d", minutes) + ":"+ String.format("%02d", seconds));
            Log.d("Time",Long.toString(currentTime));
            Log.d("Time",Long.toString(endTime));

            //stop timer when it's almost over
            if(currentTime <= 10) {
                stop_timer();
                return;
            }

            //keep runnning
            handler.postDelayed(this, 0L);
        }
    };
}
