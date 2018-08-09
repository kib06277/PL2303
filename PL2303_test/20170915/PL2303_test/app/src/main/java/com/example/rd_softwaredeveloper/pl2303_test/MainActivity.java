package com.example.rd_softwaredeveloper.pl2303_test;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class MainActivity extends AppCompatActivity
{
    public  Button Open,Read,Write,GetModemStauts;
    public  EditText keyin1,keyin2;
    public  TextView show;
    public  Spinner Baudrate,DataBits,Parity,StopBits;
    public  RadioButton DTRON,DTROFF,RTSON,RTSOFF;
    public  String strStr;
    public  static final String NULL = null;
    public int PL2303HXD_BaudRate;
    public String PL2303HXD_BaudRate_str="B4800";
    public  int mTextFontSize = 12;
    public  ScrollView mSvText;
    public  StringBuilder msbStrContent = new StringBuilder();
    public  TextView mTvResponse; // response
    public  Button mbOpen,mbGetModemStatus;
    public  RadioGroup mRadioGroup_DTR, mRadioGroup_RTS;
    public  RadioButton mRadionBtn_DTR_On,mRadionBtn_DTR_Off, mRadionBtn_RTS_On, mRadionBtn_RTS_Off;
    public  ImageView mImageDSR_OnOff,mImageRI_OnOff,mImageDCD_OnOff,mImageCTS_OnOff;
    public  boolean startflag = false;

    DataParameter data = new DataParameter();
    ItemSelectedListener lister = new ItemSelectedListener();
    EquipmentAPI equipment =  new EquipmentAPI();
    PL2303Driver mSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(data.TAG, "Enter onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get service
        mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE), this, data.ACTION_USB_PERMISSION);

        // check USB host function.
        if (!mSerial.PL2303USBFeatureSupported())
        {
            Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT).show();
            Log.d(data.TAG, "No Support USB host API");
            mSerial = null;
        }

        Log.d(data.TAG, "Leave onCreate");

        init();

