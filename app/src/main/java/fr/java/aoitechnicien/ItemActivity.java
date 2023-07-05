package fr.java.aoitechnicien;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.java.aoitechnicien.Function.ConvertFrenchDate;
import fr.java.aoitechnicien.Function.LocListener;
import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Models.ModelItem;
import fr.java.aoitechnicien.Models.ModelSite;
import fr.java.aoitechnicien.Requester.DatabaseHelper;
import fr.java.aoitechnicien.Service.LifeTime;
import fr.java.aoitechnicien.Service.ServiceNetwork;
import fr.java.aoitechnicien.databinding.ActivityItemBinding;

public class ItemActivity extends AppCompatActivity {

    private String s_uuid;
    private WebView webv;
    private ImageView icon_item_info;
    private Boolean bool_icon_item_info = false;
    private LinearLayout content_item_info;
    private TextView title_item_info, label_tiers_item, label_site_item, label_category_item, label_dateservice_item, label_status_item, show_map;
    private List<ModelItem> listItem;
    private List<ModelSite> listSite;
    LocationManager locationManager;
    private final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 200002;
    ToastHelper toastHelper;


    // -- SESSION INFORMATION
    SharedHelper sharedhelper, sharedhelperResume, sharedhelperPause;

    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String pswKey = "pswKey";
    public static final String appareilKey = "appareilKey";
    public static final String idsyncKey = "idsyncKey";
    public static final String idsiteKey = "idsiteKey";
    public static final String siteKey = "siteKey";
    public static final String uuidKey = "uuidKey";
    public static final String labelKey = "labelKey";
    public static final String tiersKey = "tiersKey";
    public static final String fkCategoryKey = "fkCategoryKey";
    public static final String categoryKey = "categoryKey";
    public static final String dateServiceKey = "dateServiceKey";
    public static final String statusKey = "statusKey";
    public static final String latKey = "latKey";
    public static final String lonKey = "lonKey";
    public static final String coordKey = "coordKey";

    // -- DB
    private static DatabaseHelper databaseHelper;

    ActivityItemBinding binding;

