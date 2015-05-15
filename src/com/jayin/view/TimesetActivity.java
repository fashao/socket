package com.jayin.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cn.daming.deskclock.Alarm;
import com.cn.daming.deskclock.AlarmProvider;
import com.cn.daming.deskclock.Alarms;
import com.cn.daming.deskclock.DeskClockMainActivity;
import com.cn.daming.deskclock.R;
import com.cn.daming.deskclock.ToastMaster;


public class TimesetActivity extends Activity {
	private SwipeMenuListView mListView;
    private static Cursor mCursor;
//	private ArrayAdapter<String> mAdapter;
	private static SimpleAdapter mAdapter;
	private  List< HashMap<String, String>> mDeviceList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.timesetlistview);

		mListView = (SwipeMenuListView) findViewById(R.id.listView_devices);
		getdata();
		mAdapter = new SimpleAdapter(this, getdata(), R.layout.timesetlistitem, new String[]{"device_name","message"}, new int[]{R.id.device_name,R.id.device_mesge});
		mListView.setAdapter(mAdapter);


		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				open(arg2);
				
			}
		});

	}
//	protected void setdata(String text ,int id) {
//		Alarm alarm = new Alarm(TimesetActivity.this);
//		alarm.label = text;
//		alarm.id = id;
//		Alarms.setAlarm(TimesetActivity.this, alarm);
//		Toast.makeText(TimesetActivity.this,"修改成功！", Toast.LENGTH_SHORT).show();
//		
//	}
	protected void open(int index) {
		Intent intent = new Intent(TimesetActivity.this,DeskClockMainActivity.class);
		intent.putExtra("device", mDeviceList.get(index).get("device_name"));
		System.out.println("open socket"+ (index+1));
		intent.putExtra("socket", index+1);
		
		startActivity( intent);
	}
	private  List<HashMap<String, String>> getdata() {
		mDeviceList.clear();
        mCursor = Alarms.getAlarmsCursorOrderByID(TimesetActivity.this.getContentResolver());

		while (mCursor.moveToNext()) {
			String nameString = mCursor.getString(mCursor.getColumnIndex("message"));

			if (nameString==null) {
				continue;
			}
			boolean flag = false;
			for (int i = 0; i < mDeviceList.size(); i++) {
				
				if (mDeviceList.get(i).get("device_name").equals(nameString)) {
					flag =true;
				}
			}
			if (!flag) {
				Cursor device_cursor ; 
				device_cursor = Alarms.getAlarmsCursorByname(getContentResolver(),nameString);
				StringBuilder builder = new StringBuilder();
				while (device_cursor.moveToNext()) {
					
					Alarm alarm = new Alarm(device_cursor);
					if (alarm.enabled) {
						final Calendar c = Calendar.getInstance();
						c.set(Calendar.HOUR_OF_DAY, alarm.hour);
						c.set(Calendar.MINUTE,  alarm.minutes);
						
						String actionString =	alarm.flag?getString(R.string.action_on):getString(R.string.action_off);
						builder.append(c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+" "+actionString+" "+alarm.daysOfWeek.toString(TimesetActivity.this, false)+"\n");
						
					}
					
					
				}
				device_cursor.close();
				System.out.println(builder.toString());
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("device_name", nameString);
				map.put("message", builder.toString());
				
				mDeviceList.add(map);
				
			}
		}
		return mDeviceList;
		
	}
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == Menu.FIRST) {
			Intent intent = new Intent(TimesetActivity.this, EditDeviceActivity.class);
			intent.putExtra("num", 5);
			startActivity(intent);
		}
		if (item.getItemId() == Menu.FIRST+1) {
			opendialog();
		}
		return true;
	}
	private void opendialog() {
		LayoutInflater inflater = LayoutInflater.from(TimesetActivity.this);
		View view = inflater.inflate(R.layout.listitem_edit_device,null);
		Button button = (Button)view.findViewById(R.id.bn_update_name);
		final EditText editText = (EditText)view.findViewById(R.id.ed_name);
		editText.setHint("输入工作日或周末");
		
		final AlertDialog dialog  = new AlertDialog.Builder(TimesetActivity.this).
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
				ToastMaster.showtoast(TimesetActivity.this, "保存成功");
				dialog.dismiss();
				
				
			}
		});
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,Menu.FIRST,0,"重命名插孔");
		menu.add(0,Menu.FIRST+1,0,"保存为情景模式");
		
		return true;
	}
	@Override
	protected void onResume() {
		
		super.onResume();
		getdata();
		mAdapter = new SimpleAdapter(this, getdata(), R.layout.timesetlistitem, new String[]{"device_name","message"}, new int[]{R.id.device_name,R.id.device_mesge});
		mListView.setAdapter(mAdapter);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCursor.close();
	}
	
}