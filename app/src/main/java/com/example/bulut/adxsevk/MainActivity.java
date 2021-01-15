package com.example.bulut.adxsevk;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText editBarcode;
    private EditText editMiktar;
    Button btnEkle;
    Button btnKaydet;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    View view = null;
    String barkodMiktar="";
    String newBarkodMiktar="";
    DownloadManager downloadManager;
    ListView listView;
    static boolean isConnected = false;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        listView=(ListView) findViewById(R.id.LV_Data);
        editBarcode=findViewById(R.id.editBarkod);
        editMiktar=findViewById(R.id.editMiktar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnEkle=findViewById(R.id.btnEkle);
        btnKaydet=findViewById(R.id.btnKaydet);


        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        listView.setAdapter(adapter);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barkodMiktar="";
                newBarkodMiktar="";
                for (int i = 0; i < adapter.getCount(); i++) {

                    String listeTam=adapter.getItem(i).toString();
                    int boyut=adapter.getItem(i).length();
                    String barkod=listeTam.substring(0,13);
                    String miktar=listeTam.substring(14,boyut);


                    barkodMiktar += "{ \"UsedBarcode\": \""+barkod+"\", \"Qty1\": "+miktar+" },";


                }

                newBarkodMiktar = barkodMiktar.substring(0, barkodMiktar.length() - 1);

                if(internetKontrol()){ //internet kontrol methodu çağırılıyor
                    getPostId(newBarkodMiktar);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Inernet Bağlantınız Yok",Toast.LENGTH_SHORT);
                    toast.show();
                }


            }
        });

        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editBarcode.getText().toString().trim().isEmpty())
                {Toast toast = Toast.makeText(getApplicationContext(),"Barkod Boş",Toast.LENGTH_SHORT);
                    toast.show();}
                else if(editMiktar.getText().toString().trim().isEmpty())
                {Toast toast = Toast.makeText(getApplicationContext(),"Miktar Boş",Toast.LENGTH_SHORT);
                    toast.show();}
                else{
                arrayList.add(0,editBarcode.getText().toString()+"," +editMiktar.getText().toString());
                adapter.notifyDataSetChanged();
                editBarcode.setText("");
                editMiktar.setText("");
                }
            }
        });

        editBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Okut");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Iptal Edildi", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Taranan : " + result.getContents(), Toast.LENGTH_LONG).show();
                editBarcode.setText(result.getContents());
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) { return true; }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_peron) {
            Intent intent = new Intent(this, PeronActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(this, AnaSayfa.class);
            startActivity(intent);
        } else if (id == R.id.nav_siparis) {
            Intent intent = new Intent(this, SiparisKontrol.class);
            startActivity(intent);
        } else if (id == R.id.nav_internet) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_depo) {
            Intent intent = new Intent(this, DepoyaIade.class);
            startActivity(intent);
        } else if (id == R.id.nav_cikis) {
            final AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Çıkış Yapmak Üzeresiniz");
            builder.setCancelable(true);
            builder.setNegativeButton("Evet",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int which) {

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
            });
            builder.setPositiveButton("Hayır",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getPostId(final String newBarkodMiktar2) {

       final String url = "http://192.168.1.75:1515/IntegratorService/connect";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String SessionID = jsonObject.getString("SessionID");

                   postNebimTransfer2(SessionID,newBarkodMiktar2);
                   // postNebimTransfer(SessionID,newBarkodMiktar2);


                    Log.w("SonucSessionID",""+SessionID);

                    Log.w("SonucjsonObject",""+jsonObject);
                    Log.w("Sonucurl",""+url);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Volley.newRequestQueue(MainActivity.this).add(istek);
    }


    private void postNebimTransfer2(String sessionID2,String newBarkodMiktar2) {

        final String url2 = "http://192.168.1.75:1515/(S("+sessionID2+"))/IntegratorService/Post?" +
                "{\n" +
                "  \"ModelType\": 59," +
                "  \"CompanyCode\": \"1\"," +
                "  \"IsCompleted\": true," +
                "  \"IsLocked\": false," +
                "  \"IsReturn\": false," +
                "  \"IsOrderBase\": false," +
                "  \"IsTransferApproved\": false," +
                "  \"ShippingPostalAddressID\": \"39F15EF1-FE9B-4904-8CF5-1EB70E8A883D\"," +
                "  \"OfficeCode\": \"M\"," +
                "  \"Description\": \"\"," +
                "  \"StoreCode\": \"\"," +
                "  \"ToStoreCode\": \"1007\"," +
                "  \"ToWarehouseCode\": \"1007\"," +
                "  \"WarehouseCode\": \"00\"," +
                "  \"StoreTransferType\": 0," +
                "  \"Lines\": [" +
                "    "+newBarkodMiktar2+"" +
                "  ]}";


        StringRequest istek = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String ModelType = jsonObject.getString("ModelType");
                    int result = Integer.parseInt(ModelType);


                    Log.w("SonucSessionID",""+ModelType);
                    Log.w("Sonucresult",""+result);

                    Log.w("Sonucurl2",""+url2);

                    if (result==59)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),"Oluşturuldu.Lütfen kontrol ediniz.",Toast.LENGTH_LONG);
                        toast.show();
                        listView.setAdapter(null);

                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Hata : Kontrol Ediniz",Toast.LENGTH_LONG);
                        toast.show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w("SonucJSONException",""+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("SonucVolleyError",""+error);
            }
        }
        );
        Volley.newRequestQueue(MainActivity.this).add(istek);
    }


    protected boolean internetKontrol() { //interneti kontrol eden method
        // TODO Auto-generated method stub
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
