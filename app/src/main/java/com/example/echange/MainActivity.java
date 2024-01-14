package com.example.echange;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.AdapterView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ExchangeRate.ApiCallback  {

    private EditText am;
    private TextView resVue;
    private double result = 0;
    private double amount = 0;
    private String fromC = "";
    private String toC = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner fromCurrencySpinner = findViewById(R.id.curr_from_spinner);
        final Spinner toCurrencySpinner = findViewById(R.id.curr_to_spinner);

        final ArrayAdapter<CharSequence> fromAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency_from_options,
                android.R.layout.simple_spinner_item
        );

        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(fromAdapter);

        final List<CharSequence> toCurrencyOptions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.currency_to_options)));
        final ArrayAdapter<CharSequence> toAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                toCurrencyOptions
        );

        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toCurrencySpinner.setAdapter(toAdapter);

        // Handle item selection on "From" spinner
        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item from "From" spinner
                String selectedFromCurrency = parentView.getItemAtPosition(position).toString();

                // Remove the selected item from "To" spinner
                fromC = selectedFromCurrency;
                toCurrencyOptions.remove(selectedFromCurrency);
                //toCurrencyOptions.
                toAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        // Handle item selection on "From" spinner
        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item from "From" spinner
                String selectedToCurrency = parentView.getItemAtPosition(position).toString();

                // Remove the selected item from "To" spinner
                toC = selectedToCurrency;
                //toCurrencyOptions.add(selectedToCurrency);
                //toCurrencyOptions.
                //toAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        Button apiCallButton = findViewById(R.id.button);

        apiCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am = findViewById(R.id.editTextNumber);
                // Get the value from the EditText
                String amountString = am.getText().toString();

                // Convert the string to a double (assuming it represents a numeric value)
                amount = Double.parseDouble(amountString);

                // Replace "https://example.com/api" with your actual API endpoint
                String apiUrl = "http://api.exchangeratesapi.io/v1/latest?access_key=7c9c05f4f14a9edcdda3503e549ec9af&symbols="+toC+","+fromC+"&format=1";

                if(toC != "" && fromC != "" && amount != 0) {
                    // Execute the API call task
                    new ExchangeRate(MainActivity.this).execute(apiUrl);
                }


            }
        });
    }

    @Override
    public void onApiResponse(String response) {
        // Handle the API response here
        if (response != null) {
            try {
                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response);

                // Access rates from the JSON response
                JSONObject rates = jsonResponse.getJSONObject("rates");

                // Example: Access USD rate
                double fromRate = rates.getDouble(fromC);

                // Example: Access MAD rate
                double toRate = rates.getDouble(toC);

                result = (amount * toRate)/fromRate;

                resVue = findViewById(R.id.textView6);

                resVue.setText(""+ result);

                // Perform comparisons or use rates as needed
                Toast.makeText(this, "Result : " + result, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "API call failed", Toast.LENGTH_SHORT).show();
        }
    }
}
