package com.maa.mobilestats;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;


import static android.content.Context.BATTERY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class FetchStats {
    ShellExecuter exe = new ShellExecuter();


    public Float FetchVoltage(){
        return Float.parseFloat(exe.Executer("cat /sys/devices/soc/qpnp-smbcharger-18/power_supply/battery/voltage_now")) / 1000;

    }

    public String FetchCurrent(){
        String current = "";
        Float Current = Float.parseFloat(exe.Executer("cat /sys/devices/soc/qpnp-smbcharger-18/power_supply/battery/current_now")) / 1000;
        if(Current > 0){
            current = "-"+Current;
        }
        else{
            current = String.valueOf(Math.abs(Current));
        }
        return current;
    }

    public int FetchChargeLvl(BatteryManager bm){
        int charge = 0;



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            charge = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

        return  charge;
    }


    public String FetchBatteryTech(){
        return  exe.Executer("cat /sys/class/power_supply/battery/technology");
    }

    public Float FetchBatteryDesignCapacity(){
        return Float.parseFloat(exe.Executer("cat /sys/devices/soc/qpnp-smbcharger-18/power_supply/battery/charge_full_design")) / 1000 ;
    }

    public Float FetchBatteryCapacity(){
        return Float.parseFloat(exe.Executer("cat /sys/devices/soc/qpnp-smbcharger-18/power_supply/battery/charge_full")) / 1000 ;
    }

    public Float FetchTemperature(){

        return Float.parseFloat(exe.Executer("cat /sys/devices/soc/qpnp-smbcharger-18/power_supply/battery/temp")) / 10;
    }

    public  String FetchStatus(){
        return  exe.Executer("cat /sys/devices/soc/qpnp-smbcharger-18/power_supply/battery/status");
    }
}
