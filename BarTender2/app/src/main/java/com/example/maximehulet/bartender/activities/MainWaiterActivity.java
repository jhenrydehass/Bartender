package com.example.maximehulet.bartender.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maximehulet.bartender.models.User;
import com.example.maximehulet.bartender.R;

/**
 * Created by maximehulet on 27/04/15.
 */
public class MainWaiterActivity extends MainActivity implements TextView.OnEditorActionListener {
    private int table;

    public int getTable(){
        return table;
    }
    public void setTable(int numTable){
        this.table = numTable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_waiter);

        // Affichage du message de bienvenue.
        TextView welcomeTxt = (TextView) findViewById(R.id.welcomeTxt);
        welcomeTxt.setText(getString(R.string.main_welcome) + " " + User.getConnectedUser().getLogin());

        EditText tableEditText = (EditText) findViewById(R.id.waiter_table_num);
        tableEditText.setOnEditorActionListener(this);
    }

    public void order(View v){
        Intent intent = new Intent(this,OrderActivity.class);
        startActivity(intent);
    }

    public void addition(View v){
        Intent intent = new Intent(this,AdditionActivity.class);
        startActivity(intent);
    }

    public void tableNum(View v){
        EditText tableEditText = (EditText) findViewById(R.id.waiter_table_num);
        int numTable = Integer.parseInt(tableEditText.getText().toString());

        setTable(numTable);
    }

    public void inventory(View v){
        Intent intent = new Intent(this,InventoryActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            tableNum(v);
            return true;
        }
        return false;
    }
}
