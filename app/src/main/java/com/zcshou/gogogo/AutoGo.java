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

    //[[len, high, l, ang], ...]
    public static double[][] mTwoPos_Len_Hig;
    public static int Pos_index = 0;

    public static int Auto_Pos_index = 1;

    public static double AutoGoNowDisLng = 0.0;
    public static double AutoGoNowDisLat = 0.0;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_autogo);

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

    public static void AutoGoCalu(){
        mTwoPos_Len_Hig = new double[MainActivity.fbl_pos_list_index - 1][4];

        int index = 0;
        String output_str = "" + Math.sqrt(9) + " | ";

        while(index < MainActivity.fbl_pos_list_index) XLog.e("markMap_Pos["+ index +"] -> " + MainActivity.fbl_pos_list[index++].toString());
        index = 0;
        while(index < MainActivity.fbl_pos_list_index - 1) {
            mTwoPos_Len_Hig[index][0] = calcTwoPosLen(MainActivity.fbl_pos_list[index].longitude * 1000, MainActivity.fbl_pos_list[index + 1].longitude * 1000);
            mTwoPos_Len_Hig[index][1] = calcTwoPosHig(MainActivity.fbl_pos_list[index].latitude * 1000, MainActivity.fbl_pos_list[index + 1].latitude * 1000);
            mTwoPos_Len_Hig[index][2] = Math.sqrt(Math.pow(mTwoPos_Len_Hig[index][0],2) + Math.pow(mTwoPos_Len_Hig[index][1],2));
            //(+, +)
            if(mTwoPos_Len_Hig[index][0] > 0.0 && mTwoPos_Len_Hig[index][1] > 0.0){
                mTwoPos_Len_Hig[index][3] = Math.toDegrees(Math.asin((mTwoPos_Len_Hig[index][1] / mTwoPos_Len_Hig[index][2])));
            }
            //(-, +)
            else if(mTwoPos_Len_Hig[index][0] < 0.0 && mTwoPos_Len_Hig[index][1] > 0.0){
                mTwoPos_Len_Hig[index][3] = -180 - (Math.toDegrees(Math.asin((mTwoPos_Len_Hig[index][1] / mTwoPos_Len_Hig[index][2]))));
            }
            //(-, -)
            else if(mTwoPos_Len_Hig[index][0] < 0.0 && mTwoPos_Len_Hig[index][1] < 0.0){
                mTwoPos_Len_Hig[index][3] = -180 - Math.toDegrees(Math.asin((mTwoPos_Len_Hig[index][1] / mTwoPos_Len_Hig[index][2])));
            }
            //(+, -)
            else{
                mTwoPos_Len_Hig[index][3] = Math.toDegrees(Math.asin((mTwoPos_Len_Hig[index][1] / mTwoPos_Len_Hig[index][2])));
            }
            output_str = index + " -> " + mTwoPos_Len_Hig[index][0] + " | " + mTwoPos_Len_Hig[index][1] + " | " + mTwoPos_Len_Hig[index][2] + " | " + mTwoPos_Len_Hig[index][3];
            XLog.e("AutoGo_MSG:" + output_str);

            index++;
        }

        opinfo.setText(output_str);
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
