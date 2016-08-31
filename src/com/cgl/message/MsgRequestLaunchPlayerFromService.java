package com.cgl.message;

import java.io.UnsupportedEncodingException;

import com.cgl.utils.Strings;

public class MsgRequestLaunchPlayerFromService extends Message{
	
	public String EXEPath = "";
	public int controller_port = 0;
	
	public MsgRequestLaunchPlayerFromService()
    {
        head = new MessageHead();
        head.id = 0;
        head.type = MessageType.REQUEST_LaunchPlayerFromService;
        head.nodeID = 0;
    }
    
    public MsgRequestLaunchPlayerFromService(long id_, int nodeId_, String _EXEPath, int _contoller_port)
    {
        head = new MessageHead();
        head.id = id_;
        head.type = MessageType.REQUEST_LaunchPlayerFromService;
        head.nodeID = nodeId_;
        EXEPath = _EXEPath;
        controller_port = _contoller_port;
    }

    public int serialize(byte[] data_)
    {
        int idx = super.serialize(data_);
        try {
            insertString(EXEPath, data, size, idx); //ignore
			idx += Strings.INT_LENGTH + EXEPath.getBytes("UnicodeLittleUnmarked").length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        insertInt(controller_port, data, size, idx);
        idx += Strings.INT_LENGTH;
        return idx;
    }

    public int deserialize(byte[] data_, int size_)
    {
        int idx = super.deserialize(data_, size_);
        try {
            EXEPath = getString(data, size, idx);
			idx += Strings.INT_LENGTH + EXEPath.getBytes("UnicodeLittleUnmarked").length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        controller_port = getInt(data, size, idx);
        idx += Strings.INT_LENGTH;
        return idx;
    }

    public String toString()
    {
        return String.format("<head:{%s}, exePath:{%s}>", head, EXEPath);
    } 
}
