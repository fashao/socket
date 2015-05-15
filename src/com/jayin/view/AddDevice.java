package com.jayin.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cn.daming.deskclock.PreferencesUtils;
import com.cn.daming.deskclock.ProgressDialogUtils;
import com.cn.daming.deskclock.R;
import com.cn.daming.deskclock.SocketUtils;

public class AddDevice extends Activity {
	Button bn_ad_device;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adddevice);
		bn_ad_device = (Button)findViewById(R.id.bn_add_device);
		bn_ad_device.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				add_device();
			}
		});
	}
	protected void add_device() {
		ProgressDialogUtils.showProgressmessge(AddDevice.this, "请稍后", "正在配置wifi...");

		final	int device = PreferencesUtils.getInt("device", 0);
		final String ssid = PreferencesUtils.getString("ssid", "");
		final String pwd = PreferencesUtils.getString("pwd", "");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SocketUtils socketUtils = new SocketUtils();
				socketUtils.host = PreferencesUtils.getString("socket_addr", "");
				socketUtils.port = PreferencesUtils.getInt("socket_port", 0);
				String dataString = ssid+":"+pwd+":"+PreferencesUtils.getString(PreferencesUtils.PHONE, "")+":"+device+":"+PreferencesUtils.getString("server", "");
				System.out.println("set up "+dataString);
				String resultString = socketUtils.clientget("setup", dataString);
				if (resultString.equals("ok")) {
					PreferencesUtils.putInt("device", device+1);
					Intent intent = new Intent(AddDevice.this, EditDeviceActivity.class);
					intent.putExtra("num", 5);
					startActivity(intent);
				}
				ProgressDialogUtils.dismiss();
				
			}
		}).start();

	}
}
