package com.example.rd_softwaredeveloper.pl2303_test;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;

import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * Created by RD-SoftwareDeveloper on 2017/9/15.
 */

public class EquipmentAPI extends AppCompatActivity
{
    DataParameter data = new DataParameter();

    public  void Send_Notifier_Message(int mmsg)
    {
        Message m = new Message();
        m.what = mmsg;
        myMessageHandler.sendMessage(m);
        Log.d(data.TAG, String.format("Msg index: %04x", mmsg));
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public  void Send_ERROR_Message(int mmsg, int value1, int value2)
    {
        Message m = new Message();
        m.what = mmsg;
        m.arg1 = value1;
        m.arg2 = value2;
        myMessageHandler.sendMessage(m);
        Log.d(data.TAG, String.format("Msg index: %04x", mmsg));
    }

    Handler myMessageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case DataParameter.START_NOTIFIER:
                    break;
                case DataParameter.STOP_NOTIFIER:
                    break;
                case DataParameter.PROG_NOTIFIER_SMALL:
                    break;
                case DataParameter.PROG_NOTIFIER_LARGE:
                    break;
                case DataParameter.ERROR_BAUDRATE_SETUP:
                    break;
                case DataParameter.ERROR_WRITE_DATA:
                    break;
                case DataParameter.ERROR_WRITE_LEN:
                    break;
                case DataParameter.ERROR_READ_DATA:
                    break;
                case DataParameter.ERROR_READ_LEN:
                    break;
                case DataParameter.ERROR_COMPARE_DATA:
                    break;
            }
            super.handleMessage(msg);
        }//handleMessage
    };
}
