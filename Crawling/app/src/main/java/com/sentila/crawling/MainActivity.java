package com.sentila.crawling;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    static double longitude;
    static double latitude;
    static double altitude;

    Button btnShowCoord;
    EditText edtAddress;
    TextView txtCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowCoord = (Button)findViewById(R.id.btnShowCoordinates);
        edtAddress = (EditText)findViewById(R.id.edtAddress);
        txtCoord = (TextView)findViewById(R.id.txtCoordinates);

        // 위치 구하기
        // double result = calDistance(latitude, longitude, lat2, ln2);

        btnShowCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetCoordinates().execute(edtAddress.getText().toString().replace(" ","+"));
            }
        });

    }

    private class GetCoordinates extends AsyncTask <String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try {
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s", address);
                response = ((HttpDataHandler) http).getHTTPData(url);
                return response;
            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            try{
                JSONObject jsonObject = new JSONObject(s);

                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();

                txtCoord.setText(String.format("Coodinates : %s / %s",lat,lng));

                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Button button1;
    private TextView txtResult;

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            altitude = location.getAltitude();

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public static double calDistance(double lat1, double lon1, double lat2, double lon2) {

        double theta, dist;
        theta = lon1 - lon2;

        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000.0;

        return dist;

    }

    private static double deg2rad(double deg) {

        return (double)(deg * Math.PI / (double)180);

    }

    private static double rad2deg(double rad) {

        return (double)(rad * (double)180 / Math.PI);

    }

}
