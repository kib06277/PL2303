package com.example.rd_softwaredeveloper.pl2303_test;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;

import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * Created by RD-SoftwareDeveloper on 2017/9/15.
 */

public class DataParameter
{
    //--------------------------------------LoopBack function-----------------------------------------//
    public  static final int START_NOTIFIER = 0x100;
    public  static final int STOP_NOTIFIER = 0x101;
    public  static final int PROG_NOTIFIER_SMALL = 0x102;
    public  static final int PROG_NOTIFIER_LARGE = 0x103;
    public  static final int ERROR_BAUDRATE_SETUP = 0x8000;
    public  static final int ERROR_WRITE_DATA = 0x8001;
    public  static final int ERROR_WRITE_LEN = 0x8002;
    public  static final int ERROR_READ_DATA = 0x8003;
    public  static final int ERROR_READ_LEN = 0x8004;
    public  static final int ERROR_COMPARE_DATA = 0x8005;

    public  static final boolean SHOW_DEBUG = true;
    public  static final int DISP_CHAR = 0;
    public  static final int LINEFEED_CODE_CRLF = 1;
    public  static final int LINEFEED_CODE_LF = 2;

    public  int mDisplayType = DISP_CHAR;
    public  int mReadLinefeedCode = LINEFEED_CODE_LF;
    public  int mWriteLinefeedCode = LINEFEED_CODE_LF;

    public  PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B300; //胞率
    public  PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8; //Date 長度 判斷是八或七
    public  PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE; //檢查碼
    public  PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1; //停止位元
    public  PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;

    public static final String ACTION_USB_PERMISSION = "com.prolific.pl2303hxdsimpletest.USB_PERMISSION";

    String TAG = "PL2303HXD_APLog";

    Timer Refresh , interval; //刷新，間隔時間
    int time = 0; //刷新時間
    int delaytime = 0; //延遲時間
    int sendtime = 0; //傳輸間距
}
