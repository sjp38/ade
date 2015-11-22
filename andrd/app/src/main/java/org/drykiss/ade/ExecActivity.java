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

public class ExecActivity extends AppCompatActivity {
    private static String TAG = "exec_activity";
    public final static String EXTRA_CMD = "command";
    private String ADE_ASSET = "ade.arm";
    private String ADE_BIN = "ade";

    private Process goProcess = null;

    TextView mOutputTextView = null;
    Handler mHandler = null;
    static final int MSG_OUTPUT = 1;

    private String goBinPath(String binname) {
        return getBaseContext().getFilesDir().getAbsolutePath()
                + "/" + binname;
    }

    private void copyGoBinary() {
        String dstFile = goBinPath(ADE_BIN);

        Log.d(TAG, "Copy Go binary from APK to " + dstFile);
        try {
            InputStream is = getAssets().open(ADE_ASSET);
            FileOutputStream fos = getBaseContext().openFileOutput(
                    ADE_BIN, MODE_PRIVATE);
            byte[] buf = new byte[8192];
            int offset;
            while ((offset = is.read(buf)) > 0) {
                fos.write(buf, 0, offset);
            }
            is.close();
            fos.flush();
            fos.close();

            Log.d(TAG, "wrote out " + dstFile);
            Runtime.getRuntime().exec("chmod 0777 " + dstFile);
            Log.d(TAG, "did chmod 0777 on " + dstFile);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.d(TAG, "interrupted from sleep");
            }
            Runtime.getRuntime().exec("sync");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeAde() {
        Toast.makeText(getApplicationContext(), "execute ade",
                Toast.LENGTH_SHORT).show();
        String adePath = goBinPath(ADE_BIN);

        File f = new File(adePath);
        if (!f.exists()) {
            copyGoBinary();
        }

        if (goProcess != null) {
            goProcess.destroy();
            goProcess = null;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(adePath);
            Log.d(TAG, "start process with command " + adePath);
            pb.redirectErrorStream(true);
            goProcess = pb.start();
            Log.d(TAG, "goProcess started");
            new CopyToAndroidLogThread(mHandler, ADE_BIN + "-stdout/stderr",
                    goProcess.getInputStream())
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void killAde() {
        Toast.makeText(getApplicationContext(), "stop ade",
                Toast.LENGTH_SHORT).show();

        if (goProcess != null) {
            goProcess.destroy();
            goProcess = null;
        }
    }

    private static class CopyToAndroidLogThread extends Thread {
        private final Handler mHandler;
        private final BufferedReader mBufIn;
        private final String mTag;

        public CopyToAndroidLogThread(Handler handler, String tag, InputStream in) {
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
        String cmd = intent.getStringExtra(EXTRA_CMD);
        executeAde();

        Log.d(TAG, "received command " + cmd);
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
