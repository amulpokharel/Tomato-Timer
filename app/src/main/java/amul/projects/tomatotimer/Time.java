package amul.projects.tomatotimer;

import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 * Created by amulpok on 5/15/17.
 */

public class Time implements Comparable{

    private long time_in_ms = 0L;

    public Time(){
        time_in_ms = SystemClock.uptimeMillis();
    }

    //time in milliseconds
    public Time(Long time) {
        time_in_ms = time;
    }

    public long getTime_in_ms(){
        return time_in_ms;
    }

    public void setTime(Long time){
        time_in_ms = time;
    }

    public void setToZero(){
        time_in_ms = 0L;
    }

    public void setTime(Time x){
        this.time_in_ms = x.getTime_in_ms();
    }

    public void setToCurrent(){
        time_in_ms = SystemClock.uptimeMillis();
    }

    public boolean isZero(){
        if(this.toSeconds() == 0)
            return true;
        else
            return false;
    }

    public void subtractCurrentTime(){
        time_in_ms = time_in_ms - SystemClock.uptimeMillis();
    }

    public void setCurrentOffsetTime(Long offset){
        time_in_ms = SystemClock.uptimeMillis() + offset;
    }

    public int toSeconds(){
        return (int) (time_in_ms/1000);
    }

    public int toMinutes(){
        return (int) ((time_in_ms/1000)/60);
    }



    public String toString(){
        return String.format("%02d", toMinutes()) + ":"+ String.format("%02d", toSeconds()%60);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (this.toSeconds() < ((Time)o).toSeconds()){
            return -1;
        }
        else if (this.toSeconds() > ((Time)o).toSeconds()){
            return 1;
        }
        else
            return 0;
    }
}

