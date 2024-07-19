package org.libsdl.app;

import android.content.Context;

/* loaded from: classes.dex */
public class SDL {
    protected static Context mContext;

    public static void setupJNI() {
        SDLActivity.nativeSetupJNI();
        SDLAudioManager.nativeSetupJNI();
        SDLControllerManager.nativeSetupJNI();
    }

    public static void initialize() {
        setContext(null);
        SDLActivity.initialize();
        SDLAudioManager.initialize();
        SDLControllerManager.initialize();
    }

    public static void setContext(Context context) {
        SDLAudioManager.setContext(context);
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void loadLibrary(String str) throws UnsatisfiedLinkError, SecurityException, NullPointerException {
        if (str == null) {
            throw new NullPointerException("No library name provided.");
        }
        try {
            Class<?> loadClass = mContext.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker");
            Class<?> loadClass2 = mContext.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker$LoadListener");
            Class<?> loadClass3 = mContext.getClassLoader().loadClass("android.content.Context");
            Class<?> loadClass4 = mContext.getClassLoader().loadClass("java.lang.String");
            Object invoke = loadClass.getDeclaredMethod("force", new Class[0]).invoke(null, new Object[0]);
            invoke.getClass().getDeclaredMethod("loadLibrary", loadClass3, loadClass4, loadClass4, loadClass2).invoke(invoke, mContext, str, null, null);
        } catch (Throwable unused) {
            System.loadLibrary(str);
        }
    }
}
