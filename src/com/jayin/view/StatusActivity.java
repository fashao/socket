package com.jayin.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cn.daming.deskclock.Alarm;
import com.cn.daming.deskclock.AlarmKlaxon;
import com.cn.daming.deskclock.Alarms;
import com.cn.daming.deskclock.PreferencesUtils;
import com.cn.daming.deskclock.ProgressDialogUtils;
import com.cn.daming.deskclock.R;
import com.cn.daming.deskclock.SocketUtils;
import com.cn.daming.deskclock.ToastMaster;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class StatusActivity extends Activity {
	private SwipeMenuListView mListView;
	private static Cursor mCursor;
	private static List<HashMap<String, String>> mDeviceList = new ArrayList<HashMap<String, String>>();
	private static HashMap<Integer, Integer> statusmap = new HashMap<Integer, Integer>();
	private static SimpleAdapter mAdapter;
    private static final int showToastOK = 20;
    private static final int showToastFail= 21;
    private static final int setadapter= 22;
    private static final String RESULT= "result";
    
    
    private  final Handler mHandler_toast = new Handler(){
    	
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case showToastOK:
				ToastMaster.showtoast(StatusActivity.this,StatusActivity.this.getString(R.string.sendOrderok));
				ProgressDialogUtils.dismiss();

				break;
			case showToastFail:
				ToastMaster.showtoast(StatusActivity.this,StatusActivity.this.getString(R.string.network_failed));
				ProgressDialogUtils.dismiss();


				break;
			case setadapter :
				setadapter(msg.getData().getString(RESULT));
				break;

			default:
				break;
			}
    	};
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		
		mListView = (SwipeMenuListView) findViewById(R.id.listView_devices_status);
		
		updatestate();
	}

	private List<HashMap<String, String>> getdata() {
		mDeviceList.clear();
		mCursor = Alarms.getAlarmsCursor(getContentResolver());
		while (mCursor.moveToNext()) {
			String nameString = mCursor.getString(mCursor
					.getColumnIndex("message"));
			if (nameString == null) {
				continue;
			}
			boolean flag = false;
			for (int i = 0; i < mDeviceList.size(); i++) {

				if (mDeviceList.get(i).get("device_name") != null
						&& mDeviceList.get(i).get("device_name")
								.equals(nameString)) {
					flag = true;
				}
			}
			int socket = mCursor.getInt(Alarm.Columns.ALARM_SOCKET_INDEX);
			if (!flag) {
				System.out.println("data " + nameString);
				Map<String, String> map = new HashMap<String, String>();
				map.put("device_name", nameString);
				if (statusmap.get(socket)!=null) {
					
				String statu = ( statusmap.get(socket) == 1?getString(R.string.device_status_on):getString(R.string.device_status_off));
				String statu_toggole = ( statusmap.get(socket) == 1?getString(R.string.device_status_on):getString(R.string.device_status_off));
				
				map.put("device_status_now", "当前状态："+statu);
				map.put("device_status_control",statu_toggole);
				mDeviceList.add((HashMap<String, String>) map);
				}else {
					System.out.println("erro statusmap.get(socket)==null"+socket);
				}

			}
		}

		return mDeviceList;
	}

	@SuppressWarnings("static-access")
	private void updatestate() {
		ProgressDialogUtils.showProgressmessge(StatusActivity.this, "提示",
				getString(R.string.isupdatestatus));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SocketUtils socketUtils = new SocketUtils();
				String result =  socketUtils.clientget(SocketUtils.GETSTATE, PreferencesUtils.getString(PreferencesUtils.PHONE, "null"));
				Message msg = new Message();
				Bundle bundle = new Bundle() ; 
				bundle.putString(RESULT, result);
				msg.what = setadapter;
				msg.setData(bundle);
				mHandler_toast.sendMessage(msg);
			}
		}).start();
		//		HttpUtils httpUtils = new HttpUtils();
