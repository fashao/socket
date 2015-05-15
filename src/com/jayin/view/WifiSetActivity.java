package com.jayin.view;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.cn.daming.deskclock.Alarm;
import com.cn.daming.deskclock.Alarms;
import com.cn.daming.deskclock.PreferencesUtils;
import com.cn.daming.deskclock.ProgressDialogUtils;
import com.cn.daming.deskclock.R;
import com.cn.daming.deskclock.SocketUtils;
import com.cn.daming.deskclock.ToastMaster;

public class WifiSetActivity extends Activity {

	EditText ed_ssid ,ed_pwd,ed_server;
	Button bt_wifiset;
	private String url="192.168.4.1";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_set);
		
		ed_pwd = (EditText)findViewById(R.id.ed_pwd);
		ed_ssid = (EditText)findViewById(R.id.ed_ssid);
		ed_server = (EditText)findViewById(R.id.ed_server);
		String ssidString  = getConnectWifiSsid();
		ed_ssid.setText(ssidString.substring(1, ssidString.length()-1));
		bt_wifiset = (Button)findViewById(R.id.bt_wifiset);
		if (PreferencesUtils.getInt("device", 0)!=0) {
			ed_pwd.setText(PreferencesUtils.getString("pwd",""));
			ed_ssid.setText(PreferencesUtils.getString("ssid",""));
			ed_server.setText(PreferencesUtils.getString("server",""));
			bt_wifiset.setText("修改参数");
		}
		bt_wifiset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				PreferencesUtils.putString( "ssid", ed_ssid.getText().toString());
				PreferencesUtils.putString("pwd", ed_pwd.getText().toString());
				PreferencesUtils.putString("server", ed_server.getText().toString());
				
				PreferencesUtils.putInt("device", 1);
				
				 submit(ed_ssid.getText().toString(),ed_pwd.getText().toString(),"0",ed_server.getText().toString());
			}
		});
		
	}
	protected void submit(final String ssid,final String pwd,final String device,final String server) {
		ProgressDialogUtils.showProgressmessge(WifiSetActivity.this, "请稍后", "正在配置wifi...");
		PreferencesUtils.putString( "socket_addr","192.168.4.1" );
		PreferencesUtils.putInt( "socket_port",8888);
		
		PreferencesUtils.putInt("socket_num",5 );
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SocketUtils socketUtils = new SocketUtils();
				socketUtils.host = PreferencesUtils.getString("socket_addr", "");
				socketUtils.port = PreferencesUtils.getInt("socket_port", 0);
				
				String dataString = ssid+":"+pwd+":"+PreferencesUtils.getString(PreferencesUtils.PHONE, "")+":"+device+":"+server;
				System.out.println("set up "+dataString);
				String result  = socketUtils.clientget("setup", dataString);
				if (result.contains("ok")) {
					System.out.println("set up ok");
				for (int i = 1; i <= PreferencesUtils.getInt( "socket_num",0); i++) {
					Alarm alarm = new Alarm(WifiSetActivity.this,i);
					alarm.label="插孔"+i;
//						alarm.socket = i;
//						System.out.println(alarm.);
					Alarms.addSocket(WifiSetActivity.this, alarm);
				}
				ProgressDialogUtils.dismiss();
				Intent intent = new Intent(WifiSetActivity.this, EditDeviceActivity.class);
				intent.putExtra("num", 5);
				startActivity(intent);
				}else {
					System.out.println("初始化插座失败");
				}
				
			}
		}).start();
		
	}
	private String getConnectWifiSsid(){
		   WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		   Log.d("wifiInfo", wifiInfo.toString());
		   Log.d("SSID",wifiInfo.getSSID());
		   return wifiInfo.getSSID();
	}
}
