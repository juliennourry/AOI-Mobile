package fr.java.aoitechnicien.Function;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.java.aoitechnicien.R;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupTitles;
    private SparseBooleanArray mCheckStates;
    private Map<Integer, Boolean> checkMap;
    private Map<String, List<String>> taskData;
    private Map<String, List<Map<String, String>>> taskDataComplete;
    // -- DB
    private static DatabaseHelper databaseHelper;

    public MyExpandableListAdapter(Context context, List<String> groupTitles, Map<String, List<String>> taskData, Map<String, List<Map<String, String>>> taskDataComplete, String dateFrequency) {
        this.context = context;
        this.groupTitles = groupTitles;
        this.taskData = taskData;
        mCheckStates = new SparseBooleanArray();
        this.checkMap = new HashMap<>();
        this.taskData = taskData;
        this.taskDataComplete = taskDataComplete;
    }

    @Override
    public int getGroupCount() {
        return groupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return taskDataComplete.get(groupTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupTitles.get(groupPosition);
    }

    @Override
    public Map<String, String> getChild(int groupPosition, int childPosition) {
        int keycm = concatLogic(groupPosition, childPosition);
        if (!checkMap.containsKey(keycm)) {
            checkMap.put(keycm, false);
        }
        return taskDataComplete.get(groupTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, null);
        }
        TextView groupTitleView = convertView.findViewById(R.id.group_name);
        if(groupTitle.length() > 0){
            groupTitleView.setText(groupTitle);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        Log.e("DEBUG_GET_GROUPPOSITION", String.valueOf(groupPosition));
//        Log.e("DEBUG_GET_ChildPOSITION", String.valueOf(childPosition));

        Map<String, String> task_info = getChild(groupPosition, childPosition);

        Log.e("DEBUG", "----------------------------");
        Log.e("DEBUG_LOOPED_LABEL", task_info.get("label"));
        Log.e("DEBUG_LOOPED_ID", task_info.get("id"));
        Log.e("DEBUG_LOOPED_CHECK", task_info.get("check"));
        Log.e("DEBUG_LOOPED_LOCK", task_info.get("lock"));
        Log.e("DEBUG", "----------------------------");

        String task_name = task_info.get("label");
        String task_id = task_info.get("id");
        String task_check = task_info.get("check");
        String task_lock = task_info.get("lock");
        Boolean isChecking = false;

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(context);

        int keycm = concatLogic(groupPosition, childPosition);



        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_item, null);
        }
        TextView taskView = convertView.findViewById(R.id.task_name);
        taskView.setText(task_name);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.task_check);
        //If checked add to the logicalChecker
        if(task_check.equals("1")){
            checkMap.put(keycm, true);
            checkBox.setChecked(true);
        } else if(!task_check.equals("1")){
            // NOT TO DEFAULT FALSE
//            checkMap.put(keycm, false);
        }
        // If lock is activated or not
        if(task_lock.equals("1")){
            checkBox.setEnabled(false);
        } else if(!task_lock.equals("1")){
            checkBox.setEnabled(true);
        }
        checkBox.setChecked(getCheckChild(groupPosition, childPosition));
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DEBUG_CHECKCHECK_ID", task_id);
                if (checkBox.isEnabled()) {
                    Log.e("DEBUG_CHECKCHECK_STATUS", "ENABLED");

                    // -- GET DATA FOR INSERT NEW TASK OR DELETE TASK
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();
                    String getTaskDateDone = currentDate.toString();
                    String getTaskLabel = task_info.get("label");
                    String getLocalMaintenanceId = task_info.get("local_maintenanceform");
                    String getLocalRealMadridFrequencyId = task_info.get("local_realmadridfrequency");
                    String getMaintTaskId = task_info.get("id");

                    Map<String, String> checkMapped = new HashMap<>();
                    checkMapped.put("doneAt", String.valueOf(currentDate));
                    checkMapped.put("label", getTaskLabel);
                    checkMapped.put("localMaintenance", getLocalMaintenanceId);
                    checkMapped.put("localRMF", getLocalRealMadridFrequencyId);
                    checkMapped.put("MaintTask", getMaintTaskId);

                    String mapString = checkMapped.toString();
                    Log.e("DEBUG_MAPPED :: ", mapString);

                    SQLiteDatabase database = databaseHelper.getWritableDatabase();
                    Boolean checkTaskDB = databaseHelper.checkTaskDone(database, checkMapped);
                    if(checkMap.get(keycm)){
                        if(checkTaskDB){
                            // -- delete task done
                            Integer checkDeleteTaskDone = databaseHelper.deleteTaskDone(database, checkMapped);
                            if(checkDeleteTaskDone > 0) {
                                Log.e("DEBUG_DELETE_TASKDONE", "TRUE");
                            }
                        }
                        checkMap.put(keycm, false);
                    } else {
                        if(!checkTaskDB){
                            // -- insert task done
                            Long insertTaskDB = databaseHelper.insertTaskDone(database, checkMapped);
                            if(insertTaskDB > 0) {
                                Log.e("DEBUG_INSERT_TASKDONE", "TRUE");
                            }
                        }
                        checkMap.put(keycm, true);
                    }
                    database.close();
                } else {
                    Log.e("DEBUG_CHECKCHECK_STATUS", "DISABLED");
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public Boolean getCheckChild(int groupPosition, int childPosition) {

        int keycm = concatLogic(groupPosition, childPosition);
        return checkMap.get(keycm);
    }

    public Integer concatLogic(int num1, int num2) {

        String strNum1 = String.valueOf(num1);
        String strNum2 = String.valueOf(num2);

        String concatenated = strNum1 + strNum2;

        return Integer.parseInt(concatenated);
    }
}
