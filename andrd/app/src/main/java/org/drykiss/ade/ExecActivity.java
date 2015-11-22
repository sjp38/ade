package org.drykiss.ade;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ExecActivity extends AppCompatActivity {
    private static String TAG = "exec_activity";
    public final static String EXTRA_CMD = "command";
    private String ADE_ASSET = "ade.arm";
    private String ADE_BIN = "ade";
    private String ADE_BIN_PATH = "";

    private BinExecutor mExecutor = null;
    private Process mProc = null;

    TextView mOutputTextView = null;
    Handler mHandler = null;
    static final int MSG_OUTPUT = 1;

    private String adeBinPath() {
        return getBaseContext().getFilesDir().getAbsolutePath()
                + "/" + ADE_BIN;
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

    public void executeAde() {
        File f = new File(ADE_BIN_PATH);
        if (!f.exists()) {
            copyAdeGoBin();
        }
        mProc = mExecutor.executeBin(Arrays.asList(ADE_BIN_PATH), true);
        new DisplayOutputThread(mHandler, ADE_BIN + "-stdout/err",
                mProc.getInputStream())
                .start();
    }

    public void killAde() {
        Toast.makeText(getApplicationContext(), "stop ade",
                Toast.LENGTH_SHORT).show();

        mExecutor.stopExecution();
        mProc = null;
    }

    private static class DisplayOutputThread extends Thread {
        private final Handler mHandler;
        private final BufferedReader mBufIn;
        private final String mTag;

        public DisplayOutputThread(Handler handler, String tag, InputStream in) {
            mHandler = handler;
            mBufIn = new BufferedReader(new InputStreamReader(in));
            mTag = tag;
        }

        private void sendOutput(String output) {
            Message msg = mHandler.obtainMessage();
            msg.what = MSG_OUTPUT;
            msg.obj = output;
            mHandler.sendMessage(msg);
        }

        @Override
        public void run() {
            String tag = TAG + "/" + mTag;
            while (true) {
                String line = null;
                try {
                    line = mBufIn.readLine();
                } catch (IOException e) {
                    Log.d(tag, "Exception: " + e.toString());
                    sendOutput("\n[ade] Exception: " + e.toString() + "\n");
                    return;
                }
                if (line == null) {
                    Log.d(tag, "terminated");
                    sendOutput("\n[ade] terminated\n");
                    // EOF
                    return;
                }
                Log.d(tag, line);
                sendOutput(line + "\n");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exec);

        ADE_BIN_PATH = getBaseContext().getFilesDir().getAbsolutePath() + "/" + ADE_BIN;
        mExecutor = new BinExecutor();

        mOutputTextView = (TextView)findViewById(R.id.output_textview);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what != MSG_OUTPUT) {
                    Log.d(TAG, "unexpected message came to handler");
                    return;
                }
                String out_msg = (String) msg.obj;
                mOutputTextView.append(out_msg);

                super.handleMessage(msg);
            }
        };

        Intent intent = getIntent();
        executeAde();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exec, menu);
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
}
