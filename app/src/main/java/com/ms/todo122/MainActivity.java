package com.ms.todo122;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //defining objects
    EditText editText;
    Button add;
    ListView listView;
    DBAdapter myDb;
    ImageView checkBox;
    TextView listText;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!TextUtils.isEmpty(editText.getText().toString())) {
                        myDb.insertRow(editText.getText().toString());
                        populatelist();
                        editText.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Set the Task First!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        listText = (TextView) findViewById(R.id.tasktext);
        add = (Button) findViewById(R.id.button);
//        delete = (Button) findViewById(R.id.deletebtn);
//        edit = (Button) findViewById(R.id.editbtn);

        OpenDb();
        populatelist();

        //on press of Add button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    myDb.insertRow(editText.getText().toString());
                    populatelist();
                    editText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Please Set the Task First!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //on Click or on Checked of a listView Item.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("WrongViewCast")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                checkBox = (ImageView) findViewById(R.id.imagecheck);
                final Cursor cursor = myDb.getRow(id);
                if (cursor.moveToFirst()) {
                    int set = 1;
                    long idDb = cursor.getLong(DBAdapter.COL_ROWID);
                    myDb.setStatus(idDb, set);
                    populatelist();
                    counter++;
                }
                cursor.close();
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

//                startActionMode(modeCallBack);
//                view.setSelected(true);
//                return true;


                final PopupMenu popupMenu = new PopupMenu(MainActivity.this, listView);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.delete:
                                myDb.deleteRow(id);
                                populatelist();
                                return true;

                            case R.id.edit:

                                // get prompt_edit.xml view
                                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                                View promptView = layoutInflater.inflate(R.layout.prompt_edit, null);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                alertDialogBuilder.setView(promptView);

                                final EditText editTextprompt = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);

                                // setup a dialog window
                                alertDialogBuilder.setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int Did) {
                                                myDb.updateRow(id, editTextprompt.getText().toString());
                                                populatelist();
                                            }
                                        })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int Did) {
                                                        dialog.cancel();
                                                    }
                                                });

                                // create an alert dialog
                                AlertDialog alert = alertDialogBuilder.create();
                                alert.show();
                                return true;
                        }
                        return true;
                    }
                });
                return true;
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

    //to display the list from the database..
    private void populatelist() {
        Cursor cursor = myDb.getAllRows();
        String[] task = new String[]{DBAdapter.KEY_TASK};
        int[] to = new int[]{R.id.tasktext};
        SimpleCursorAdapter simpleCursorAdapter;
        simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.item_layout, cursor, task, to, 0);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(simpleCursorAdapter);
    }

//    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false;
//        }
//
//        public void onDestroyActionMode(ActionMode mode) {
//            mode = null;
//        }
//
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            mode.setTitle("Options");
//            mode.getMenuInflater().inflate(R.menu.popup_menu, menu);
//            return true;
//        }
//
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            final int id = item.getItemId();
//return true
//        }
//    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.completed) {

            Intent i = new Intent(MainActivity.this, DoneActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

