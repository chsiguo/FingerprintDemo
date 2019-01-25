package com.createchance.fingerprintdemo;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Handler;
//import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by baniel on 7/21/16.
 */
public class MyAuthCallback extends FingerprintManager.AuthenticationCallback {

    private Handler handler = null;
    private String TAG="fingerprint";
    public MyAuthCallback(Handler handler) {
        super();

        this.handler = handler;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);

        if (handler != null) {
            handler.obtainMessage(MainActivity.MSG_AUTH_ERROR, errMsgId, 0).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        super.onAuthenticationHelp(helpMsgId, helpString);

        if (handler != null) {
            handler.obtainMessage(MainActivity.MSG_AUTH_HELP, helpMsgId, 0).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
       // int fingerId = result.getFingerprint().getFingerId();

       try {
            Field field = result.getClass().getDeclaredField("mFingerprint");
            field.setAccessible(true);
            Object fingerPrint = field.get(result);

            Class<?> clzz = Class.forName("android.hardware.fingerprint.Fingerprint");
            Method getName = clzz.getDeclaredMethod("getName");
            Method getFingerId = clzz.getDeclaredMethod("getFingerId");
            Method getGroupId = clzz.getDeclaredMethod("getGroupId");
            Method getDeviceId = clzz.getDeclaredMethod("getDeviceId");

            CharSequence name = (CharSequence) getName.invoke(fingerPrint);
            int fingerId = (int) getFingerId.invoke(fingerPrint);
            int groupId = (int) getGroupId.invoke(fingerPrint);
            long deviceId = (long) getDeviceId.invoke(fingerPrint);
            if (handler != null) {
                handler.obtainMessage(MainActivity.MSG_AUTH_SUCCESS, fingerId, 0).sendToTarget();
            }
            Log.d(TAG, "name: " + name);
            Log.d(TAG, "fingerId: " + fingerId);
            Log.d(TAG, "groupId: " + groupId);
            Log.d(TAG, "deviceId: " + deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            if (handler != null) {
                handler.obtainMessage(MainActivity.MSG_AUTH_SUCCESS, 7, 0).sendToTarget();
            }
        }

    }
    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();

        if (handler != null) {
            handler.obtainMessage(MainActivity.MSG_AUTH_FAILED).sendToTarget();
        }
    }
}
