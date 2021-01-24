package in.yale.mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.comapi.Callback;
import com.comapi.Comapi;
import com.comapi.ComapiClient;
import com.comapi.ComapiConfig;
import com.comapi.Session;
import com.comapi.internal.network.ComapiResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static in.yale.mobile.R.string.logOut;
import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {


    private WebView webLoad;
    private String search_Text = "";
    private boolean loggedIn,employLoggedIn;
    private TextView userName;
    private String count;
    private ProgressDialog progressDialog;
    private String strLocationClicked = "notclicked";
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private String phone_number;
    private String currentVersionCode;
    private String latestVersion;
    private String mandatoryUpdate;
    private SwipeRefreshLayout swipe;
    private ValueCallback<Uri[]> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 10;
    private ProgressBar progressView;
    private FrameLayout frame;
    private int alertCount = 0;
    private Boolean popfeedBool,poplocBool;
    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 17326;
    private String mGeoLocationRequestOrigin = null;
    private GeolocationPermissions.Callback mGeoLocationCallback = null;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle params;
    private String messageForEmployee = "";
    private String sPackageNameToUse;
    private String packageName = null;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"JavascriptInterface", "ClickableViewAccessibility", "SetJavaScriptEnabled", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // dotdigital
        FirebaseApp.initializeApp(this);
        ComapiConfig config = new ComapiConfig() .apiSpaceId("ea59654c-3999-4311-b84f-b7d5792f68d9") .authenticator(new ChallengeHandler());
        Comapi.initialiseShared(getApplication(), config, new Callback<ComapiClient>() {
            @Override
            public void success(ComapiClient client) {
                //Use ComapiClient object to communicate with services
                compapiCommunicate(client);
            }

            @Override
            public void error(Throwable t) {
                //Toast.makeText(MainActivity.this, t.getMessage() + " :Fail initializeComapi", Toast.LENGTH_LONG).show();
            }
        });
        // end dotdigital
        params = new Bundle();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Check Google play services are updated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        sendRegistrationToServer(msg);
                    }
                });


        progressView = findViewById(R.id.progressBar);
        progressView.setMax(100);
        //progressView.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
        getVersionInfo();
        CookieManager.getInstance().setCookie(Constants.BASE_URL + "?mobileapp=1", "mobile_app_auth=4XcGAuoS3m3zVUChP59iFAs8vuOZ96B3Gxj5n3MqAMwoM3gMNHWE73gqeVP5JS1J");
        webLoad = findViewById(R.id.webLoad);
        webLoad.setWebContentsDebuggingEnabled(true);
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
        popfeedBool =  getIntent().getBooleanExtra("action_feedback_barcode", false);
        poplocBool = getIntent().getBooleanExtra("action_location_barcode", false);

        //update
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(listener);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                    requestUpdate(appUpdateInfo);
                    Toast.makeText(MainActivity.this, "Kindly Update the App", Toast.LENGTH_SHORT).show();
                }
                else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    notifyUser();
                }
                else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                    notifyUser();
                }
            }
        });
        //end update


        CookieManager.getInstance().setAcceptThirdPartyCookies(webLoad, true);
        final WebSettings webSettings = webLoad.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webLoad.addJavascriptInterface(new WebAppInterface(), "StatusHandler");
        webLoad.addJavascriptInterface(new WebAppClickInterface(), "ClickHandler");
        webLoad.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                if (url.contains("https://secure.billtrust.com")) {
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(MainActivity.this,
                            Environment.DIRECTORY_DOWNLOADS, "invoice.pdf");
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File",
                            Toast.LENGTH_LONG).show();
                    webLoad.reload();
                }
            }
        });

        webLoad.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                mGeoLocationRequestOrigin = null;
                mGeoLocationCallback = null;
                // Ask for location permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(R.string.permission_location_rationale)
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mGeoLocationRequestOrigin = origin;
                                        mGeoLocationCallback = callback;
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                                    }
                                }).show();
                    } else {
                        // No explanation needed, we can request the permission
                        mGeoLocationRequestOrigin = origin;
                        mGeoLocationCallback = callback;
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }

                } else {
                    // Tell Webview that permission has been granted
                    callback.invoke(origin, true, true);
                }
            }

            public void onProgressChanged(WebView view, int Progress) {
                progressView.setVisibility(View.VISIBLE);
                progressView.setProgress(Progress);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (Progress == 100) {
                    progressView.setVisibility(View.GONE);
                   // drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else
                    progressView.setVisibility(View.VISIBLE);
                   // drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                super.onProgressChanged(view, Progress);
            }

            @SuppressLint("NewApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                }

                mUploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();

                try {
                    startActivityForResult(intent, FILECHOOSER_RESULTCODE);
                } catch (ActivityNotFoundException e) {
                    mUploadMessage = null;
                    return false;
                }

                return true;
            }
        });

        webLoad.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(webLoad, url, favicon);
                swipe.setRefreshing(false);
                swipe.setEnabled(true);

                if (strLocationClicked.equals("clicked")) {
                    if (url.contains(Constants.BASE_URL)) {
                        //callLoginWebService();
                        strLocationClicked = "notclicked";
                    }
                }
            }


            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(webLoad, url);

                if (popfeedBool) {
                    webLoad.evaluateJavascript(" jQuery(document).ready(function(){jQuery('#report-bug-link').click();})", null);
                    popfeedBool = false;
                } else if (poplocBool) {
                    strLocationClicked = "clicked";
                    webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
                    poplocBool = false;
                }

                webLoad.evaluateJavascript("window.isEmployeeLoggedIn", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if(s.equals("1")){
                            employLoggedIn = true;
                        }else{
                            employLoggedIn = false;
                        }
                        if(!messageForEmployee.equals("")) {
                            MainActivity.this.postMessage(messageForEmployee);
                        }
                    }
                });

            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String myvar = "<!DOCTYPE html> <html> <head>   <meta charset=\"utf-8\" />   <title>No connection to the internet</title>   <style>       html,body { margin:0; padding:0; }       html {         background: #FFFFFF -webkit-linear-gradient(top, #FFF 0%, #FFFFFF 100%) no-repeat;         background: #FFFFFF linear-gradient(to bottom, #FFF 0%, #FFFFFF 100%) no-repeat;       }       body {         font-family: sans-serif;         color: #000;         text-align: center;         font-size: 150%;       }       h1, h2 { font-weight: normal; }       h1 { margin: 0 auto; padding: 0.15em; font-size: 10em; text-shadow: 0 2px 2px #000; }       h2 { margin-bottom: 2em; }  .btn {   box-sizing: border-box;   appearance: none;   background-color: transparent;   border: 2px solid #000000;   border-radius: 0.6em;   color: #000000;   cursor: pointer;   align-self: center;   font-size: 1rem;   font-weight: 400;   line-height: 1;   margin: 20px;   padding: 1.2em 2.8em;   text-decoration: none;   text-align: center;   text-transform: uppercase;   font-weight: 700; } a {   text-decoration: none;   color: inherit; }  .btn:hover, .btn:focus {   color: #fff;   outline: 0; }  .first {   transition: box-shadow 300ms ease-in-out, color 300ms ease-in-out; }  .first:hover {   box-shadow: 0 0 40px 40px #000000 inset; }     </style> </head> <body> <h1>⚠</h1> <h2>"+description+"</h2> <h4>Error or Disconnected</h4> <button class=\"btn first\"><a href=\""+Constants.BASE_URL+"\">Retry</a></button> </body> </html>";
                webLoad.loadDataWithBaseURL("", myvar, "text/html", "UTF-8", "");
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(!(!url.contains("cart") && url.contains("checkout")) && !url.contains("?mobileapp=1") && url.contains(Constants.BASE_URL)){
                    url = url + "?mobileapp=1";
                }

                if (url.endsWith(".pdf")) {
                    try {
                        if (packageName != null) {
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            CustomTabColorSchemeParams params = new CustomTabColorSchemeParams.Builder()
                                    .setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                    .setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark))
                                    .setSecondaryToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                    .build();
                            builder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params);
                            builder.setStartAnimations(MainActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                            builder.setExitAnimations(MainActivity.this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.intent.setPackage(packageName);
                            customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        }
                    } catch (Exception ignored) {
                        Toast.makeText(MainActivity.this, "Cant Open file at this Moment!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.startsWith("mailto:")) {
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                    return true;
                }

                if (url.startsWith("share:")) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = webLoad.getUrl();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Product detail Page");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    return true;
                }
                if (url.startsWith("print:")) {
                    printAction();
                    return true;
                }

                view.loadUrl(url);

                return true;
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                return super.shouldInterceptRequest(view, url);
            }
        });

        //callLoginWebService();
        if (Utils.checkInternet(MainActivity.this)) {
            webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            String myvar = "<!DOCTYPE html> <html> <head>   <meta charset=\"utf-8\" />   <title>No connection to the internet</title>   <style>       html,body { margin:0; padding:0; }       html {         background: #FFFFFF -webkit-linear-gradient(top, #FFF 0%, #FFFFFF 100%) no-repeat;         background: #FFFFFF linear-gradient(to bottom, #FFF 0%, #FFFFFF 100%) no-repeat;       }       body {         font-family: sans-serif;         color: #000;         text-align: center;         font-size: 150%;       }       h1, h2 { font-weight: normal; }       h1 { margin: 0 auto; padding: 0.15em; font-size: 10em; text-shadow: 0 2px 2px #000; }       h2 { margin-bottom: 2em; }  .btn {   box-sizing: border-box;   appearance: none;   background-color: transparent;   border: 2px solid #000000;   border-radius: 0.6em;   color: #000000;   cursor: pointer;   align-self: center;   font-size: 1rem;   font-weight: 400;   line-height: 1;   margin: 20px;   padding: 1.2em 2.8em;   text-decoration: none;   text-align: center;   text-transform: uppercase;   font-weight: 700; } a {   text-decoration: none;   color: inherit; }  .btn:hover, .btn:focus {   color: #fff;   outline: 0; }  .first {   transition: box-shadow 300ms ease-in-out, color 300ms ease-in-out; }  .first:hover {   box-shadow: 0 0 40px 40px #000000 inset; }     </style> </head> <body> <h1>⚠</h1> <h2>No connection to the internet</h2> <button class=\"btn first\"><a href=\""+Constants.BASE_URL+"\">Retry</a></button> </body> </html>";
            webLoad.loadDataWithBaseURL("", myvar, "text/html", "UTF-8", "");
        }
        callShopbyList();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        View logoView = toolbar.findViewById(R.id.logo);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle params = new Bundle();
                String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                params.putString("date",dateStr);
                mFirebaseAnalytics.logEvent("navbar_Logo", params);
                webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");

            }
        });

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        View logo = headerview.findViewById(R.id.imageView);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        View login = headerview.findViewById(R.id.textView);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!employLoggedIn) {
                        //Bundle params = new Bundle();
                        String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                        params.putString("date", dateStr);
                        mFirebaseAnalytics.logEvent("navdrawer_LOGINREGISTER", params);
                        webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });


        navigationView.setNavigationItemSelectedListener(this);
        barcodeIntentListener(webLoad);
        packageName = getPackageNameToUse();
    }

    private String getPackageNameToUse() {
        if (sPackageNameToUse != null) return sPackageNameToUse;

        PackageManager pm = getPackageManager();
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASE_URL));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction("android.support.customtabs.action.CustomTabsService");
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        if (packagesSupportingCustomTabs.isEmpty()) {
            sPackageNameToUse = null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            sPackageNameToUse = packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                && !hasSpecializedHandlerIntents(this, activityIntent)
                && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            sPackageNameToUse = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains("com.android.chrome")) {
            sPackageNameToUse = "com.android.chrome";
        } else if (packagesSupportingCustomTabs.contains("com.chrome.beta")) {
            sPackageNameToUse = "com.chrome.beta";
        } else if (packagesSupportingCustomTabs.contains("com.chrome.dev")) {
            sPackageNameToUse = "com.chrome.dev";
        } else if (packagesSupportingCustomTabs.contains("com.google.android.apps.chrome")) {
            sPackageNameToUse = "com.google.android.apps.chrome";
        }
        return sPackageNameToUse;
    }

    private boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(
                    intent,
                    PackageManager.GET_RESOLVED_FILTER);
            if (handlers.size() == 0) {
                return false;
            }
            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter == null) continue;
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue;
                if (resolveInfo.activityInfo == null) continue;
                return true;
            }
        } catch (RuntimeException ignored) { }
        return false;
    }

    private void compapiCommunicate(final ComapiClient client)
    {
        if(client.getSession() != null && client.getSession().isSuccessfullyCreated()) {
            client.service().profile().getProfile(client.getSession().getProfileId(), new Callback<ComapiResult<Map<String, Object>>>() {
                @Override
                public void success(ComapiResult<Map<String, Object>> result) {
                    //Toast.makeText(MainActivity.this, result+ " :Success AccountCreation", Toast.LENGTH_LONG).show();
                    Map<String, Object> additionalMap = new HashMap<>();
                    //Add the user's email address to the profile
                    additionalMap.put("email", "rajkumar.t+200701@ziffity.com");
                    client.service().profile().patchMyProfile(additionalMap, result.getETag(), new Callback<ComapiResult<Map<String, Object>>>() {
                        @Override
                        public void success(ComapiResult<Map<String, Object>> result) {
                            //Toast.makeText(MainActivity.this, result+" :Success PatchProfile", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void error(Throwable t) {
                            //Toast.makeText(MainActivity.this, t.getMessage()+" :Fail PatchProfile", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void error(Throwable t) {
                    //Toast.makeText(MainActivity.this, t.getMessage() +" :Fail AccountCreation", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            client.service().session().startSession(new Callback<Session>() {
                @Override
                public void success(Session result) {
                    //Toast.makeText(MainActivity.this, result+" :Success startSession", Toast.LENGTH_LONG).show();
                    compapiCommunicate(client);
                }

                @Override
                public void error(Throwable t) {
                    //Toast.makeText(MainActivity.this, t.getMessage() + " :Fail startSession", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void barcodeIntentListener(WebView webLoad)
    {
        if (getIntent().getBooleanExtra("from_scanner", false)) {
            String CustomrUrl = getIntent().getStringExtra("custom_url");
            webLoad.loadUrl(Constants.BASE_URL + CustomrUrl);

        }else if (getIntent().getBooleanExtra("menuItem_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/?mobileapp=1");

        } else if (getIntent().getBooleanExtra("alertDialog_barcode", false)) {

            String result = getIntent().getStringExtra("search_Text");
            String loadURL;
            try {
                loadURL = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + URLEncoder.encode(result, "UTF-8") + "&search=1/?mobileapp=1";
            } catch (Exception e) {
                loadURL = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + result + "&search=1/?mobileapp=1";
            }
            webLoad.loadUrl(loadURL);

        } else if (getIntent().getBooleanExtra("action_login_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_locations_barcode", false)) {

            webLoad.loadUrl(Constants.LOCATION_URL);

        } else if (getIntent().getBooleanExtra("login_barcode", false)) {

            if (loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "shoppinglist?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_catalog_barcode", false)) {

            if (loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "yourcatalog?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_help_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "help-center?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_employee_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "employee/login?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_login_barcode", false)) {

            if (loggedIn) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/logout?mobileapp=1");
            } else {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
            }

        } else if (getIntent().getBooleanExtra("logoView_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");

        } else if (getIntent().getBooleanExtra("two_barcode", false)) {

            webLoad.loadUrl(getIntent().getStringExtra("two_url"));

        } else if (getIntent().getBooleanExtra("three_barcode", false)) {

            webLoad.loadUrl(getIntent().getStringExtra("three_url"));

        } else if (getIntent().getBooleanExtra("pushNotifyBoolean", false)) {
            try {
                if (Patterns.WEB_URL.matcher(getIntent().getStringExtra("pushNotify")).matches()) {
                    webLoad.loadUrl(getIntent().getStringExtra("pushNotify"));
                }
            } catch (Exception ef) { }
        }
    }

    private void notifyUser() {

        Snackbar snackbar =
                Snackbar.make(findViewById(android.R.id.content),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("INSTALL", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appUpdateManager != null) {
                    appUpdateManager.completeUpdate();
                }
            }
        });
        snackbar.setActionTextColor(
                getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED){

                notifyUser();
            }
            else if (installState.installStatus() == InstallStatus.INSTALLED){
                if (appUpdateManager != null){
                    appUpdateManager.unregisterListener(listener);
                }
            }
        }
    };

    private void requestUpdate(AppUpdateInfo appUpdateInfo){
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.FLEXIBLE,MainActivity.this,MY_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }


    private void printAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter printAdapter = webLoad.createPrintDocumentAdapter();
            String jobName = "Print";
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }

    private void callShopbyList() {
        Constants.apiCall = Boolean.TRUE;
        if (Utils.checkInternet(MainActivity.this)) {
            ApiTask apiTask = new ApiTask(this);
            apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
            apiTask.setParams(null, Constants.BASE_URL + Constants.SHOP_BY_LIST);
            apiTask.responseCallBack(new ApiTask.ResponseListener() {
                @Override
                public void jsonResponse(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                    } catch (JSONException e) {
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        MenuItem nav_shopbylist = menu.findItem(R.id.nav_shopbylist);
                        nav_shopbylist.setVisible(FALSE);
                        //e.printStackTrace();
                    } catch (Exception unused) { }
                }
            });

        }
    }

    private void getVersionInfo() {
        int currentVersion = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionCode = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void callLoginWebService(String data) {

        //Constants.apiCall = FALSE;
        //if (Utils.checkInternet(MainActivity.this)) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        loggedIn = jsonObject.getBoolean("loggedIn");

                        JSONObject branch_number = jsonObject.getJSONObject("branch");
                        phone_number = branch_number.getString("phone");

                        JSONObject address = jsonObject.getJSONObject("branch");
                        String addressLine1 = address.getString("addressLine1");

                        String city_name = address.getString("city");

                        String state_name = address.getString("state");

                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();

                        MenuItem nav_contact = menu.findItem(R.id.nav_contact);
                        nav_contact.setTitle(phone_number);

                        MenuItem nav_service = menu.findItem(R.id.nav_customer);
                        nav_service.setTitle(Constants.SERVICE_NUMBER);

                        MenuItem nav_branch = menu.findItem(R.id.nav_branch);
                        nav_branch.setTitle("BRANCH :" + addressLine1);

                        MenuItem nav_city = menu.findItem(R.id.nav_city);
                        nav_city.setTitle(city_name + ", " + state_name);
                        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
                        nav_city.getActionView().findViewById(R.id.changeBranch).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Bundle params = new Bundle();
                                String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                                params.putString("date",dateStr);
                                mFirebaseAnalytics.logEvent("navdrawer_BranchChange", params);
                                strLocationClicked = "clicked";
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
                                }
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        });
                        Toolbar toolbar = findViewById(R.id.toolbar);
                        Menu menuBar = toolbar.getMenu();

                        MenuItem nav_cart = menuBar.findItem(R.id.action_cart);
                        View actionView = MenuItemCompat.getActionView(nav_cart);
                        TextView extCartItemCount = actionView.findViewById(R.id.cart_badge);

                        MenuItem nav_account = menuBar.findItem(R.id.action_login);


                        if (jsonObject.has("cart")) {
                            JSONObject cart = jsonObject.getJSONObject("cart");
                            count = cart.getString("count");
                            int value = Integer.parseInt(count);
                            if (!(value >= 1)) {
                                extCartItemCount.setVisibility(View.GONE);
                                extCartItemCount.setText(count);
                            } else {
                                extCartItemCount.setVisibility(View.VISIBLE);
                                extCartItemCount.setText(count);
                            }
                        } else {
                            extCartItemCount.setVisibility(View.GONE);
                        }

                        if (loggedIn) {
                            String profileName = jsonObject.getString("name");
                            View header = navigationView.getHeaderView(0);
                            userName = header.findViewById(R.id.textView);
                            userName.setText(profileName.toUpperCase());
                            MenuItem nav_login = menu.findItem(R.id.nav_login);
                            nav_login.setVisible(true);
                            nav_login.setTitle(logOut);
                            nav_account.setTitle("My Account");
                        } else {
                            View header = navigationView.getHeaderView(0);
                            userName = header.findViewById(R.id.textView);
                            MenuItem nav_employee = menu.findItem(R.id.nav_employee);
                            if (employLoggedIn) {
                                userName.setText(jsonObject.getString("name"));
                                nav_employee.setVisible(false);
                            } else {
                                userName.setText("LOGIN / REGISTER");
                                nav_employee.setVisible(true);
                            }
                            nav_account.setTitle("Login");
                            MenuItem nav_login = menu.findItem(R.id.nav_login);
                            nav_login.setVisible(false);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == 1 && resultCode == Barcode.RESULT_OK) {
            if (data.getBooleanExtra("action_location_barcode",false)) {
                strLocationClicked = "clicked";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
                }
            } else {
                String result = data.getStringExtra("result");
                webLoad.loadUrl(Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + result + "&search=1/?mobileapp=1");
            }
        } else if (requestCode == 2) {
            if (resultCode == ActivityList.RESULT_OK) {
                String result = data.getStringExtra("result");
                String loadURL = Constants.BASE_URL + result.trim();
                webLoad.loadUrl(loadURL + "/?mobileapp=1");
            }
        } else if (requestCode == 3) {
            if (resultCode == ActivityShopList.RESULT_OK) {
                String result = data.getStringExtra("result");
                String loadURL = result.trim() + "/?mobileapp=1";
                webLoad.loadUrl(loadURL);
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mUploadMessage == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            }
            mUploadMessage = null;
        } else if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "App Download Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getCookie(String siteName, String CookieName) {
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp = cookies.split(";");
        for (String ar1 : temp) {
            if (ar1.contains(CookieName)) {
                String[] temp1 = ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;
    }

    protected static View getToolbarLogoView(Toolbar toolbar) {
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<>();
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (webLoad.canGoBack()) {
                String currentURL = webLoad.getUrl();
                if (currentURL.equals(Constants.BASE_URL + "checkout/#payment")) {
                    webLoad.goBack();
                } else {
                    webLoad.goBack();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem cartView = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(cartView, R.layout.action_item);
        MenuItemCompat.getActionView(cartView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/?mobileapp=1");
            }
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //final Bundle params = new Bundle();
        String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        params.putString("date",dateStr);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            mFirebaseAnalytics.logEvent("navbar_Search", params);
            if(alertCount==0) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                ImageView userInputDialogImageView = mView.findViewById(R.id.barcode_imageView);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                search_Text = userInputDialogEditText.getText().toString();
                                alertCount = 0;
                                String loadURLEncode;
                                try {
                                    loadURLEncode = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + URLEncoder.encode(search_Text, "UTF-8") + "&search=1/?mobileapp=1";
                                } catch (Exception e) {
                                    loadURLEncode = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + search_Text + "&search=1/?mobileapp=1";
                                }
                                webLoad.loadUrl(loadURLEncode);
                                mFirebaseAnalytics.logEvent("alert_Ok", params);
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                        alertCount = 0;
                                        mFirebaseAnalytics.logEvent("alert_Cancel", params);
                                    }
                                });

                final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert_roundedcorner));
                userInputDialogImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseAnalytics.logEvent("alert_BarcodeScanner", params);
                        alertDialogAndroid.cancel();
                        alertCount = 0;
                        Intent i = new Intent(MainActivity.this, Barcode.class);
                        startActivityForResult(i, 1);

                    }
                });
                alertDialogAndroid.show();
                alertCount++;
            }
        }

        if (id == R.id.action_cart) {
            mFirebaseAnalytics.logEvent("navbar_Cart", params);
            webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/?mobileapp=1");
        } else if (id == R.id.action_location) {
            mFirebaseAnalytics.logEvent("navbar_Branch", params);
            strLocationClicked = "clicked";
            webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
        } else if (id == R.id.action_login) {
            mFirebaseAnalytics.logEvent("navbar_Login", params);
            webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
        } else if (id == R.id.action_feedback) {
            mFirebaseAnalytics.logEvent("navbar_Feedback", params);
            webLoad.evaluateJavascript("jQuery(document).ready(function(){jQuery('#report-bug-link').click();})", null);
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Bundle params = new Bundle();
        String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        params.putString("date",dateStr);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_branch) {
            // Handle the camera action
        } else if (id == R.id.nav_category) {
            mFirebaseAnalytics.logEvent("navdrawer_SHOPBYCATEGORY", params);
            Intent i = new Intent(this, ActivityList.class);
            startActivityForResult(i, 2);
        } else if (id == R.id.nav_shopbylist) {
            mFirebaseAnalytics.logEvent("navdrawer_SHOPBYLIST", params);
            Intent i = new Intent(this, ActivityShopList.class);
            startActivityForResult(i, 3);
        } else if (id == R.id.nav_locations) {
            mFirebaseAnalytics.logEvent("navdrawer_OURLOCATIONS", params);
            webLoad.loadUrl(Constants.LOCATION_URL + "?mobileapp=1");
        } else if (id == R.id.nav_list) {
            mFirebaseAnalytics.logEvent("navdrawer_YOURLIST", params);
            if (employLoggedIn || loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "wishlists?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
        } else if (id == R.id.nav_catalog) {
            mFirebaseAnalytics.logEvent("navdrawer_YOURCATALOG", params);
            if (employLoggedIn || loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "yourcatalog?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
        } else if (id == R.id.nav_barcode) {
            mFirebaseAnalytics.logEvent("navdrawer_SCANBARCODE", params);
            Intent i = new Intent(this, Barcode.class);
            startActivityForResult(i, 1);
        } else if (id == R.id.nav_photo) {
            mFirebaseAnalytics.logEvent("navdrawer_SUBMITAPHOTO", params);
            startActivity(new Intent(this, SubmitPhoto.class));
        } else if (id == R.id.nav_help) {
            mFirebaseAnalytics.logEvent("navdrawer_HELPCENTER", params);
            webLoad.loadUrl(Constants.BASE_URL + "help-center?mobileapp=1");
        } else if (id == R.id.nav_employee) {
            mFirebaseAnalytics.logEvent("navdrawer_EMPLOYEELOGIN", params);
            webLoad.loadUrl(Constants.BASE_URL + "employee/login?mobileapp=1");
        } else if (id == R.id.nav_contact) {
            mFirebaseAnalytics.logEvent("navdrawer_BRANCHNUMBER", params);
            String number = phone_number;
            if (((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                    != TelephonyManager.PHONE_TYPE_NONE)
            {
                if (checkPermission(Manifest.permission.CALL_PHONE)) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
                }
            } else {
                Toast.makeText(this, "Phone call not available", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_customer) {
            mFirebaseAnalytics.logEvent("navdrawer_CUSTOMERSERVICE", params);
            String number = Constants.SERVICE_NUMBER;
            if (((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                    != TelephonyManager.PHONE_TYPE_NONE)
            {
                if (checkPermission(Manifest.permission.CALL_PHONE)) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
                }
            } else {
                Toast.makeText(this, "Phone call not available", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_login) {
            mFirebaseAnalytics.logEvent("navdrawer_LOGOUT", params);
            if (loggedIn) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/logout/?mobileapp=1");
            } else {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
            }
        }

        if (id == R.id.nav_service || id == R.id.nav_branchnumber || id == R.id.nav_branch || id == R.id.nav_city || id == R.id.nav_contact || id == R.id.nav_customer) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return false;
        } else {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length <= 0 || (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGeoLocationCallback != null ) {
                        mGeoLocationCallback.invoke(mGeoLocationRequestOrigin, true, true);
                    }
                } else {
                    if (mGeoLocationCallback != null) {
                        mGeoLocationCallback.invoke(mGeoLocationRequestOrigin, false, false);
                    }
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        if (strLocationClicked.equals("clicked")) {
            swipe.setEnabled(false);
            swipe.setRefreshing(false);

        } else if (webLoad.getUrl().equals(Constants.BASE_URL + "checkout/")) {
            swipe.setEnabled(false);
            swipe.setRefreshing(false);

        } else {
            swipe.setRefreshing(true);
            webLoad.reload();
        }
    }

    public void postMessage(String message) {
        String hash = "";
        messageForEmployee = message;
        try {
            final JSONObject object = new JSONObject(message);

            if (!hash.equals(object.getString("hashKey"))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callLoginWebService(object.getString("data"));
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                hash = object.getString("hashKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postClickMessage(String message) {
        try {
            final JSONObject object = new JSONObject(message);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if ("ShopByCategory".equals(object.getString("Event"))){
                            Intent i = new Intent(MainActivity.this, ActivityList.class);
                            startActivityForResult(i, 2);
                        }else if ("ShopByList".equals(object.getString("Event")))
                        {
                            Intent i = new Intent(MainActivity.this, ActivityShopList.class);
                            startActivityForResult(i, 3);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error! Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void postMessage(String message) {
            MainActivity.this.postMessage(message);
        }
    }

    public class WebAppClickInterface {
        @JavascriptInterface
        public void postMessage(String message) {
            MainActivity.this.postClickMessage(message);
        }
    }

}
