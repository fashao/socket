package com.cn.daming.deskclock;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.view.WindowManager;

public class Police extends Service {
	private Timer mTimer;
	private static final int SHOWDIALOG = 0x3;
	private static final int RING=0x4;
	protected static final int STOP = 0x5;
	Handler mHandler  = new Handler(){
		MediaPlayer mp;
		public void handleMessage(android.os.Message msg) {
			
			if (msg.what ==SHOWDIALOG ) {
				opendialog(Police.this);
			}
			if (msg.what == RING) {
				mp = new MediaPlayer();
			
				try {
					mp.reset();
					mp.setDataSource(Police.this,
							RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
					mp.prepare();
					mp.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			if (msg.what == STOP) {
				mp.stop();
			}
		};
	}; 
	static  boolean istart = false;
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("Police service oncreat");
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Police service onStartCommand");
		
		if (istart) {
		

			System.out.println("Police service is already start ");
	
		}else {
			istart = true;
			startTimer();
		}
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		istart =false;
	}
	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
			CheckTask locktask = new CheckTask();
			mTimer.schedule(locktask, 0L, 60*1000L);
		}

	}
	class CheckTask extends TimerTask{

		@Override
		public void run() {
			SocketUtils socketUtils = new SocketUtils() ; 
			String result =  socketUtils.clientget("check", PreferencesUtils.getString(PreferencesUtils.PHONE, "null"));
			if (result.equals("0")) {
			System.out.println("Police run!!!!!!!!!");	
			mHandler.sendEmptyMessage(SHOWDIALOG);
			mHandler.sendEmptyMessage(RING);
			}else {
				System.out.println("police check ok");
			}
		}
		
	}
	private  void opendialog(Context context) {
		
		AlertDialog.Builder  builder =  new AlertDialog.Builder(context) .setTitle("重要提示")
				
                .setMessage("服务器已经和插座失去联系！！！")
                .setCancelable(false)
                .setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

						mHandler.sendEmptyMessage(STOP);
					}
				}); 
		final AlertDialog alertDialog = builder.create();
		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		alertDialog.show();
		                
		
	}


}
