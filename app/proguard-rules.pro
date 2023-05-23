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

#-injars      bin/classes
#-injars      bin/resources.ap_
#-injars      libs
#-outjars     bin/application.apk
#-libraryjars /usr/local/android-sdk/platforms/android-28/android.jar

-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep class * extends androidx.fragment.app.Fragment{}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.firebase.** { *; }

-keep class com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang { *; }
-keep class com.rodaClone.driver.connection.responseModels.CompanyModel { *; }
-keep class com.rodaClone.driver.connection.responseModels.CompanyModel$Company { *; }
-keep class com.rodaClone.driver.connection.responseModels.Complaint { *; }
-keep class com.rodaClone.driver.connection.responseModels.Country { *; }
-keep class com.rodaClone.driver.connection.responseModels.CustomWalletModel { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$Amount { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$MonthlyTrips { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$Report { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$Report$Day { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$Report$Hour { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$Report$Month { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$Report$Year { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$TodayTrips { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$TotalTrips { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$WeeklyTrips { *; }
-keep class com.rodaClone.driver.connection.responseModels.DashboardModel$YesterdayTrips { *; }
-keep class com.rodaClone.driver.connection.responseModels.Document { *; }
-keep class com.rodaClone.driver.connection.responseModels.DriverModel { *; }
-keep class com.rodaClone.driver.connection.responseModels.DriverModel$CarDetails { *; }
-keep class com.rodaClone.driver.connection.responseModels.DriverModel$Driver { *; }
-keep class com.rodaClone.driver.connection.responseModels.Faq { *; }
-keep class com.rodaClone.driver.connection.responseModels.GroupDocument { *; }
-keep class com.rodaClone.driver.connection.responseModels.GetDocument { *; }
-keep class com.rodaClone.driver.connection.responseModels.HistoryModel { *; }
-keep class com.rodaClone.driver.connection.responseModels.HistoryModel$History { *; }
-keep class com.rodaClone.driver.connection.responseModels.Languages { *; }
-keep class com.rodaClone.driver.connection.responseModels.NightUploadBoth { *; }
-keep class com.rodaClone.driver.connection.HeadOfficeNumber { *; }

-keep class com.rodaClone.driver.connection.responseModels.NightUploadBoth$InstantImageUpload { *; }
-keep class com.rodaClone.driver.connection.responseModels.NotificationResponseModel { *; }
-keep class com.rodaClone.driver.connection.responseModels.NotificationData { *; }
-keep class com.rodaClone.driver.connection.responseModels.RequestResponseData { *; }
-keep class com.rodaClone.driver.connection.responseModels.Stops { *; }
-keep class com.rodaClone.driver.connection.responseModels.Others { *; }
-keep class com.rodaClone.driver.connection.responseModels.RequestResponseDataClass { *; }
-keep class com.rodaClone.driver.connection.responseModels.RequestUserData { *; }
-keep class com.rodaClone.driver.connection.responseModels.Route { *; }
-keep class com.rodaClone.driver.connection.responseModels.S3Model { *; }
-keep class com.rodaClone.driver.connection.responseModels.SingleHistoryResponse { *; }
-keep class com.rodaClone.driver.connection.responseModels.Sos { *; }
-keep class com.rodaClone.driver.connection.responseModels.Step { *; }
-keep class com.rodaClone.driver.connection.responseModels.Subscription { *; }
-keep class com.rodaClone.driver.connection.responseModels.Translation { *; }
-keep class com.rodaClone.driver.connection.responseModels.Vehicles { *; }
-keep class com.rodaClone.driver.connection.responseModels.Vehicles$Vehiclemodel { *; }
-keep class com.rodaClone.driver.connection.responseModels.WalletResponsModel { *; }
-keep class com.rodaClone.driver.connection.BaseResponse { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$DataObjectsAllApi { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$RequestDataResult { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$OnlineOfflineData { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$NewUser { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$SignupResponseData { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$SignupServiceLocation { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$ReqDriverData { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$MetaData { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$RequestBillDataResponse { *; }
-keep class com.rodaClone.driver.connection.BaseResponse$CancelReason { *; }
-keep class com.rodaClone.driver.connection.User { *; }
-keep class com.rodaClone.driver.connection.CarDetails { *; }
-keep class com.rodaClone.driver.connection.DocumentExpiry { *; }
-keep class com.rodaClone.driver.connection.CustomException { *; }
-keep class com.rodaClone.driver.connection.FavPlace { *; }
-keep class com.rodaClone.driver.connection.FavPlace$Favourite { *; }
-keep class com.rodaClone.driver.connection.LanguageModel { *; }
-keep class com.rodaClone.driver.connection.LanguageModel$Data { *; }
-keep class com.rodaClone.driver.connection.SubscriptionModel { *; }
-keep class com.rodaClone.driver.connection.TranslationModel { *; }
-keep class com.rodaClone.driver.ut.Utilz$ValidationError { *; }
-keep class com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupServiceLocData { *; }
-keep class com.rodaClone.driver.loginSignup.vehicleFrag.model.ServiceTypes { *; }
-keep class com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupTypeData { *; }
-keep class com.rodaClone.driver.connection.responseModels.DocumentUploadResult { *; }


#AWS
-keep class org.apache.commons.logging.**               { *; }
-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
-keep class com.amazonaws.javax.xml.stream.**           { *; }
-keep class com.amazonaws.services.**.model.*Exception* { *; }
-keep class com.amazonaws.internal.**                                   { *; }
-keep class org.codehaus.**                             { *; }
-keepattributes Signature,*Annotation*,EnclosingMethod
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class com.amazonaws.** { *; }

-keep class com.google.android.gms.** { *; }

-dontwarn com.fasterxml.jackson.databind.**
-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn org.apache.http.annotation.**
-dontwarn org.ietf.jgss.**
-dontwarn org.joda.convert.**
-dontwarn org.w3c.dom.bootstrap.**

#SDK split into multiple jars so certain classes may be referenced but not used
-dontwarn com.amazonaws.services.s3.**
-dontwarn com.amazonaws.services.sqs.**

-dontnote com.amazonaws.services.sqs.QueueUrlHandler

