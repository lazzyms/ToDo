package com.ms.todo122;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by SMS on 2/13/2016.
 */
public class DoneActivity extends AppCompatActivity {

    ListView doneList;
    DBAdapter myDb;
    TextView doneText;
    Button clear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        doneList = (ListView) findViewById(R.id.listView2);
        doneText = (TextView) findViewById(R.id.completedtasktext);

        OpenDb();
        completeList();

        clear = (Button) findViewById(R.id.button2);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleardata(v);
                completeList();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseDb();
    }

    private void OpenDb() {
        myDb = new DBAdapter(this);
        myDb.open();
    }

    private void CloseDb() {
        myDb = new DBAdapter(this);
        myDb.close();
    }

    // for Complete Task List
    private void completeList() {
        Cursor cursor = myDb.completed();
        String[] task = new String[]{DBAdapter.KEY_TASK};
        int[] to = new int[]{R.id.completedtasktext};
        SimpleCursorAdapter simpleCursorAdapter;
        simpleCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.done_item_layout, cursor, task, to, 0);
        doneList = (ListView) findViewById(R.id.listView2);
        doneList.setAdapter(simpleCursorAdapter);
    }

    //for deleting all data from completeTask
    public void cleardata(View view) {
        myDb.deleteCompleted();
        completeList();
    }

}