    @Override
    protected void onResume() {
        super.onResume();

        LifeTime myApp = (LifeTime) getApplication();
        boolean isForeground = myApp.isAppInForeground();

        // -- SESSION INFORMATION
        sharedhelperResume = new SharedHelper(ItemActivity.this, sessionKey);

        if (isForeground) {
            // -- SYNC DATA LOOP
            Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
            intentService.putExtra("login", sharedhelperResume.getParam(loginKey).trim());
            intentService.putExtra("password", sharedhelperResume.getParam(pswKey).trim());
            Log.i("DEBUG_ITEM_LIFETIME_RESUME", "TRUE");
            stopService(intentService);
            startService(intentService);
        } else {
            // -- SYNC DATA LOOP
            Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
            intentService.putExtra("login", sharedhelperResume.getParam(loginKey).trim());
            intentService.putExtra("password", sharedhelperResume.getParam(pswKey).trim());
            Log.i("DEBUG_ITEM_LIFETIME_RESUME", "FALSE");
            stopService(intentService);
            //Toast.makeText(this, "Service stoppé", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // -- SESSION INFORMATION
        sharedhelperPause = new SharedHelper(ItemActivity.this, sessionKey);

        // -- SYNC DATA LOOP
        Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
        intentService.putExtra("login", sharedhelperPause.getParam(loginKey).trim());
        intentService.putExtra("password", sharedhelperPause.getParam(pswKey).trim());
        Log.i("DEBUG_ITEM_LIFETIME_PAUSE", "TRUE");
        stopService(intentService);
        //Toast.makeText(this, "Service stoppé", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(this);

        // -- GPS
        long minTime = 1000;
        float minDistance = 0;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Log.e("DEBUG_GPS", "Enabled !");
            checkLocationPermission(getBaseContext());
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListenerGPS);

        }
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListenerGPS);

        // -- CALL PERMISSION LOCATION
        isLocationEnabled();
        checkLocationPermission(this);

        // -- SESSION INFORMATION
        sharedhelper = new SharedHelper(ItemActivity.this, appareilKey);

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        binding = ActivityItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        s_uuid = getIntent().getStringExtra("uuid");
        sharedhelper.stockParam(uuidKey, s_uuid);

        icon_item_info = findViewById(R.id.icon_item_info);
        content_item_info = findViewById(R.id.content_item_info);
        title_item_info = findViewById(R.id.title_item_info);
        label_tiers_item = findViewById(R.id.label_tiers_item);
        label_site_item = findViewById(R.id.label_site_item);
        label_category_item = findViewById(R.id.label_category_item);
        label_dateservice_item = findViewById(R.id.label_dateservice_item);
        label_status_item = findViewById(R.id.label_status_item);
        show_map = findViewById(R.id.show_map);


        // -- CHECK INFO UUID APPAREIL
        if(databaseHelper.checkUuidItem(database, sharedhelper.getParam(uuidKey))) {
            listItem = databaseHelper.getAppareil(database, sharedhelper.getParam(uuidKey));
            for (ModelItem item : listItem) {
                // --
                sharedhelper.stockParam(idsyncKey, String.valueOf(item.getIdSync()));
                // --
                sharedhelper.stockParam(statusKey, "1");
                if(databaseHelper.checkOfftimeItem(database, String.valueOf(item.getIdSync()))) {
                    label_status_item.setText(getResources().getString(R.string.online));
                    label_status_item.setTextColor(ContextCompat.getColor(this, R.color.green));
                } else {
                    label_status_item.setText(getResources().getString(R.string.offline));
                    label_status_item.setTextColor(ContextCompat.getColor(this, R.color.red));
                }
                // --
                sharedhelper.stockParam(uuidKey, item.getUuid());
                // --
                sharedhelper.stockParam(labelKey, item.getLabel());
                title_item_info.setText(item.getLabel());
                // --
                sharedhelper.stockParam(dateServiceKey, item.getOnAt());

                label_dateservice_item.setText(new ConvertFrenchDate(item.getOnAt()).convertDate());
                // --
                sharedhelper.stockParam(fkCategoryKey, String.valueOf(item.getFkCategory()));
                sharedhelper.stockParam(categoryKey, item.getCategory());
                label_category_item.setText(item.getCategory());
                // -- GET SITE & TIERS
                database = databaseHelper.getWritableDatabase();
                listSite = databaseHelper.getSite(database, item.getIdSite());
                for (ModelSite site : listSite) {
                    // --
                    sharedhelper.stockParam(tiersKey, site.getTiers());
                    label_tiers_item.setText(site.getTiers());
                    // --
                    sharedhelper.stockParam(siteKey, site.getLabel());
                    label_site_item.setText(site.getLabel());
                }
            }
        }

        show_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webv = findViewById(R.id.map);
                if(webv.getVisibility() == View.VISIBLE){
                    webv.setVisibility(View.GONE);
                    show_map.setText(getResources().getString(R.string.open_map));
                } else {
                    webv.setVisibility(View.VISIBLE);
                    show_map.setText(getResources().getString(R.string.close_map));
                }
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bool_icon_item_info) {
                    bool_icon_item_info = false;
                    icon_item_info.setImageResource(R.drawable.ic_baseline_arrow_right_24);
                    content_item_info.setVisibility(View.GONE);
                } else {
                    bool_icon_item_info = true;
                    icon_item_info.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                    content_item_info.setVisibility(View.VISIBLE);
                }
            }
        };

        icon_item_info.setOnClickListener(onClickListener);
        title_item_info.setOnClickListener(onClickListener);

        // -- default Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_item,new ItemButtonFragment());
        fragmentTransaction.addToBackStack("01");
        fragmentTransaction.commit();
        fragmentManager.popBackStack("01", 0);




    }

    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            /*String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();*/

            webv = findViewById(R.id.map);
            webv.getSettings().setJavaScriptEnabled(true);
            webv.loadUrl("file:///android_asset/map.html");
            webv.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    webv.evaluateJavascript("map = L.map('map').setView(["+latitude+","+longitude+"], 17);" +
                            "L.marker(["+latitude+","+longitude+"],17).addTo(map);" +
                            "map.addLayer(osmLayer);", null);

                    sharedhelper.stockParam(latKey, String.valueOf(latitude));
                    sharedhelper.stockParam(lonKey, String.valueOf(longitude));
                    sharedhelper.stockParam(coordKey, String.valueOf(latitude)+";"+String.valueOf(longitude));
                    locationManager.removeUpdates(locationListenerGPS);
                }

            });
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private boolean checkLocationPermission(Context context) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }
        return true;
    }

    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            /*AlertDialog.Builder alertDialog=new AlertDialog.Builder(getBaseContext());
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){

                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();*/
            //toastHelper.LoadToasted("L'application AOI n'a pas les accès à la localisation, veuillez activer la localisation pour poursuivre.");
        }
        else{
            /*AlertDialog.Builder alertDialog=new AlertDialog.Builder(getBaseContext());
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");
            alertDialog.setNegativeButton("Back to interface",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();*/
            //toastHelper.LoadToasted("Accès à la localisation validée.");

        }
    }

}