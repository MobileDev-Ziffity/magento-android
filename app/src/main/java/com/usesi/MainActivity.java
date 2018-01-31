package com.usesi;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Web View
        final WebView myWebView = findViewById(R.id.mainWebView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Main Menu
        mDrawerList = (ListView)findViewById(R.id.nav_list);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0: Toast.makeText(MainActivity.this, "Shop By Category Clicked!", Toast.LENGTH_SHORT).show();
                        myWebView.loadUrl("javascript:(function() { " +
                                "document.getElementsByClassName('mm-fullsubopen')[0].style.display='block'; })()");
                            break;
                    case 1: Toast.makeText(MainActivity.this, "Scan a Barcode Clicked!", Toast.LENGTH_SHORT).show();
                            break;

                    case 2: myWebView.loadUrl("https://www.usesi.com/quickorder/");
                            mDrawerLayout.closeDrawers();
                            break;

                    case 3: Toast.makeText(MainActivity.this, "Submit a Photo Clicked!", Toast.LENGTH_SHORT).show();
                            break;

                    case 4: Toast.makeText(MainActivity.this, "Your Branch Clicked!", Toast.LENGTH_SHORT).show();
                            break;

                    case 5: myWebView.loadUrl("https://www.usesi.com/help-center/");
                            mDrawerLayout.closeDrawers();
                            break;
                    default: Toast.makeText(MainActivity.this, "It seems 'position' is not the right variable for this switch statement!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
        *  Tool Bar Icons
        * */

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

//        TextView faBars = findViewById(R.id.fa_bars);
        TextView faSearch = findViewById(R.id.fa_search);
        TextView faShoppingCart = findViewById(R.id.fa_shopping_cart);
        TextView faBuilding = findViewById(R.id.fa_building);

//        faBars.setTypeface(fontAwesomeFont);
        faSearch.setTypeface(fontAwesomeFont);
        faShoppingCart.setTypeface(fontAwesomeFont);
        faBuilding.setTypeface(fontAwesomeFont);


        // Web View
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(myWebView, url);
                myWebView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('page-header')[0].style.display='none'; })()");
//                Toast.makeText(getApplicationContext(), "WebView started Loading USESI.com", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(myWebView, url);
                Toast.makeText(getApplicationContext(), "WebView Loaded USESI.com", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        myWebView.loadUrl("https://usesi.com/");


        //Button OnClick Events

//        Button menu = (Button) findViewById(R.id.fa_bars);
//        menu.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_LONG).show();
//
//            }
//        });

        Button search = (Button) findViewById(R.id.fa_search);
        search.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_LONG).show();

            }
        });

        Button cart = (Button) findViewById(R.id.fa_shopping_cart);
        cart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                myWebView.loadUrl("https://www.usesi.com/checkout/cart/");

            }
        });

        Button acc = (Button) findViewById(R.id.fa_building);
        acc.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_LONG).show();

            }
        });



    }

    // Helper method for adding the list items in the drawer
    private void addDrawerItems() {
        String[] menuArray = { "Shop By Category", "Scan a Barcode", "Quick Order", "Submit a Photo", "Your Branch", "Help" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // helper method to setup the drawer
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
