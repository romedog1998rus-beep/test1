package com.forge.signalmapper;

import android.Manifest;import android.app.*;import android.os.*;import android.content.*;import android.content.pm.PackageManager;import android.location.*;import android.telephony.*;import android.view.*;import android.webkit.*;import android.widget.*;import java.io.*;import java.text.*;import java.util.*;

public class MainActivity extends Activity{
  WebView map; TextView status, log; ArrayList<String> rows=new ArrayList<>(); int lastDbm=-120; LocationManager lm;
  public void onCreate(Bundle b){super.onCreate(b); rows.add("time,lat,lon,dbm,zone"); buildUi(); askPerms(); setupSignal(); setupGps();}
  void buildUi(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(0xff080404);
    TextView title=new TextView(this);title.setText("✦ FORGE SIGNAL MAPPER ✦\nNoosphere dead-zone auspex");title.setTextColor(0xffff3b1f);title.setTextSize(20);title.setGravity(17);title.setPadding(10,16,10,12);root.addView(title,new LinearLayout.LayoutParams(-1,-2));
    status=new TextView(this);status.setText("Ожидание GPS и сигнала…");status.setTextColor(0xfff2b06a);status.setPadding(14,8,14,8);root.addView(status,new LinearLayout.LayoutParams(-1,-2));
    map=new WebView(this);map.getSettings().setJavaScriptEnabled(true);map.loadUrl("file:///android_asset/map.html");root.addView(map,new LinearLayout.LayoutParams(-1,0,1));
    LinearLayout bar=new LinearLayout(this);bar.setPadding(8,8,8,8);Button save=btn("EXPORT CSV");Button mark=btn("MARK RITE");bar.addView(mark,new LinearLayout.LayoutParams(0,-2,1));bar.addView(save,new LinearLayout.LayoutParams(0,-2,1));root.addView(bar);
    log=new TextView(this);log.setTextColor(0xffd6a25f);log.setPadding(12,6,12,14);root.addView(log,new LinearLayout.LayoutParams(-1,-2));setContentView(root);save.setOnClickListener(v->exportCsv());mark.setOnClickListener(v->toast("Точка будет записана при следующем GPS обновлении"));}
  Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextColor(0xffffd08a);b.setBackgroundColor(0xff2b0907);return b;}
  void askPerms(){if(Build.VERSION.SDK_INT>=23)requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE},7);}
  void setupGps(){lm=(LocationManager)getSystemService(LOCATION_SERVICE);try{lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,3,l->{record(l);});}catch(Exception e){status.setText("Нет разрешения GPS");}}
  void record(Location l){String zone=lastDbm<-110?"DEAD":(lastDbm<-95?"WEAK":"OK");String t=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date());rows.add(t+","+l.getLatitude()+","+l.getLongitude()+","+lastDbm+","+zone);status.setText("LAT "+l.getLatitude()+"  LON "+l.getLongitude()+"  SIGNAL "+lastDbm+" dBm  "+zone);map.evaluateJavascript("addPoint("+l.getLatitude()+","+l.getLongitude()+","+lastDbm+")",null);log.setText("Записано точек: "+(rows.size()-1));}
  void setupSignal(){try{TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);if(Build.VERSION.SDK_INT>=31)tm.registerTelephonyCallback(getMainExecutor(),new TelephonyCallback(){ });}catch(Exception ignored){} }
  void exportCsv(){try{File f=new File(getExternalFilesDir(null),"forge_signal_points.csv");FileWriter w=new FileWriter(f);for(String r:rows)w.write(r+"\n");w.close();toast("CSV: "+f.getAbsolutePath());}catch(Exception e){toast("Ошибка экспорта: "+e.getMessage());}}
  void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
