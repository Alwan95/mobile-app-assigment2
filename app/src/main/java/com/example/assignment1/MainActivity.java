package com.example.assignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    static final int REQUEST_CODE_LOCATION = 1001;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    Spinner ddFrom;
    Spinner ddTo;
    String countryCode="";
    TextView result;
    static final String URL_ALL = "http://data.fixer.io/api/latest?access_key=1886d9895812f965427053dd7cf2f2b9";
    static final String URL_CURRENCY="http://data.fixer.io/api/latest?access_key=1886d9895812f965427053dd7cf2f2b9&symbols=";
    private RequestQueue mRequestQueue;
    EditText amount;
    ExchangeRates rates = new ExchangeRates();
    LinearLayout mainLayout;
    List<String> keysList;
    ArrayAdapter<String> spinnerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ddFrom = findViewById(R.id.dropdownFrom);
        ddTo = findViewById(R.id.dropdownTo);
        result = findViewById(R.id.result);
        amount = findViewById(R.id.inputAmount);
        ddFrom.setOnItemSelectedListener(itemSelected);
        ddTo.setOnItemSelectedListener(itemSelected);
        keysList = new ArrayList<String>();
        mRequestQueue = Volley.newRequestQueue(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // when permission is granted
            getLocation();
        } else {
            // when permission is denied
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }
        loadData();
    }

    OnItemSelectedListener itemSelected = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Exchange();

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void Exchange() {
        if (!TextUtils.isEmpty(amount.getText())) {

            String fromCurrencie = ddFrom.getSelectedItem().toString();
            String toCurrencie = ddTo.getSelectedItem().toString();
            GetCurrency(fromCurrencie,toCurrencie);
        }else {
            result.setText("");
        }
    }

    public void showAllRates(View view) {
        mainLayout = findViewById(R.id.mainLayou);
        int width = mainLayout.getWidth();
        Intent intent = new Intent(this, AllRates.class);
        intent.putExtra("width", width);
        startActivity(intent);
    }

    private void loadData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_ALL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("rates");
                    Iterator keysToCopy = jsonObject.keys();
                    while (keysToCopy.hasNext()) {
                        String key = (String) keysToCopy.next();
                        //  Log.d("ddd",key.toString() );
                        keysList.add(key);
                    }
                     spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, keysList);
                    ddFrom.setAdapter(spinnerAdapter);
                    ddTo.setAdapter(spinnerAdapter);
                    int position = spinnerAdapter.getPosition(countryCode);
                    ddFrom.setSelection(position);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // initialize location
                Location location = task.getResult();
                if (location != null) {

                    try {
                        // initialize geoCoder
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        // initialize address list
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        // set country
                        Log.d("ddd",addresses.get(0).getCountryCode());

                        GetCountryCurrency(addresses.get(0).getCountryCode().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void GetCountryCurrency (String countryName){

        Locale locale = new Locale( "en",countryName);
        Currency currency = Currency.getInstance(locale);
        countryCode = currency.getCurrencyCode();
    }

    private void GetCurrency(String fromCurrency, String toCurrency){
        String url = URL_CURRENCY+fromCurrency+","+toCurrency;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("rates");
                    double firstValue=jsonObject.getDouble(fromCurrency);
                    double secondValue = jsonObject.getDouble(toCurrency);
                    int amountValue = Integer.parseInt(amount.getText().toString());
                    double total = (amountValue/firstValue )*secondValue;

                    String resultToShow = amountValue + " " + fromCurrency + " = " + decimalFormat.format(total) + " " + toCurrency;
                    result.setText(resultToShow + "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(jsonObjectRequest);

    }

    public void Convert(View view) {
        if (!TextUtils.isEmpty(amount.getText())) {
            String fromCurrencie = ddFrom.getSelectedItem().toString();
            String toCurrencie = ddTo.getSelectedItem().toString();
            GetCurrency(fromCurrencie, toCurrencie);
        }else {
            result.setText("");
        }
    }
}