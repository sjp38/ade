package org.drykiss.ade;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EditorActivity extends AppCompatActivity {
    private final static String TAG = "ade_editor";


    private void doRun() {
        Intent intent = new Intent(this, ExecActivity.class);
        intent.putExtra(ExecActivity.EXTRA_CMD, "ade");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Toast.makeText(getApplicationContext(), "Selected New", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_load:
                Toast.makeText(getApplicationContext(), "Selected Load", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_save:
                Toast.makeText(getApplicationContext(), "Selected Save", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_save_as:
                Toast.makeText(getApplicationContext(), "Selected Save as", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_format:
                Toast.makeText(getApplicationContext(), "Selected Format", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_run:
                doRun();
                Toast.makeText(getApplicationContext(), "Selected Run", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Selected settings", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}