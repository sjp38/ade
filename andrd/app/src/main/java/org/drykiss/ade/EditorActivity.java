package org.drykiss.ade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private final static String TAG = "ade";

    private String ADE_ASSET = "ade.arm";
    private String ADE_BIN = "ade";

    private Process goProcess = null;

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
            new CopyToAndroidLogThread(ADE_BIN + "-stdout/stderr", goProcess.getInputStream())
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
        private final BufferedReader mBufIn;
        private final String mTag;

        public CopyToAndroidLogThread(String tag, InputStream in) {
            mBufIn = new BufferedReader(new InputStreamReader(in));
            mTag = tag;
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
                    return;
                }
                if (line == null) {
                    // EOF
                    return;
                }
                Log.d(tag, line);
            }
        }
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
                executeAde();
                Toast.makeText(getApplicationContext(), "Selected Run", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Selected settings", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}