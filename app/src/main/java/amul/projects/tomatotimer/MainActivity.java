package amul.projects.tomatotimer;


import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //binds
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.left_drawer) ListView mDrawerList;

    //drawer options/variables
    private String[] drawerOptions = {"Timer", "Settings"};
    FragmentManager fm = getFragmentManager();
    TimerFragment timerFrag = new TimerFragment();
    SettingsFragment settingFrag = new SettingsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fm.beginTransaction().replace(R.id.mainFrame, timerFrag).commit();

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {

        // Insert the fragment by replacing any existing fragment
        if(position == 0) {
            fm.beginTransaction()
                    .replace(R.id.mainFrame, timerFrag)
                    .commit();
        }
        else if(position == 1){
            fm.beginTransaction()
                    .replace(R.id.mainFrame, settingFrag)
                    .commit();
        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(drawerOptions[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

}
