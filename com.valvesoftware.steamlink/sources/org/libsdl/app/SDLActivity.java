package org.libsdl.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.PointerIcon;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Hashtable;
import java.util.Locale;
import org.qtproject.qt5.android.QtNative;

/* loaded from: classes.dex */
public class SDLActivity extends Activity implements View.OnSystemUiVisibilityChangeListener {
    static final int COMMAND_CHANGE_TITLE = 1;
    static final int COMMAND_CHANGE_WINDOW_STYLE = 2;
    static final int COMMAND_SET_KEEP_SCREEN_ON = 5;
    static final int COMMAND_TEXTEDIT_HIDE = 3;
    protected static final int COMMAND_USER = 32768;
    private static final int SDL_MAJOR_VERSION = 3;
    private static final int SDL_MICRO_VERSION = 0;
    private static final int SDL_MINOR_VERSION = 0;
    protected static final int SDL_ORIENTATION_LANDSCAPE = 1;
    protected static final int SDL_ORIENTATION_LANDSCAPE_FLIPPED = 2;
    protected static final int SDL_ORIENTATION_PORTRAIT = 3;
    protected static final int SDL_ORIENTATION_PORTRAIT_FLIPPED = 4;
    protected static final int SDL_ORIENTATION_UNKNOWN = 0;
    private static final int SDL_SYSTEM_CURSOR_ARROW = 0;
    private static final int SDL_SYSTEM_CURSOR_CROSSHAIR = 3;
    private static final int SDL_SYSTEM_CURSOR_HAND = 11;
    private static final int SDL_SYSTEM_CURSOR_IBEAM = 1;
    private static final int SDL_SYSTEM_CURSOR_NO = 10;
    private static final int SDL_SYSTEM_CURSOR_SIZEALL = 9;
    private static final int SDL_SYSTEM_CURSOR_SIZENESW = 6;
    private static final int SDL_SYSTEM_CURSOR_SIZENS = 8;
    private static final int SDL_SYSTEM_CURSOR_SIZENWSE = 5;
    private static final int SDL_SYSTEM_CURSOR_SIZEWE = 7;
    private static final int SDL_SYSTEM_CURSOR_WAIT = 2;
    private static final int SDL_SYSTEM_CURSOR_WAITARROW = 4;
    private static final String TAG = "SDL";
    protected static boolean mActivityCreated;
    public static boolean mBrokenLibraries;
    protected static SDLClipboardHandler mClipboardHandler;
    protected static Locale mCurrentLocale;
    public static NativeState mCurrentNativeState;
    protected static int mCurrentRotation;
    protected static Hashtable<Integer, PointerIcon> mCursors;
    protected static boolean mFullscreenModeActive;
    protected static HIDDeviceManager mHIDDeviceManager;
    public static boolean mHasFocus;
    public static final boolean mHasMultiWindow;
    public static boolean mIsResumedCalled;
    protected static int mLastCursorID;
    protected static ViewGroup mLayout;
    protected static SDLGenericMotionListener_API12 mMotionListener;
    public static NativeState mNextNativeState;
    protected static boolean mSDLMainFinished;
    protected static Thread mSDLThread;
    protected static boolean mScreenKeyboardShown;
    protected static SDLActivity mSingleton;
    protected static SDLSurface mSurface;
    protected static DummyEdit mTextEdit;
    Handler commandHandler = new SDLCommandHandler();
    protected final int[] messageboxSelection = new int[1];
    private final Runnable rehideSystemUi = new Runnable() { // from class: org.libsdl.app.SDLActivity.7
        @Override // java.lang.Runnable
        public void run() {
            SDLActivity.this.getWindow().getDecorView().setSystemUiVisibility(5894);
        }
    };

    /* loaded from: classes.dex */
    public enum NativeState {
        INIT,
        RESUMED,
        PAUSED
    }

    public static native void nativeAddTouch(int i, String str);

    public static native boolean nativeAllowRecreateActivity();

    public static native int nativeCheckSDLThreadCounter();

    public static native void nativeFocusChanged(boolean z);

    public static native String nativeGetHint(String str);

    public static native boolean nativeGetHintBoolean(String str, boolean z);

    public static native String nativeGetVersion();

    public static native void nativeLowMemory();

    public static native void nativePause();

    public static native void nativePermissionResult(int i, boolean z);

    public static native void nativeQuit();

    public static native void nativeResume();

    public static native int nativeRunMain(String str, String str2, Object obj);

    public static native void nativeSendQuit();

    public static native void nativeSetNaturalOrientation(int i);

    public static native void nativeSetScreenResolution(int i, int i2, int i3, int i4, float f, float f2);

    public static native void nativeSetenv(String str, String str2);

