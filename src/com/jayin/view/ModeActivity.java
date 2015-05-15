package com.jayin.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cn.daming.deskclock.Alarm;
import com.cn.daming.deskclock.AlarmProvider;
import com.cn.daming.deskclock.Alarms;
import com.cn.daming.deskclock.PreferencesUtils;
import com.cn.daming.deskclock.R;
import com.cn.daming.deskclock.ToastMaster;

public class ModeActivity extends Activity {
	SwipeMenuListView listView ; 
	Button bn_add_timeMode;
	List<String> list_mode  ; 
	TextView textView_now_mode ; 
	private static SimpleAdapter mAdapter;
	private static String wokingtable = "";
	private static boolean ischangemode=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode);
		ischangemode =false;
		wokingtable = PreferencesUtils.getString("wokingtable", "alarms");
		listView = (SwipeMenuListView) findViewById(R.id.listView_time_mode);
		bn_add_timeMode = (Button)findViewById(R.id.add_mode);
		textView_now_mode = (TextView)findViewById(R.id.text_now_mode);
		updatenow_mode(wokingtable);
		mAdapter = new SimpleAdapter(this, getdata(), R.layout.timesetlistitem, new String[]{"mode_name","message"}, new int[]{R.id.device_name,R.id.device_mesge});
		
		listView.setAdapter(mAdapter);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new AlertDialog.Builder(ModeActivity.this).setTitle(getString(R.string.mode_dailog_title))
					.setItems(R.array.mode_dailog_menu,new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {

							String[] menus = getResources().getStringArray(R.array.mode_dailog_menu);
							if (menus[which].equals("删除")) {
								deletetable(position);
							}
							if (menus[which].equals("启用")) {
								ischangemode =true;
								setdefalutmode(position);
							}
						}

					})
					.show()
					;
				
				return true;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				editmode(list_mode.get(arg2));
			}
		});
		bn_add_timeMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				opendialog();
			}
		});
		
	}
	private void opendialog() {
		LayoutInflater inflater = LayoutInflater.from(ModeActivity.this);
		View view = inflater.inflate(R.layout.listitem_edit_device,null);
		Button button = (Button)view.findViewById(R.id.bn_update_name);
		final EditText editText = (EditText)view.findViewById(R.id.ed_name);
		editText.setHint("如工作日或周末");
		
		final AlertDialog dialog  = new AlertDialog.Builder(ModeActivity.this).
				setTitle("命名该情景模式").
				setView(view)
				.create();
		dialog.show();
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = editText.getText().toString();
				if (name.contains(" ")) {
					name = name.replace(" ", "");
				}
				AlarmProvider.copytable(name==null?"默认情景模式":name);
				ToastMaster.showtoast(ModeActivity.this, "创建成功");
				dialog.dismiss();
				editmode(name);
				
			}
		});
		
	}
	protected void editmode(String mode_name) {
	
		nowMode = PreferencesUtils.getString("wokingtable", "alarms");
			PreferencesUtils.putString("wokingtable", mode_name);
		Intent intent = new Intent(ModeActivity.this,TimesetActivity.class);
		startActivity(intent);

	}
	String nowMode ;
	@Override
	protected void onResume() {
		System.out.println("Mode acitivy onResume()");
		if (ischangemode) {
			
		}else {
			PreferencesUtils.putString("wokingtable", nowMode);
			
		}
		mAdapter = new SimpleAdapter(ModeActivity.this, getdata(), R.layout.timesetlistitem, new String[]{"mode_name","message"}, new int[]{R.id.device_name,R.id.device_mesge});
		listView.setAdapter(mAdapter);
		super.onResume();

	}

	protected void setdefalutmode(int arg2) {
		PreferencesUtils.putString("wokingtable", list_mode.get(arg2));
		ToastMaster.showtoast(ModeActivity.this, "情景模式已经设置为"+(list_mode.get(arg2).equals("alarms")?"默认":list_mode.get(arg2)));
//		setothercolor(arg2);
		updatenow_mode(list_mode.get(arg2));
	}
	private void setothercolor(int arg2) {
		for (int i = 0; i < list_mode.size(); i++) {
			if (i == arg2) {
				listView.getChildAt(arg2).setBackgroundColor(Color.GREEN);
			}else {
				listView.getChildAt(i).setBackgroundColor(0x66ccff);

			}
		}
	}

	private void deletetable(int position) {
		AlarmProvider.deltetable(list_mode.remove(position));
		mAdapter = new SimpleAdapter(ModeActivity.this, getdata(), R.layout.timesetlistitem, new String[]{"mode_name","message"}, new int[]{R.id.device_name,R.id.device_mesge});
		listView.setAdapter(mAdapter);
		
	}
	private List<HashMap<String, String>> getdata() {
		List<HashMap<String, String>> dataList = new ArrayList<HashMap<String,String>>();
		list_mode = AlarmProvider.getAlltable();
		for (int i = 0; i < list_mode.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
					map.put("mode_name", list_mode.get(i).equals("alarms")?"默认":list_mode.get(i));
					Cursor	cursor = Alarms.getAlarmsCursorbytable(getContentResolver(), list_mode.get(i),wokingtable);
					StringBuilder builder = new StringBuilder();
					String lastname = "";
					while (cursor.moveToNext()) {
						
						Alarm alarm = new Alarm(cursor);
						if (alarm.enabled) {
							final Calendar c = Calendar.getInstance();
							c.set(Calendar.HOUR_OF_DAY, alarm.hour);
							c.set(Calendar.MINUTE,  alarm.minutes);
							
							String actionString =	alarm.flag?getString(R.string.action_on):getString(R.string.action_off);
							
							if (alarm.label.equals(lastname)) {
								builder.append("\t "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+" "+actionString+" "+alarm.daysOfWeek.toString(ModeActivity.this, false)+"\t");
								
							}else {
								if (lastname.equals("")) {
									builder.append(alarm.label+": "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+" "+actionString+" "+alarm.daysOfWeek.toString(ModeActivity.this, false));
									
								}else {
									builder.append("\n"+alarm.label+": "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+" "+actionString+" "+alarm.daysOfWeek.toString(ModeActivity.this, false));
									
								}
										
							}
							
							lastname = alarm.label;
						}
						
					}
//					System.out.println("mesg :"+builder.toString());
					cursor.close();
					map.put("message", builder.toString());

			dataList.add(map);
		}
		return dataList;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	public void updatenow_mode(String mode) {
		if (mode.equals("alarms")) {
			textView_now_mode.setText("当前工作模式：默认");
			
		}else {
			textView_now_mode.setText("当前工作模式："+mode);
			
		}
	}
}
