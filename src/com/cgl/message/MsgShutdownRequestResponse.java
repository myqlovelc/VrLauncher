package com.cgl.message;

import com.cgl.utils.Strings;

public class MsgShutdownRequestResponse extends Message {
	public long requestId = 0;
	public int status = 0;
	
	public static int OK = 0;
	
	public MsgShutdownRequestResponse()
    {
        head = new MessageHead();
        head.type = MessageType.ACK_Shutdown_OK;
        head.id = 0;
        head.nodeID = 0;
    }
	
    public MsgShutdownRequestResponse(long _id, int _nodeID, long _requestId, int _status)
    {
        head = new MessageHead();
        head.type = MessageType.ACK_Shutdown_OK;
        head.id = _id;
        head.nodeID = _nodeID;
        requestId = _requestId;
        status = _status;
    }
    
    public int serialize(byte[] _data)
    {
        int idx = super.serialize(_data);
        insertLong(requestId, data, size, idx);
        idx += Strings.LONG_LENGTH;
        insertInt(status, data, size, idx);
        idx += Strings.INT_LENGTH;
        return idx;
    }

    public int deserialize(byte[] _data, int _size)
    {
        int idx = super.deserialize(_data, _size);
        requestId = getLong(data, size, idx);
        idx += Strings.LONG_LENGTH;
        status = getInt(data, size, idx);
        idx += Strings.INT_LENGTH;
        return idx;
    }
}
