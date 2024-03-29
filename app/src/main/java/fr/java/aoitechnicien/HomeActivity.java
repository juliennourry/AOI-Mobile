package fr.java.aoitechnicien;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;
import fr.java.aoitechnicien.Service.LifeTime;
import fr.java.aoitechnicien.Service.ServiceNetwork;
import fr.java.aoitechnicien.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {



    ActivityHomeBinding binding;
    SharedHelper sharedhelper, sharedhelperResume, sharedhelperPause;
    ServiceNetwork servicenetwork;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String pswKey = "pswKey";
    // -- DB
    private static DatabaseHelper databaseHelper;

    @Override
    protected void onResume() {
        super.onResume();

        LifeTime myApp = (LifeTime) getApplication();
        boolean isForeground = myApp.isAppInForeground();

        // -- SESSION INFORMATION
        sharedhelperResume = new SharedHelper(HomeActivity.this, sessionKey);

        if (isForeground) {
            // -- SYNC DATA LOOP
            Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
            intentService.putExtra("login", sharedhelperResume.getParam(loginKey).trim());
            intentService.putExtra("password", sharedhelperResume.getParam(pswKey).trim());
            Log.i("DEBUG_HOME_LIFETIME_RESUME", "TRUE");
            stopService(intentService);
            startService(intentService);
        } else {
            // -- SYNC DATA LOOP
            Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
            intentService.putExtra("login", sharedhelperResume.getParam(loginKey).trim());
            intentService.putExtra("password", sharedhelperResume.getParam(pswKey).trim());
            Log.i("DEBUG_HOME_LIFETIME_RESUME", "FALSE");
            stopService(intentService);
            //Toast.makeText(this, "Service stoppé", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // -- SESSION INFORMATION
        sharedhelperPause = new SharedHelper(HomeActivity.this, sessionKey);

        // -- SYNC DATA LOOP
        Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
        intentService.putExtra("login", sharedhelperPause.getParam(loginKey).trim());
        intentService.putExtra("password", sharedhelperPause.getParam(pswKey).trim());
        Log.i("DEBUG_HOME_LIFETIME_PAUSE", "TRUE");
        stopService(intentService);
        //Toast.makeText(this, "Service stoppé", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- SESSION INFORMATION
        sharedhelper = new SharedHelper(HomeActivity.this, sessionKey);

        // -- default Fragment
        if(savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.frame_layout,new HomeFragment()).commit();
        }

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.home_nav:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.power_nav:
                    //showDeconnectDialog(this, database);
                    replaceFragment(new InformationFragment());
                    break;
                case R.id.sync_nav:
                    showResyncDialog(this, database);
                    break;
                case R.id.gestion_nav:
                    //replaceFragment(new HomeFragment());

                    replaceFragment(new ScanFragment());
                    break;
                default:
                    break;
            }

            return true;
        });

        // -- Dialog View
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // -- OPEN DIALOG MENU BOTTOM
//                //showBottomDialog();
//
//                replaceFragment(new ScanFragment());
//            }
//        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        LinearLayout interventionLayout = dialog.findViewById(R.id.interventionSlide);
        LinearLayout orderLayout = dialog.findViewById(R.id.orderSlide);
        ImageView cancelSlide = dialog.findViewById(R.id.cancelSlide);

        cancelSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.close_menu), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        interventionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                replaceFragment(new HomeFragment());
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.open_intervention), Toast.LENGTH_SHORT).show();
            }
        });

        orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                replaceFragment(new HomeFragment());
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.open), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void refreshFragment() {
        replaceFragment(new ScanFragment());
    }


    public void showDeconnectDialog(Context context, SQLiteDatabase database) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.logout))
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        sharedhelper.stockParam(tokenKey, "");
                        sharedhelper.stockParam(loginKey, "");
                        sharedhelper.stockParam(pswKey, "");

                        databaseHelper.verifyAndInsert(database, "0");


                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.putExtra("extra_sync", "false");
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the popup
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showResyncDialog(Context context, SQLiteDatabase database) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.reload_sync))
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // -- SYNC DATA LOOP
                        Toast.makeText(context, R.string.loading_service, Toast.LENGTH_LONG).show();
                        Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
                        intentService.putExtra("login", sharedhelper.getParam(loginKey).trim());
                        intentService.putExtra("password", sharedhelper.getParam(pswKey).trim());
                        Log.e("DEBUG_DESTROY_START", "TRUE");
                        stopService(intentService);
                        //Toast.makeText(context, "Service stoppé", Toast.LENGTH_LONG).show();
                        startService(intentService);
                        Toast.makeText(context, R.string.service_online, Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the popup
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



}