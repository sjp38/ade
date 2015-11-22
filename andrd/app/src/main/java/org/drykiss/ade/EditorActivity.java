package org.drykiss.ade;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EditorActivity extends AppCompatActivity {
    private final static String TAG = "ade_editor";

    private static String ADE_ASSET = "ade.arm";
    private static String ADE_BIN = "ade";
    private String ADE_BIN_PATH = "";
    private String TMP_DIR = "tmp";
    private String TMP_SRC = "tmp.go";

    private EditText mSrcEdittext = null;
    private BinExecutor mExecutor = null;
    private Process mProc = null;
    private String mFilename = null;

    private void doNew() {
        LayoutInflater inflater = this.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New file name")
                .setMessage("Insert file name for new Go program")
                .setView(inflater.inflate(R.layout.dialog_edittext, null))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edittext = (EditText)((AlertDialog)dialog)
                                .findViewById(R.id.input_edittext);
                        mFilename = edittext.getText().toString();
                        Toast.makeText(getApplicationContext(), mFilename, Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing for now.
                    }
                })
                .create().show();
    }

    private void doLoad() {
    }

    private void doRun() {
        Intent intent = new Intent(this, ExecActivity.class);
        intent.putExtra(ExecActivity.EXTRA_CMD, "run");
        startActivity(intent);
    }

    private void copyAdeGoBin() {
        Log.d(TAG, "Copy Go binary from APK to " + ADE_BIN_PATH);
        try {
            InputStream is = getAssets().open(ADE_ASSET);
            mExecutor.copyExecBinary(is, ADE_BIN_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mSrcEdittext = (EditText)findViewById(R.id.src_editText)

        ADE_BIN_PATH = getBaseContext().getFilesDir().getAbsolutePath() + "/" + ADE_BIN;
        mExecutor = new BinExecutor();

        File f = new File(ADE_BIN_PATH);
        if (!f.exists()) {
            copyAdeGoBin();
        }
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
                doNew();
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