package com.example.num_rec1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient extends Thread {
    public OutputStream outputStream;
    BufferedReader br;
    Socket socket;
    Thread thread=new Thread();
    Boolean flag=false;
    public TCPClient(){
        this.start();
    }
    @Override
    public void run() {
        try {
            this.socket = new Socket("192.168.235.37", 12345);
            if(socket!=null) {
                outputStream = socket.getOutputStream();
                br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                flag=true;
            }
        }
        catch (Exception e){
            System.out.println("FAIL!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    public void send(byte[] bytes){
        try {
            outputStream.write(bytes);
        }
        catch (Exception e){}
    }
    public void send(ByteArrayOutputStream buffer){
        int length=0;
        int point=0;
        int outputBufferLength=1024;
        try {
            byte[] bytessend=new byte[outputBufferLength];
            byte[] bytes=buffer.toByteArray();
            length=bytes.length;
            for(point=0;point+outputBufferLength<length;point+=outputBufferLength) {
                System.arraycopy(bytes, point, bytessend, 0, outputBufferLength);
                send(bytessend);
            }
            if(length==point-1);
            else {
                System.arraycopy(bytes,point,bytessend,0,length-point);
                send(bytessend);
            }
        }
        catch (Exception e){}
    }
    public void closeLink(){
        try{
            socket.close();
        }
        catch (Exception e){}
    }
    public void waiting(){
        while(flag);
    }
}