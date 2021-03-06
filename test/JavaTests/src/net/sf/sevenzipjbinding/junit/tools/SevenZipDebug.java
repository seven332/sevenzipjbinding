package net.sf.sevenzipjbinding.junit.tools;

public class SevenZipDebug {
    private static native int nativeGetObjectCount();

    private static native void nativePrintObjects();

    private static native int nativeGetAttachedThreadCount();

    public static int getCPPObjectCount() {
        return nativeGetObjectCount();
    }

    public static void printCPPObjects() {
        nativePrintObjects();
    }

    public static int getAttachedThreadCount() {
        return nativeGetAttachedThreadCount();
    }
}
