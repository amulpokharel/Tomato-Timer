package amul.projects.tomatotimer;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amulpok on 5/15/17.
 */

public class TimerFragment extends Fragment {
    //Binds
    @BindView(R.id.timer) TextView timer;
    @BindView(R.id.statusText) TextView status;
    @BindView(R.id.startFab) FloatingActionButton startBtn;

    //Timer variables
    private static long BREAK_LENGTH = 3000L;
    private static long POMODORO_LENGTH = 3000L;
    private static Handler handler = new Handler();
    private static boolean activeThread = false;
    long currentPomodoroLength;
    long currentBreakLength;
    Time startTime;
    Time currentTime;
    Time endTime;
    int pomodoro_cycle = 1;
    boolean break_time = false;
    int notificationID = 0;
    android.support.v4.app.NotificationCompat.Builder nBuilder;
    NotificationManager mNotificationManager;

    SharedPreferences sharedPref;


    public static TimerFragment newInstance(){
        return new TimerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(!activeThread){
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            startTime = new Time(0L);
            currentTime = new Time(0L);
            endTime = new Time(0L);

            currentPomodoroLength = sharedPref.getLong("pomodoro_length", POMODORO_LENGTH);
            Log.d("pomodoro length", Long.toString(currentPomodoroLength));
            currentBreakLength = sharedPref.getLong("break_length", BREAK_LENGTH);
            Log.d("break length", Long.toString(currentBreakLength));

            nBuilder = new android.support.v4.app.NotificationCompat.Builder(getActivity())
                    .setSmallIcon(R.drawable.ic_clock)
                    .setContentTitle("Pomodoro")
                    .setContentText(Time.FormatMS(currentPomodoroLength));

            mNotificationManager =
                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);}
        else{
            
        }


    }

    public void onDestroy(){
        mNotificationManager.cancelAll();
        super.onDestroy();
    }

    public void onStop(){
        mNotificationManager.cancel(notificationID);
        super.onStop();
    }

    //onclick method for start button. initializes + starts the timer
    @OnClick(R.id.startFab)
    void start_timer(){
        //disable the button to avoid multiple threads
        startBtn.hide();

        //set up times
        startTime.setToCurrent();
        currentTime.setToCurrent();
        endTime.setToCurrent();
        if(break_time)
            endTime.setOffset(currentBreakLength);
        else
            endTime.setOffset(currentPomodoroLength);


        //set text for TextViews
        if(break_time){
            status.setText("Enjoy your break(#"+ pomodoro_cycle + ")!");
            timer.setText(Time.FormatMS(currentBreakLength));
        }
        else {
            status.setText("Pomodoro #" + pomodoro_cycle + " in Progress");
            timer.setText(Time.FormatMS(currentPomodoroLength));
        }



        //start thread
        handler.postDelayed(timerUpdateThread, 0L);
    }

    //onclick method for the stop button. ends the timer, updates messages accordingly
    @OnClick(R.id.stopFab)
    void stop_timer(){

        //remove thread from callback, so it stops running
        handler.removeCallbacks(timerUpdateThread);

        //logic?
        if(currentTime.getTime_in_ms() <= 10) {
            if(pomodoro_cycle <= 4) {
                if(!break_time){
                    if (pomodoro_cycle == 4){
                        status.setText("Pomodoro completed!");
                        nBuilder.setContentText("Finished!");
                        nBuilder.setOngoing(false);
                        mNotificationManager.notify(notificationID, nBuilder.build());
                        pomodoro_cycle = 1;
                        break_time = false;
                        startBtn.show();
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
            startBtn.show();
        }

    }

    //thread to handle timer
    private Runnable timerUpdateThread = new Runnable() {
        @Override
        public void run() {
            currentTime.setTime(endTime.getTime_in_ms() - SystemClock.uptimeMillis());

            //update the timer
            if((currentTime.getTime_in_ms()%1000)<25) {
                activeThread = true;
                timer.setText(currentTime.toString());
                nBuilder.setContentText(currentTime.toString());
                if (break_time)
                    nBuilder.setContentTitle("Break #" + pomodoro_cycle);
                else
                    nBuilder.setContentTitle("Pomodoro #" + pomodoro_cycle);
                nBuilder.setOngoing(true);
                mNotificationManager.notify(notificationID, nBuilder.build());
                Log.d("Time", currentTime.toString());
                Log.d("Time", endTime.toString());
            }

            //stop timer when it's almost over
            if(currentTime.getTime_in_ms() <= 10) {
                stop_timer();
                activeThread = false;
                return;
            }

            //keep running
            handler.postDelayed(this, 0L);
        }
    };
}