package fr.java.aoitechnicien;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.java.aoitechnicien.Function.MyExpandableListAdapter;


public class TaskFragment extends Fragment {

    MyExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    int idArg;
    String labelArg;
    TextView tvFrequency;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<String> groupTitles = Arrays.asList("Cabine", "Paliers", "Portes", "Machinerie", "Gaine", "Cuvette");
        Map<String, List<String>> taskData = new HashMap<>();
        taskData.put("Cabine", Arrays.asList("Boutons", "Eclairage", "Voyants", "Alarmes", "Téléalarme", "Précision d'arrêt", "Inspection", "Signalisations"));
        taskData.put("Paliers", Arrays.asList("Boutons", "Indicateurs", "Flèches", "Voyants", "Arrêts à niveaux", "Appel pompier"));
        taskData.put("Portes", Arrays.asList("Fonctionnement", "Réouverture", "Contact de choc", "Contrôle mécanique", "Contrôle électrique", "déverrouillage manuel", "Fonctionnement barrière cellule"));
        taskData.put("Machinerie", Arrays.asList("Treuil", "Flexible Hy", "Centrale Hy(nivx)", "Présence bte rouge", "Eclairage"));
        taskData.put("Gaine", Arrays.asList("Eclairage", "Contrôle contre-poids"));
        taskData.put("Cuvette", Arrays.asList("Stop", "Poulie couroie"));



        View fRoot = inflater.inflate(R.layout.fragment_task, container, false);

        idArg = getArguments().getInt("id");
        labelArg = getArguments().getString("label");

        tvFrequency = fRoot.findViewById(R.id.frequency_name_task);
        tvFrequency.setText(labelArg);

        expandableListView = fRoot.findViewById(R.id.groupTask);
        expandableListAdapter = new MyExpandableListAdapter(fRoot.getContext(), groupTitles, taskData);
        expandableListView.setAdapter(expandableListAdapter);

        return fRoot;
    }
}