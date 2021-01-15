package com.example.bulut.adxsevk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeronActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btnGet,btnUpdate;
    EditText editUrun,editPeron;
    SimpleAdapter ADAhere;
    ListView LV2;
    TextView txtItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peron);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnGet=findViewById(R.id.btnUrun);
        btnUpdate=findViewById(R.id.btnUpdate);
        editUrun=findViewById(R.id.editUrun);
        editPeron=findViewById(R.id.editUrunDegis);
        LV2=(ListView)findViewById(R.id.LV2);
        txtItem=findViewById(R.id.txtItem);


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(internetKontrol()){ //internet kontrol methodu çağırılıyor
                    List<Map<String,String>> MyData = null;
                    PeronActivity.GetData mydata =new GetData();
                    MyData= mydata.doInBackground();
                    String[] fromwhere = { "ItemDimCode"};

                    int[] viewswhere = {R.id.ItemDimCode};
                    ADAhere = new SimpleAdapter(PeronActivity.this, MyData,R.layout.list2, fromwhere, viewswhere);
                    LV2.setAdapter(ADAhere);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Inernet Bağlantınız Yok",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetKontrol()){ //internet kontrol methodu çağırılıyor

                    List<Map<String,String>> MyData2 = null;
                    PeronActivity.SetData mydata =new SetData();
                    MyData2= mydata.doInBackground();
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Kayıt Yapıldı...",Toast.LENGTH_SHORT);
                    toast2.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Inernet Bağlantınız Yok",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
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
        }else if (id == R.id.nav_cikis) {
            final AlertDialog.Builder builder= new AlertDialog.Builder(PeronActivity.this);
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

    public class GetData {
        Connection connect;
        String ConnectionResult = "";
        Boolean isSuccess = false;

        public List<Map<String,String>> doInBackground() {
            List<Map<String, String>> data = null;
            data = new ArrayList<Map<String, String>>();

            try
            {
                ConnectionHelper conStr=new ConnectionHelper();
                connect =conStr.connectionclasss();        // Connect to database


                if (connect == null)
                {
                    ConnectionResult = "Bağlantı Kontrol ediniz!";

                }
                else {

                    if (editUrun.getText().toString().isEmpty()) {
                        Toast toast2 = Toast.makeText(getApplicationContext(),"Lütfen boşlukları Doldurun...",Toast.LENGTH_SHORT);
                        toast2.show();
                    }
                    else {
                        ProgressDialog progressDialog = new ProgressDialog(PeronActivity.this);
                        progressDialog.setMessage("Devam eden işleminiz bulunmaktadır. Lütfen bekleyiniz..");
                        progressDialog.show();
                        String query2 ="SELECT top 1 ItemDescription FROM cdItemdesc where LangCode='EN' AND ItemDescription LIKE '%"+ editUrun.getText().toString() +"%'";
                        Statement stmt2 = connect.createStatement();
                        ResultSet rs2 = stmt2.executeQuery(query2);


                        while (rs2.next()) {


                            Map<String, String> datanum2 = new HashMap<String, String>();
                            datanum2.put("ItemDimCode", rs2.getString("ItemDescription"));
                            editPeron.setText(rs2.getString("ItemDescription"));
                            txtItem.setText(rs2.getString("ItemDescription"));
                            data.add(datanum2);
                        }
                        ConnectionResult = " Başarılı";
                        isSuccess = true;
                        connect.close();
                        progressDialog.dismiss();

                    }
                    Toast toast2 = Toast.makeText(getApplicationContext(),ConnectionResult,Toast.LENGTH_SHORT);
                    toast2.show();

                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                ConnectionResult = ex.getMessage();
            }
            return data;
        }
    }

    private class SetData {
        Connection connect;
        String ConnectionResult = "";
        Boolean isSuccess = false;
        public List<Map<String,String>> doInBackground() {
            List<Map<String, String>> data = null;
            data = new ArrayList<Map<String, String>>();

            try
            {
                ConnectionHelper conStr=new ConnectionHelper();
                connect =conStr.connectionclasss();        // Connect to database


                if (connect == null)
                {
                    ConnectionResult = "Bağlantı Kontrol ediniz!";
                }
                else {

                    if (editPeron.getText().toString().isEmpty()) {
                        Toast toast2 = Toast.makeText(getApplicationContext(),"Lütfen boşlukları Doldurun...",Toast.LENGTH_SHORT);
                        toast2.show();
                    }

                    String query2 ="update cdItemdesc  set ItemDescription='"+ editPeron .getText().toString().trim() +"' where LangCode='EN' AND ItemDescription='"+ txtItem.getText().toString().trim() +"'";
                    Statement stmt2 = connect.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(query2);

                    ConnectionResult = " Kayıt Başarılı";
                    isSuccess = true;
                    connect.close();
                }


            }
            catch (Exception ex)
            {
                isSuccess = false;
                ConnectionResult = ex.getMessage();
            }
            return data;

        }
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
