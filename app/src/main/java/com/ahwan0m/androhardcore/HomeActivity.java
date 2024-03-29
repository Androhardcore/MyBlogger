package com.ahwan0m.androhardcore;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog progressDialog;
    private PrefManager prefManager;
    private Activity mContext;
    private WebView webView;
    private String url = "https://www.Ceritaharian.online/";
    private View llError;
    private String currentUrl = "";
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private AdRequest interAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        llError = findViewById(R.id.llError);

        prefManager = new PrefManager(mContext);

        loadAds();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phno = "tel:" + getResources().getString(R.string.phone);
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(phno));
                mContext.startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        webView = (WebView) findViewById(R.id.webView);

        if (isOnline()) {
            llError.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            startWebView(url);
        } else {
            llError.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }


    }

    private void loadAds() {
     /*   mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        interAdRequest = new AdRequest.Builder().build();
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(interAdRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });*/

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (webView.canGoBack()) {
            if (isOnline()) {
                llError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.goBack();
            } else {
                llError.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {

            String shareBody = webView.getTitle() + " - " + getResources().getString(R.string.app_name) + webView.getUrl();

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

            return true;
        } else if (id == R.id.action_rate) {
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName()));
                Toast.makeText(mContext, "Aplikasi belum di upload di Play Store HEHEHEHE", Toast.LENGTH_SHORT).show();
               return true;
                // startActivity(myIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No application can handle this request."
                        + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_home) {
            if (isOnline()) {
                llError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                startWebView(url);
            } else {
                llError.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        } else if (id == R.id.nav_tutorial) {
            if (isOnline()) {
                llError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                startWebView("https://www.ahwan0m.site/search/label/Tutorial");
            }
        } else if (id == R.id.nav_linux) {
            if (isOnline()) {
                llError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                startWebView("https://www.ahwan0m.site/search/label/Linux");
            }
        } else if (id == R.id.nav_android) {
            if (isOnline()) {
                llError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                startWebView("https://www.ahwan0m.site/search/label/Android");
            }
        }
        else if (id == R.id.nav_about_me) {
            if (isOnline()) {
                llError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                startWebView("https://www.ahwan0m.site/p/about-us.html");
            }
        }  else if (id == R.id.nav_discalimer) {
            if (isOnline()) {
                llError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                startWebView("https://www.ahwan0m.site/p/disclaimer.html");
            }
        }else if (id == R.id.nav_privacy) {
            if (isOnline()) {
                llError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                startWebView("https://www.ahwan0m.site/p/tos.html");
            }
        }
        else if (id == R.id.nav_share) {

            String shareBody = "https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

        }
        //nav rate us
        else if (id == R.id.nav_rate) {
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName()));
                Toast.makeText(mContext, "Aplikasi belum di upload di Play Store HEHEHEHE", Toast.LENGTH_SHORT).show();
                return true;
            }
            catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "No application can handle this request."
                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
        }
        //report BUG
        else if (id == R.id.nav_bug_report) {

            String[] TO = {getResources().getString(R.string.email)};
            String[] CC = {""};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("[BUG REPORT] kirim email ke:" + getResources().getString(R.string.email)));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " - Inquiry from mobile");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hallo admin saya mau melaporkan BUG di Aplikasi " + getResources().getString(R.string.app_name) + "\nURL: " + webView.getUrl() + "\n\n");

                startActivity(Intent.createChooser(emailIntent, "Report BUG via..."));
                finish();
            }

        else if (id == R.id.nav_mail) {

            String[] TO = {getResources().getString(R.string.email)};
            String[] CC = {""};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("kirim email ke:" + getResources().getString(R.string.email)));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " - Inquiry from mobile");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hallo, " + getResources().getString(R.string.app_name) + "\nURL: " + webView.getUrl() + "\n\n");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                finish();
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                currentUrl = url;
                view.loadUrl(url);
                prefManager.addVar();
                if (prefManager.getVar() % PrefManager.ADS_SHOW_TIME == 0) {
                    // Load ads into Interstitial Ads
                    mInterstitialAd.loadAd(interAdRequest);
                    if (mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                }
                return true;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(mContext);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    progressDialog.cancel();
                    prefManager.addVar();
                }
            }


            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        prefManager.addVar();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                //super.onReceivedHttpError(view, request, errorResponse);
                if (isOnline()) {
                    llError.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                } else {
                    llError.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });

        // Javascript enabled on webview
        webView.getSettings().setJavaScriptEnabled(true);

        // Other webview options+

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);

        currentUrl = url;
        //Load url in webview
        webView.loadUrl(url);


    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
