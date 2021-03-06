package com.codepath.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class ToDoList extends ActionBarActivity {
    private EditText etItem; // empty
    private TextView tvLabel; // empty
    private List<Item> myItems = new ArrayList<Item>();
    private ArrayAdapter<Item> myAdapter;

    private ListView lvItems;
    private EditText etNewItem;
    private Button dueBtn;

    private final int REQUEST_CODE = 1934;
    private int curPos;
    private int currentDue = Item.DEFAULT_TIMESTAMP;
    private int curStatus;
    private String curName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        setTitle("Codepath Todo List");

        etNewItem = (EditText) findViewById(R.id.etItem);
        lvItems = (ListView) findViewById(R.id.lvItems);
        dueBtn = (Button) findViewById(R.id.btnDue);

        readDBItems();

        myAdapter = new MyListAdapter();

        // Populate List View
        ListView list = (ListView) findViewById(R.id.lvItems);
        list.setAdapter(myAdapter);

        setupListViewListener();

        // Get values from Edit Text
        etItem = (EditText) findViewById(R.id.etItem);
        tvLabel = (TextView) findViewById(R.id.tvLabel);
    }

    private class MyListAdapter extends ArrayAdapter<Item> {
        public MyListAdapter() {
            super(ToDoList.this, R.layout.item_view, myItems);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;


           // final int pos = position;

            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            // find the item
            Item currentItem;
            currentItem = getItem(position);
            curName = currentItem.getName();

            // fill the view
            final TextView myTask = (TextView) itemView.findViewById(R.id.item_tvDescription);
            final TextView myDue = (TextView) itemView.findViewById(R.id.item_tvDue);
            final ImageView myStatus = (ImageView) itemView.findViewById(R.id.item_ivStatus);
            final Item finalCurrentItem = currentItem;

            myStatus.setOnClickListener(new ImageView.OnClickListener() {
               @Override
               public void onClick(View v) {

                   curName = finalCurrentItem.getName();

                   curStatus = getDBItemStatus(curName);
                 //  Toast.makeText(ToDoList.this, "Editing: "+ curStatus, Toast.LENGTH_SHORT).show();
                   flipDBStatus(curName, curStatus);

                   if (curStatus == 1){ // Task was flipped to done, set it to inactive
                       myStatus.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_015);
                       myTask.setPaintFlags(myTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                       myDue.setPaintFlags(myDue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                   }
                   else {
                       myStatus.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_000);
                       myTask.setPaintFlags(myTask.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                       myDue.setPaintFlags(myDue.getPaintFlags() & ~ Paint.STRIKE_THRU_TEXT_FLAG);
                   }
               }
           } );

            curStatus = getDBItemStatus(curName);

            if (curStatus == 0){  // Task IS done, show it as such.
                myStatus.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_015);
                myTask.setPaintFlags(myTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                myDue.setPaintFlags(myDue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else {
                myStatus.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_000);
                myTask.setPaintFlags(myTask.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                myDue.setPaintFlags(myDue.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            myTask.setText(currentItem.getName());
            myDue.setText(getValueFromArray(currentItem.getDue()));
            return itemView;
        }
    }


    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                Item itName = new Item();

                itName.setName(myItems.get(pos).getName());

                removeDBItem(itName);
                myItems.remove(pos);
                myAdapter.notifyDataSetChanged();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                curPos = pos;
                launchEditView(myItems.get(pos).getName());
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

            if (updatedItem.length() > 0) {
                Item thisName = getDBItem(getDBItemId(updatedItem));

                myItems.remove(curPos);
                myItems.add(curPos, thisName);

                myAdapter.notifyDataSetChanged();
            }
            else {
                // String came back empty
                Toast.makeText(this, "Nothing to update", Toast.LENGTH_SHORT).show();
            }
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

        if (itemText.length() > 0 ) {
            etNewItem.setText("");
            Item itName = new Item(itemText, currentDue);
            myAdapter.add(itName);
            writeDBItems(itName);
        }
        else {
            Toast.makeText(this, "Please enter a task.", Toast.LENGTH_SHORT).show();
        }
    }

    private void readDBItems() {
        TodoItemDatabase db = new TodoItemDatabase(this);
        int maxItems = db.getMaxItems();

        if (maxItems == 0 ) {
            Toast.makeText(this, "No tasks found. Add new tasks.", Toast.LENGTH_SHORT).show();
        }
        else {
            myItems = db.getItemsList();
        }
    }

    private void writeDBItems(Item itemName) {
        TodoItemDatabase db = new TodoItemDatabase(this);
        db.addItem(itemName);
    }

    private void flipDBStatus(String name, int status) {

        TodoItemDatabase db = new TodoItemDatabase(this);

        db.flipStatus(name, status);
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

    private int getDBItemStatus(String name) {
        TodoItemDatabase db = new TodoItemDatabase(this);
        return db.getItemStatus(name);
    }

    public void onAddDueDate(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select priority")
                .setItems(R.array.priority_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Resources res = getResources();
                        String[] priorities = res.getStringArray(R.array.priority_array);
                        currentDue = which;

                        dueBtn.setText(priorities[which]);
                    }
                });

        builder.create().show();
    }

    private String getValueFromArray (int index) {
        Resources res = getResources();
        String[] priorities = res.getStringArray(R.array.priority_array);
        return priorities[index];
    }

}
