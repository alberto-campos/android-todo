package com.codepath.todolist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class EditItemActivity extends ActionBarActivity {

    private String myItem;
    private EditText editedItem;

    private final int REQUEST_CODE = 1934;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        myItem = getIntent().getStringExtra("item");

        editedItem = (EditText) findViewById(R.id.etModifyItem);
        editedItem.setText(myItem);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
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
        Intent data = new Intent();
        TodoItemDatabase db = new TodoItemDatabase(this);
        Item itName = new Item();

        itName = db.getItem(db.getItemId(myItem));

        myItem = editedItem.getText().toString();

        itName.setName(myItem);

        db.updateItem(itName);
        db.close();

        // pass data back
        data.putExtra("item", myItem);
        setResult(RESULT_OK, data);
        this.finish();
    }

    public void onCancel(View view) {
        setResult(RESULT_CANCELED);
        this.finish();
    }
}
