package com.jayin.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cn.daming.deskclock.Alarm;
import com.cn.daming.deskclock.Alarms;
import com.cn.daming.deskclock.R;

public class EditDeviceActivity extends Activity {
	private SwipeMenuListView mListView;
//	private SimpleAdapter mAdapter ; 
	private static List< String> mDeviceList = new ArrayList<String>();
    EditTextAdater mAdapter;
    private static Cursor mCursor;

    public static int num;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_device);
        mCursor = Alarms.getAlarmsCursorOrderByID(EditDeviceActivity.this.getContentResolver());

		mListView = (SwipeMenuListView)findViewById(R.id.listView_edit_devices);
		 mAdapter = new EditTextAdater(getdata(), EditDeviceActivity.this);
		mListView.setAdapter(mAdapter);
	
	}
	protected void setdata(String text ,int id) {
		Alarm alarm = new Alarm(EditDeviceActivity.this,id);
		alarm.label = text;
		alarm.id = id;
		Alarms.setAlarm(EditDeviceActivity.this, alarm);
		Toast.makeText(EditDeviceActivity.this,"修改成功！", Toast.LENGTH_SHORT).show();
		
	}
	class EditTextAdater extends BaseAdapter{

		List<String> listdata ;
		Context context ; 
		private LayoutInflater inflater;
		public EditTextAdater(List<String> list ,Context context) {
			this.listdata = list;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return listdata.size();
		}

		@Override
		public Object getItem(int position) {
			return listdata.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null ) {
				String text = listdata.get(position);
				convertView = inflater.inflate(R.layout.listitem_edit_device, null);
				 final EditText editText = (EditText)convertView.findViewById(R.id.ed_name);
				 Button button = (Button)convertView.findViewById(R.id.bn_update_name);
				 button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						setdata(editText.getText().toString(), position+1);
						
					}
				});
				 editText.setText(text);
			
				
			}
			return convertView;
		}
		
	}
	private  List<String> getdata() {
		mDeviceList.clear();
        mCursor = Alarms.getAlarmsCursorOrderByID(EditDeviceActivity.this.getContentResolver());

		while (mCursor.moveToNext()) {
			String nameString = mCursor.getString(mCursor.getColumnIndex("message"));

			if (nameString==null) {
				continue;
			}
			boolean flag = false;
			for (int i = 0; i < mDeviceList.size(); i++) {
				
				if (mDeviceList.get(i).equals(nameString)) {
					flag =true;
				}
			}
			if (!flag) {
				mDeviceList.add(nameString);
				
			}
		}
		return mDeviceList;
		
	}
}
