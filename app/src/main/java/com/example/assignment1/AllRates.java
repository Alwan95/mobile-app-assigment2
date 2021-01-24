package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


public class AllRates extends AppCompatActivity {
    static final String URL_ALL = "http://data.fixer.io/api/latest?access_key=1886d9895812f965427053dd7cf2f2b9";
    private RequestQueue mRequestQueue;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    TableLayout ratesTable ;
    ExchangeRates exchangeRates =new ExchangeRates();
    Bundle bundle ;
    int width;
    TextView date ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rates);
        ratesTable= findViewById(R.id.ratesTable);
        bundle = getIntent().getExtras();
        width=  bundle.getInt("width");
        date= findViewById(R.id.conversionDate);
        mRequestQueue = Volley.newRequestQueue(this);
        Date currentTime = Calendar.getInstance().getTime();
        date.setText(currentTime.toString());
        setTableRows();
    }
    public void setTableRows (){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_ALL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int columnWidth = width/3;
                    JSONObject jsonObject = response.getJSONObject("rates");
                   double UsdCurrency=jsonObject.getDouble("USD");
                   Iterator keysToCopy = jsonObject.keys();
                    int i =0;
                    while (keysToCopy.hasNext()) {
                        String key = (String) keysToCopy.next();
                        double value =  jsonObject.getDouble(key);
                        TableRow row = new TableRow(AllRates.this);
                       // keysList.add(key);
                        if(i%2==0){

                            row.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        }

                        TextView txv = new TextView(AllRates.this);
                        txv.setText(key);
                        txv.setWidth(columnWidth);
                        txv.setHeight(100);
                        txv.setGravity(Gravity.CENTER);
                        row.addView(txv,0);

                        TextView txv2 = new TextView(AllRates.this);
                        txv2.setText(String.valueOf(decimalFormat.format(value)));
                        txv2.setWidth(columnWidth);
                        txv2.setGravity(Gravity.CENTER);
                        txv2.setHeight(100);
                        row.addView(txv2,1);


                        double eqUSD =UsdCurrency*value;
                        TextView txv3 = new TextView(AllRates.this);
                        txv3.setText(decimalFormat.format(eqUSD));
                        txv3.setWidth(columnWidth);
                        txv3.setHeight(100);
                        txv3.setGravity(Gravity.CENTER);
                        row.addView(txv3,2);

                        ratesTable.addView(row,i);
                        i++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AllRates.this, "Failed/table", Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
}