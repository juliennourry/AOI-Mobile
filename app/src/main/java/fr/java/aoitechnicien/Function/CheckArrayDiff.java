package fr.java.aoitechnicien.Function;

import android.util.Log;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CheckArrayDiff {

    private String[][] set1, set2;

    public CheckArrayDiff(String[][] set1, String[][] set2) {

        this.set1 = set1;
        this.set2 = set2;
    }

    public Set<String[]> checkArrayDiff() {

//        for (String[] item : set1) {
//            // Perform operations on each item
//            Log.i("DEBUG_CONTACTDIFF", "[VALUE :: " + item[0] + " VL :: " + item[1] + "]");
//        }
//        for (String[] item : set2) {
//            // Perform operations on each item
//            Log.i("DEBUG_CONTACTDIFF", "[VALUE2 :: " + item[0] + " VL :: " + item[1] + "]");
//        }

        Set<String[]> group1 = new HashSet<>(Arrays.asList(set1));
        Set<String[]> group2 = new HashSet<>(Arrays.asList(set2));

        Set<String[]> addedElements = new HashSet<>();
        for (String[] item : set2) {
            boolean found = false;
            for (String[] element : set1) {
                if (Arrays.equals(item, element)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                addedElements.add(item);
            }
        }
        for (String[] element : addedElements) {
            Log.i("DEBUG_CONTACTDIFFF", "[VALUE2 :: " + element[0] + " VL :: " + element[1] + "]");
        }
        return addedElements;
    }

}
