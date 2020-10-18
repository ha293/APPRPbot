package com.starway.starrobot.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteService extends Service {

    private ServerSocket server;

    public RemoteService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            server = new ServerSocket(8765);
            while (true) {
                Socket socket = server.accept();
                DataInputStream stream = new DataInputStream(socket.getInputStream());
                System.out.println(stream.readUTF());
                //TODO: 远程遥控待实现
                stream.close();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
