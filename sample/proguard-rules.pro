# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
     public static *** i(...);
      public static *** w(...);
      public static *** e(...);
}

# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
#-dontoptimize
#-dontpreverify

# If you want to enable optimization, you should include the
# following:
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-optimizationpasses 5
#-allowaccessmodification
#
# Note that you cannot just include these flags in your own
# configuration file; if you are including this file, optimization
# will be turned off. You'll need to either edit this file, or
# duplicate the contents of this file and remove the include of this
# file from your project's proguard.config path property.


# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
#-dontwarn android.support.**
#-dontwarn com.google.ads.**

## END -- Google Play Services proguard.txt
# Other settings
#-keep class com.android.**
#-keep class com.google.android.**
#-keep class com.google.android.gms.**
#-keep class com.google.android.gms.location.**
#-keep class com.google.api.client.**
#-keep class com.google.maps.android.**
#-keep class libcore.**
#-keep class com.google.android.gms.internal.** { *; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}

# Glide#######
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# keep the class and specified members from being removed or renamed
-keep class xyz.phongtoanhuu.danmei.entity.** { *; }
-keep class xyz.phongtoanhuu.danmei.response.** { *; }

-keep class com.folioreader.** { *; }
-keep public class com.folioreader.** { *; }
-dontwarn nl.siegmann.epublib.**

-keepnames class com.facebook.FacebookActivity
-keepnames class com.facebook.CustomTabActivity

-keep class com.facebook.all.All


## keep the class and specified members from being removed or renamed
#-keep class vip.mmoney.mechanicalutilities.data.remote.** { *; }


