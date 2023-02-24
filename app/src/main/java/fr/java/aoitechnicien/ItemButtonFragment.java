package fr.java.aoitechnicien;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ItemButtonFragment extends Fragment {

    private Button btnItemIntervention, btnItemMaintenance, btnItemOfftime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fRoot = inflater.inflate(R.layout.fragment_item_button, container, false);

        btnItemIntervention = fRoot.findViewById(R.id.btnItemIntervention);
        btnItemMaintenance = fRoot.findViewById(R.id.btnItemMaintenance);
        btnItemOfftime = fRoot.findViewById(R.id.btnItemOfftime);


        btnItemIntervention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new InterventionFragment());
            }
        });
        btnItemMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new MaintenanceFragment());
            }
        });
        btnItemOfftime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(fRoot.getContext());
            }
        });



        return fRoot;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_item, fragment);
        fragmentTransaction.addToBackStack("02");
        fragmentTransaction.commit();
    }

    public void showDeleteConfirmationDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Souhaitez-vous indiquer au système l'arrêt de cet appareil?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform the positive action, such as deleting the item
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