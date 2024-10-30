package com.zcshou.gogogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import java.math.*;

import com.baidu.mapapi.model.LatLng;
import com.elvishew.xlog.XLog;
import com.zcshou.gogogo.MainActivity;
import com.zcshou.joystick.JoyStick;
import com.zcshou.service.AutoService;

public class AutoGo extends Activity{

    MainActivity mMainActivity;

    public static TextView opinfo;

    //[[len, high, l, ang, len_real], ...]
    public static double[][] mTwoPos_Len_Hig;
    public static int Pos_index = 0;

    public static boolean AutoGo_open = false;

    public static int Auto_Pos_index = 0;

    public static double AutoGoNowDisLng = 0.0;
    public static double AutoGoNowDisLat = 0.0;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_autogo);

        AutoGo_open = true;

        opinfo = (TextView) findViewById(R.id.opinfo);

        AutoGoCalu();

        Button button_1 = (Button) findViewById(R.id.opinfo_btn);
        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AutoService.class);
                startService(intent);
            }
        });
    }

    private double Haversine(LatLng p1, LatLng p2){
        double R = 6371.008;
        double deltaLatitude = Math.toRadians(p2.latitude - p1.latitude);
        double deltaLongitude = Math.toRadians(p2.longitude - p1.longitude);
        double latitude1 = Math.toRadians(p1.latitude);
        double latitude2 = Math.toRadians(p2.latitude);
        double a = Math.pow(Math.sin(deltaLatitude / 2), 2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(deltaLongitude / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return  R * c;
    }

    public void AutoGoCalu(){
        mTwoPos_Len_Hig = new double[MainActivity.fbl_pos_list_index - 1][5];
        Auto_Pos_index = 0;

        int index = 0;
        String show_info_in_TextView = "";

        while(index < MainActivity.fbl_pos_list_index) XLog.e("markMap_Pos["+ index +"] -> " + MainActivity.fbl_pos_list[index++].toString());
        index = 0;
        while(index < MainActivity.fbl_pos_list_index - 1) {
            mTwoPos_Len_Hig[index][0] = calcTwoPosLen(MainActivity.fbl_pos_list[index].longitude * 1000, MainActivity.fbl_pos_list[index + 1].longitude * 1000);
            mTwoPos_Len_Hig[index][1] = calcTwoPosHig(MainActivity.fbl_pos_list[index].latitude * 1000, MainActivity.fbl_pos_list[index + 1].latitude * 1000);
            mTwoPos_Len_Hig[index][2] = Math.sqrt(Math.pow(mTwoPos_Len_Hig[index][0],2) + Math.pow(mTwoPos_Len_Hig[index][1],2));
            mTwoPos_Len_Hig[index][4] = Haversine(MainActivity.fbl_pos_list[index], MainActivity.fbl_pos_list[index + 1]) * 1000;

            double tDegrees = Math.toDegrees(Math.asin((mTwoPos_Len_Hig[index][1] / mTwoPos_Len_Hig[index][2])));
            tDegrees = new BigDecimal(tDegrees).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();

            //(+, +)
            if(mTwoPos_Len_Hig[index][0] > 0.0 && mTwoPos_Len_Hig[index][1] > 0.0){
                mTwoPos_Len_Hig[index][3] = tDegrees;
            }
            //(-, +)
            else if(mTwoPos_Len_Hig[index][0] < 0.0 && mTwoPos_Len_Hig[index][1] > 0.0){
                mTwoPos_Len_Hig[index][3] = -180 - (tDegrees);
            }
            //(-, -)
            else if(mTwoPos_Len_Hig[index][0] < 0.0 && mTwoPos_Len_Hig[index][1] < 0.0){
                mTwoPos_Len_Hig[index][3] = -180 - tDegrees;
            }
            //(+, -)
            else{
                mTwoPos_Len_Hig[index][3] = tDegrees;
            }

            String output_str = index + " -> " +  "Angle:" + mTwoPos_Len_Hig[index][3] + " | " + "Len:" + mTwoPos_Len_Hig[index][4];
            show_info_in_TextView += output_str + '\n';
            XLog.e("AutoGo_MSG:" + output_str);

            index++;
        }

        opinfo.setText(show_info_in_TextView);
    }



    private double VincentyConstents(LatLng p1, LatLng p2){
        double res = 0.0;

        int a = 6378137;
        double b = 6356752.3142;
        double f = 1 / 298.257223563;

        return res;
    }

    public static double[][] get_mTwoPos_Len_Hig(){
        return mTwoPos_Len_Hig;
    }

    @Override
    public void onDestroy(){
        stopService(new Intent(getBaseContext(), AutoService.class));
        super.onDestroy();
    }

    private static double calcTwoPosLen(double x_1, double x_2){return (x_2 - x_1);}

    private static double calcTwoPosHig(double y_1, double y_2){return (y_2 - y_1);}

}
