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

    Timer Refresh , interval; //刷新，間隔時間
    int time = 0; //刷新時間
    int delaytime = 0; //延遲時間
    int sendtime = 0; //傳輸間距

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
                        Log.i("EE", "Thread");
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                        Thread.sleep(1000);
                        Log.i("EE", "next Thread");
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
                    Log.i("EE", "mHandler");
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

        //timer = (Chronometer)findViewById(R.id.timer);
        //datatime = (Chronometer)findViewById(R.id.datatime);

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

    Handler myMessageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case START_NOTIFIER:
                    Write.setEnabled(false);
                    Read.setEnabled(false);
                    break;
                case STOP_NOTIFIER:
                    Write.setEnabled(true);
                    Read.setEnabled(true);
                    break;
                case PROG_NOTIFIER_SMALL:
                    break;
                case PROG_NOTIFIER_LARGE:
                    break;
                case ERROR_BAUDRATE_SETUP:
                    break;
                case ERROR_WRITE_DATA:
                    break;
                case ERROR_WRITE_LEN:
                    break;
                case ERROR_READ_DATA:
                    break;
                case ERROR_READ_LEN:
                    break;
                case ERROR_COMPARE_DATA:
                    break;
            }
            super.handleMessage(msg);
        }//handleMessage
    };

    private void Send_Notifier_Message(int mmsg)
    {
        Message m= new Message();
        m.what = mmsg;
        myMessageHandler.sendMessage(m);
        Log.d(TAG, String.format("Msg index: %04x", mmsg));
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void Send_ERROR_Message(int mmsg, int value1, int value2)
    {
        Message m= new Message();
        m.what = mmsg;
        m.arg1 = value1;
        m.arg2 = value2;
        myMessageHandler.sendMessage(m);
        Log.d(TAG, String.format("Msg index: %04x", mmsg));
    }

    private Runnable tLoop = new Runnable()
    {
        public void run()
        {
            int res = 0, len, i;
            Time t = new Time();
            byte[] rbuf = new byte[4096];
            final int mBRateValue[] = {9600, 19200, 115200};
            PL2303Driver.BaudRate mBRate[] = {PL2303Driver.BaudRate.B9600, PL2303Driver.BaudRate.B19200, PL2303Driver.BaudRate.B115200};

            if(null==mSerial)
            {
                Log.i("error", "not Connected");
                return;
            }
            if(!mSerial.isConnected())
            {
                Log.i("error", "Connected");
                return;
            }
            t.setToNow();
            Random mRandom = new Random(t.toMillis(false));

            byte[] byteArray1 = new byte[256]; //test pattern-1
            mRandom.nextBytes(byteArray1);//fill buf with random bytes
            Send_Notifier_Message(START_NOTIFIER);

            for(int WhichBR = 0 ; WhichBR < mBRate.length ; WhichBR++)
            {
                try
                {
                    res = mSerial.setup(mBRate[WhichBR], mDataBits, mStopBits, mParity, mFlowControl);
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(res < 0)
                {
                    Send_Notifier_Message(START_NOTIFIER);
                    Send_ERROR_Message(ERROR_BAUDRATE_SETUP, mBRateValue[WhichBR], 0);
                    Log.d(TAG, "Fail to setup = "+res);
                    return;
                }
                Send_Notifier_Message(PROG_NOTIFIER_LARGE);

                for(int times=0;times<2;times++)
                {
                    len = mSerial.write(byteArray1, byteArray1.length);
                    if( len<0 )
                    {
                        Send_ERROR_Message(ERROR_WRITE_DATA, mBRateValue[WhichBR], 0);
                        Log.d(TAG, "Fail to write="+len);
                        return;
                    }

                    if( len!=byteArray1.length )
                    {
                        Send_ERROR_Message(ERROR_WRITE_LEN, mBRateValue[WhichBR], len);
                        return;
                    }
                    Send_Notifier_Message(PROG_NOTIFIER_SMALL);

                    len = mSerial.read(rbuf);
                    if(len<0)
                    {
                        Send_ERROR_Message(ERROR_READ_DATA, mBRateValue[WhichBR], 0);
                        return;
                    }
                    Log.d(TAG, "read length="+len+";byteArray1 length="+byteArray1.length);

                    if(len!=byteArray1.length)
                    {
                        Send_ERROR_Message(ERROR_READ_LEN, mBRateValue[WhichBR], len);
                        return;
                    }
                    Send_Notifier_Message(PROG_NOTIFIER_SMALL);

                    for(i=0;i<len;i++)
                    {
                        if(rbuf[i]!=byteArray1[i])
                        {
                            Send_ERROR_Message(ERROR_COMPARE_DATA, rbuf[i], byteArray1[i]);
                            Log.d(TAG, "Data is wrong at "+ String.format("rbuf[%d]=%02X,byteArray1[%d]=%02X", i, rbuf[i], i, byteArray1[i]));
                            return;
                        }//if
                    }//for
                    Send_Notifier_Message(PROG_NOTIFIER_LARGE);
                }//for(times)
            }//for(WhichBR)

            try
            {
                res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 )
            {
                Send_ERROR_Message(ERROR_BAUDRATE_SETUP, 0, 0);
                return;
            }
            Send_Notifier_Message(STOP_NOTIFIER);

        }//run()
    };//Runnable tLoop

    private void writeDataToSerial()
    {
        Log.d(TAG, "Enter writeDataToSerial");
        if(null==mSerial)
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

        Toast.makeText(this, "Write length: "+strWrite.length()+" bytes", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Leave writeDataToSerial");
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
        StringBuffer sbHex = new StringBuffer();

        Refresh = new Timer();
        interval = new Timer();

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

        try
        {
            //宣告Timer
            Timer timer01 = new Timer();

            //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
            timer01.schedule(Refreshtask, 0,1000);
        }
        catch (Exception e)
        {
            Log.i("HH", "e = " + e);
        }

        if(len < 0)
        {
            Log.i("TT","time = " + time);
            Log.d(TAG, "Fail to bulkTransfer(read data)");
            return;
        }
        else if (len > 0)
        {
            Log.i("TT","time = " + time);
            if (SHOW_DEBUG)
            {
                Log.d(TAG, "read len : " + len);
            }

            for (int j = 0; j < len; j++)
            {
                sbHex.append((char) (rbuf[j]&0x000000FF));
            }

            keyin2.setText(sbHex.toString());

//            if(sendtime != 0)
//            {
//                Log.i("Test", "sendtime = " + sendtime);
//                delaytime = time;
//                Log.i("Test", "delaytime = " + delaytime);
//                if(delaytime > sendtime)
//                {
//                    keyin2.setText(sbHex.toString());
//                }
//            }
//            else
//            {
//                sendtime = time;
//            }
        }
        else
        {
            time++;
            Log.i("TT","time = " + time);
            if (SHOW_DEBUG)
            {
                Log.d(TAG, "read len : 0 ");
            }
            Log.i("TT","time = " + time);
            return;
        }

        Log.d(TAG, "Leave readDataFromSerial");
    }//readDataFromSerial

    //TimerTask無法直接改變元件因此要透過Handler來當橋樑
    private Handler handler = new Handler()
    {
        public  void  handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 1:
                    keyin2.setText("123");
                    time = 0;
                    break;
            }
        }
    };

    private TimerTask Refreshtask = new TimerTask()
    {
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            if (startflag)
            {
                //如果startflag是true則每秒tsec+1
                time++;
                Message message = new Message();
                //傳送訊息1
                if(time == 1000)
                {
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        }
    };

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

}
