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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;
import fr.java.aoitechnicien.Service.ServiceNetwork;
import fr.java.aoitechnicien.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    SharedHelper sharedhelper;
    ServiceNetwork servicenetwork;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String pswKey = "pswKey";
    // -- DB
    private static DatabaseHelper databaseHelper;

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
                    showDeconnectDialog(this, database);
                    break;
                case R.id.sync_nav:
                    showResyncDialog(this, database);
                    break;
                case R.id.gestion_nav:
                    replaceFragment(new HomeFragment());
                    break;
                default:
                    break;
            }

            return true;
        });

        // -- Dialog View
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // -- OPEN DIALOG MENU BOTTOM
                //showBottomDialog();

                replaceFragment(new ScanFragment());
            }
        });
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
                Toast.makeText(HomeActivity.this, "Fermeture du menu", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        interventionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                replaceFragment(new HomeFragment());
                Toast.makeText(HomeActivity.this, "Open intervention", Toast.LENGTH_SHORT).show();
            }
        });

        orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                replaceFragment(new HomeFragment());
                Toast.makeText(HomeActivity.this, "Open order", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("Souhaitez-vous vous déconnecter?")
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
        builder.setMessage("Souhaitez-vous relancer le service de synchronisation?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // -- SYNC DATA LOOP
                        Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
                        intentService.putExtra("login", sharedhelper.getParam(loginKey).trim());
                        intentService.putExtra("password", sharedhelper.getParam(pswKey).trim());
                        stopService(intentService);
                        startService(intentService);

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