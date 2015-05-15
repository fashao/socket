package com.cn.daming.deskclock;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ProgressDialogUtils {
static    private ProgressDialog pd;
static private Handler handler ;
private static final int MSG_DISMISS=0;
static{
	handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == MSG_DISMISS) {
				System.out.println("get msg。。。");
				pd.dismiss();
			}
		}
	};
}
    public static void showProgressmessge(Context context ,String title , String mesg) {
		
    	pd = ProgressDialog
    			
				.show(context, title, mesg);
    	pd.setCancelable(true);
    }
    public static void dismiss() {
    	handler.sendEmptyMessage(MSG_DISMISS);
    }
}
