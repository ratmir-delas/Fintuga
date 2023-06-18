package com.tugasoft.fintuga.tools;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tugasoft.fintuga.R;

import java.util.ArrayList;
import java.util.List;

class HistoryListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    public String contextName;
    public List<Integer> id;
    public List<String> historyDate;
    public List<Double> historyPrincipal;
    public List<String> mainTitle;
    public List<Double> time;
    public List<Double> withdrawal = new ArrayList<>();
    SQLiteDatabase myDatabase;

    private final static String packageName = "tools";
    private final static String emiContext = packageName + ".EmiCalculatorActivity";
    private final static String fdContext = packageName +".FdCalculatorActivity";

    public HistoryListAdapter(Activity contex, List<String> mainTitle, List<Double> historyPrincipal, List<String> historyDate, List<Integer> id, List<Double> time, List<Double> withdrawal) {
        super(contex, R.layout.history_element, mainTitle);
        this.context = contex;
        String localClassName = contex.getLocalClassName();
        this.contextName = localClassName;
        this.mainTitle = mainTitle;
        this.historyPrincipal = historyPrincipal;
        this.historyDate = historyDate;
        this.id = id;
        this.time = time;
        if (localClassName.equals("tools.SwpCalculatorActivity")) {
            this.withdrawal = withdrawal;
        }
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        View inflate = context.getLayoutInflater().inflate(R.layout.history_element, (ViewGroup) null, true);
        TextView textView = inflate.findViewById(R.id.nameid);
        TextView textView2 = inflate.findViewById(R.id.historyPrincipal);
        TextView textView3 = inflate.findViewById(R.id.historyDate);


        textView.setText(mainTitle.get(i));
        textView2.setText(historyPrincipal.get(i) + " €" );
        textView3.setText(historyDate.get(i));

        textView.setSelected(true);
        textView2.setSelected(true);
        textView3.setSelected(true);

        myDatabase = getContext().openOrCreateDatabase("EMI", 0, null);
        if (contextName.equals(emiContext)) {
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS emiTable(name TEXT,principalAmount DOUBLE,interest DOUBLE,tenure DOUBLE,date TEXT,id INTEGER PRIMARY KEY)");
        } else if (contextName.equals(fdContext)) {
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS fdTable(name TEXT,principalAmount DOUBLE,interest DOUBLE,tenure DOUBLE,date TEXT,id INTEGER PRIMARY KEY)");
        } else {
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS swpTable(name TEXT,principalAmount DOUBLE,interest DOUBLE,tenure DOUBLE,date TEXT,id INTEGER PRIMARY KEY)");
        }
        inflate.findViewById(R.id.delete).setOnClickListener(view12 -> {
            if (contextName.equals(emiContext)) {
                SQLiteDatabase sQLiteDatabase = myDatabase;
                sQLiteDatabase.execSQL("DELETE FROM emiTable WHERE id=" + id.get(i));
            } else if (contextName.equals(fdContext)) {
                SQLiteDatabase sQLiteDatabase2 = myDatabase;
                sQLiteDatabase2.execSQL("DELETE FROM fdTable WHERE id=" + id.get(i));
            } else {
                SQLiteDatabase sQLiteDatabase8 = myDatabase;
                sQLiteDatabase8.execSQL("DELETE FROM swpTable WHERE id=" + id.get(i));
                withdrawal.remove(i);
            }
            id.remove(i);
            time.remove(i);
            historyPrincipal.remove(i);
            mainTitle.remove(i);
            historyDate.remove(i);
            notifyDataSetChanged();
        });
        inflate.findViewById(R.id.apply).setOnClickListener(view1 -> {
            Intent intent;
            if (contextName.equals(emiContext)) {
                intent = new Intent(getContext(), EmiCalculatorActivity.class);
            } else if (contextName.equals(fdContext)) {
                intent = new Intent(getContext(), FdCalculatorActivity.class);
            } else {
                intent = new Intent(getContext(), SwpCalculatorActivity.class);
            }
            intent.putExtra("Open", String.valueOf(i));
            getContext().startActivity(intent);
        });

        return inflate;
    }
}