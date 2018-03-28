package com.taskdone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.taskdone.R;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends ArrayAdapter<Reminder> {
    List<Reminder> list;

    public ReminderAdapter(@NonNull Context context, int resource, @NonNull List<Reminder> objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @NonNull
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        String type = ((Reminder) this.list.get(position)).getType();
        switch (type) {
            case "preset":
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.reminder_preset, null);
                final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                ((TextView) convertView.findViewById(R.id.title)).setText(((Reminder) this.list.get(position)).getTitle());
                checkBox.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (checkBox.isChecked()) {
                            ((Reminder) ReminderAdapter.this.list.get(position)).setChecked(true);
                        } else {
                            ((Reminder) ReminderAdapter.this.list.get(position)).setChecked(false);
                        }
                    }
                });
                return convertView;
//            case "custom":
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.reminder_custom, null);
//                List<String> spinnerList = new ArrayList();
//                spinnerList.add("minute(s)");
//                spinnerList.add("hour(s)");
//                spinnerList.add("day(s)");
//                ArrayAdapter<String> spinnerAdaper = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, spinnerList);
//                final Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner);
//                spinnerAdaper.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                spinner.setAdapter(spinnerAdaper);
//                final CheckBox check = (CheckBox) convertView.findViewById(R.id.checkbox);
//                check.setOnClickListener(new OnClickListener() {
//                    public void onClick(View view) {
//                        if (check.isChecked()) {
//                            ((Reminder) ReminderAdapter.this.list.get(position)).setChecked(true);
//                        } else {
//                            ((Reminder) ReminderAdapter.this.list.get(position)).setChecked(false);
//                        }
//                    }
//                });
//                final EditText editText = (EditText) convertView.findViewById(R.id.edittext);
//                editText.addTextChangedListener(new TextWatcher() {
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    }
//
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    }
//
//                    public void afterTextChanged(Editable editable) {
//                        long milisFromSpinner = 0;
//                        if (spinner.getSelectedItemPosition() == 0) {
//                            milisFromSpinner = 60;
//                        } else if (spinner.getSelectedItemPosition() == 1) {
//                            milisFromSpinner = 3600;
//                        } else if (spinner.getSelectedItemPosition() == 2) {
//                            milisFromSpinner = 86400;
//                        }
//                        ((Reminder) ReminderAdapter.this.list.get(position)).setMilis((((long) Integer.valueOf(editable.toString()).intValue()) * milisFromSpinner) * 1000);
//                    }
//                });
//                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        long milisFromSpinner = 0;
//                        if (i == 0) {
//                            milisFromSpinner = 60;
//                        } else if (i == 1) {
//                            milisFromSpinner = 3600;
//                        } else if (i == 2) {
//                            milisFromSpinner = 86400;
//                        }
//                        if (editText.getText().toString().isEmpty()) {
//                            ((Reminder) ReminderAdapter.this.list.get(position)).setChecked(false);
//                        } else {
//                            ((Reminder) ReminderAdapter.this.list.get(position)).setMilis((((long) Integer.valueOf(editText.getText().toString()).intValue()) * milisFromSpinner) * 1000);
//                        }
//                    }
//
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//                    }
//                });
//                return convertView;
//            case "add":
//                return LayoutInflater.from(getContext()).inflate(R.layout.reminder_add_button, null);
            default:
                return convertView;
        }
    }

    public int totalChecked() {
        int total = 0;
        for (Reminder r : this.list) {
            if (r.isChecked()) {
                total++;
            }
        }
        return total;
    }

    public ArrayList<Long> getTimes() {
        ArrayList<Long> arrayList = new ArrayList();
        for (Reminder r : this.list) {
            if (r.isChecked()) {
                arrayList.add(Long.valueOf(r.getMilis()));
            }
        }
        return arrayList;
    }
}
