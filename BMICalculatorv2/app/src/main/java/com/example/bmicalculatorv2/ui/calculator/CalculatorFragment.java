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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bmicalculatorv2.BMIResult;
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
    BMIResult result;

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
            result = new BMIResult(weightText.getText().toString(), heightText.getText().toString());
            printBMI();
            weightText.onEditorAction(EditorInfo.IME_ACTION_DONE); // Hides the keyboard I think lmao
        }
    }

    // Print BMI and assessments
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void printBMI(){
        // Print calculation results to page
        results.setText(" BMI:\n" + result.getBmi() +
                "\nBMI Range:\n" + result.getRange() +
                "\n\nWeight Class:\n" + result.getCategory() +
                "\nRisk Assessment:\n" + result.getRisk());

        saveResults();
    }

    // Save to local database
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveResults(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        String formattedDate = dateTime.format(dateFormat);
        db.execSQL(
                "insert into bmiresults(datetime, weight, height) " +
                        "values('" + formattedDate + "', '"
                        + result.getWeight() + "', '"
                        + result.getHeight() +  "')");
    }
}