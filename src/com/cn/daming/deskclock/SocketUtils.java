package com.cn.daming.deskclock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class SocketUtils {
	public  String host = "100.84.44.77";  //要连接的服务端IP地址
	public  int port = 8899;   //要连接的服务端对应的监听端口
	public static final String GETACTION = "getaction";  
	public static final String SETACTION = "setaction";  
	public static final String GETSTATE = "getstate";  
	public static final String SETSTATE = "upload";  
	
	public SocketUtils() {
		host = PreferencesUtils.getString("server", host);
		System.out.println("host "+host);
	}
	public  String clientget(final String key,final String value)  {
		
		 SocketThread socketThread = new SocketThread(key, value);
		socketThread.start();
		try {
			socketThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return socketThread.getResult() ;
	  
		
	}


	private  String get(String key, String value) {
		Socket client;
		try {
			client = new Socket(host, port);
			 Writer writer = new OutputStreamWriter(client.getOutputStream());
			    writer.write(key+"="+value);
			    writer.write("eof\n");  
			    writer.flush();
			   BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			   client.setSoTimeout(30*1000);
			    StringBuffer sb = new StringBuffer();
			    String temp;
			    int index;
			    try {
			       while ((temp=br.readLine()) != null) {
			      	 if ((index = temp.indexOf("eof")) != -1) {  
			               sb.append(temp.substring(0, index));  
			               break;  
			           }  
			          sb.append(temp);
			       }
			    } catch (SocketTimeoutException e) {
			       System.out.println("数据读取超时。");
			    }
			    writer.close();
			    br.close();
			    client.close();
			    return sb.toString();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}
	class SocketThread extends Thread
	{
		String result = null ; 
		String key = "" ; 
		String value = "" ; 
		
		public SocketThread(String key,String value) {
			this.key =key ; 
			this.value =value;
		}
		@Override
		public void run() {
			this.result = get(key, value);
		}
		public String getResult() {
			return result;
		}
	}
}
