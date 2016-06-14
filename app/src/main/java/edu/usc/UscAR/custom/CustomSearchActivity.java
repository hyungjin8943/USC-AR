package edu.usc.UscAR.custom;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.beyondar.example.R;

import java.util.ArrayList;

public class CustomSearchActivity extends Activity implements View.OnClickListener {

    public static String query; // autocomplete query sentence
    private Button mClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_search);

        mClear = (Button) findViewById(R.id.clearBtn);
        mClear.setOnClickListener(this);

        ArrayList<String> buildingName = new ArrayList<String>();

        buildingName.add("aa");
        buildingName.add("ab");
        buildingName.add("ac");
        buildingName.add("ad");
        buildingName.add("abc");
        buildingName.add("abcd");
        buildingName.add("aabb");
        buildingName.add("aabc");
        buildingName.add("aarre");
        buildingName.add("aasdf");


        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.inputText);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, buildingName);

        autoCompleteTextView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index, long arg3) {

                autoCompleteTextView.setText(av.getItemAtPosition(index).toString()); // click the item and set the symbol text(AAPL)
            }
        });

        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // no need to do anything
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (((AutoCompleteTextView) autoCompleteTextView).isPerformingCompletion()) {
                    return;
                }

                if (charSequence.length() < 1) { // minimum 1
                    return;
                }

                query = charSequence.toString();
                Log.e("query", query);
                arrayAdapter.notifyDataSetChanged();

                // new AutoComplete().execute();
            }

            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v == mClear) {
            // clear AutoCompleteTextView
            AutoCompleteTextView atv = (AutoCompleteTextView) findViewById(R.id.inputText);
            // Log.e("get",atv.getText().toString());
            atv.setText("");
        }
    }
}
