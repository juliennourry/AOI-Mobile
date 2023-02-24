package fr.java.aoitechnicien.Function;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.java.aoitechnicien.R;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupTitles;
    private Map<String, List<String>> taskData;
    private SparseBooleanArray mCheckStates;
    private Map<Integer, Boolean> checkMap;

    public MyExpandableListAdapter(Context context, List<String> groupTitles, Map<String, List<String>> taskData) {
        this.context = context;
        this.groupTitles = groupTitles;
        this.taskData = taskData;
        mCheckStates = new SparseBooleanArray();
        this.checkMap = new HashMap<>();
    }

    @Override
    public int getGroupCount() {
        return groupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return taskData.get(groupTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        int keycm = concatLogic(groupPosition, childPosition);
        if (!checkMap.containsKey(keycm)) {
            checkMap.put(keycm, false);
        }
        return taskData.get(groupTitles.get(groupPosition)).get(childPosition);
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
        String task = (String) getChild(groupPosition, childPosition);
        Boolean isChecking = false;
        int keycm = concatLogic(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_item, null);
        }
        TextView taskView = convertView.findViewById(R.id.task_name);
        taskView.setText(task);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.task_check);
        checkBox.setChecked(getCheckChild(groupPosition, childPosition));
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkMap.get(keycm)){
                    checkMap.put(keycm, false);
                } else {
                    checkMap.put(keycm, true);
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
