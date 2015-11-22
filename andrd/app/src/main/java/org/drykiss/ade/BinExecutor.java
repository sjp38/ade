package org.drykiss.ade;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BinExecutor {
    static String TAG = "binExecutor";
    private Process mProc = null;

    public void copyExecBinary(InputStream is, String dstFile) {
        Log.d(TAG, "Copy Go binary from APK to " + dstFile);
        try {
            FileOutputStream fos = new FileOutputStream(dstFile);
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

    public Process executeBin(String binPath, boolean redirStderr) {
        Log.d(TAG, "execute " + binPath);

        File f = new File(binPath);
        if (!f.exists()) {
            Log.e(TAG, "file " + binPath + " not exist!");
        }

        if (mProc != null) {
            mProc.destroy();
            mProc = null;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(binPath);
            Log.d(TAG, "start process with command " + binPath);
            pb.redirectErrorStream(redirStderr);
            mProc = pb.start();
            Log.d(TAG, "goProcess started");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mProc;
    }

    public void stopExecution() {
        Log.d(TAG, "stop execution");

        if (mProc != null) {
            mProc.destroy();
            mProc = null;
        }
    }
}
