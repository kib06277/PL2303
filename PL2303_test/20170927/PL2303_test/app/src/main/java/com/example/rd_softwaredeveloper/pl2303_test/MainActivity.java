package com.example.rd_softwaredeveloper.pl2303_test;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class MainActivity extends AppCompatActivity
{
    private static final boolean SHOW_DEBUG = true;
    private static final int DISP_CHAR = 0;
    private static final int LINEFEED_CODE_CRLF = 1;
    private static final int LINEFEED_CODE_LF = 2;

    private int mDisplayType = DISP_CHAR;
    private int mReadLinefeedCode = LINEFEED_CODE_LF;
    private int mWriteLinefeedCode = LINEFEED_CODE_LF;

    private PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B300; //胞率
    private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8; //Date 長度 判斷是八或七
    private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE; //檢查碼
    private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1; //停止位元
    private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;

    private static final String ACTION_USB_PERMISSION = "com.prolific.pl2303hxdsimpletest.USB_PERMISSION";

    PL2303Driver mSerial;
    String TAG = "PL2303HXD_APLog";

    private Button Open,Read,Write,GetModemStauts;
    private EditText keyin1,keyin2;
    private TextView show;
    private Spinner Baudrate,DataBits,Parity,StopBits;
    private RadioButton DTRON,DTROFF,RTSON,RTSOFF;
    private String strStr;

    private static final String NULL = null;
    public int PL2303HXD_BaudRate;
    public String PL2303HXD_BaudRate_str="B4800";
    private int mTextFontSize = 12;
    private ScrollView mSvText;
    private StringBuilder msbStrContent = new StringBuilder();
    private TextView mTvResponse; // response
    private Button mbOpen,mbGetModemStatus;
    private RadioGroup mRadioGroup_DTR, mRadioGroup_RTS;
    private RadioButton mRadionBtn_DTR_On,mRadionBtn_DTR_Off, mRadionBtn_RTS_On, mRadionBtn_RTS_Off;
    private ImageView mImageDSR_OnOff,mImageRI_OnOff,mImageDCD_OnOff,mImageCTS_OnOff;
    private boolean startflag = false;
    int datatime = 0 , delaytime = 0 , i = 0 , time = 0 , datelen = 0 , datelen1 = 0;
    StringBuffer sbHex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "Enter onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get service
        mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE), this, ACTION_USB_PERMISSION);

        // check USB host function.
        if (!mSerial.PL2303USBFeatureSupported())
        {
            Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No Support USB host API");
            mSerial = null;
        }

        Log.d(TAG, "Leave onCreate");

        init();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                while(true)
                {
                    try
                    {
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                        Thread.sleep(500);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
        Baudrate.setOnItemSelectedListener(new baudRateOnItemSelectedListener());

        ArrayAdapter<CharSequence> DataBits_adapter = ArrayAdapter.createFromResource(this, R.array.DataBits_Var, android.R.layout.simple_spinner_item);
        DataBits_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DataBits.setAdapter(DataBits_adapter);
        DataBits.setOnItemSelectedListener(new DataBitsOnItemSelectedListener());

        ArrayAdapter<CharSequence> Parity_adapter = ArrayAdapter.createFromResource(this, R.array.Parity_Var, android.R.layout.simple_spinner_item);
        Parity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Parity.setAdapter(Parity_adapter);
        Parity.setOnItemSelectedListener(new ParityOnItemSelectedListener());

        ArrayAdapter<CharSequence> StopBits_adapter = ArrayAdapter.createFromResource(this, R.array.StopBits_Var, android.R.layout.simple_spinner_item);
        StopBits_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        StopBits.setAdapter(StopBits_adapter);
        StopBits.setOnItemSelectedListener(new StopBitsOnItemSelectedListener());
    }

    protected void onStop()
    {
        Log.d(TAG, "Enter onStop");
        super.onStop();
        Log.d(TAG, "Leave onStop");
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "Enter onDestroy");

        if(mSerial!=null)
        {
            mSerial.end();
            mSerial = null;
        }

        super.onDestroy();
        Log.d(TAG, "Leave onDestroy");
    }

    public void onStart()
    {
        Log.d(TAG, "Enter onStart");
        super.onStart();
        Log.d(TAG, "Leave onStart");
    }

    public void onResume()
    {
        Log.d(TAG, "Enter onResume");
        super.onResume();
        String action =  getIntent().getAction();
        Log.d(TAG, "onResume:"+action);

        //if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
        if(!mSerial.isConnected())
        {
            if (SHOW_DEBUG)
            {
                Log.d(TAG, "New instance : " + mSerial);
            }

            if( !mSerial.enumerate() )
            {
                Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                Log.d(TAG, "onResume:enumerate succeeded!");
            }
        }//if isConnected

        Log.d(TAG, "Leave onResume");
    }

    //--------------------------------------LoopBack function-----------------------------------------//
    private static final int START_NOTIFIER = 0x100;
    private static final int STOP_NOTIFIER = 0x101;
    private static final int PROG_NOTIFIER_SMALL = 0x102;
    private static final int PROG_NOTIFIER_LARGE = 0x103;
    private static final int ERROR_BAUDRATE_SETUP = 0x8000;
    private static final int ERROR_WRITE_DATA = 0x8001;
    private static final int ERROR_WRITE_LEN = 0x8002;
    private static final int ERROR_READ_DATA = 0x8003;
    private static final int ERROR_READ_LEN = 0x8004;
    private static final int ERROR_COMPARE_DATA = 0x8005;

    private void writeDataToSerial()
    {
        Log.d(TAG, "Enter writeDataToSerial");
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

        if (SHOW_DEBUG)
        {
            Log.d(TAG, "PL2303Driver Write 2(" + strWrite.length() + ") : " + strWrite);
        }
        int res = mSerial.write(strWrite.getBytes(), strWrite.length());
        if( res<0 )
        {
            Log.d(TAG, "setup2: fail to controlTransfer: "+ res);
            return;
        }
    }

    private void openUsbSerial()
    {
        Log.d(TAG, "Enter  openUsbSerial");
        if(null == mSerial)
        {
            return;
        }

        if (mSerial.isConnected())
        {
            if (SHOW_DEBUG)
            {
                Log.d(TAG, "openUsbSerial : isConnected ");
            }
            String str = Baudrate.getSelectedItem().toString();
            int baudRate = Integer.parseInt(str);
            switch (baudRate)
            {
                case 300:
                    mBaudrate = PL2303Driver.BaudRate.B300;
                    break;
                case 600:
                    mBaudrate = PL2303Driver.BaudRate.B600;
                    break;
                case 1200:
                    mBaudrate = PL2303Driver.BaudRate.B1200;
                    break;
                case 2400:
                    mBaudrate = PL2303Driver.BaudRate.B2400;
                    break;
                case 4800:
                    mBaudrate = PL2303Driver.BaudRate.B4800;
                    break;
                case 9600:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
                case 14400:
                    mBaudrate = PL2303Driver.BaudRate.B14400;
                    break;
                case 19200:
                    mBaudrate = PL2303Driver.BaudRate.B19200;
                    break;
                case 38400:
                    mBaudrate = PL2303Driver.BaudRate.B38400;
                    break;
                case 115200:
                    mBaudrate = PL2303Driver.BaudRate.B115200;
                    break;
                default:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
            }
            Log.d(TAG, "baudRate:" + baudRate);

            if (!mSerial.InitByBaudRate(mBaudrate,700))
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
        Log.d(TAG, "Leave openUsbSerial");
    }//openUsbSerial

    private void readDataFromSerial()
    {
        int len;
        byte[] rbuf = new byte[20];
        sbHex = new StringBuffer();

        Log.d(TAG, "Enter readDataFromSerial");

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
            if (SHOW_DEBUG)
            {
                Log.d(TAG, "openUsbSerial : isConnected ");
            }
            String str = Baudrate.getSelectedItem().toString();
            int baudRate = Integer.parseInt(str);
            switch (baudRate)
            {
                case 300:
                    mBaudrate = PL2303Driver.BaudRate.B300;
                    break;
                case 600:
                    mBaudrate = PL2303Driver.BaudRate.B600;
                    break;
                case 1200:
                    mBaudrate = PL2303Driver.BaudRate.B1200;
                    break;
                case 2400:
                    mBaudrate = PL2303Driver.BaudRate.B2400;
                    break;
                case 4800:
                    mBaudrate = PL2303Driver.BaudRate.B4800;
                    break;
                case 9600:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
                case 14400:
                    mBaudrate = PL2303Driver.BaudRate.B14400;
                    break;
                case 19200:
                    mBaudrate = PL2303Driver.BaudRate.B19200;
                    break;
                case 38400:
                    mBaudrate = PL2303Driver.BaudRate.B38400;
                    break;
                case 115200:
                    mBaudrate = PL2303Driver.BaudRate.B115200;
                    break;
                default:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
            }
            Log.d(TAG, "baudRate:" + baudRate);

            if (!mSerial.InitByBaudRate(mBaudrate,1000))
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
            Log.d(TAG, "Fail to bulkTransfer(read data)");
            return;
        }
        else if (len > 0)
        {
            if (SHOW_DEBUG)
            {
                Log.d(TAG, "read len : " + len);
            }

            for (int j = 0; j < len; j++)
            {
                sbHex.append((char) (rbuf[j]&0x000000FF));
            }

            //判斷時間
            //判斷資料有沒有變長如果有變長時間時間重新計算

            Log.i("TT", "time = " + time);
            Log.i("UU", "datelen = " + datelen);
            Log.i("EE", "datelen1 = " + datelen1);
            for(;(time <= 50) && (datelen >= datelen1);)
            {
                if(datatime == 0)
                {
                    datatime = (int)System.currentTimeMillis();
                    datelen = sbHex.length();
                    Log.i("UU", "datelen = " + datelen);
                }
                else
                {
                    delaytime = (int)System.currentTimeMillis();
                    time = Math.abs(datatime) - Math.abs(delaytime);
                    Log.i("TT", "time = " + time);
                    datelen1 = sbHex.length();
                    Log.i("EE", "datelen1 = " + datelen1);
                }
            }
            keyin2.setText(sbHex);

            return;
        }
        else
        {
            if (SHOW_DEBUG)
            {
                Log.d(TAG, "read len : 0 ");
            }

            if(datatime == 0)
            {
                datatime = (int)System.currentTimeMillis();
            }
            else
            {
                delaytime = (int)System.currentTimeMillis();
                time = Math.abs(datatime) - Math.abs(delaytime);
                if(time >= 2000)
                {
                    keyin2.setText("");
                    datatime = 0 ;
                }
                time = 0;
            }
            return;
        }
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

    public class baudRateOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(null == mSerial)
            {
                return;
            }
            if(!mSerial.isConnected())
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
                    mBaudrate = PL2303Driver.BaudRate.B300;
                    break;
                case 600:
                    mBaudrate = PL2303Driver.BaudRate.B600;
                    break;
                case 1200:
                    mBaudrate = PL2303Driver.BaudRate.B1200;
                    break;
                case 2400:
                    mBaudrate = PL2303Driver.BaudRate.B2400;
                    break;
                case 4800:
                    mBaudrate = PL2303Driver.BaudRate.B4800;
                    break;
                case 9600:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
                case 14400:
                    mBaudrate = PL2303Driver.BaudRate.B14400;
                    break;
                case 19200:
                    mBaudrate = PL2303Driver.BaudRate.B19200;
                    break;
                case 38400:
                    mBaudrate = PL2303Driver.BaudRate.B38400;
                    break;
                case 115200:
                    mBaudrate = PL2303Driver.BaudRate.B115200;
                    break;
                default:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
            }

            int res = 0;
            try
            {
                Log.i("test","mBaudrate = " + mBaudrate + " , mDataBits = " + mDataBits + " , mStopBits = " + mStopBits + " , mParity = " + mParity + " ,mFlowControl = " + mFlowControl);
                res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res < 0 )
            {
                Log.d(TAG, "fail to setup");
                return;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing.
        }
    }

    public class DataBitsOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(null == mSerial)
            {
                return;
            }
            if(!mSerial.isConnected())
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
                    mDataBits = PL2303Driver.DataBits.D5;
                    break;
                case 6:
                    mDataBits = PL2303Driver.DataBits.D6;
                    break;
                case 7:
                    mDataBits = PL2303Driver.DataBits.D7;
                    break;
                default:
                    mDataBits = PL2303Driver.DataBits.D8;
                    break;
            }

            int res = 0;
            try
            {
                Log.i("test","mBaudrate = " + mBaudrate + " , mDataBits = " + mDataBits + " , mStopBits = " + mStopBits + " , mParity = " + mParity + " ,mFlowControl = " + mFlowControl);
                res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 )
            {
                Log.d(TAG, "fail to setup");
                return;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing.
        }
    }

    public class ParityOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(null == mSerial)
            {
                return;
            }
            if(!mSerial.isConnected())
            {
                return;
            }

            String newParity;
            newParity = parent.getItemAtPosition(position).toString();

            switch (newParity)
            {
                case "odd":
                    mParity = PL2303Driver.Parity.ODD;
                    break;
                case "even":
                    mParity = PL2303Driver.Parity.EVEN;
                    break;
                default:
                    mParity = PL2303Driver.Parity.NONE;
                    break;
            }

            int res = 0;
            try
            {
                Log.i("test","mBaudrate = " + mBaudrate + " , mDataBits = " + mDataBits + " , mStopBits = " + mStopBits + " , mParity = " + mParity + " ,mFlowControl = " + mFlowControl);
                res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 )
            {
                Log.d(TAG, "fail to setup");
                return;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing.
        }
    }

    public class StopBitsOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(null == mSerial)
            {
                return;
            }
            if(!mSerial.isConnected())
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
                    mStopBits = PL2303Driver.StopBits.S1;
                    break;
                default:
                    mStopBits = PL2303Driver.StopBits.S2;
                    break;
            }

            int res = 0;
            try
            {
                Log.i("test","mBaudrate = " + mBaudrate + " , mDataBits = " + mDataBits + " , mStopBits = " + mStopBits + " , mParity = " + mParity + " ,mFlowControl = " + mFlowControl);
                res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 )
            {
                Log.d(TAG, "fail to setup");
                return;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing.
        }
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

        Log.d(TAG, "Enter  PL2303HXD_GetModemStauts");
        res = mSerial.PL2303HXD_GetCommModemStatus();

        if (res[0] < 0 )
        {
            Log.d(TAG, "PL2303HXD_GetCommModemStatus: failed");
            sb.append("PL2303HXD_GetCommModemStatus: failed\n");
            return;
        }
        else
        {
            Status= res[1];
            Log.d(TAG, "PL2303HXD_GetCommModemStatusl :" + Status);
            sb.append("PL2303HXD_GetCommModemStatusl :" + Status +"\n");
        }

        //CTS
        if((Status & PL2303Driver.PL2303HXD_CTS_ON)==PL2303Driver.PL2303HXD_CTS_ON)
        {
            Log.d(TAG, "PL2303HXD_CTS_ON");
            sb.append("PL2303HXD_CTS_ON)\n");
            mImageCTS_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_on));
        }
        else
        {
            Log.d(TAG, "PL2303HXD_CTS_Off");
            sb.append("PL2303HXD_CTS_Off\n");
            mImageCTS_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_off1));
        }

        //DCD
        if((Status & PL2303Driver.PL2303HXD_DCD_ON)==PL2303Driver.PL2303HXD_DCD_ON)
        {
            Log.d(TAG, "PL2303HXD_DCD_ON");
            sb.append("PL2303HXD_DCD_ON\n");
            mImageDCD_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_on));
        }
        else
        {
            Log.d(TAG, "PL2303HXD_DCD_Off");
            sb.append("PL2303HXD_DCD_Off\n");
            mImageDCD_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_off1));
        }

        //DSR
        if((Status & PL2303Driver.PL2303HXD_DSR_ON)==PL2303Driver.PL2303HXD_DSR_ON)
        {
            Log.d(TAG, "PL2303HXD_DSR_ON");
            sb.append("PL2303HXD_DSR_ON\n");
            mImageDSR_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_on));
        }
        else
        {
            Log.d(TAG, "PL2303HXD_DSR_Off");
            sb.append("PL2303HXD_DSR_Off\n");
            mImageDSR_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_off1));
        }

        //RI
        if((Status & PL2303Driver.PL2303HXD_RI_ON)==PL2303Driver.PL2303HXD_RI_ON)
        {
            Log.d(TAG, "PL2303HXD_RI_ON");
            sb.append("PL2303HXD_RI_ON\n");
            mImageRI_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_on));
        }
        else
        {
            Log.d(TAG, "PL2303HXD_RI_Off");
            sb.append("PL2303HXD_RI_Off\n");
            mImageRI_OnOff.setImageDrawable(getResources().getDrawable(R.drawable.ic_off1));
        }

        try
        {
            mTvResponse.setText(sb.toString());
        }
        catch (Exception e)
        {
            Log.d(TAG, "TestModemStauts error:::");
            e.printStackTrace();
        }
        Log.d(TAG, "Leave  PL2303HXD_GetModemStauts");
    }

    public  void PL2303HXD_DTR_SignalChange(RadioGroup group, int checkedId)
    {
        int res;
        Log.d(TAG, "Enter  PL2303HXD_DTR_SignalChange");
        if(null == mSerial)
        {
            Log.d(TAG, "return");
            return;
        }

        Log.d(TAG, "R.id.DTRON = " + R.id.DTRON);
        switch(checkedId)
        {
            case R.id.DTRON:
                Log.d(TAG, "DTRON");
                res = mSerial.setDTR (true);
                if(res < 0)
                {
                    Log.d(TAG, "Fail to setDTR");
                    return;
                }
                Log.d(TAG, "DTR ON");
                break;
            case R.id.DTROFF:
                Log.d(TAG, "DTROFF");
                res = mSerial.setDTR (false);
                if(res < 0)
                {
                    Log.d(TAG, "Fail to setDTR");
                    return;
                }
                Log.d(TAG, "DTR Off");
                break;
            default:
                Log.d(TAG, "no change ");
                break;
        }
        Log.d(TAG, "Exit  PL2303HXD_DTR_SignalChange");
    }

    public  void PL2303HXD_RTS_SignalChange(RadioGroup group, int checkedId)
    {
        int res;
        Log.d(TAG, "Enter  PL2303HXD_RTS_SignalChange");

        if(null == mSerial)
        {
            return;
        }

        switch(checkedId)
        {
            case R.id.RTSON:
                res = mSerial.setRTS (true);
                if(res < 0)
                {
                    Log.d(TAG, "Fail to setRTS");
                    return;
                }
                Log.d(TAG, "RTS ON");
                break;

            case R.id.RTSOFF:
                res = mSerial.setRTS (false);
                if(res < 0)
                {
                    Log.d(TAG, "Fail to setRTS");
                    return;
                }

                Log.d(TAG, "RTS Off");
                break;

            default:
                Log.d(TAG, "no change ");
                break;
        }
        Log.d(TAG, "Exit  PL2303HXD_RTS_SignalChange");
    }
}
