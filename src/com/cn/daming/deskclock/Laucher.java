package com.cn.daming.deskclock;

import com.jayin.view.MianActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Laucher extends Activity {
	EditText editText_ph;
	Button button_createButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_laucher);
		editText_ph = (EditText)findViewById(R.id.ed_phone);
		button_createButton = (Button)findViewById(R.id.bn_createUser);
		if (!checkisfirst()) {
			button_createButton.setText("登录");
		}
	
		button_createButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int i = checkinput(editText_ph.getText().toString());
				if (  i == -1) {
					
				
				ProgressDialogUtils.showProgressmessge(Laucher.this, "正在创建用户", "请稍后...");
				SocketUtils socketUtils = new SocketUtils();
				socketUtils.clientget("init", editText_ph.getText().toString());
				ProgressDialogUtils.dismiss();
				ToastMaster.showtoast(Laucher.this, "创建成功！");
				PreferencesUtils.putString(PreferencesUtils.PHONE, editText_ph.getText().toString());
				Intent intent = new Intent(Laucher.this, MianActivity.class);
				startActivity(intent);
				}else if (i== 0) {
					Intent intent = new Intent(Laucher.this, MianActivity.class);
					startActivity(intent);
				}else {
					return ;
				}
			}
		});
	}
	protected int checkinput(String string) {
		String phString = PreferencesUtils.getString(PreferencesUtils.PHONE, "null");
		if (phString.equals("null")) {  //fisrt time 
			return -1; 
		}
		if (!phString.equals(string)) { //second
			ToastMaster.showtoast(Laucher.this, "手机号码错误");
			return 1;
		}else {
			return 0;
		}
	}
	public boolean checkisfirst() {
		String phString = PreferencesUtils.getString(PreferencesUtils.PHONE, "null");
		if (phString.equals("null")) {  //fisrt time 
			return true; 
		}
		return false;
	}
}