//        new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                // TODO Auto-generated method stub
//                while(true)
//                {
//                    try
//                    {
//                        Message msg = new Message();
//                        msg.what = 1;
//                        mHandler.sendMessage(msg);
//                        Thread.sleep(1000);
//                    }
//                    catch(Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
    }

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    readDataFromSerial();
                break;
            }
            super.handleMessage(msg);
        }
    };

    public void init()
    {
        Open = (Button) findViewById(R.id.Open);
        Read = (Button) findViewById(R.id.Read);
        Write = (Button)findViewById(R.id.Write);
        GetModemStauts = (Button) findViewById(R.id.GetModemStauts);

        keyin1 = (EditText)findViewById(R.id.keyin1);
        keyin2 = (EditText)findViewById(R.id.keyin2);

        show = (TextView)findViewById(R.id.show);

        Baudrate = (Spinner)findViewById(R.id.Baudrate);
        DataBits = (Spinner)findViewById(R.id.DataBits);
        Parity = (Spinner)findViewById(R.id.Parity);
        StopBits = (Spinner)findViewById(R.id.StopBits);

        DTRON = (RadioButton)findViewById(R.id.DTRON);
        DTROFF = (RadioButton)findViewById(R.id.DTROFF);
        RTSON = (RadioButton)findViewById(R.id.RTSON);
        RTSOFF = (RadioButton)findViewById(R.id.RTSOFF);

        mRadioGroup_DTR = (RadioGroup)findViewById(R.id.DTR);
        mRadioGroup_RTS = (RadioGroup)findViewById(R.id.RTS);

        mImageDSR_OnOff = (ImageView) findViewById(R.id.imageDSR);
        mImageRI_OnOff = (ImageView) findViewById(R.id.imageRI);
        mImageDCD_OnOff = (ImageView) findViewById(R.id.imageDCD);
        mImageCTS_OnOff = (ImageView) findViewById(R.id.imageCTS);

        mImageDSR_OnOff.setImageResource(R.drawable.ic_off1);
        mImageRI_OnOff.setImageResource(R.drawable.ic_off1);
        mImageDCD_OnOff.setImageResource(R.drawable.ic_off1);
        mImageCTS_OnOff.setImageResource(R.drawable.ic_off1);

        mRadioGroup_DTR.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // TODO Auto-generated method stub
                PL2303HXD_DTR_SignalChange(group, checkedId);
            }
        });

        mRadioGroup_RTS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // TODO Auto-generated method stub
                PL2303HXD_RTS_SignalChange(group, checkedId);
            }
        });

        Open.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                openUsbSerial();
            }
        });

        Write.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                writeDataToSerial();
            }
        });

        Read.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                readDataFromSerial();
            }
        });

        GetModemStauts.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                PL2303HXD_GetModemStauts();
            }
        });

        ArrayAdapter<CharSequence> Baudrate_adapter = ArrayAdapter.createFromResource(this, R.array.BaudRate_Var, android.R.layout.simple_spinner_item);
        Baudrate_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Baudrate.setAdapter(Baudrate_adapter);
        Baudrate.setOnItemSelectedListener(new ItemSelectedListener.baudRateOnItemSelectedListener());

        ArrayAdapter<CharSequence> DataBits_adapter = ArrayAdapter.createFromResource(this, R.array.DataBits_Var, android.R.layout.simple_spinner_item);
        DataBits_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DataBits.setAdapter(DataBits_adapter);
        DataBits.setOnItemSelectedListener(new ItemSelectedListener.DataBitsOnItemSelectedListener());

        ArrayAdapter<CharSequence> Parity_adapter = ArrayAdapter.createFromResource(this, R.array.Parity_Var, android.R.layout.simple_spinner_item);
        Parity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Parity.setAdapter(Parity_adapter);
        Parity.setOnItemSelectedListener(new ItemSelectedListener.ParityOnItemSelectedListener());

        ArrayAdapter<CharSequence> StopBits_adapter = ArrayAdapter.createFromResource(this, R.array.StopBits_Var, android.R.layout.simple_spinner_item);
        StopBits_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        StopBits.setAdapter(StopBits_adapter);
        StopBits.setOnItemSelectedListener(new ItemSelectedListener.StopBitsOnItemSelectedListener());
    }

    protected void onStop()
    {
        Log.d(data.TAG, "Enter onStop");
        super.onStop();
        Log.d(data.TAG, "Leave onStop");
    }

    @Override
    protected void onDestroy()
    {
        Log.d(data.TAG, "Enter onDestroy");

        if(mSerial!=null)
        {
            mSerial.end();
            mSerial = null;
        }

        super.onDestroy();
        Log.d(data.TAG, "Leave onDestroy");
    }

    public void onStart()
    {
        Log.d(data.TAG, "Enter onStart");
        super.onStart();
        Log.d(data.TAG, "Leave onStart");
    }

    public void onResume()
    {
        Log.d(data.TAG, "Enter onResume");
        super.onResume();
        String action =  getIntent().getAction();
        Log.d(data.TAG, "onResume:"+action);

        //if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
        if(!mSerial.isConnected())
        {
            if (data.SHOW_DEBUG)
            {
                Log.d(data.TAG, "New instance : " + mSerial);
            }

            if( !mSerial.enumerate() )
            {
                Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                Log.d(data.TAG, "onResume:enumerate succeeded!");
            }
        }//if isConnected

        Log.d(data.TAG, "Leave onResume");
    }

    public  void writeDataToSerial()
    {
        Log.d(data.TAG, "Enter writeDataToSerial");
        if(null == mSerial)
        {
            Log.d("error", "not Connected");
            return;
        }

        if(!mSerial.isConnected())
        {
            Log.d("error", "Connected");
            return;
        }
        String strWrite = keyin1.getText().toString();

        if (data.SHOW_DEBUG)
        {
            Log.d(data.TAG, "PL2303Driver Write 2(" + strWrite.length() + ") : " + strWrite);
        }
        int res = mSerial.write(strWrite.getBytes(), strWrite.length());
        if( res<0 )
        {
            Log.d(data.TAG, "setup2: fail to controlTransfer: "+ res);
            return;
        }

        Toast.makeText(this, "Write length: "+strWrite.length()+" bytes", Toast.LENGTH_SHORT).show();

        Log.d(data.TAG, "Leave writeDataToSerial");
    }

    public  void openUsbSerial()
    {
        Log.d(data.TAG, "Enter  openUsbSerial");
        if(null ==  mSerial)
        {
            return;
        }

        if (mSerial.isConnected())
        {
            if (data.SHOW_DEBUG)
            {
                Log.d(data.TAG, "openUsbSerial : isConnected ");
            }
            String str = Baudrate.getSelectedItem().toString();
            int baudRate = Integer.parseInt(str);
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
            Log.d(data.TAG, "baudRate:" + baudRate);

            if (!mSerial.InitByBaudRate(data.mBaudrate,700))
            {
                if(!mSerial.PL2303Device_IsHasPermission())
                {
                    Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();
                }

                if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip()))
                {
                    Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "connected" , Toast.LENGTH_SHORT).show();
            }
        }//isConnected
        Log.d(data.TAG, "Leave openUsbSerial");
    }//openUsbSerial

    public  void readDataFromSerial()
    {
        int len;
        byte[] rbuf = new byte[20];
        StringBuffer sbHex = new StringBuffer();

        Log.d(data.TAG, "Enter readDataFromSerial");

        if(null == mSerial)
        {
            return;
        }

        if(!mSerial.isConnected())
        {
            return;
        }
        else
        {
            if (data.SHOW_DEBUG)
            {
                Log.d(data.TAG, "openUsbSerial : isConnected ");
            }
            String str = Baudrate.getSelectedItem().toString();
            int baudRate = Integer.parseInt(str);
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
            Log.d(data.TAG, "baudRate:" + baudRate);

            if (!mSerial.InitByBaudRate(data.mBaudrate,1000))
            {
                if(!mSerial.PL2303Device_IsHasPermission())
                {
                    Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();
                }

                if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip()))
                {
                    Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        len = mSerial.read(rbuf);

        if(len < 0)
        {
            Log.i("TT","time = " + data.time);
            Log.d(data.TAG, "Fail to bulkTransfer(read data)");
            return;
        }
        else if (len > 0)
        {
            Log.i("TT","time = " + data.time);
            if (data.SHOW_DEBUG)
            {
                Log.d(data.TAG, "read len : " + len);
            }

            for (int j = 0; j < len; j++)
            {
                sbHex.append((char) (rbuf[j]&0x000000FF));
            }

            keyin2.setText(sbHex.toString());
        }
        else
        {
            data.time++;
            Log.i("TT","time = " + data.time);
            if (data.SHOW_DEBUG)
            {
                Log.d(data.TAG, "read len : 0 ");
            }
            Log.i("TT","time = " + data.time);
            return;
        }

        Log.d(data.TAG, "Leave readDataFromSerial");
    }//readDataFromSerial

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  void PL2303HXD_DTR_SignalChange(RadioGroup group, int checkedId)
    {
        int res;
        Log.d(data.TAG, "Enter  PL2303HXD_DTR_SignalChange");
        Log.i("EE", "mSerial = " + mSerial);
        if(null == mSerial)
        {
            Log.d(data.TAG, "return");
            return;
        }

        Log.d(data.TAG, "R.id.DTRON = " + R.id.DTRON);
        switch(checkedId)
        {
            case R.id.DTRON:
                Log.d(data.TAG, "DTRON");
                res = mSerial.setDTR (true);
                if(res < 0)
                {
                    Log.d(data.TAG, "Fail to setDTR");
                    return;
                }
                Log.d(data.TAG, "DTR ON");
                break;
            case R.id.DTROFF:
                Log.d(data.TAG, "DTROFF");
                res = mSerial.setDTR (false);
                if(res < 0)
                {
                    Log.d(data.TAG, "Fail to setDTR");
                    return;
                }
                Log.d(data.TAG, "DTR Off");
                break;
            default:
                Log.d(data.TAG, "no change ");
                break;
        }
        Log.d(data.TAG, "Exit  PL2303HXD_DTR_SignalChange");
    }

    public  void PL2303HXD_RTS_SignalChange(RadioGroup group, int checkedId)
    {
        int res;
        Log.d(data.TAG, "Enter  PL2303HXD_RTS_SignalChange");

        if(null == mSerial)
        {
            return;
        }

        switch(checkedId)
        {
            case R.id.RTSON:
                res = mSerial.setRTS (true);
                if(res<0)
                {
                    Log.d(data.TAG, "Fail to setRTS");
                    return;
                }
                Log.d(data.TAG, "RTS ON");
                break;

            case R.id.RTSOFF:
                res = mSerial.setRTS (false);
                if(res<0)
                {
                    Log.d(data.TAG, "Fail to setRTS");
                    return;
                }

                Log.d(data.TAG, "RTS Off");
                break;

            default:
                Log.d(data.TAG, "no change ");
                break;
        }
        Log.d(data.TAG, "Exit  PL2303HXD_RTS_SignalChange");
    }

    public  void PL2303HXD_GetModemStauts()
    {
        int[] res = new int[2];
        int Status=0;

        StringBuilder sb = new StringBuilder();

        if (null == mSerial)
        {
            return;
        }

        if (!mSerial.isConnected())
        {
            return;
        }

        Log.d(data.TAG, "Enter  PL2303HXD_GetModemStauts");
        res = mSerial.PL2303HXD_GetCommModemStatus();

        if (res[0] < 0 )
        {
            Log.d(data.TAG, "PL2303HXD_GetCommModemStatus: failed");
            sb.append("PL2303HXD_GetCommModemStatus: failed\n");
            return;
        }
        else
        {
            Status= res[1];
            Log.d(data.TAG, "PL2303HXD_GetCommModemStatusl :" + Status);
            sb.append("PL2303HXD_GetCommModemStatusl :" + Status +"\n");
        }

        //CTS
        if((Status & PL2303Driver.PL2303HXD_CTS_ON)==PL2303Driver.PL2303HXD_CTS_ON)
        {
            Log.d(data.TAG, "PL2303HXD_CTS_ON");
            sb.append("PL2303HXD_CTS_ON)\n");
            mImageCTS_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_on));
        }
        else
        {
            Log.d(data.TAG, "PL2303HXD_CTS_Off");
            sb.append("PL2303HXD_CTS_Off\n");
            mImageCTS_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_off1));
        }

        //DCD
        if((Status & PL2303Driver.PL2303HXD_DCD_ON)==PL2303Driver.PL2303HXD_DCD_ON)
        {
            Log.d(data.TAG, "PL2303HXD_DCD_ON");
            sb.append("PL2303HXD_DCD_ON\n");
            mImageDCD_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_on));
        }
        else
        {
            Log.d(data.TAG, "PL2303HXD_DCD_Off");
            sb.append("PL2303HXD_DCD_Off\n");
            mImageDCD_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_off1));
        }

        //DSR
        if((Status & PL2303Driver.PL2303HXD_DSR_ON)==PL2303Driver.PL2303HXD_DSR_ON)
        {
            Log.d(data.TAG, "PL2303HXD_DSR_ON");
            sb.append("PL2303HXD_DSR_ON\n");
            mImageDSR_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_on));
        }
        else
        {
            Log.d(data.TAG, "PL2303HXD_DSR_Off");
            sb.append("PL2303HXD_DSR_Off\n");
            mImageDSR_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_off1));
        }

        //RI
        if((Status & PL2303Driver.PL2303HXD_RI_ON)==PL2303Driver.PL2303HXD_RI_ON)
        {
            Log.d(data.TAG, "PL2303HXD_RI_ON");
            sb.append("PL2303HXD_RI_ON\n");
            mImageRI_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_on));
        }
        else
        {
            Log.d(data.TAG, "PL2303HXD_RI_Off");
            sb.append("PL2303HXD_RI_Off\n");
            mImageRI_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_off1));
        }

        try
        {
            mTvResponse.setText(sb.toString());
        }
        catch (Exception e)
        {
            Log.d(data.TAG, "TestModemStauts error:::");
            e.printStackTrace();
        }
        Log.d(data.TAG, "Leave  PL2303HXD_GetModemStauts");
    }
}
