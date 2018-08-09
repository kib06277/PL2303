package com.example.rd_softwaredeveloper.pl2303_test;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import java.io.IOException;
import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * Created by RD-SoftwareDeveloper on 2017/9/15.
 */

public class ItemSelectedListener
{
    static DataParameter data = new DataParameter();
    public static class baudRateOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(null == data.mSerial)
            {
                return;
            }
            if(!data.mSerial.isConnected())
            {
                return;
            }
            int baudRate = 0;
            String newBaudRate;
            newBaudRate = parent.getItemAtPosition(position).toString();

            try
            {
                baudRate= Integer.parseInt(newBaudRate);
            }
            catch (NumberFormatException e)
            {
                System.out.println(" parse int error!!  " + e);
            }

            switch (baudRate)
            {
                case 300:
                    data.mBaudrate = PL2303Driver.BaudRate.B300;
                    break;
                case 600:
                    data.mBaudrate = PL2303Driver.BaudRate.B600;
                    break;
                case 1200:
                    data.mBaudrate = PL2303Driver.BaudRate.B1200;
                    break;
                case 2400:
                    data.mBaudrate = PL2303Driver.BaudRate.B2400;
                    break;
                case 4800:
                    data.mBaudrate = PL2303Driver.BaudRate.B4800;
                    break;
                case 9600:
                    data.mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
                case 14400:
                    data.mBaudrate = PL2303Driver.BaudRate.B14400;
                    break;
                case 19200:
                    data.mBaudrate = PL2303Driver.BaudRate.B19200;
                    break;
                case 38400:
                    data.mBaudrate = PL2303Driver.BaudRate.B38400;
                    break;
                case 115200:
                    data.mBaudrate = PL2303Driver.BaudRate.B115200;
                    break;
                default:
                    data.mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
            }

            int res = 0;
            try
            {
                Log.i("test","mBaudrate = " + data.mBaudrate + " , mDataBits = " + data.mDataBits + " , mStopBits = " + data.mStopBits + " , mParity = " + data.mParity + " ,mFlowControl = " + data.mFlowControl);
                res = data.mSerial.setup(data.mBaudrate, data.mDataBits, data.mStopBits, data.mParity, data.mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res < 0 )
            {
                Log.d(data.TAG, "fail to setup");
                return;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing.
        }
    }

    public static class DataBitsOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(null == data.mSerial)
            {
                return;
            }
            if(!data.mSerial.isConnected())
            {
                return;
            }
            int DataBits = 0;
            String newDataBits;
            newDataBits = parent.getItemAtPosition(position).toString();

            try
            {
                DataBits= Integer.parseInt(newDataBits);
            }
            catch (NumberFormatException e)
            {
                System.out.println(" parse int error!!  " + e);
            }

            switch (DataBits)
            {
                case 5:
                    data.mDataBits = PL2303Driver.DataBits.D5;
                    break;
                case 6:
                    data.mDataBits = PL2303Driver.DataBits.D6;
                    break;
                case 7:
                    data.mDataBits = PL2303Driver.DataBits.D7;
                    break;
                default:
                    data.mDataBits = PL2303Driver.DataBits.D8;
                    break;
            }

            int res = 0;
            try
            {
                Log.i("test","mBaudrate = " + data.mBaudrate + " , mDataBits = " + data.mDataBits + " , mStopBits = " + data.mStopBits + " , mParity = " + data.mParity + " ,mFlowControl = " + data.mFlowControl);
                res = data.mSerial.setup(data.mBaudrate, data.mDataBits, data.mStopBits, data.mParity, data.mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 )
            {
                Log.d(data.TAG, "fail to setup");
                return;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing.
        }
    }

    public static class ParityOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(null == data.mSerial)
            {
                return;
            }
            if(!data.mSerial.isConnected())
            {
                return;
            }

            String newParity;
            newParity = parent.getItemAtPosition(position).toString();

            switch (newParity)
            {
                case "odd":
                    data.mParity = PL2303Driver.Parity.ODD;
                    break;
                case "even":
                    data.mParity = PL2303Driver.Parity.EVEN;
                    break;
                default:
                    data.mParity = PL2303Driver.Parity.NONE;
                    break;
            }

            int res = 0;
            try
            {
                Log.i("test","mBaudrate = " + data.mBaudrate + " , mDataBits = " + data.mDataBits + " , mStopBits = " + data.mStopBits + " , mParity = " + data.mParity + " ,mFlowControl = " + data.mFlowControl);
                res = data.mSerial.setup(data.mBaudrate, data.mDataBits, data.mStopBits, data.mParity, data.mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 )
            {
                Log.d(data.TAG, "fail to setup");
                return;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing.
        }
    }

    public static class StopBitsOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(null == data.mSerial)
            {
                return;
            }
            if(!data.mSerial.isConnected())
            {
                return;
            }
            int StopBits = 0;
            String newStopBits;
            newStopBits = parent.getItemAtPosition(position).toString();

            try
            {
                StopBits = Integer.parseInt(newStopBits);
            }
            catch (NumberFormatException e)
            {
                System.out.println(" parse int error!!  " + e);
            }

            switch (StopBits)
            {
                case 1:
                    data.mStopBits = PL2303Driver.StopBits.S1;
                    break;
                default:
                    data.mStopBits = PL2303Driver.StopBits.S2;
                    break;
            }

            int res = 0;
            try
            {
                Log.i("test","mBaudrate = " + data.mBaudrate + " , mDataBits = " + data.mDataBits + " , mStopBits = " + data.mStopBits + " , mParity = " + data.mParity + " ,mFlowControl = " + data.mFlowControl);
                res = data.mSerial.setup(data.mBaudrate, data.mDataBits, data.mStopBits, data.mParity, data.mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 )
            {
                Log.d(data.TAG, "fail to setup");
                return;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing.
        }
    }
}
