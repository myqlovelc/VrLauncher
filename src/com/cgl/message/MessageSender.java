package com.cgl.message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.cgl.utils.*;
import com.cgl.vrlauncher.Service1;

public class MessageSender {
	
	private static String key = "1994111012345678";
	
	public static void sendMessage(DatagramSocket socket, byte[] sendBuf, int length, InetAddress addr, int port) {
		
		try {

            if (Service1.getCommunicationMode() == 1) {

                byte newBuf[] = new byte[length];

                System.arraycopy(sendBuf, 0, newBuf, 0, length);

                byte[] encrypted = AES.aesEncrypt(newBuf, key);

                DatagramPacket sendPacket = new DatagramPacket(encrypted , encrypted.length ,
                        addr , port );
                socket.send(sendPacket);

            }
            else if (Service1.getCommunicationMode() == 2016) {
            	
            	
                DatagramPacket sendPacket = new DatagramPacket(sendBuf , length ,
                        addr , port );
                socket.send(sendPacket);
            }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static byte[] receiveMessage(DatagramPacket packet) {
		
		try {
			
			//System.out.println(new String(packet.getData(), 0, packet.getLength()));

            if (Service1.getCommunicationMode() == 1) {
                byte newBuf[] = new byte[packet.getLength()];

                System.arraycopy(packet.getData(), 0, newBuf, 0, packet.getLength());

                byte[] bytes = AES.aesDecrypt(newBuf, key);

                return bytes;
            }
			else if (Service1.getCommunicationMode() == 2016) {
                return packet.getData();
            }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

        return null;
	}

}
