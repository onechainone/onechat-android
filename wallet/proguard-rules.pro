# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in <Android-SDK>/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Allow obfuscation of android.support.v7.internal.view.menu.**
# to avoid problem on Samsung 4.2.2 devices with appcompat v21
# see https://code.google.com/p/android/issues/detail?id=78377
-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}


#-dontoptimize
#-dontobfuscate
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue
-dontwarn sun.misc.Unsafe
-dontwarn sun.misc.Cleaner
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn sun.nio.ch.DirectBuffer
-dontwarn net.jcip.annotations.GuardedBy
-dontwarn com.subgraph.orchid.**

-dontwarn retrofit2.**
-dontwarn demo.**
-dontwarn com.google.**
#-keep class demo.** { *;}

# Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# 抛出异常时保留代码行号
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable

-keep class onechat.androidapp.graphenechain.fragments.** { *; }
-keep class onechat.androidapp.graphenechain.models.** { *; }
-keep class oneapp.onecore.graphenej.models.** { *; }
#-keep class org.bitcoinj.crypto.MnemonicCode
-keep class onechat.androidapp.onemessage.chat.utils.** { *; }
-keep class org.bitcoinj.** { *; }

# OkHttp
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-keep class okio.**{*;}
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-keep public class * implements java.io.Serializable {*;}
-keep public class * implements android.os.Parcelable {*;}

#enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.gyf.barlibrary.* {*;}

#Gif
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}

#腾讯地图 2D sdk
-keep class com.tencent.mapsdk.**{*;}
-keep class com.tencent.tencentmap.**{*;}
-keepclassmembers class ** {
    public void on*Event(...);
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}
-keep class com.tencent.tencentmap.lbssdk.service.**{*;}


-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**

#友盟混淆
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class onechat.androidapp.R$*{
 public static final int *;
 }

 -keep class io.reactivex.** { *; }
 -dontwarn io.reactivex.**

#ShareSDK
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*

#-keep class com.oneapp.**{*;}
-keep class oneapp.graphenechain.fragments.** { *; }
-keep class org.bitcoinj.**{*;}
-keep class oneapp.graphenechain.models.** { *; }
-keep class oneapp.onecore.graphenej.models.** { *; }

-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}

-assumenosideeffects class android.util.Log {

      public static boolean isLoggable(java.lang.String,int);

      public static int v(...);

      public static int i(...);

      public static int w(...);

      public static int d(...);

     public static int e(...);

  }

  #SwipeBackLayout
  -dontwarn me.imid.swipebacklayout.lib.**
  -keep class me.imid.swipebacklayout.lib.**{*;}

#chat
-keep class oneapp.onechat.oneandroid.onewallet.network.ServiceConstants{*;}
-keep class oneapp.onechat.oneandroid.onemessage.common.**{*;}
-keep class oneapp.onechat.oneandroid.onemessage.beanchat.**{*;}
-keep class oneapp.onechat.oneandroid.onemessage.community.**{*;}
-keep class oneapp.onechat.oneandroid.onewallet.Constants{*;}
-keep class oneapp.onechat.oneandroid.onemessage.Constants{*;}
-keep class oneapp.onechat.oneandroid.onemessage.OnePushStartReceiver{*;}
-keep class oneapp.onechat.oneandroid.onemessage.CommonConstants{*;}
-keep class oneapp.onechat.oneandroid.graphenechain.utils.**{*;}
-keep class oneapp.onechat.oneandroid.onemessage.RpcCallProxy{*;}
-keep class oneapp.onechat.oneandroid.onemessage.SystemInfo{*;}
-keep class oneapp.onechat.oneandroid.onewallet.keepservicealive.**{*;}
-keep class oneapp.onechat.oneandroid.onewallet.modle.**{*;}
#-keep class oneapp.onechat.oneandroid.onemessage.**{*;}
#-keep class oneapp.onechat.oneandroid.onewallet.**{*;}

-keep class oneapp.onechat.oneandroid.graphenechain.database.**{*;}
-keep class oneapp.onecore.graphenej.Util{*;}
-keep class oneapp.onechat.oneandroid.graphenechain.smartcoinswallet.WebsocketWorkerThread{*;}
-keep class oneapp.onechat.oneandroid.graphenechain.interfaces.**{*;}
-keep class oneapp.onecore.graphenej.interfaces.**{*;}
-keep class oneapp.onechat.oneandroid.BuildConfig{*;}
-keep class org.CoreUtils{*;}
#

-keep class oneapp.onechat.oneandroid.onemessage.bean.** { *; }
-keep class oneapp.onechat.oneandroid.graphenechain.fragments.** { *; }
-keep class oneapp.onechat.oneandroid.graphenechain.models.** { *; }
-keep class oneapp.onecore.graphenej.models.** { *; }
#-keep class org.bitcoinj.crypto.MnemonicCode
-keep class oneapp.onechat.oneandroid.onemessage.chat.utils.** { *; }
-keep class org.bitcoinj.** { *; }
##############################################
##
## Android开发中一些需要保留的公共部分
##
##############################################
#
## 保留Annotation不混淆
#-keepattributes *Annotation*,InnerClasses
#
## 避免混淆泛型
#-keepattributes Signature
#
## 抛出异常时保留代码行号
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable
#
## 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
## 因为这些子类都有可能被外部调用
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Appliction
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class * extends android.view.View
#-keep public class com.android.vending.licensing.ILicensingService
#
##-keep public class * extends BalancesWebsocket
##-keep public class * extends BalancesWebsocketBackend
#
#-keep class oneapp.graphenechain.fragments.** { *; }
#-keep class org.bitcoinj.crypto.MnemonicCode
#-keep class oneapp.graphenechain.models.** { *; }
#-keep class oneapp.onecore.graphenej.models.** { *; }
#
## 保留support下的所有类及其内部类
#-keep class android.support.** {*;}
#
## 保留继承的
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.support.v7.**
#-keep public class * extends android.support.annotation.**
#
## 保留R下面的资源
#-keep class **.R$* {*;}
#
## 保留本地native方法不被混淆
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
## 保留在Activity中的方法参数是view的方法，
## 这样以来我们在layout中写的onClick就不会被影响
#-keepclassmembers class * extends android.app.Activity{
#    public void *(android.view.View);
#}
#
## 保留枚举类不被混淆
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## 保留我们自定义控件（继承自View）不被混淆
#-keep public class * extends android.view.View{
#    *** get*();
#    void set*(***);
#    public <init>(android.content.Context);
#    public <init>(android.content.Context, android.util.AttributeSet);
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#
## 保留Parcelable序列化类不被混淆
#-keep class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}
#
## 保留Serializable序列化的类不被混淆
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    !static !transient <fields>;
#    !private <fields>;
#    !private <methods>;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
#
## 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
#-keepclassmembers class * {
#    void *(**On*Event);
#    void *(**On*Listener);
#}
#
## webView处理，项目中没有使用到webView忽略即可
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#    public *;
#}
#-keepclassmembers class * extends android.webkit.webViewClient {
#    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
#    public boolean *(android.webkit.WebView, java.lang.String);
#}
#-keepclassmembers class * extends android.webkit.webViewClient {
#    public void *(android.webkit.webView, jav.lang.String);
#}


