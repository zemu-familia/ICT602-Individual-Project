package com.example.bmicalculatorv2.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bmicalculatorv2.BMIResult;
import com.example.bmicalculatorv2.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<BMIResult> result;
    private Context testContext;

    public CustomAdapter(Context context, ArrayList<BMIResult> result){
        testContext = context;
        mInflater = LayoutInflater.from(context);
        this.result = result;
    }


    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view == null){
            view = mInflater.inflate(R.layout.linear_layout_table, null);
            holder = new ViewHolder();
            holder.date = (TextView) view.findViewById(R.id.datetext);
            holder.time = (TextView) view.findViewById(R.id.timetext);
            holder.weight = (TextView) view.findViewById(R.id.weighttext);
            holder.height = (TextView) view.findViewById(R.id.heighttext);
            holder.bmi = (TextView) view.findViewById(R.id.bmitext);
            holder.classText = (TextView) view.findViewById(R.id.classtext);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        // Put bmi calculation result values into TextView
        holder.date.setText(result.get(i).getDateTime());
        holder.weight.setText(result.get(i).getWeight());
        holder.height.setText(result.get(i).getHeight());
        holder.bmi.setText(result.get(i).getBmi());
        holder.classText.setText(result.get(i).getCategory());

        return view;
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }
    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    static class ViewHolder{
        TextView date, time, weight, height, bmi, classText;
    }
}
