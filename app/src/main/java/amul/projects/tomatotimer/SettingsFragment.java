package amul.projects.tomatotimer;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amulpok on 5/15/17.
 */

public class SettingsFragment extends Fragment {
    @BindView(R.id.fullScreen) CheckBox fullscreen_checkbox;
    @BindView(R.id.darkMode) CheckBox darkMode_checkbox;
    @BindView(R.id.pomodoroLength) TextView pomodoroLength;
    @BindView(R.id.breakLength) TextView breakLength;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public static SettingsFragment newInstance(){
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        ButterKnife.bind(this, view);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        fullscreen_checkbox.setChecked(sharedPref.getBoolean("fullscreen", false));
        darkMode_checkbox.setChecked(sharedPref.getBoolean("darkmode", false));
        pomodoroLength.setText(sharedPref.getString("pomodoro_length", "Enter Pomodoro Length (in minutes)"));
        breakLength.setText(sharedPref.getString("break_length", "Enter Break Length (in minutes)"));

        return view;
    }

    @OnClick({R.id.fullScreen, R.id.darkMode})
    public void onCheckboxClicked(View view) {

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.fullScreen:
                if (checked) {
                    editor.putBoolean("fullscreen", true);
                }
                else {
                    editor.putBoolean("fullscreen", false);
                }
                break;
            case R.id.darkMode:
                if (checked) {
                    editor.putBoolean("darkmode", true);
                    getActivity().setTheme(R.style.DarkTheme);
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), getActivity().getClass()));
                }
                else {
                    editor.putBoolean("darkmode", false);
                    getActivity().setTheme(R.style.AppTheme);
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), getActivity().getClass()));
                }
                break;
        }

        editor.commit();
    }

    @OnClick({R.id.submitBreak, R.id.submitPomodoro})
    public void onButtonClicked(View view){
        switch(view.getId()) {
            case R.id.submitPomodoro:
                breakLength.getText();

                break;
            case R.id.submitBreak:

                break;
        }

        editor.commit();
    }
}
