-keep class com.dfx.core.NativeBridge { *; }
-keepclassmembers class com.dfx.core.NativeBridge {
    public static *;
}
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
-repackageclasses 'a'
-allowaccessmodification
-overloadaggressively
