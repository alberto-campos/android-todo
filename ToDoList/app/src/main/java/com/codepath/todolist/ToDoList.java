package com.codepath.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.File.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.codepath.todolist.Item;

public class ToDoList extends ActionBarActivity {

    // EditText field

    private EditText etItem; // empty
    private TextView tvLabel; // empty
    private ArrayList<String> todoItems;
    private ArrayAdapter<String> todoAdapter;
    private ListView lvItems;
    private EditText etNewItem;


    private final int REQUEST_CODE = 1934;
    private int curPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        etNewItem = (EditText) findViewById(R.id.etItem);
        lvItems = (ListView) findViewById(R.id.lvItems);
      //  readItems();
        readDBItems();

        todoAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, todoItems);
        lvItems.setAdapter(todoAdapter);
        setupListViewListener();

        // Get values from Edit Text
        etItem = (EditText) findViewById(R.id.etItem);
        tvLabel = (TextView) findViewById(R.id.tvLabel);

    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                Item itName = new Item();
                itName.setName(todoItems.get(pos).toString());
                removeDBItem(itName);

                todoItems.remove(pos);
                todoAdapter.notifyDataSetChanged();
               // writeItems();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                curPos = pos;
                launchEditView(todoItems.get(pos).toString());
            }
        });
    }

    private void launchEditView(String item) {
        // Opening Edit view
        Intent i = new Intent(ToDoList.this, EditItemActivity.class);
        i.putExtra("item", item);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String updatedItem = data.getExtras().getString("item");

            if (updatedItem.toString().length() > 0) {
                Item itName = getDBItem(getDBItemId(todoItems.get(curPos)));

                itName.setName(updatedItem);
                updateDBItems(itName);

                todoItems.remove(curPos);
                todoItems.add(curPos, updatedItem);
                todoAdapter.notifyDataSetChanged();
               // writeItems();
                Toast.makeText(this, "Edited: " + updatedItem + ".", Toast.LENGTH_SHORT).show();
            }

            else {
                // String came back empty
                Toast.makeText(this, "Nothing to update", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "User canceled.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_to_do_list, menu);
        return true;
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

    public void onSubmit(View view) {

        String itemText = etNewItem.getText().toString();

        if (itemText.toString().length() > 0 ) {
            todoAdapter.add(itemText);
            etNewItem.setText("");
           // writeItems();
            Item itName = new Item(itemText);
            writeDBItems(itName);

            // Toast to display item added
            Toast.makeText(this, itemText + " added successfully.", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Please enter a task.", Toast.LENGTH_SHORT).show();
    }

    private void readDBItems() {
        TodoItemDatabase db = new TodoItemDatabase(this);
        int maxItems = db.getMaxItems();
        todoItems = new ArrayList<String>();

        if (maxItems == 0 ) {
            Toast.makeText(this, "No tasks found. Add new tasks.", Toast.LENGTH_SHORT).show();
        }

        for (int i = 1; (i <= maxItems); i++) {
            todoItems.add(db.getItem(i).getName());

        }
    }

    private void updateDBItems(Item itemName) {
        TodoItemDatabase db = new TodoItemDatabase(this);
        db.updateItem(itemName);
    }

    private void writeDBItems(Item itemName) {
        TodoItemDatabase db = new TodoItemDatabase(this);
        db.addItem(itemName);
    }

    private void removeDBItem(Item delItem) {
        TodoItemDatabase db = new TodoItemDatabase(this);

        int maxItems = db.getMaxItems();
        if (maxItems > 0)
            db.deleteItem(delItem);
        else
            Toast.makeText(this, "Nothing to update", Toast.LENGTH_SHORT).show();
    }


    private Item getDBItem(int id) {
        TodoItemDatabase db = new TodoItemDatabase(this);
        return db.getItem(id);
    }


    private int getDBItemId(String name) {
        TodoItemDatabase db = new TodoItemDatabase(this);
        return db.getItemId(name);
    }

//    private  void readItems() {
//
//        File fileDir = getFilesDir();
//        File todoFile = new File (fileDir, "todoItems.txt");
//        try {
//            todoItems = new ArrayList<String>(FileUtils.readLines(todoFile));
//        } catch (IOException e) {
//            todoItems = new ArrayList<String>();
//        }
//
//    }
//
//    private void writeItems() {
//        File filesDir = getFilesDir();
//        File todoFile = new File(filesDir, "todoItems.txt");
//        try {
//            FileUtils.writeLines(todoFile, todoItems);
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }

    public void onAddDueDate(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select priority")
                .setItems(R.array.priority_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Resources res = getResources();
                        String[] priorities = res.getStringArray(R.array.priority_array);

                        Toast.makeText(getApplicationContext(), "This task should get done "+ priorities[which].toLowerCase() + ".", Toast.LENGTH_SHORT).show();
                    }
                });

        builder.create().show();

    }
}
