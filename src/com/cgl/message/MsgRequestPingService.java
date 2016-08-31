package com.cgl.message;

import com.cgl.utils.Strings;

/**
 * Created by dell on 2016/1/23.
 */
public class MsgRequestPingService extends Message {

    public int controller_port;

    public MsgRequestPingService()
    {
        head = new MessageHead();
        head.type = MessageType.REQUEST_PingService;
        head.id = 0;
        head.nodeID = 0;
    }

    public MsgRequestPingService(long id_, int nodeId_, int _controller_port)
    {
        head = new MessageHead();
        head.id = id_;
        head.type = MessageType.REQUEST_PingService;
        head.nodeID = nodeId_;
        controller_port = _controller_port;
    }

    public int serialize(byte[] data_)
    {
        int idx = super.serialize(data_);
        insertInt(controller_port, data, size, idx);
        idx += Strings.INT_LENGTH;
        return idx;
    }

    public int deserialize(byte[] data_, int size_)
    {
        int idx = super.deserialize(data_, size_);
        controller_port = getInt(data, size, idx);
        idx += Strings.INT_LENGTH;
        return idx;
    }

}
