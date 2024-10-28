package com.zcshou.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Bundle;

import com.zcshou.gogogo.AutoGo;
import com.zcshou.service.ServiceGo;

public class AutoService extends Service{

    @Override
    public IBinder onBind(Intent intent){


        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){



        return START_STICKY;
    }

    @Override
    public void onDestroy(){



        super.onDestroy();
    }
}
