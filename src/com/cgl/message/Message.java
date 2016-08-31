package com.cgl.message;

import java.io.UnsupportedEncodingException;

import com.cgl.utils.*;

public class Message
{
    public byte[] data;
    public int size;
    public MessageHead head;

    protected static int getInt(byte[] _data, int _size, int startIdx)
    {
        if (_size < (startIdx + Strings.INT_LENGTH)) {
        	throw new java.lang.ArrayIndexOutOfBoundsException();
        }

        int value = 0;

        int shift = 0;
        for (int i = 0; i < Strings.INT_LENGTH; i++)
        {
            value |= (((int)_data[startIdx + i] & 0xFF) << shift);
            shift += 8;
        }
        return value;
    }

    protected static void insertInt(int value, byte[] _data, int _size, int startIdx)
    {
    	if (_size < (startIdx + Strings.INT_LENGTH)) {
        	throw new java.lang.ArrayIndexOutOfBoundsException();
        }

        for (int i = 0; i < Strings.INT_LENGTH; i++)
        {
            _data[startIdx + i] = (byte)value;
            value >>= 8;
        }
    }

    protected static long getLong(byte[] _data, int _size, int startIdx)
    {
    	if (_size < (startIdx + Strings.LONG_LENGTH)) {
        	throw new java.lang.ArrayIndexOutOfBoundsException();
        }

        long value = 0;

        int shift = 0;
        for (int i = 0; i < Strings.LONG_LENGTH; i++)
        {
            value |= (((long)_data[startIdx + i] & 0xFF) << shift);
            shift += 8;
        }

        return value;
    }

    protected static void insertLong(long value, byte[] _data, int _size, int startIdx)
    {
    	if (_size < (startIdx + Strings.LONG_LENGTH)) {
        	throw new java.lang.ArrayIndexOutOfBoundsException();
        }

        for (int i = 0; i < Strings.LONG_LENGTH; i++)
        {
            _data[startIdx + i] = (byte)value;
            value >>= 8;
        }
    }

    protected static String getString(byte[] _data, int _size, int startIdx)
    {
        if (_size < (startIdx + Strings.INT_LENGTH)) {
            throw new java.lang.ArrayIndexOutOfBoundsException();
        }
        int length = getInt(_data, _size, startIdx);
        startIdx += Strings.INT_LENGTH;

        byte[] bytes = new byte[length];
        System.arraycopy(_data, startIdx, bytes, 0, length);

        String value = "";
        try {
            value = new String(bytes, 0, length, "UnicodeLittleUnmarked");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return value;
    }

    protected static void insertString(String value, byte[] _data, int _size, int startIdx)
    {
        try {
            byte[] unicode_bytes = value.getBytes("UnicodeLittleUnmarked");

            if (_size < (startIdx + Strings.INT_LENGTH)) {
                throw new java.lang.ArrayIndexOutOfBoundsException();
            }
            insertInt(unicode_bytes.length, _data, _size, startIdx);
            startIdx += Strings.INT_LENGTH;

            for (int i = 0; i < unicode_bytes.length; i++)
            {
                _data[startIdx + i] = unicode_bytes[i];
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static MessageHead parseHead(byte[] _data, int _size)
    {
        MessageHead head = new MessageHead();
        if (_size < MessageHead.LENGTH)
        {
            head.type = MessageType.Invalid;
        }
        else
        {
        	int idx = 0;
            head.id = getLong(_data, _size, idx);
            idx += 8;
            head.nodeID = getInt(_data, _size, idx);
            idx += 4;
            head.type = getInt(_data, _size, idx);
            idx += 4;
        }
        return head;
    }

    private void insertHead()
    {
        int idx = 0;
        insertLong(head.id, data, data.length, idx);
        idx += 8;
        insertInt((int)head.nodeID, data, data.length, idx);
        idx += 4;
        insertInt((int)head.type, data, data.length, idx);
        idx += 4;
    }

    protected int serialize(byte[] data_) { 
    	data = data_; 
    	size = data_.length; 
    	insertHead();
        return MessageHead.LENGTH;
	}
    
    protected int deserialize(byte[] data_, int size_) { 
    	data = data_; 
    	size = size_; 
    	head = parseHead(data, size); 
    	return MessageHead.LENGTH; 
	}

    public String toString()
    {
        return "<id:{" + head.id + "}, nodeID:{" + head.nodeID + "}, type={" + head.type + "}>";
    }
    
}