//		httpUtils.sHttpCache.clear();
//		httpUtils.send(HttpMethod.GET, PreferencesUtils.BASEURL
//				+ "state?getstate=all", new RequestCallBack<String>() {
//
//			@Override
//			public void onSuccess(ResponseInfo<String> responseInfo) {
//				String result = responseInfo.result;
//				statusmap.clear();
//				System.out.println("get :" + result);
//				String[] items = result.split(",");
//				for (int i = 0; i < items.length; i++) {
//					String[] item = items[i].split(":");
//					if (item.length == 2) {
//						statusmap.put(Integer.valueOf(item[0]), Integer.valueOf(item[1]));
//					}
//				}
//				mAdapter = new SimpleAdapter(StatusActivity.this, getdata(),
//						R.layout.listitem_status, new String[] { "device_name",
//								"device_status_now", "device_status_control" },
//						new int[] { R.id.device_names, R.id.device_status_now,
//								R.id.status_toggleButton }){
//					@Override
//					public View getView(final int position,
//							View convertView, ViewGroup parent) {
//	                        final View view=super.getView(position, convertView, parent);
//	                    	 final ToggleButton toggleButton =(ToggleButton)view.findViewById(R.id.status_toggleButton);
//	                    	 toggleButton.setChecked(statusmap.get(position+1) == 1);
//	                    	 toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//								
//								@Override
//								public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//									controlSocket(StatusActivity.this,position+1, isChecked);
//									
//								}
//							});
//	                    	 return view;
//					}
//				};
//				mListView.setAdapter(mAdapter);
//				
//				ProgressDialogUtils.dismiss();
//			}
//
//			@Override
//			public void onFailure(HttpException error, String msg) {
//				ToastMaster.showtoast(StatusActivity.this, getString(R.string.network_failed));
//			}
//		});
	}

	public void setadapter(String result) {
		if (result!=null&&result.contains(":")) {
			statusmap.clear();
			System.out.println("get :" + result);
			String[] items = result.split(",");
			for (int i = 0; i < items.length; i++) {
				String[] item = items[i].split(":");
				if (item.length == 2) {
					statusmap.put(Integer.valueOf(item[0]), Integer.valueOf(item[1]));
				}
			}
			mAdapter = new SimpleAdapter(StatusActivity.this, getdata(),
					R.layout.listitem_status, new String[] { "device_name",
							"device_status_now", "device_status_control" },
					new int[] { R.id.device_names, R.id.device_status_now,
							R.id.status_toggleButton }){
				@Override
				public View getView(final int position,
						View convertView, ViewGroup parent) {
                        final View view=super.getView(position, convertView, parent);
                    	 final ToggleButton toggleButton =(ToggleButton)view.findViewById(R.id.status_toggleButton);
                    	 toggleButton.setChecked(statusmap.get(position+1) == 1);
                    	 toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								
								controlSocket(StatusActivity.this,position+1, isChecked);
								
							}
						});
                    	 return view;
				}
			};
			mListView.setAdapter(mAdapter);
			
			ProgressDialogUtils.dismiss();
		}else {
			ToastMaster.showtoast(StatusActivity.this, getString(R.string.network_failed));

		}
	}
	public   void controlSocket(final Context context, final int socket,final boolean ischeck) {
//		HttpUtils httpUtils = new HttpUtils();
//		httpUtils.send(HttpMethod.GET, PreferencesUtils.BASEURL
//				+ "action?setaction="+socket+":"+(ischeck?1:0), new RequestCallBack<String>() {
//
//					@Override
//					public void onSuccess(ResponseInfo<String> responseInfo) {
//						ProgressDialogUtils.dismiss();
//
//						ToastMaster.showtoast(context,context.getString(R.string.sendOrderok));
//					}
//
//					@Override
//					public void onFailure(HttpException error, String msg) {
//						ToastMaster.showtoast(context,context.getString(R.string.network_failed));
//									
//					}
//			
//				});
		
		ProgressDialogUtils.showProgressmessge(context, "提示",
				context.getString(R.string.ispostdata));
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SocketUtils socketUtils = new SocketUtils();
				String phone = PreferencesUtils.getString(PreferencesUtils.PHONE, "null") ;
				
				String result	 = socketUtils.clientget(SocketUtils.SETACTION, phone+":"+(socket/6)+","+socket+":"+(ischeck?1:0));
				if (result!=null&&result.equals("ok")) {

					mHandler_toast.sendEmptyMessage(showToastOK);

				}else {
					mHandler_toast.sendEmptyMessage(showToastFail);

				}				
			}
		}).start();

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,Menu.FIRST,0,getString(R.string.menu_updatestatus));
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == Menu.FIRST) {
		updatestate();
		}
		return true;
	}
}
