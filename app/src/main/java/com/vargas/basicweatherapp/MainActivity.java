package com.vargas.basicweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vargas.basicweatherapp.services.models.Countries;
import com.vargas.basicweatherapp.services.weatherServices;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Double temp=0.0;
    private int pressure=0;
    private int humidity;
    private String status;
    private boolean found=false;
    private weatherServices services;
    private String apiKey="4e570e8710cad287de79e22d5f0af301";

    private TextView pressureField=null;
    private TextView humidityField=null;
    private TextView temperatureField=null;
    private TextView statusField=null;
    private EditText cityField=null;
    private AutoCompleteTextView countryField=null;
    private Button submitButton=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initElements();

        services= new weatherServices(apiKey);
    }

    private void initViews() {
        this.pressureField=findViewById(R.id.pressureField);
        this.humidityField=findViewById(R.id.humidityField);
        this.temperatureField=findViewById(R.id.temperatureField);
        this.statusField=findViewById(R.id.statusField);
        this.countryField=findViewById(R.id.countryField);
        this.cityField=findViewById(R.id.cityField);
        this.submitButton=findViewById(R.id.submitButton);
    }

    public void initElements(){
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestInfo();
            }
        });

        String[] countryList=null;
        countryList=Countries.getCountryNames(Locale.getDefault().getLanguage().toString());
        Log.d("Countries", "Country List: "+countryList);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,countryList);
        this.countryField.setAdapter(adapter);

    }

    public void setElements(){
            if (this.found) {
                this.temperatureField.setText("" + this.temp+"°c");
                this.pressureField.setText("" + this.pressure+"hPa");
                this.humidityField.setText("" + this.humidity+"%");
                this.statusField.setText(this.status);
            }
            else {
                this.temperatureField.setText("");
                this.pressureField.setText("");
                this.humidityField.setText("");
                this.statusField.setText(this.status) ;
            }

    }

    public void requestInfo(){
        String city= cityField.getText().toString();
        String iso= Countries.getISOCodeByName(this.countryField.getText().toString(), (String) Locale.getDefault().getLanguage());
        if (iso!=null) iso=iso.toLowerCase();
        else iso="";
        services.requestData(city,iso,(isNetworkError, statusCode, root) -> {
            if (!isNetworkError) {
                switch (statusCode) {
                    case 404:
                        Log.d("Error", "404: Not found");
                        this.found=false;
                        this.status="Not found";
                        break;

                    case 200:
                        this.found=true;
                        this.temp = root.getMain().getTemp();
                        this.pressure = root.getMain().getPressure();
                        this.humidity = root.getMain().getHumidity();
                        this.status=root.getWeather().getDescription();

                        break;
                }
            }
            else Log.d("Error", "Network Error");

            setElements();
        });

    }
}