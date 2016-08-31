package com.cgl.message;

import com.cgl.utils.Strings;

/**
 * Created by dell on 2016/1/23.
 */
public class PingServiceRequestResponse extends Message {

    public static int OK = 0;

    public long requestId = 0;
    public int status = PingServiceRequestResponse.OK;

    public PingServiceRequestResponse(long id_, int nodeId_)
    {
        head = new MessageHead();
        head.id = id_;
        head.type = MessageType.ACK_PingService_OK;
        head.nodeID = nodeId_;
    }

    public PingServiceRequestResponse(long _id, int _nodeID, long _requestId, int _status)
    {
        head = new MessageHead();
        head.type = MessageType.ACK_PingService_OK;
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
