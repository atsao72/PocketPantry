package com.rookapplications.austin.pocketpantry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, PantryFragment.OnFragmentInteractionListener,
        RecipeFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Menu optionsMenu;
    JSONArray pantryItems;
    SharedPreferences preferences;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String strJson = preferences.getString("pantryData","");
        if(!strJson.equals("")){
            try{
                JSONObject jsonData = new JSONObject(strJson);
                pantryItems = jsonData.getJSONArray("pantry");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        else{
            pantryItems = new JSONArray();
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new Fragment();
        String tag = "";
        switch (position){
            case 0:
                fragment = new PantryFragment();
                tag = "PANTRY_FRAGMENT";
                break;
            case 1:
                fragment = new RecipeFragment();
                tag = "RECIPE_FRAGMENT";
                break;
        }
        fragmentTransaction.replace(R.id.container, fragment, tag).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        JSONObject jObj = new JSONObject();
        try{
            jObj.put("pantry", pantryItems);
            preferences.edit().putString("pantryData", jObj.toString()).apply();
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void onAddItemFragment() {
        showInputDialog();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = "My Pantry";
                break;
            case 1:
                mTitle = "Find Recipes";
                break;
        }
    }
//PantryFragment Listener
    public JSONArray getPantry(){
        return pantryItems;
    }

    public void updatePantry(JSONArray array){
        pantryItems = array;
    }

    public void restoreActionBar() {
        updateOptionsMenu();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    private void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.layout_add_item, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.et_item_input);
        final DatePicker datePicker = (DatePicker)promptView.findViewById(R.id.datePicker);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year =  datePicker.getYear();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        PantryFragment fragment = (PantryFragment) getSupportFragmentManager().findFragmentByTag("PANTRY_FRAGMENT");
                        fragment.addItem(editText.getText().toString(), calendar.getTime());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            optionsMenu = menu;
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateOptionsMenu(){
        MenuItem menuItem = optionsMenu.findItem(R.id.action_example);
        if(mTitle.equals("My Pantry")){
            menuItem.setTitle("Add Item");
            menuItem.setVisible(true);
        }
        else
        {
            menuItem.setVisible(false);
        }
    }
}
