package fr.java.aoitechnicien;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class ItemButtonFragment extends Fragment {

    private Button btnItemIntervention, btnItemMaintenance, btnItemOfftime, btnItemMontage, btnRetourItemButton;
    private String currentDate;
    private Long checkIdMaintenance;
    Map<String, String> mapMaintenance;

    // -- DB
    private static DatabaseHelper databaseHelper;

    // -- SESSION INFORMATION
    SharedHelper sharedhelper, sharedhelper_user;
    public static final String sessionKey = "sessionKey";
    public static final String loginKey = "loginKey";
    public static final String appareilKey = "appareilKey";
    public static final String idsyncKey = "idsyncKey";
    public static final String coordKey = "coordKey";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fRoot = inflater.inflate(R.layout.fragment_item_button, container, false);

        // -- SESSION INFORMATION
        sharedhelper_user = new SharedHelper(getActivity(), sessionKey);
        sharedhelper = new SharedHelper(getActivity(), appareilKey);

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        btnItemIntervention = fRoot.findViewById(R.id.btnItemIntervention);
        btnItemMaintenance = fRoot.findViewById(R.id.btnItemMaintenance);
        btnItemOfftime = fRoot.findViewById(R.id.btnItemOfftime);
        btnItemMontage = fRoot.findViewById(R.id.btnItemMontage);
        btnRetourItemButton = fRoot.findViewById(R.id.btnRetourItemButton);

        if(databaseHelper.checkOfftimeItem(database, sharedhelper.getParam("idsyncKey"))) {
            btnItemOfftime.setVisibility(View.GONE);
        } else {
            btnItemOfftime.setVisibility(View.VISIBLE);
        }


        // -- Current date
        Date dated = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDate = format.format(dated);


        btnItemIntervention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new InterventionFragment(), currentDate, 0L);
            }
        });
        btnItemMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapMaintenance = new HashMap<>();
                // -- CLASH CREATE OR ADD NEW MAINTENANCE
                String idUser = databaseHelper.getIdSyncUser(database, sharedhelper_user.getParam("loginKey"));
                mapMaintenance.put("fk_user", idUser);
                mapMaintenance.put("fk_item", sharedhelper.getParam("idsyncKey"));
                mapMaintenance.put("coordinates", sharedhelper.getParam("coordKey"));

                // -- Check If MaintenanceForm exist
                checkIdMaintenance = DatabaseHelper.checkAwaitMaint(database, sharedhelper.getParam("idsyncKey"), idUser);
                if(checkIdMaintenance > 0L) {
                    showNewMaintenanceDialogExist(fRoot.getContext(), database, checkIdMaintenance);
                } else {
                    showNewMaintenanceDialog(fRoot.getContext(), database);
                }

            }
        });
        btnItemOfftime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(fRoot.getContext(), database, sharedhelper.getParam("idsyncKey"));
            }
        });
        btnItemMontage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { replaceFragment(new MontageFragment(), currentDate, 0L); }
        });

        btnRetourItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });



        return fRoot;
    }

    private void replaceFragment(Fragment fragment, String currentDate, Long id_maint) {

        Bundle bundle = new Bundle();
        bundle.putString("date", currentDate);
        bundle.putLong("id_maint", id_maint);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_item, fragment);
        fragmentTransaction.addToBackStack("02");
        fragmentTransaction.commit();
    }

    public void dismiss() {
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    public void showDeleteConfirmationDialog(Context context, SQLiteDatabase database, String fk_itemsite) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Souhaitez-vous mettre en service cet appareil?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(databaseHelper.onlineOfftime(database, fk_itemsite)){
                            Activity currentActivity = getActivity();
                            Intent intent = new Intent(currentActivity, ItemActivity.class);
                            startActivity(intent);
                        }
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

    public void showNewMaintenanceDialog(Context context, SQLiteDatabase database) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.create_maintenance))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.btn_oui), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform OK action
                        // -- IN MAP : fk_user, fk_item, coord, send to 9
                        SQLiteDatabase database = databaseHelper.getWritableDatabase();
                        Long id_maint = databaseHelper.insertMaintenance(database, mapMaintenance);
                        String idUser = databaseHelper.getIdSyncUser(database, sharedhelper_user.getParam("loginKey"));
                        if(id_maint > 0) {
//                            Log.i("DEBUG_NEWMAINTENANCE", "Insert new Maintenance SUCCESS");
                            dismiss();

                            Map<String, String> mapDeleteMaintenance = new HashMap<>();
                            mapDeleteMaintenance.put("Id", String.valueOf(id_maint));
                            mapDeleteMaintenance.put("FkItemSite", sharedhelper.getParam("idsyncKey"));
                            mapDeleteMaintenance.put("FkUser", idUser);

                            Boolean idDeleteMaint = databaseHelper.deleteOldMaintenance(database, mapDeleteMaintenance);
                            if (idDeleteMaint) {
                                replaceFragment(new MaintenanceFragment(), currentDate, id_maint);
                            }
                        } else {
                            Log.e("DEBUG_NEWMAINTENANCE", "Insert new Maintenance FAIL");
                        }
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.btn_non), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the popup
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showNewMaintenanceDialogExist(Context context, SQLiteDatabase database, Long id_maint) {
        Long idMaint = id_maint;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.continue_maintenance))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.btn_oui), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform OK action
                        if(idMaint > 0L) {
//                            Log.i("DEBUG_NEWMAINTENANCE", "Insert new Maintenance SUCCESS");

                            dismiss();
                            replaceFragment(new MaintenanceFragment(), currentDate, idMaint);
                        } else {
                            Log.e("DEBUG_NEWMAINTENANCE", "Insert new Maintenance FAIL");
                        }
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.btn_non), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the popup
                        dialog.cancel();
                        showNewMaintenanceDialog(context, database);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}