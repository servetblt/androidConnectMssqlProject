package com.example.bulut.adxsevk;

import android.app.Activity;
import android.app.FragmentTransaction;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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

public class SiparisKontrol extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btn_Get;
    ListView LV_Data;
    SimpleAdapter ADAhere;
    EditText toDate;
    EditText fromDate;
    EditText Urun;
    CheckBox checkBox,checkBeden;
    TextView TextRenk;

    @Override
    protected void onStart() {
        super.onStart();
        EditText txtDate=findViewById(R.id.etxt_todate);
        EditText txtDate2=findViewById(R.id.etxt_fromdate);
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){
                    DateDialog dialog=new DateDialog(v);
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    dialog.show(ft,"DatPicker");
                }
            }
        });

        txtDate2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    DateDialog dialog=new DateDialog(v);
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    dialog.show(ft,"DatPicker");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_siparis_kontrol);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LV_Data=(ListView)findViewById(R.id.LV_Data);
        btn_Get=(Button)findViewById(R.id.btn_Get);
        Urun=findViewById(R.id.editItem);
        checkBox=findViewById(R.id.checkBox);
        checkBeden=findViewById(R.id.checkBeden);

        TextRenk=findViewById(R.id.txtRenk);

        toDate=findViewById(R.id.etxt_todate);
        fromDate=findViewById(R.id.etxt_fromdate);
        final Activity activity=this;
        btn_Get.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(internetKontrol()){ //internet kontrol methodu çağırılıyor
                    List<Map<String,String>> MyData = null;
                    GetData mydata =new GetData();
                    MyData= mydata.doInBackground();
                    String[] fromwhere = { "Barcodee","Qtyy","ItemDimCode","ItemTypeCode"};

                    int[] viewswhere = {R.id.Barcodee ,R.id.Qtyy, R.id.ItemDimCode,R.id.ItemTypeCode};
                    ADAhere = new SimpleAdapter(SiparisKontrol.this, MyData,R.layout.listtemplate2, fromwhere, viewswhere);
                    LV_Data.setAdapter(ADAhere);

                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Inernet Bağlantınız Yok",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        } );

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
            final AlertDialog.Builder builder= new AlertDialog.Builder(SiparisKontrol.this);
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

    public List<Map<String, String>> doInBackground() {
        List<Map<String, String>> data = null;
        data = new ArrayList<Map<String, String>>();

        try {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionclasss();        // Connect to database


            if (connect == null) {
                ConnectionResult = "Bağlantı Kontrol ediniz!";

            } else {

                if (Urun.getText().toString().isEmpty() || toDate.getText().toString() == "" || fromDate.getText().toString() == "") {
                    Toast toast2 = Toast.makeText(getApplicationContext(), "Lütfen boşlukları Doldurun...", Toast.LENGTH_SHORT);
                    toast2.show();
                } else {

                    if (checkBox.isChecked()) {
                        ProgressDialog progressDialog = new ProgressDialog(SiparisKontrol.this);
                        progressDialog.setMessage("Devam eden işleminiz bulunmaktadır. Lütfen bekleyiniz..");
                        progressDialog.show();
                        TextRenk.setText("Renk");
                        String query =
                                "  SELECT  Barcode = (select ColorDescription from cdColorDesc where cdColorDesc.ColorCode=trOrderLine.ColorCode)\n" +
                                        "  ,ItemDim1Code = SUM(trOrderLine.Qty1) \n" +
                                        "  ,ItemDescription=cdItemDesc.ItemDescription \n" +
                                        "  ,Envanter=(INVENTORY.InventoryQty1) \n" +
                                        "  FROM (SELECT * , CustomerCode = CurrAccCode, SaleProcessCode = ProcessCode FROM trOrderHeader WITH(NOLOCK)) AS trOrderHeader \n" +
                                        "  INNER JOIN (SELECT * , ProductCode = ItemCode FROM trOrderLine WITH(NOLOCK) WHERE ItemTypeCode = 1) AS trOrderLine ON trOrderLine.OrderHeaderID = trOrderHeader.OrderHeaderID \n" +
                                        "  INNER JOIN (SELECT SUM(ItemInventory.InventoryQty1) as InventoryQty1,ItemCode,ColorCode FROM ItemInventory WITH(NOLOCK) WHERE ItemTypeCode = 1 AND WarehouseCode='1007' GROUP BY ItemInventory.ItemCode,ColorCode ) AS INVENTORY ON INVENTORY.ItemCode = trOrderLine.ItemCode and INVENTORY.ColorCode = trOrderLine.ColorCode\n" +
                                        "  LEFT OUTER JOIN (SELECT OrderLineID , InvoicedAmount = SUM(trInvoiceLineCurrency.TaxBase) , InvoicedNetAmount = SUM(trInvoiceLineCurrency.NetAmount) \n" +
                                        "   FROM trInvoiceHeader WITH(NOLOCK) \n" +
                                        "   INNER JOIN trInvoiceLine WITH(NOLOCK) ON trInvoiceLine.InvoiceHeaderID = trInvoiceHeader.InvoiceHeaderID  \n" +
                                        "  LEFT OUTER JOIN trInvoiceLineCurrency WITH(NOLOCK) ON trInvoiceLineCurrency.InvoiceLineID = trInvoiceLine.InvoiceLineID AND trInvoiceLineCurrency.CurrencyCode = trInvoiceHeader.LocalCurrencyCode\n" +
                                        "   GROUP BY OrderLineID \n" +
                                        "   ) AS Invoices \n" +
                                        "   ON Invoices.OrderLineID = trOrderLine.OrderLineID \n" +
                                        "   LEFT OUTER JOIN cdItemDesc WITH(NOLOCK) ON cdItemDesc.ItemTypeCode = trOrderLine.ItemTypeCode AND cdItemDesc.ItemCode = trOrderLine.ItemCode AND cdItemDesc.LangCode = 'EN' \n" +
                                        "   WHERE trOrderLine.ItemTypeCode = 1 \n" +
                                        "   AND trOrderHeader.OrderTypeCode = 1\n" +
                                        " and ItemDescription like '%" + Urun.getText().toString() + "%' " +
                                        "  AND OrderDate BETWEEN '" + toDate.getText().toString() + "' AND '" + fromDate.getText().toString() + "'" +
                                        " and ProcessCode='R' AND trOrderHeader.StoreCode='1007'\n" +
                                        "   group by cdItemDesc.ItemDescription,trOrderHeader.StoreCode,INVENTORY.InventoryQty1,trOrderLine.ColorCode";
                        Statement stmt = connect.createStatement();
                        ResultSet rs = stmt.executeQuery(query);


                        while (rs.next()) {


                            Map<String, String> datanum = new HashMap<String, String>();
                            datanum.put("Barcodee", rs.getString("Barcode"));
                            datanum.put("Qtyy", rs.getString("Envanter"));
                            datanum.put("ItemDimCode", rs.getString("ItemDescription"));
                            datanum.put("ItemTypeCode", rs.getString("ItemDim1Code"));
                            data.add(datanum);
                        }
                        ConnectionResult = " Başarılı";
                        isSuccess = true;
                        connect.close();
                        progressDialog.dismiss();
                    }
                    if (checkBeden.isChecked()) {
                        ProgressDialog progressDialog = new ProgressDialog(SiparisKontrol.this);
                        progressDialog.setMessage("Devam eden işleminiz bulunmaktadır. Lütfen bekleyiniz..");
                        progressDialog.show();
                        TextRenk.setText("Renk-Beden");
                        String query = "SELECT  trOrderLine.ItemCode,Barcode =  ColorDescription+' - '+trOrderLine.ItemDim1Code\n" +
                                "                                     ,ItemDim1Code = SUM(trOrderLine.Qty1) \n" +
                                "                                     ,ItemDescription=cdItemDesc.ItemDescription \n" +
                                "                                     ,Envanter=(INVENTORY.InventoryQty1) \n" +
                                "                                     FROM (SELECT * , CustomerCode = CurrAccCode, SaleProcessCode = ProcessCode FROM trOrderHeader WITH(NOLOCK)) AS trOrderHeader \n" +
                                "                                     INNER JOIN (SELECT * , ProductCode = ItemCode FROM trOrderLine WITH(NOLOCK) WHERE ItemTypeCode = 1) AS trOrderLine ON trOrderLine.OrderHeaderID = trOrderHeader.OrderHeaderID \n" +
                                "                                   INNER JOIN (SELECT SUM(ItemInventory.InventoryQty1) as InventoryQty1,ItemCode,ColorCode,ItemDim1Code FROM ItemInventory WITH(NOLOCK) WHERE ItemTypeCode = 1 AND WarehouseCode='1007' GROUP BY ItemInventory.ItemCode,ColorCode,ItemDim1Code ) AS INVENTORY ON INVENTORY.ItemCode = trOrderLine.ItemCode and INVENTORY.ColorCode = trOrderLine.ColorCode and INVENTORY.ItemDim1Code = trOrderLine.ItemDim1Code\n" +
                                "                                      LEFT OUTER JOIN (SELECT OrderLineID , InvoicedAmount = SUM(trInvoiceLineCurrency.TaxBase) , InvoicedNetAmount = SUM(trInvoiceLineCurrency.NetAmount) \n" +
                                "                                        FROM trInvoiceHeader WITH(NOLOCK) \n" +
                                "                                        INNER JOIN trInvoiceLine WITH(NOLOCK) ON trInvoiceLine.InvoiceHeaderID = trInvoiceHeader.InvoiceHeaderID  \n" +
                                "                                       LEFT OUTER JOIN trInvoiceLineCurrency WITH(NOLOCK) ON trInvoiceLineCurrency.InvoiceLineID = trInvoiceLine.InvoiceLineID AND trInvoiceLineCurrency.CurrencyCode = trInvoiceHeader.LocalCurrencyCode\n" +
                                "                                        GROUP BY OrderLineID \n" +
                                "                                        ) AS Invoices \n" +
                                "                                        ON Invoices.OrderLineID = trOrderLine.OrderLineID \n" +
                                "                                        LEFT OUTER JOIN cdItemDesc WITH(NOLOCK) ON cdItemDesc.ItemTypeCode = trOrderLine.ItemTypeCode AND cdItemDesc.ItemCode = trOrderLine.ItemCode AND cdItemDesc.LangCode = 'EN' \n" +
                                "                                      INNER JOIN cdColorDesc ON cdColorDesc.ColorCode=trOrderLine.ColorCode\n" +
                                "                                     WHERE trOrderLine.ItemTypeCode = 1 \n" +
                                "                                      AND trOrderHeader.OrderTypeCode = 1\n" +
                                "                                      and ItemDescription like '%" + Urun.getText().toString() + "%' " +
                                "                                     AND OrderDate BETWEEN '" + toDate.getText().toString() + "' AND '" + fromDate.getText().toString() + "'" +
                                "and ProcessCode='R' AND trOrderHeader.StoreCode='1007'\n" +
                                "                                      group by trOrderLine.ItemCode,cdItemDesc.ItemDescription,trOrderHeader.StoreCode\n" +
                                ",trOrderLine.ColorCode,cdColorDesc.ColorDescription,trOrderLine.ItemDim1Code,INVENTORY.InventoryQty1;";

                        Statement stmt = connect.createStatement();
                        ResultSet rs = stmt.executeQuery(query);


                        while (rs.next()) {


                            Map<String, String> datanum = new HashMap<String, String>();
                            datanum.put("Barcodee", rs.getString("Barcode"));
                            datanum.put("Qtyy", rs.getString("Envanter"));
                            datanum.put("ItemDimCode", rs.getString("ItemDescription"));
                            datanum.put("ItemTypeCode", rs.getString("ItemDim1Code"));
                            data.add(datanum);
                        }
                        ConnectionResult = " Başarılı";
                        isSuccess = true;
                        connect.close();
                        progressDialog.dismiss();
                    } else {
                        ProgressDialog progressDialog = new ProgressDialog(SiparisKontrol.this);
                        progressDialog.setMessage("Devam eden işleminiz bulunmaktadır. Lütfen bekleyiniz..");
                        progressDialog.show();
                        TextRenk.setText("İptal");
                        String query2 = "" +
                                "SELECT  Barcode = cast(SUM(trOrderLine.CancelQty1)as int) \n" +
                                "   , ItemDim1Code = SUM(trOrderLine.Qty1) \n" +
                                "   ,ItemDescription=cdItemDesc.ItemDescription \n" +
                                "   ,Envanter=(INVENTORY.InventoryQty1) \n" +
                                "   FROM (SELECT * , CustomerCode = CurrAccCode, SaleProcessCode = ProcessCode FROM trOrderHeader WITH(NOLOCK)) AS trOrderHeader \n" +
                                "   INNER JOIN (SELECT * , ProductCode = ItemCode FROM trOrderLine WITH(NOLOCK) WHERE ItemTypeCode = 1) AS trOrderLine ON trOrderLine.OrderHeaderID = trOrderHeader.OrderHeaderID \n" +
                                "   INNER JOIN (SELECT SUM(ItemInventory.InventoryQty1) as InventoryQty1,ItemCode FROM ItemInventory WITH(NOLOCK) WHERE ItemTypeCode = 1 AND WarehouseCode='1007' GROUP BY ItemInventory.ItemCode ) AS INVENTORY ON INVENTORY.ItemCode = trOrderLine.ItemCode\n" +
                                " LEFT OUTER JOIN (SELECT OrderLineID , InvoicedAmount = SUM(trInvoiceLineCurrency.TaxBase) , InvoicedNetAmount = SUM(trInvoiceLineCurrency.NetAmount) \n" +
                                "   FROM trInvoiceHeader WITH(NOLOCK) \n" +
                                "   INNER JOIN trInvoiceLine WITH(NOLOCK) ON trInvoiceLine.InvoiceHeaderID = trInvoiceHeader.InvoiceHeaderID  \n" +
                                "  LEFT OUTER JOIN trInvoiceLineCurrency WITH(NOLOCK) ON trInvoiceLineCurrency.InvoiceLineID = trInvoiceLine.InvoiceLineID AND trInvoiceLineCurrency.CurrencyCode = trInvoiceHeader.LocalCurrencyCode\n" +
                                "   GROUP BY OrderLineID \n" +
                                "   ) AS Invoices \n" +
                                "   ON Invoices.OrderLineID = trOrderLine.OrderLineID \n" +
                                "   LEFT OUTER JOIN cdItemDesc WITH(NOLOCK) ON cdItemDesc.ItemTypeCode = trOrderLine.ItemTypeCode AND cdItemDesc.ItemCode = trOrderLine.ItemCode AND cdItemDesc.LangCode = 'EN' \n" +
                                "   WHERE trOrderLine.ItemTypeCode = 1 \n" +
                                "   AND trOrderHeader.OrderTypeCode = 1\n" +
                                "   and ItemDescription like '%" + Urun.getText().toString() + "%'\n" +
                                "   AND OrderDate BETWEEN '" + toDate.getText().toString() + "' AND '" + fromDate.getText().toString() + "' and ProcessCode='R' AND trOrderHeader.StoreCode='1007'\n" +
                                "   group by cdItemDesc.ItemDescription,trOrderHeader.StoreCode,INVENTORY.InventoryQty1";
                        Statement stmt2 = connect.createStatement();
                        ResultSet rs2 = stmt2.executeQuery(query2);


                        while (rs2.next()) {


                            Map<String, String> datanum2 = new HashMap<String, String>();
                            datanum2.put("Barcodee", rs2.getString("Barcode"));
                            datanum2.put("Qtyy", rs2.getString("Envanter"));
                            datanum2.put("ItemDimCode", rs2.getString("ItemDescription"));
                            datanum2.put("ItemTypeCode", rs2.getString("ItemDim1Code"));
                            data.add(datanum2);
                        }
                        ConnectionResult = " Başarılı";
                        isSuccess = true;
                        connect.close();
                        progressDialog.dismiss();

                    }

                }
                Toast toast2 = Toast.makeText(getApplicationContext(), ConnectionResult, Toast.LENGTH_SHORT);
                toast2.show();

            }
        } catch (Exception ex) {
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

