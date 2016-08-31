package com.cgl.vrlauncher;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import com.cgl.message.*;
import com.cgl.utils.*;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class Service1 extends Service {
	
	private String LAUNCH_FEEDBACK_ACTION = "vr.launch";
	private String SHUTDOWN_FEEDBACK_ACTION = "vr.shutdown";
	
	private DatagramSocket serverSocket = null;
	
	private PackageManager packageManager = null;
	private ActivityManager activityManager = null;
	
	private final String MY_ACTION = "android.com.example.broadcastreceiver.action.MYACTION";
	
	private static String app_name = "com.cgl.VrPlayer";
	private static String activity_name = "oculus.MainActivity";
	
	//private static String app_name = "com.yourcompany.vrtemplate";
	//private static String activity_name = "oculus.MainActivity";
	
	private static InetAddress launch_addr = null;
	private static long launch_head_id = 0;
	private static int launch_controller_port = 0;
	
	private static InetAddress shutdown_addr = null;
	private static long shutdown_head_id = 0;
	private static int shutdown_controller_port = 0;

	private String TAG = getClass().getName();
	//private String ServiceName = "com.service.demo.Service2";
	private String Process_Name = "com.cgl.vrlauncher:service2";
	
	private static final int NOTIFY_FAKEPLAYER_ID = 1339;
	
	public static int launch_flag = 0;
	public static int shutdown_flag = 0;
	
	private static String fileDirectory = "";
	private static String configFileName = "config.xml";
	private static File configFile = null;
	
	private static int communicationMode = 1;
	
	public static int getCommunicationMode() {
		return communicationMode;
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
			
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals(LAUNCH_FEEDBACK_ACTION)) {
	
				String result = arg1.getStringExtra("result");
				
				if (result.equals("success")) {
					
					Service1.launch_flag = 1;
				}
				else if (result.equals("fail")) {
					//Toast.makeText(arg0, "启动VrPlayer失败", Toast.LENGTH_SHORT).show();
				}
			}
			
			else if (arg1.getAction().equals(SHUTDOWN_FEEDBACK_ACTION)) {
				String result = arg1.getStringExtra("result");
				
				if (result.equals("success")) {
					//Toast.makeText(arg0, "关闭VrPlayer成功", Toast.LENGTH_SHORT).show();
					Service1.shutdown_flag = 1;
				}
				else if (result.equals("fail")) {
					//Toast.makeText(arg0, "关闭VrPlayer失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private Service_1 service_1 = new Service_1.Stub() {

		@Override
		public void stopService() throws RemoteException {
			Intent i = new Intent(Service1.this, Service2.class);
			Service1.this.stopService(i);
		}

		@Override
		public void startService() throws RemoteException {
			Intent i = new Intent(Service1.this, Service2.class);
			Service1.this.startService(i);
		}
	};

	public void onCreate() {
		Log.v("Tag", "服务1已创建");
		
		packageManager = this.getPackageManager();  
		activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		
		fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + "/";
        Log.v(TAG, "myqTmac " + fileDirectory);
        
		configFile = new File(fileDirectory + configFileName);
        if (configFile.exists() && configFile.isFile()) {

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = null;
                builder = factory.newDocumentBuilder();
                Document document = builder.parse(configFile);
                NodeList communication_nodes = document.getElementsByTagName("CommunicationMode");
                String commModeNum = communication_nodes.item(0).getTextContent();
                communicationMode = Integer.parseInt(commModeNum);
                /*NodeList validate_nodes = document.getElementsByTagName("ValidateDevice");
                String validateNum = validate_nodes.item(0).getTextContent();
                validateDevice = Integer.parseInt(validateNum);*/
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
		new Thread() {
			public void run() {
				while (true) {
					//boolean isRun = isServiceRunning(Service1.this, ServiceName);
					boolean isRun = isProessRunning(Service1.this, Process_Name);
					if (isRun==false) {
						try {
							Log.i(TAG, "myqTmac: "+service_1);
							//Toast.makeText(Service1.this, "Service1开启", Toast.LENGTH_SHORT).show();
							service_1.startService();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
		//Toast.makeText(this, "Service1开启", Toast.LENGTH_SHORT).show();
		new Thread(new UDPTerminal()).start();
		//new Thread(new UDPResponseSender()).start();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("Tag", "服务1已启动");
		
        IntentFilter filter = new IntentFilter();
        filter.addAction(LAUNCH_FEEDBACK_ACTION);
        filter.addAction(SHUTDOWN_FEEDBACK_ACTION);
        registerReceiver(receiver, filter);
		
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
		Notification notification = new Notification.Builder(this) 
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentTitle("VrLauncher") 
	        .setContentText("Service is currently running")
	        .setContentIntent(pendingIntent) 
	        .build();  
		
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		startForeground(NOTIFY_FAKEPLAYER_ID, notification);
		
		flags = START_STICKY;
		
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public IBinder onBind(Intent intent) {
		return (IBinder) service_1;
	}
	
	/*@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
	}*/

	public static boolean isProessRunning(Context context, String proessName) {

		boolean isRunning = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> lists = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : lists) {
			if (info.processName.equals(proessName)) {
				//Log.i("Service1", "" + info.processName);
				isRunning = true;
			}
		}

		return isRunning;
	}
	
	class UDPResponseSender implements Runnable {
		
		public void run() {
			Log.v("TAG", "myqTmac UDP response thread started");
			while (true) {
				if (serverSocket != null && launch_flag == 1 && launch_addr != null) {
					byte[] sendBuf = new byte[Strings.BUFFER_LENGTH];
					MsgLaunchRequestResponse response = new MsgLaunchRequestResponse((long)1, (int)0, 
							launch_head_id, MsgLaunchRequestResponse.OK);
					
					int length = response.serialize(sendBuf);
					
					MessageSender.sendMessage(serverSocket, sendBuf, length, 
							launch_addr, launch_controller_port);
					launch_flag = 0;
					launch_addr = null;
				}
				if (serverSocket != null && shutdown_flag == 1 && shutdown_addr != null) {
					byte[] sendBuf = new byte[Strings.BUFFER_LENGTH];
					MsgShutdownRequestResponse response = new MsgShutdownRequestResponse((long)1, (int)0, 
							shutdown_head_id, MsgShutdownRequestResponse.OK);
					
					int length = response.serialize(sendBuf);
					
					MessageSender.sendMessage(serverSocket, sendBuf, length, 
							shutdown_addr, shutdown_controller_port);
					shutdown_flag = 0;
					shutdown_addr = null;
				}
				
			}
		}
	}
	
	class UDPTerminal implements Runnable {
		
		public void run() {
			
			//Thread current = Thread.currentThread();
			//System.out.println(current.getId());

			try {
				Log.v("TAG", "myqTmac UDP thread started");
				serverSocket = new DatagramSocket(Strings.SERVICE_PORT);
				//InetAddress group =InetAddress.getByName(Strings.MULTICAST_HOST);
				//serverSocket.joinGroup(group);
				
				byte[] recvBuf = new byte[Strings.BUFFER_LENGTH];
		        DatagramPacket recvPacket = new DatagramPacket(recvBuf , recvBuf.length);
	            
				while (true) {
					
			        serverSocket.receive(recvPacket);
					//String recvStr = new String(recvPacket.getData() , 0 , recvPacket.getLength());
					
					Log.v("TAG", "myqTmac");
					
					try {
						byte[] data = MessageSender.receiveMessage(recvPacket);
				        
				        if (data == null) {
				        	continue;
				        }
				        
				        InetAddress addr = recvPacket.getAddress();
						
						MessageHead head = com.cgl.message.Message.parseHead(data, data.length);
						
						if (head.type == MessageType.REQUEST_PingService) {
							Log.v("TAG", "Ping Service");
							MsgRequestPingService msg = new MsgRequestPingService();
							int len = msg.deserialize(data, data.length);
							
							byte[] sendBuf = new byte[Strings.BUFFER_LENGTH];
							PingServiceRequestResponse response = new PingServiceRequestResponse((long)1, (int)0, 
									head.id, PingServiceRequestResponse.OK);
							
							int length = response.serialize(sendBuf);
							
							Log.v("TAG", "Ping Service: " + addr.getHostAddress() + " " + msg.controller_port);
							
							MessageSender.sendMessage(serverSocket, sendBuf, length, 
									addr, msg.controller_port);
						}
						
						else {
//							List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses(); 
//							boolean isAppRunning = false;
//						    if (appProcesses == null) 
//						    	isAppRunning = false; 
//						  
//						    for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) { 
//						    	// The name of the process that this object is associated with. 
//						    	Log.v("TAG", "App name: " + appProcess.processName);
//						    	if (appProcess.processName.equals(app_name) 
//						    			&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) { 
//						    		isAppRunning = true; 
//						    	} 
//						    } 
							
							List<RunningTaskInfo> list = activityManager.getRunningTasks(100);
							
							boolean isAppRunning = false;
						    if (list == null) 
						    	isAppRunning = false; 
							
						    for (RunningTaskInfo info : list) {
						    	Log.v("TAG", "App top name: " + info.topActivity.getPackageName());
						    	Log.v("TAG", "App base name: " + info.baseActivity.getPackageName());
						        if (info.topActivity.getPackageName().equals(app_name) 
						        		&& info.baseActivity.getPackageName().equals(app_name)) {
						            isAppRunning = true;
						            //find it, break
						            break;
						        }
						    }
						    
							Log.v("TAG", "程序是否运行：" + isAppRunning);
							
							if (head.type == MessageType.REQUEST_LaunchPlayerFromService) {
								Log.v("TAG", "运行程序");
								
								Service1.launch_flag = 0;
								
								MsgRequestLaunchPlayerFromService msg = new MsgRequestLaunchPlayerFromService();
								int len = msg.deserialize(data, data.length);
								
								/*List<RunningTaskInfo> list = activityManager.getRunningTasks(100);
								boolean isAppRunning = false;
								for (RunningTaskInfo info : list) {
									Log.v("TAG", "当前程序：" + info.topActivity.getPackageName());
									if (info.topActivity.getPackageName().equals(app_name) 
											|| info.baseActivity.getPackageName().equals(app_name)) {
										isAppRunning = true;
										break;
									}
								}*/
									
								Intent mIntent = new Intent();         
								ComponentName comp = new ComponentName(app_name, activity_name); 
								
								if (isAppRunning) {
									Log.v("TAG", "程序正在运行");
									byte[] sendBuf = new byte[Strings.BUFFER_LENGTH];
									MsgLaunchRequestResponse response = new MsgLaunchRequestResponse((long)1, (int)0, 
											head.id, MsgLaunchRequestResponse.OK);
									
									int length = response.serialize(sendBuf);
									
									MessageSender.sendMessage(serverSocket, sendBuf, length, 
											addr, msg.controller_port);
								}
								else {
									try {
										Drawable icon = packageManager.getActivityIcon(comp);
										
										if (icon != null) {
											mIntent.setComponent(comp); 
											mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											startActivity(mIntent);
										}
										
									} catch (NameNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
							        launch_addr = recvPacket.getAddress();
									launch_head_id = head.id;
									launch_controller_port = msg.controller_port;
								}
								
								byte[] sendBuf = new byte[Strings.BUFFER_LENGTH];
								MsgLaunchRequestResponse response = new MsgLaunchRequestResponse((long)1, (int)0, 
										head.id, MsgLaunchRequestResponse.OK);
								
								int length = response.serialize(sendBuf);
								
								MessageSender.sendMessage(serverSocket, sendBuf, length, 
										launch_addr, msg.controller_port);
							}
							
							else if (head.type == MessageType.REQUEST_ShutdownPlayer) {
								Log.v("TAG", "关闭程序");
								
								Service1.shutdown_flag = 0;
								
								MsgRequestShutdownPlayer msg = new MsgRequestShutdownPlayer();
								int len = msg.deserialize(data, data.length);
								
//								if (!isAppRunning) {
//									byte[] sendBuf = new byte[Strings.BUFFER_LENGTH];
//									MsgShutdownRequestResponse response = new MsgShutdownRequestResponse((long)1, (int)0, 
//											head.id, MsgShutdownRequestResponse.OK);
//									
//									int length = response.serialize(sendBuf);
//									
//									MessageSender.sendMessage(serverSocket, sendBuf, length, 
//											addr, msg.controller_port);
//								}
								
								//if (isAppRunning) {
									
									Intent intent = new Intent();
							        
							        /*  设置Intent对象的action属性  */
							        intent.setAction(MY_ACTION);
							        
							        /* 为Intent对象添加附加信息 */
							        intent.putExtra("msg", "shut_down");
							        
							        /* 发布广播 */
							        sendBroadcast(intent);
									
									shutdown_addr = recvPacket.getAddress();
									shutdown_head_id = head.id;
									shutdown_controller_port = msg.controller_port;
								//}
						        
						        byte[] sendBuf = new byte[Strings.BUFFER_LENGTH];
						        MsgShutdownRequestResponse response = new MsgShutdownRequestResponse((long)1, (int)0, 
						        		head.id, MsgShutdownRequestResponse.OK);
								
								int length = response.serialize(sendBuf);
								
								MessageSender.sendMessage(serverSocket, sendBuf, length, 
										addr, msg.controller_port);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
			        
				}
		        
			}catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
