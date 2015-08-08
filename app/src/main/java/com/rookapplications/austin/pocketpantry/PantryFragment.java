package com.rookapplications.austin.pocketpantry;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PantryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PantryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PantryFragment extends Fragment {

    public static final String PANTRY_FILE = "pantry_file";
    private OnFragmentInteractionListener mListener;
    private TableLayout tableLayout;
    private int numItemsListed;
    private JSONArray jItemsArray;

    public PantryFragment() {
        numItemsListed = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pantry, container, false);
        tableLayout = (TableLayout) view.findViewById(R.id.tableLayout);
        jItemsArray = new JSONArray();
        updatePantryList(true);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            mListener.onSectionAttached(0);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addItem(String item, Date expiration) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String message = item + "#" + format.format(expiration) + '\n';
        jItemsArray.put(message);
        updatePantryList(false);
    }

    private void updatePantryList(boolean isFirstTime) {
        try {
            if (isFirstTime) {
                for (int i = 0; i < jItemsArray.length(); i++) {

                    addTableRow(jItemsArray.getString(i));
                }
            } else {
                String line = jItemsArray.getString(numItemsListed);
                if (line != null) {
                    addTableRow(line);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addTableRow(String line) {
        String item = line.split("#")[0];
        String expiration = line.split("#")[1];
        View tableRow = getActivity().getLayoutInflater().inflate(R.layout.layout_table_row, null, false);
        TextView itemName = (TextView) tableRow.findViewById(R.id.item_name);
        TextView expirationDate = (TextView) tableRow.findViewById(R.id.item_expiration);
        itemName.setText(item);
        expirationDate.setText(expiration);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date date = format.parse(expiration);
            if (date.before(Calendar.getInstance().getTime())) {
                expirationDate.setTextColor(Color.RED);
                itemName.setTextColor(Color.RED);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tableLayout.addView(tableRow);
        numItemsListed++;
    }

    private void setTextColor() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onSectionAttached(int number);
    }

}
