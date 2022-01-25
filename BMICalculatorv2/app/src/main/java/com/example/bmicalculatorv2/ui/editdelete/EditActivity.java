package com.example.bmicalculatorv2.ui.editdelete;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bmicalculatorv2.BMIResult;
import com.example.bmicalculatorv2.DataHelper;
import com.example.bmicalculatorv2.R;
import com.example.bmicalculatorv2.ui.calculator.CalculatorFragment;
import com.example.bmicalculatorv2.ui.history.HistoryFragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditActivity extends AppCompatActivity {
    EditText weightText, heightText;
    Button updateBtn, resetBtn;
    int idToEdit;

    DataHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Update");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DataHelper(getBaseContext());

        weightText = findViewById(R.id.editWeight);
        heightText = findViewById(R.id.editHeight);

        updateBtn = findViewById(R.id.btnUpdate);
        resetBtn = findViewById(R.id.btnReset);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                idToEdit = extras.getInt("id");
            }
        }else{
            idToEdit = (int) savedInstanceState.getSerializable("id");
        }


        getValues();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValues();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void getValues(){
        String weightString, heightString;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bmiresults WHERE id = " + idToEdit, null);

        if(cursor.moveToFirst()){
            weightString = cursor.getString(2);
            heightString = cursor.getString(3);

            weightText.setText(weightString);
            heightText.setText(heightString);
        }else{
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void validate(){
        if(weightText.getText().toString().trim().length() == 0 || heightText.getText().toString().trim().length() == 0){ // If empty, notify user that input cannot be empty
            Toast.makeText(this, "Please input weight and height", Toast.LENGTH_SHORT).show();
        }else if(Double.parseDouble(weightText.getText().toString()) < 1 || Double.parseDouble(heightText.getText().toString()) < 1) { // If input is less than 1, notify user that values inserted is invalid
            Toast.makeText(this, "Invalid value(s) inserted", Toast.LENGTH_SHORT).show();
        }else{ // If all inputs are valid, proceed to calculation and hide keyboard automatically
            weightText.onEditorAction(EditorInfo.IME_ACTION_DONE); // Hides the keyboard I think lmao
            updateBMI();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateBMI(){
        // Get values in double, convert height from cm to m
        double weight = Double.parseDouble(weightText.getText().toString());
        double height = Double.parseDouble(heightText.getText().toString());

        ContentValues values = new ContentValues();
        values.put("weight", weight);
        values.put("height", height);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.update("bmiresults", values, "id = " + idToEdit, null) > 0){
            int count = 0;
            for (BMIResult result:HistoryFragment.bmiResults) {
                if(result.getId() == idToEdit){
                    HistoryFragment.bmiResults.get(count).setHeight(Double.toString((height)));
                    HistoryFragment.bmiResults.get(count).setWeight(Double.toString(weight));
                    break;
                }
                count++;
            }

            HistoryFragment.hf.refresh();
            Toast.makeText(this, "Update successful.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "An error occurred while updating data.", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}