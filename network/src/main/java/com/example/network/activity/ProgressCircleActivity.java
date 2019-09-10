package com.example.network.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.network.R;
import com.example.network.widget.TextProgressCircle;

public class ProgressCircleActivity extends AppCompatActivity {
    private TextProgressCircle tpc_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_circle);
        tpc_progress = findViewById(R.id.tpc_progress);
        initProgressSpinner();
    }

    private void initProgressSpinner() {
        ArrayAdapter<String> progressAdapter = new ArrayAdapter<String>(this, R.layout.item_select, progressArray);
        Spinner sp_progress = findViewById(R.id.sp_progress);
        sp_progress.setPrompt("请选择进度值");
        sp_progress.setAdapter(progressAdapter);
        sp_progress.setOnItemSelectedListener(new DividerSelectedListener());
        sp_progress.setSelection(0);
    }

    private String[] progressArray = {"0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};

    class DividerSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int progress = Integer.parseInt(progressArray[position]);
            tpc_progress.setProgress(progress, -1);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