    public static native int nativeSetupJNI();

    public static native void onNativeAccel(float f, float f2, float f3);

    public static native void onNativeClipboardChanged();

    public static native void onNativeDarkModeChanged(boolean z);

    public static native void onNativeDropFile(String str);

    public static native void onNativeKeyDown(int i);

    public static native void onNativeKeyUp(int i);

    public static native void onNativeKeyboardFocusLost();

    public static native void onNativeLocaleChanged();

    public static native void onNativeMouse(int i, int i2, float f, float f2, boolean z);

    public static native void onNativeResize();

    public static native void onNativeRotationChanged(int i);

    public static native boolean onNativeSoftReturnKey();

    public static native void onNativeSurfaceChanged();

    public static native void onNativeSurfaceCreated();

    public static native void onNativeSurfaceDestroyed();

    public static native void onNativeTouch(int i, int i2, int i3, float f, float f2, float f3);

    public static boolean shouldMinimizeOnFocusLoss() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String[] getArguments() {
        return new String[0];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getMainFunction() {
        return "SDL_main";
    }

    protected boolean onUnhandledMessage(int i, Object obj) {
        return false;
    }

    static {
        mHasMultiWindow = Build.VERSION.SDK_INT >= 24;
        mBrokenLibraries = true;
        mSDLMainFinished = false;
        mActivityCreated = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static SDLGenericMotionListener_API12 getMotionListener() {
        if (mMotionListener == null) {
            if (Build.VERSION.SDK_INT >= 26) {
                mMotionListener = new SDLGenericMotionListener_API26();
            } else if (Build.VERSION.SDK_INT >= 24) {
                mMotionListener = new SDLGenericMotionListener_API24();
            } else {
                mMotionListener = new SDLGenericMotionListener_API12();
            }
        }
        return mMotionListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getMainSharedObject() {
        String str;
        String[] libraries = mSingleton.getLibraries();
        if (libraries.length > 0) {
            str = "lib" + libraries[libraries.length - 1] + ".so";
        } else {
            str = "libmain.so";
        }
        return getContext().getApplicationInfo().nativeLibraryDir + "/" + str;
    }

    protected String[] getLibraries() {
        return new String[]{"SDL3", "main"};
    }

    public void loadLibraries() {
        for (String str : getLibraries()) {
            SDL.loadLibrary(str);
        }
    }

    public static void initialize() {
        mSingleton = null;
        mSurface = null;
        mTextEdit = null;
        mLayout = null;
        mClipboardHandler = null;
        mCursors = new Hashtable<>();
        mLastCursorID = 0;
        mSDLThread = null;
        mIsResumedCalled = false;
        mHasFocus = true;
        mNextNativeState = NativeState.INIT;
        mCurrentNativeState = NativeState.INIT;
    }

    protected SDLSurface createSDLSurface(Context context) {
        return new SDLSurface(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        String message;
        String path;
        LocaleList locales;
        Locale locale;
        Log.v(TAG, "Device: " + Build.DEVICE);
        Log.v(TAG, "Model: " + Build.MODEL);
        Log.v(TAG, "onCreate()");
        super.onCreate(bundle);
        if (mSDLMainFinished || mActivityCreated) {
            boolean nativeAllowRecreateActivity = nativeAllowRecreateActivity();
            if (mSDLMainFinished) {
                Log.v(TAG, "SDL main() finished");
            }
            if (nativeAllowRecreateActivity) {
                Log.v(TAG, "activity re-created");
            } else {
                Log.v(TAG, "activity finished");
                System.exit(0);
                return;
            }
        }
        mActivityCreated = true;
        try {
            Thread.currentThread().setName("SDLActivity");
        } catch (Exception e) {
            Log.v(TAG, "modify thread properties failed " + e.toString());
        }
        try {
            loadLibraries();
            mBrokenLibraries = false;
            message = "";
        } catch (Exception e2) {
            System.err.println(e2.getMessage());
            mBrokenLibraries = true;
            message = e2.getMessage();
        } catch (UnsatisfiedLinkError e3) {
            System.err.println(e3.getMessage());
            mBrokenLibraries = true;
            message = e3.getMessage();
        }
        if (!mBrokenLibraries) {
            String str = String.valueOf(3) + "." + String.valueOf(0) + "." + String.valueOf(0);
            String nativeGetVersion = nativeGetVersion();
            if (!nativeGetVersion.equals(str)) {
                mBrokenLibraries = true;
                message = "SDL C/Java version mismatch (expected " + str + ", got " + nativeGetVersion + ")";
            }
        }
        if (mBrokenLibraries) {
            mSingleton = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("An error occurred while trying to start the application. Please try again and/or reinstall." + System.getProperty("line.separator") + System.getProperty("line.separator") + "Error: " + message);
            builder.setTitle("SDL Error");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() { // from class: org.libsdl.app.SDLActivity.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    SDLActivity.mSingleton.finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
            return;
        }
        int nativeCheckSDLThreadCounter = nativeCheckSDLThreadCounter();
        if (nativeCheckSDLThreadCounter != 0) {
            if (nativeAllowRecreateActivity()) {
                Log.v(TAG, "activity re-created // run_count: " + nativeCheckSDLThreadCounter);
            } else {
                Log.v(TAG, "activity finished // run_count: " + nativeCheckSDLThreadCounter);
                System.exit(0);
                return;
            }
        }
        SDL.setupJNI();
        SDL.initialize();
        mSingleton = this;
        SDL.setContext(this);
        mClipboardHandler = new SDLClipboardHandler();
        mHIDDeviceManager = HIDDeviceManager.acquire(this);
        mSurface = createSDLSurface(this);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        mLayout = relativeLayout;
        relativeLayout.addView(mSurface);
        nativeSetNaturalOrientation(getNaturalOrientation());
        int currentRotation = getCurrentRotation();
        mCurrentRotation = currentRotation;
        onNativeRotationChanged(currentRotation);
        try {
            if (Build.VERSION.SDK_INT < 24) {
                mCurrentLocale = getContext().getResources().getConfiguration().locale;
            } else {
                locales = getContext().getResources().getConfiguration().getLocales();
                locale = locales.get(0);
                mCurrentLocale = locale;
            }
        } catch (Exception unused) {
        }
        int i = getContext().getResources().getConfiguration().uiMode & 48;
        if (i == 16) {
            onNativeDarkModeChanged(false);
        } else if (i == 32) {
            onNativeDarkModeChanged(true);
        }
        setContentView(mLayout);
        setWindowStyle(false);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null || (path = intent.getData().getPath()) == null) {
            return;
        }
        Log.v(TAG, "Got filename: " + path);
        onNativeDropFile(path);
    }

    protected void pauseNativeThread() {
        mNextNativeState = NativeState.PAUSED;
        mIsResumedCalled = false;
        if (mBrokenLibraries) {
            return;
        }
        handleNativeState();
    }

    protected void resumeNativeThread() {
        mNextNativeState = NativeState.RESUMED;
        mIsResumedCalled = true;
        if (mBrokenLibraries) {
            return;
        }
        handleNativeState();
    }

    @Override // android.app.Activity
    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();
        HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(true);
        }
        if (mHasMultiWindow) {
            return;
        }
        pauseNativeThread();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();
        HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(false);
        }
        if (mHasMultiWindow) {
            return;
        }
        resumeNativeThread();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStop() {
        Log.v(TAG, "onStop()");
        super.onStop();
        if (mHasMultiWindow) {
            pauseNativeThread();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStart() {
        Log.v(TAG, "onStart()");
        super.onStart();
        if (mHasMultiWindow) {
            resumeNativeThread();
        }
    }

    public static int getNaturalOrientation() {
        Activity activity = (Activity) getContext();
        if (activity == null) {
            return 0;
        }
        Configuration configuration = activity.getResources().getConfiguration();
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        if ((rotation == 0 || rotation == 2) && configuration.orientation == 2) {
            return 1;
        }
        return ((rotation == 1 || rotation == 3) && configuration.orientation == 1) ? 1 : 3;
    }

    public static int getCurrentRotation() {
        int rotation;
        Activity activity = (Activity) getContext();
        if (activity == null || (rotation = activity.getWindowManager().getDefaultDisplay().getRotation()) == 0) {
            return 0;
        }
        if (rotation == 1) {
            return 90;
        }
        if (rotation != 2) {
            return rotation != 3 ? 0 : 270;
        }
        return 180;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        Log.v(TAG, "onWindowFocusChanged(): " + z);
        if (mBrokenLibraries) {
            return;
        }
        mHasFocus = z;
        if (z) {
            mNextNativeState = NativeState.RESUMED;
            getMotionListener().reclaimRelativeMouseModeIfNeeded();
            handleNativeState();
            nativeFocusChanged(true);
            return;
        }
        nativeFocusChanged(false);
        if (mHasMultiWindow) {
            return;
        }
        mNextNativeState = NativeState.PAUSED;
        handleNativeState();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        Log.v(TAG, "onLowMemory()");
        super.onLowMemory();
        if (mBrokenLibraries) {
            return;
        }
        nativeLowMemory();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        Log.v(TAG, "onConfigurationChanged()");
        super.onConfigurationChanged(configuration);
        if (mBrokenLibraries) {
            return;
        }
        Locale locale = mCurrentLocale;
        if (locale == null || !locale.equals(configuration.locale)) {
            mCurrentLocale = configuration.locale;
            onNativeLocaleChanged();
        }
        int i = configuration.uiMode & 48;
        if (i == 16) {
            onNativeDarkModeChanged(false);
        } else {
            if (i != 32) {
                return;
            }
            onNativeDarkModeChanged(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        Log.v(TAG, "onDestroy()");
        HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            HIDDeviceManager.release(hIDDeviceManager);
            mHIDDeviceManager = null;
        }
        SDLAudioManager.release(this);
        if (mBrokenLibraries) {
            super.onDestroy();
            return;
        }
        if (mSDLThread != null) {
            nativeSendQuit();
            try {
                mSDLThread.join(500L);
            } catch (Exception e) {
                Log.v(TAG, "Problem stopping SDLThread: " + e);
            }
        }
        nativeQuit();
        super.onDestroy();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (nativeGetHintBoolean("SDL_ANDROID_TRAP_BACK_BUTTON", false) || isFinishing()) {
            return;
        }
        super.onBackPressed();
    }

    public static void manualBackButton() {
        mSingleton.pressBackButton();
    }

    public void pressBackButton() {
        runOnUiThread(new Runnable() { // from class: org.libsdl.app.SDLActivity.2
            @Override // java.lang.Runnable
            public void run() {
                if (SDLActivity.this.isFinishing()) {
                    return;
                }
                SDLActivity.this.superOnBackPressed();
            }
        });
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode;
        if (mBrokenLibraries || (keyCode = keyEvent.getKeyCode()) == 25 || keyCode == 24 || keyCode == 27 || keyCode == 168 || keyCode == 169) {
            return false;
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public static void handleNativeState() {
        NativeState nativeState = mNextNativeState;
        if (nativeState == mCurrentNativeState) {
            return;
        }
        if (nativeState == NativeState.INIT) {
            mCurrentNativeState = mNextNativeState;
            return;
        }
        if (mNextNativeState == NativeState.PAUSED) {
            if (mSDLThread != null) {
                nativePause();
            }
            SDLSurface sDLSurface = mSurface;
            if (sDLSurface != null) {
                sDLSurface.handlePause();
            }
            mCurrentNativeState = mNextNativeState;
            return;
        }
        if (mNextNativeState == NativeState.RESUMED && mSurface.mIsSurfaceReady && mHasFocus && mIsResumedCalled) {
            if (mSDLThread == null) {
                mSDLThread = new Thread(new SDLMain(), "SDLThread");
                mSurface.enableSensor(1, true);
                mSDLThread.start();
            } else {
                nativeResume();
            }
            mSurface.handleResume();
            mCurrentNativeState = mNextNativeState;
        }
    }

    /* loaded from: classes.dex */
    protected static class SDLCommandHandler extends Handler {
        protected SDLCommandHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Window window;
            Context context = SDL.getContext();
            if (context == null) {
                Log.e(SDLActivity.TAG, "error handling message, getContext() returned null");
                return;
            }
            int i = message.arg1;
            if (i == 1) {
                if (context instanceof Activity) {
                    ((Activity) context).setTitle((String) message.obj);
                    return;
                } else {
                    Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
                    return;
                }
            }
            if (i == 2) {
                if (context instanceof Activity) {
                    Window window2 = ((Activity) context).getWindow();
                    if (window2 != null) {
                        if ((message.obj instanceof Integer) && ((Integer) message.obj).intValue() != 0) {
                            window2.getDecorView().setSystemUiVisibility(5894);
                            window2.addFlags(1024);
                            window2.clearFlags(2048);
                            SDLActivity.mFullscreenModeActive = true;
                            return;
                        }
                        window2.getDecorView().setSystemUiVisibility(256);
                        window2.addFlags(2048);
                        window2.clearFlags(1024);
                        SDLActivity.mFullscreenModeActive = false;
                        return;
                    }
                    return;
                }
                Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
                return;
            }
            if (i == 3) {
                if (SDLActivity.mTextEdit != null) {
                    SDLActivity.mTextEdit.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                    ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(SDLActivity.mTextEdit.getWindowToken(), 0);
                    SDLActivity.mScreenKeyboardShown = false;
                    SDLActivity.mSurface.requestFocus();
                    return;
                }
                return;
            }
            if (i == 5) {
                if (!(context instanceof Activity) || (window = ((Activity) context).getWindow()) == null) {
                    return;
                }
                if ((message.obj instanceof Integer) && ((Integer) message.obj).intValue() != 0) {
                    window.addFlags(128);
                    return;
                } else {
                    window.clearFlags(128);
                    return;
                }
            }
            if (!(context instanceof SDLActivity) || ((SDLActivity) context).onUnhandledMessage(message.arg1, message.obj)) {
                return;
            }
            Log.e(SDLActivity.TAG, "error handling message, command is " + message.arg1);
        }
    }

    boolean sendCommand(int i, Object obj) {
        Message obtainMessage = this.commandHandler.obtainMessage();
        obtainMessage.arg1 = i;
        obtainMessage.obj = obj;
        boolean sendMessage = this.commandHandler.sendMessage(obtainMessage);
        if (i == 2) {
            boolean z = false;
            if (obj instanceof Integer) {
                Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                defaultDisplay.getRealMetrics(displayMetrics);
                if (displayMetrics.widthPixels == mSurface.getWidth() && displayMetrics.heightPixels == mSurface.getHeight()) {
                    z = true;
                }
                if (((Integer) obj).intValue() == 1) {
                    z = !z;
                }
            }
            if (z && getContext() != null) {
                synchronized (getContext()) {
                    try {
                        getContext().wait(500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sendMessage;
    }

    public static boolean setActivityTitle(String str) {
        return mSingleton.sendCommand(1, str);
    }

    public static void setWindowStyle(boolean z) {
        mSingleton.sendCommand(2, Integer.valueOf(z ? 1 : 0));
    }

    public static void setOrientation(int i, int i2, boolean z, String str) {
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity != null) {
            sDLActivity.setOrientationBis(i, i2, z, str);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:33:0x0075, code lost:
    
        if (r7 != false) goto L59;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setOrientationBis(int i, int i2, boolean z, String str) {
        int i3;
        int i4;
        boolean contains = str.contains("LandscapeRight");
        int i5 = SDL_SYSTEM_CURSOR_SIZENESW;
        if (contains && str.contains("LandscapeLeft")) {
            i3 = SDL_SYSTEM_CURSOR_SIZENESW;
        } else if (str.contains("LandscapeLeft")) {
            i3 = 0;
        } else {
            i3 = str.contains("LandscapeRight") ? 8 : -1;
        }
        boolean z2 = str.contains("Portrait ") || str.endsWith("Portrait");
        if (z2 && str.contains("PortraitUpsideDown")) {
            i4 = SDL_SYSTEM_CURSOR_SIZEWE;
        } else if (z2) {
            i4 = 1;
        } else {
            i4 = str.contains("PortraitUpsideDown") ? SDL_SYSTEM_CURSOR_SIZEALL : -1;
        }
        boolean z3 = i3 != -1;
        boolean z4 = i4 != -1;
        int i6 = SDL_SYSTEM_CURSOR_NO;
        if (z4 || z3) {
            if (!z) {
                if (z4) {
                }
                i3 = i4;
                i6 = i3;
            } else if (!z4 || !z3) {
            }
        } else if (!z) {
            if (i <= i2) {
                i5 = SDL_SYSTEM_CURSOR_SIZEWE;
            }
            i6 = i5;
        }
        Log.v(TAG, "setOrientation() requestedOrientation=" + i6 + " width=" + i + " height=" + i2 + " resizable=" + z + " hint=" + str);
        mSingleton.setRequestedOrientation(i6);
    }

    public static void minimizeWindow() {
        if (mSingleton == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        mSingleton.startActivity(intent);
    }

    public static boolean isScreenKeyboardShown() {
        if (mTextEdit != null && mScreenKeyboardShown) {
            return ((InputMethodManager) SDL.getContext().getSystemService("input_method")).isAcceptingText();
        }
        return false;
    }

    public static boolean supportsRelativeMouse() {
        if (Build.VERSION.SDK_INT >= 27 || !isDeXMode()) {
            return getMotionListener().supportsRelativeMouse();
        }
        return false;
    }

    public static boolean setRelativeMouseEnabled(boolean z) {
        if (!z || supportsRelativeMouse()) {
            return getMotionListener().setRelativeMouseEnabled(z);
        }
        return false;
    }

    public static boolean sendMessage(int i, int i2) {
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity == null) {
            return false;
        }
        return sDLActivity.sendCommand(i, Integer.valueOf(i2));
    }

    public static Context getContext() {
        return SDL.getContext();
    }

    public static boolean isAndroidTV() {
        if (((UiModeManager) getContext().getSystemService("uimode")).getCurrentModeType() == 4) {
            return true;
        }
        if (Build.MANUFACTURER.equals("MINIX") && Build.MODEL.equals("NEO-U1")) {
            return true;
        }
        if (Build.MANUFACTURER.equals("Amlogic") && Build.MODEL.equals("X96-W")) {
            return true;
        }
        return Build.MANUFACTURER.equals("Amlogic") && Build.MODEL.startsWith("TV");
    }

    public static double getDiagonal() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Activity activity = (Activity) getContext();
        if (activity == null) {
            return 0.0d;
        }
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double d = displayMetrics.widthPixels / displayMetrics.xdpi;
        double d2 = displayMetrics.heightPixels / displayMetrics.ydpi;
        return Math.sqrt((d * d) + (d2 * d2));
    }

    public static boolean isTablet() {
        return getDiagonal() >= 7.0d;
    }

    public static boolean isChromebook() {
        if (getContext() == null) {
            return false;
        }
        return getContext().getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
    }

    public static boolean isDeXMode() {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        try {
            Configuration configuration = getContext().getResources().getConfiguration();
            Class<?> cls = configuration.getClass();
            return cls.getField("SEM_DESKTOP_MODE_ENABLED").getInt(cls) == cls.getField("semDesktopModeEnabled").getInt(configuration);
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean getManifestEnvironmentVariables() {
        Bundle bundle;
        try {
            if (getContext() == null || (bundle = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), 128).metaData) == null) {
                return false;
            }
            for (String str : bundle.keySet()) {
                if (str.startsWith("SDL_ENV.")) {
                    nativeSetenv(str.substring(8), bundle.get(str).toString());
                }
            }
            return true;
        } catch (Exception e) {
            Log.v(TAG, "exception " + e.toString());
            return false;
        }
    }

    public static View getContentView() {
        return mLayout;
    }

    /* loaded from: classes.dex */
    static class ShowTextInputTask implements Runnable {
        static final int HEIGHT_PADDING = 15;

        /* renamed from: h */
        public int f2h;

        /* renamed from: w */
        public int f3w;

        /* renamed from: x */
        public int f4x;

        /* renamed from: y */
        public int f5y;

        public ShowTextInputTask(int i, int i2, int i3, int i4) {
            this.f4x = i;
            this.f5y = i2;
            this.f3w = i3;
            this.f2h = i4;
            if (i3 <= 0) {
                this.f3w = 1;
            }
            if (i4 + HEIGHT_PADDING <= 0) {
                this.f2h = -14;
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(this.f3w, this.f2h + HEIGHT_PADDING);
            layoutParams.leftMargin = this.f4x;
            layoutParams.topMargin = this.f5y;
            if (SDLActivity.mTextEdit == null) {
                SDLActivity.mTextEdit = new DummyEdit(SDL.getContext());
                SDLActivity.mLayout.addView(SDLActivity.mTextEdit, layoutParams);
            } else {
                SDLActivity.mTextEdit.setLayoutParams(layoutParams);
            }
            SDLActivity.mTextEdit.setVisibility(0);
            SDLActivity.mTextEdit.requestFocus();
            ((InputMethodManager) SDL.getContext().getSystemService("input_method")).showSoftInput(SDLActivity.mTextEdit, 0);
            SDLActivity.mScreenKeyboardShown = true;
        }
    }

    public static boolean showTextInput(int i, int i2, int i3, int i4) {
        return mSingleton.commandHandler.post(new ShowTextInputTask(i, i2, i3, i4));
    }

    public static boolean isTextInputEvent(KeyEvent keyEvent) {
        if (keyEvent.isCtrlPressed()) {
            return false;
        }
        return keyEvent.isPrintingKey() || keyEvent.getKeyCode() == 62;
    }

    public static boolean handleKeyEvent(View view, int i, KeyEvent keyEvent, InputConnection inputConnection) {
        int action;
        InputDevice device;
        int deviceId = keyEvent.getDeviceId();
        int source = keyEvent.getSource();
        if (source == 0 && (device = InputDevice.getDevice(deviceId)) != null) {
            source = device.getSources();
        }
        if (SDLControllerManager.isDeviceSDLJoystick(deviceId)) {
            if (keyEvent.getAction() == 0) {
                if (SDLControllerManager.onNativePadDown(deviceId, i) == 0) {
                    return true;
                }
            } else if (keyEvent.getAction() == 1 && SDLControllerManager.onNativePadUp(deviceId, i) == 0) {
                return true;
            }
        }
        if ((source & 8194) == 8194 && ((i == 4 || i == 125) && ((action = keyEvent.getAction()) == 0 || action == 1))) {
            return true;
        }
        if (keyEvent.getAction() == 0) {
            if (isTextInputEvent(keyEvent)) {
                if (inputConnection != null) {
                    inputConnection.commitText(String.valueOf((char) keyEvent.getUnicodeChar()), 1);
                } else {
                    SDLInputConnection.nativeCommitText(String.valueOf((char) keyEvent.getUnicodeChar()), 1);
                }
            }
            onNativeKeyDown(i);
            return true;
        }
        if (keyEvent.getAction() != 1) {
            return false;
        }
        onNativeKeyUp(i);
        return true;
    }

    public static Surface getNativeSurface() {
        SDLSurface sDLSurface = mSurface;
        if (sDLSurface == null) {
            return null;
        }
        return sDLSurface.getNativeSurface();
    }

    public static void initTouch() {
        for (int i : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(i);
            if (device != null && ((device.getSources() & 4098) == 4098 || device.isVirtual())) {
                int id = device.getId();
                if (id < 0) {
                    id--;
                }
                nativeAddTouch(id, device.getName());
            }
        }
    }

    public int messageboxShowMessageBox(int i, String str, String str2, int[] iArr, int[] iArr2, String[] strArr, int[] iArr3) {
        this.messageboxSelection[0] = -1;
        if (iArr.length != iArr2.length && iArr2.length != strArr.length) {
            return -1;
        }
        final Bundle bundle = new Bundle();
        bundle.putInt("flags", i);
        bundle.putString("title", str);
        bundle.putString("message", str2);
        bundle.putIntArray("buttonFlags", iArr);
        bundle.putIntArray("buttonIds", iArr2);
        bundle.putStringArray("buttonTexts", strArr);
        bundle.putIntArray("colors", iArr3);
        runOnUiThread(new Runnable() { // from class: org.libsdl.app.SDLActivity.3
            @Override // java.lang.Runnable
            public void run() {
                SDLActivity.this.messageboxCreateAndShow(bundle);
            }
        });
        synchronized (this.messageboxSelection) {
            try {
                this.messageboxSelection.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return this.messageboxSelection[0];
    }

    protected void messageboxCreateAndShow(Bundle bundle) {
        int i;
        int i2;
        int i3;
        int[] intArray = bundle.getIntArray("colors");
        if (intArray != null) {
            i = intArray[0];
            i2 = intArray[1];
            int i4 = intArray[2];
            i3 = intArray[3];
            int i5 = intArray[4];
        } else {
            i = 0;
            i2 = 0;
            i3 = 0;
        }
        final AlertDialog create = new AlertDialog.Builder(this).create();
        create.setTitle(bundle.getString("title"));
        create.setCancelable(false);
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.libsdl.app.SDLActivity.4
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                synchronized (SDLActivity.this.messageboxSelection) {
                    SDLActivity.this.messageboxSelection.notify();
                }
            }
        });
        TextView textView = new TextView(this);
        textView.setGravity(17);
        textView.setText(bundle.getString("message"));
        if (i2 != 0) {
            textView.setTextColor(i2);
        }
        int[] intArray2 = bundle.getIntArray("buttonFlags");
        int[] intArray3 = bundle.getIntArray("buttonIds");
        String[] stringArray = bundle.getStringArray("buttonTexts");
        final SparseArray sparseArray = new SparseArray();
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        for (int i6 = 0; i6 < stringArray.length; i6++) {
            Button button = new Button(this);
            final int i7 = intArray3[i6];
            button.setOnClickListener(new View.OnClickListener() { // from class: org.libsdl.app.SDLActivity.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SDLActivity.this.messageboxSelection[0] = i7;
                    create.dismiss();
                }
            });
            int i8 = intArray2[i6];
            if (i8 != 0) {
                if ((i8 & 1) != 0) {
                    sparseArray.put(66, button);
                }
                if ((intArray2[i6] & 2) != 0) {
                    sparseArray.put(111, button);
                }
            }
            button.setText(stringArray[i6]);
            if (i2 != 0) {
                button.setTextColor(i2);
            }
            if (i3 != 0) {
                Drawable background = button.getBackground();
                if (background == null) {
                    button.setBackgroundColor(i3);
                } else {
                    background.setColorFilter(i3, PorterDuff.Mode.MULTIPLY);
                }
            }
            linearLayout.addView(button);
        }
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(1);
        linearLayout2.addView(textView);
        linearLayout2.addView(linearLayout);
        if (i != 0) {
            linearLayout2.setBackgroundColor(i);
        }
        create.setView(linearLayout2);
        create.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: org.libsdl.app.SDLActivity.6
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialogInterface, int i9, KeyEvent keyEvent) {
                Button button2 = (Button) sparseArray.get(i9);
                if (button2 == null) {
                    return false;
                }
                if (keyEvent.getAction() == 1) {
                    button2.performClick();
                }
                return true;
            }
        });
        create.show();
    }

    @Override // android.view.View.OnSystemUiVisibilityChangeListener
    public void onSystemUiVisibilityChange(int i) {
        Handler handler;
        if (mFullscreenModeActive) {
            if (((i & 4) == 0 || (i & 2) == 0) && (handler = getWindow().getDecorView().getHandler()) != null) {
                handler.removeCallbacks(this.rehideSystemUi);
                handler.postDelayed(this.rehideSystemUi, 2000L);
            }
        }
    }

    public static boolean clipboardHasText() {
        return mClipboardHandler.clipboardHasText();
    }

    public static String clipboardGetText() {
        return mClipboardHandler.clipboardGetText();
    }

    public static void clipboardSetText(String str) {
        mClipboardHandler.clipboardSetText(str);
    }

    public static int createCustomCursor(int[] iArr, int i, int i2, int i3, int i4) {
        PointerIcon create;
        Bitmap createBitmap = Bitmap.createBitmap(iArr, i, i2, Bitmap.Config.ARGB_8888);
        mLastCursorID++;
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Hashtable<Integer, PointerIcon> hashtable = mCursors;
                Integer valueOf = Integer.valueOf(mLastCursorID);
                create = PointerIcon.create(createBitmap, i3, i4);
                hashtable.put(valueOf, create);
                return mLastCursorID;
            } catch (Exception unused) {
            }
        }
        return 0;
    }

    public static void destroyCustomCursor(int i) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                mCursors.remove(Integer.valueOf(i));
            } catch (Exception unused) {
            }
        }
    }

    public static boolean setCustomCursor(int i) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                mSurface.setPointerIcon(SDLSurface$$ExternalSyntheticApiModelOutline0.m12m(mCursors.get(Integer.valueOf(i))));
                return true;
            } catch (Exception unused) {
            }
        }
        return false;
    }

    public static boolean setSystemCursor(int i) {
        PointerIcon systemIcon;
        int i2 = 1004;
        switch (i) {
            case 0:
                i2 = 1000;
                break;
            case 1:
                i2 = 1008;
                break;
            case 2:
            case 4:
                break;
            case QtNative.IdRightHandle /* 3 */:
                i2 = 1007;
                break;
            case 5:
                i2 = 1017;
                break;
            case SDL_SYSTEM_CURSOR_SIZENESW /* 6 */:
                i2 = 1016;
                break;
            case SDL_SYSTEM_CURSOR_SIZEWE /* 7 */:
                i2 = 1014;
                break;
            case 8:
                i2 = 1015;
                break;
            case SDL_SYSTEM_CURSOR_SIZEALL /* 9 */:
                i2 = 1020;
                break;
            case SDL_SYSTEM_CURSOR_NO /* 10 */:
                i2 = 1012;
                break;
            case SDL_SYSTEM_CURSOR_HAND /* 11 */:
                i2 = 1002;
                break;
            default:
                i2 = 0;
                break;
        }
        if (Build.VERSION.SDK_INT < 24) {
            return true;
        }
        try {
            SDLSurface sDLSurface = mSurface;
            systemIcon = PointerIcon.getSystemIcon(SDL.getContext(), i2);
            sDLSurface.setPointerIcon(systemIcon);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static void requestPermission(String str, int i) {
        int checkSelfPermission;
        if (Build.VERSION.SDK_INT < 23) {
            nativePermissionResult(i, true);
            return;
        }
        Activity activity = (Activity) getContext();
        checkSelfPermission = activity.checkSelfPermission(str);
        if (checkSelfPermission != 0) {
            activity.requestPermissions(new String[]{str}, i);
        } else {
            nativePermissionResult(i, true);
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        boolean z = false;
        if (iArr.length > 0 && iArr[0] == 0) {
            z = true;
        }
        nativePermissionResult(i, z);
    }

    public static int openURL(String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(str));
            intent.addFlags(1208483840);
            mSingleton.startActivity(intent);
            return 0;
        } catch (Exception unused) {
            return -1;
        }
    }

    public static int showToast(String str, int i, int i2, int i3, int i4) {
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity == null) {
            return -1;
        }
        try {
            sDLActivity.runOnUiThread(new Runnable(str, i, i2, i3, i4) { // from class: org.libsdl.app.SDLActivity.1OneShotTask
                int mDuration;
                int mGravity;
                String mMessage;
                int mXOffset;
                int mYOffset;

                {
                    this.mMessage = str;
                    this.mDuration = i;
                    this.mGravity = i2;
                    this.mXOffset = i3;
                    this.mYOffset = i4;
                }

                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Toast makeText = Toast.makeText(SDLActivity.mSingleton, this.mMessage, this.mDuration);
                        int i5 = this.mGravity;
                        if (i5 >= 0) {
                            makeText.setGravity(i5, this.mXOffset, this.mYOffset);
                        }
                        makeText.show();
                    } catch (Exception e) {
                        Log.e(SDLActivity.TAG, e.getMessage());
                    }
                }
            });
            return 0;
        } catch (Exception unused) {
            return -1;
        }
    }
}
