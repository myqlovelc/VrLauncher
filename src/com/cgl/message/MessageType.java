package com.cgl.message;

public class MessageType {
	public static int Alive = 0;
	public static int CMD_Play = 16;
    public static int CMD_ChangeVideo = 17;
	public static int CMD_Stop = 18;
	public static int CMD_Pause = 19;
	public static int CMD_Resume = 20;
	public static int CMD_Register = 21;
	public static int CMD_Seek = 22;
	public static int CMD_KeepAlive = 23;
	public static int ACK_Register_OK = 32;
    public static int ACK_Play_OK = 33;
	public static int ACK_Stop_OK = 34;
    public static int ACK_Pause_OK = 35;
    public static int ACK_Resume_OK = 36;
    public static int ACK_Seek_OK = 37;
    public static int ACK_Launch_OK = 38;
    public static int ACK_Shutdown_OK = 39;
    public static int ACK_PingService_OK = 40;
	public static int STATUS_Home = 64;
	public static int STATUS_Playing = 65;
	public static int STATUS_VideoInfo = 66;
	public static int STATUS_LoadingVideo = 67;
	public static int STATUS_Paused = 68;
    public static int STATUS_Headset = 69;
    public static int REQUEST_GetNodeInfo = 128;
	public static int RESPONSE_GetNodeInfo = 129;
    public static int REQUEST_GetNodeInfoFromService = 130;
    public static int RESPONSE_GetNodeInfoFromService = 131;
	public static int REQUEST_LaunchPlayerFromService = 132;
	public static int REQUEST_ShutdownPlayer = 133;
    public static int REQUEST_PingService = 134;
	public static int Invalid = 0xFFFF;

}
