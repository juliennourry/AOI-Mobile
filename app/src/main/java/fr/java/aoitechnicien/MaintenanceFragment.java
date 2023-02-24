package fr.java.aoitechnicien;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import fr.java.aoitechnicien.Function.MyExpandableListAdapter;

public class MaintenanceFragment extends Fragment {

    MyExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView, expandableListView2, expandableListView3 ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Map<Integer, String> frequencies = new HashMap<>();
        frequencies.put(1, "Toutes les 6 semaines");
        frequencies.put(2, "Visite semestrielle");
        frequencies.put(3, "Visite annuelle");



        View fRoot = inflater.inflate(R.layout.fragment_maintenance, container, false);

        createTextViewsFromMap(frequencies, fRoot);

        /*expandableListView = fRoot.findViewById(R.id.groupList1);
        expandableListView2 = fRoot.findViewById(R.id.groupList2);
        expandableListView3 = fRoot.findViewById(R.id.groupList3);
        expandableListAdapter = new MyExpandableListAdapter(fRoot.getContext(), groupTitles, taskData);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView2.setAdapter(expandableListAdapter);
        expandableListView3.setAdapter(expandableListAdapter);*/



        return fRoot;
    }


    private void createTextViewsFromMap(Map<Integer, String> freq, View fRoot) {
        LinearLayout layout = fRoot.findViewById(R.id.linear_layout_frequency);

        for (Map.Entry<Integer, String> entry : freq.entrySet()) {
            TextView textView = new TextView(fRoot.getContext());
            textView.setText(entry.getValue());
            textView.setId(entry.getKey());
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.theme_color));
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new TaskFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", entry.getKey());
                    bundle.putString("label", entry.getValue());
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout_item,fragment);
                    fragmentTransaction.addToBackStack("02");
                    fragmentTransaction.commit();
                    fragmentManager.popBackStack("02", 0);
                }
            });


            layout.addView(textView);
        }
    }

}