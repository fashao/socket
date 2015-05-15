package com.jayin.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cn.daming.deskclock.Police;
import com.cn.daming.deskclock.PreferencesUtils;
import com.cn.daming.deskclock.R;
public class MianActivity extends Activity {
	MyImageView joke;
	MyImageView timeset;
	MyImageView device_status;
	MyImageView wifiset;
	MyImageView time_mode;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startService(new Intent(MianActivity.this, Police.class));
		joke = (MyImageView) findViewById(R.id.add_device);
		timeset = (MyImageView)findViewById(R.id.time);
		device_status = (MyImageView)findViewById(R.id.state);
		time_mode = (MyImageView)findViewById(R.id.mode);
		time_mode.setOnClickIntent(new MyImageView.OnViewClick() {
			
			@Override
			public void onClick() {
		startActivity(new Intent(MianActivity.this,ModeActivity.class));

				
			}
		});	
		
		wifiset = (MyImageView)findViewById(R.id.wifisetup);
		wifiset.setOnClickIntent(new MyImageView.OnViewClick() {
			
			@Override
			public void onClick() {
		startActivity(new Intent(MianActivity.this,WifiSetActivity.class));

				
			}
		});	
		timeset.setOnClickIntent(new MyImageView.OnViewClick() {
			
			@Override
			public void onClick() {
		startActivity(new Intent(MianActivity.this,TimesetActivity.class));

				
			}
		});
		joke.setOnClickIntent(new MyImageView.OnViewClick() {

			public void onClick() {
				Toast.makeText(MianActivity.this, "点击事件", 1000)
						.show();
				System.out.println("1");
				startActivity(new Intent(MianActivity.this,AddDevice.class));
			}
		});
		device_status.setOnClickIntent(new MyImageView.OnViewClick() {
			
			@Override
			public void onClick() {
				startActivity(new Intent(MianActivity.this,StatusActivity.class));

				
			}
		});
	}

	
}