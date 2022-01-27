package com.example.bmicalculatorv2.ui.history;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.bmicalculatorv2.BMIResult;
import com.example.bmicalculatorv2.DataHelper;
import com.example.bmicalculatorv2.databinding.FragmentHistoryBinding;
import com.example.bmicalculatorv2.R;
import com.example.bmicalculatorv2.ui.editdelete.EditActivity;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {
    public static HistoryFragment hf;

    private FragmentHistoryBinding binding;
    public static ArrayList<BMIResult> bmiResults;


    String[] resultStrings;
    ListView lv;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        bmiResults = new ArrayList<>();
        hf = this;
        refresh();

        return root;
    }

    public void refresh(){
        DataHelper dbHelper;

        dbHelper = new DataHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from bmiresults", null);

        lv = (ListView) binding.lv;

        if(cursor.moveToFirst()){ // If there are results, get values and insert into rows
            int id, count;
            String datetime, weight, height;
            String summary;
            count = 0;
            resultStrings = new String[cursor.getCount()];

            // Go through all rows in db, get values, then format and add into array
            BMIResult result;
            do{
                // Get values from database
                id = cursor.getInt(0);
                datetime = cursor.getString(1);
                weight = cursor.getString(2);
                height = cursor.getString(3);

                result = new BMIResult(id, weight, height, datetime);
                bmiResults.add(result);

                // Summary of the record to be displayed in ListView
                summary = "[" + datetime + "]\n" +
                        result.getWeight() + "kg " + result.getHeight() + "cm\n" +
                        "BMI = " + result.getBmi() + "\nBMI Range = " + result.getRange() + "\n" +
                        "Weight Classification = " + result.getCategory() + "\n" +
                        "Assessment = " + result.getRisk();
                resultStrings[count] = summary;
                count++;
            }while(cursor.moveToNext());

            lv.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, resultStrings));
            lv.setSelected(true);
            lv.setDividerHeight(10);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                    BMIResult itemToEdit = bmiResults.get(i);
                    final CharSequence[] dialogitem = {"Update", "Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Options");
                    builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int item) {
                            Intent intent;
                            switch(item){
                                case 0: // Go to edit page
                                    intent = new Intent(getContext(), EditActivity.class);
                                    intent.putExtra("id", itemToEdit.getId());
                                    startActivity(intent);
                                    break;
                                case 1: // Show confirm deletion popup
                                    deleteConfirm(itemToEdit.getId());
                                    break;
                            }
                            refresh();
                        }
                    });
                    builder.create().show();
                }
            });
        }else{ // Else, just say no saved data
            ArrayList<String> string = new ArrayList<>();
            string.add("No saved data found.\n\nPrevious BMI Calculator results will be displayed here.\n\nClick on saved results to view their details.");
            lv.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, string));
            lv.setClickable(false);

            // Set to do nothing in case user empties database. Without this, onClickListener will still try to get values which will crash the program since there are no values.
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // Do nothing
                }
            });
        }
        ((ArrayAdapter)lv.getAdapter()).notifyDataSetInvalidated();
        cursor.close();
    }

    public void deleteConfirm(int id){
        BMIResult itemToDelete;
        String message = "";
        for (BMIResult result:bmiResults) {
            if(result.getId() == id) {
                itemToDelete = result;

                message += "[" + itemToDelete.getDateTime() + "]\n" +
                        itemToDelete.getWeight() + "kg " + itemToDelete.getHeight() + "cm\n" +
                        "BMI = " + itemToDelete.getBmi();
                message += "\n\nDelete this record?";
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Delete Confirmation");
        builder.setMessage(message);


        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SQLiteDatabase db = new DataHelper(getContext()).getWritableDatabase();
                if(db.delete("bmiresults", "id = " + id, null) > 0){
                    Toast.makeText(getActivity(), "Record successfully deleted.", Toast.LENGTH_SHORT).show();

                    // Clear array to prevent duplicate data on refresh
                    bmiResults.clear();

                    refresh();
                }else{
                    Toast.makeText(getActivity(), "An error occurred while deleting record.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        Button yes, no;

        AlertDialog editedColors = builder.create(); // Another AlertDialog to change button colors
        editedColors.show();
        yes = editedColors.getButton(AlertDialog.BUTTON_POSITIVE);
        no = editedColors.getButton(AlertDialog.BUTTON_NEGATIVE);

        yes.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        no.setTextColor(getResources().getColor(R.color.black));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}