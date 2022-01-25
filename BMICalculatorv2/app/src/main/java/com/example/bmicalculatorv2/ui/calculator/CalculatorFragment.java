package com.example.bmicalculatorv2.ui.calculator;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bmicalculatorv2.DataHelper;
import com.example.bmicalculatorv2.databinding.FragmentCalculatorBinding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalculatorFragment extends Fragment {

    private CalculatorViewModel calculatorViewModel;
    private FragmentCalculatorBinding binding;

    Button btn;
    EditText weightText, heightText;
    TextView results;
    DataHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calculatorViewModel =
                new ViewModelProvider(this).get(CalculatorViewModel.class);

        binding = FragmentCalculatorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DataHelper(getContext());

        // Bind Calculate button to java and, set onClick to validate()
        btn = (Button) binding.btnCalculate;
        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                validate();
            }
        });
        results = (TextView) binding.results;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Check if inputs are valid before calculation
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void validate(){
        weightText = (EditText) binding.editWeight; //in kg
        heightText = (EditText) binding.editHeight; //in cm

        if(weightText.getText().toString().trim().length() == 0 || heightText.getText().toString().trim().length() == 0){ // If empty, notify user that input cannot be empty
            Toast.makeText(getActivity(), "Please input weight and height", Toast.LENGTH_SHORT).show();
        }else if(Double.parseDouble(weightText.getText().toString()) < 1 || Double.parseDouble(heightText.getText().toString()) < 1) { // If input is less than 1, notify user that values inserted is invalid
            Toast.makeText(getActivity(), "Invalid value(s) inserted", Toast.LENGTH_SHORT).show();
        }else{ // If all inputs are valid, proceed to calculation and hide keyboard automatically
            calculateBMI(weightText, heightText);
            weightText.onEditorAction(EditorInfo.IME_ACTION_DONE); // Hides the keyboard I think lmao
        }
    }

    // Calculate BMI
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void calculateBMI(EditText weightText, EditText heightText){
        // Get values in double, convert height from cm to m
        double weight = Double.parseDouble(weightText.getText().toString());
        double height = Double.parseDouble(heightText.getText().toString()) / 100;

        // Calculate BMI (weight / square of height)
        double BMI = weight / (height * height);
        BMI = Math.round(BMI * 10.0) / 10.0; // Round to 1 decimal place

        printBMI(weight, height * 100, BMI);
    }

    // Print BMI and assessments
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void printBMI(double weight, double height, double BMI){
        String bmiCategory, rangeBMI, healthRisk;
        if(BMI < 18.5){
            bmiCategory = "Underweight";
            rangeBMI = "18.4 and below";
            healthRisk = "Malnutrition risk";
        }else if(BMI < 25){
            bmiCategory = "Normal";
            rangeBMI = "18.5 - 24.9";
            healthRisk = "Low risk";
        }else if(BMI < 30) {
            bmiCategory = "Overweight";
            rangeBMI = "25 - 29.9";
            healthRisk = "Enhanced risk";
        }else if(BMI < 35){
            bmiCategory = "Moderately obese";
            rangeBMI = "30 - 34.9";
            healthRisk = "Medium risk";
        }else if(BMI < 40){
            bmiCategory = "Severely obese";
            rangeBMI = "35 - 39.9";
            healthRisk = "High risk";
        }else{
            bmiCategory = "Very severely obese";
            rangeBMI = "40 and above";
            healthRisk = "Very high risk";
        }

        // Print calculation results to page
        results.setText(" BMI:\n" + BMI +
                "\nBMI Range:\n" + rangeBMI +
                "\n\nWeight Class:\n" + bmiCategory +
                "\nRisk Assessment:\n" + healthRisk);

        saveResults(weight, height);
    }


    // Save to local database
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveResults(double weight, double height){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        String formattedDate = dateTime.format(dateFormat);
        db.execSQL(
                "insert into bmiresults(datetime, weight, height) " +
                        "values('" + formattedDate + "', '"
                        + weight + "', '"
                        + height +  "')");
    }
}