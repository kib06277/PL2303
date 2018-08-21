;
;   Android SDK for PL2303HXD / PL2303RA / PL2303 EA / PL2303SA
;
;   Copyright (c) 2013-2017, Prolific Technology Inc.


==================================================
Files Description
==================================================

1. PL2303 Sample Test Android App Software  (ap_PL2303HXDSimpleTest.apk)
	- This is demo application to detect PL2303 device in Android and do simple test.	
	- �i�H�����bAndroid�W���檺App
	- For Android OS v3.2 and above.
	- Demo APP v1.0.0.1.

2. PL2303 Sample Test Android App Source Code 
	- Source code for ap_PL2303HXDSimpleTest.apk
	- �ӥ�SampleCode��DemoAP����l�X
	- AppSampleCode: v1.0.0.0.33

3. PL2303 Android Java Driver Library  (pl2303driver.jar)
	- lib: JAR file. Android App development need to import this file.
	- lib: ���@��JAR�ɮ�,  �}�oAndroid App�ݭnimport�ӥ�JAR�ɮ�.
	- JAR file v1.0.0.0

4. PL2303 Android App Development Reference Document  
	- doc: Reference document for writing Android Application Software (index.html)
	- doc: �}�oAndroid App�ݭn�ѦҸӥ�doc���, ��_�l�ɮ׬�index.html
	- document v1.0.0.0. 

5. PL2303 Android USB Host Demo AP User Manual 
	- User's Guide for running the above sample program.

6. PL2303HXD Android Host Compatibility List  
	- Compatible Android host devices tested. (not updated anymore)



Revision History:
==================================================
Date: 05/12/2017
==================================================
SDK: v1.0.0.15

1 port Lib: v2.0.13.35
            - Added support for PL2303SA chip 

==================================================
Date: 11/07/2016
==================================================
SDK: v1.0.0.14

1 port Lib: v2.0.12.34
            - Fixed close port issue. 

         1 port UART AP: v2.0.2.21
            -  Fixed usb keyboard issue

         8 GPIO AP: v1.0.0.3
            - Support Android 5.0

         ModemStatus AP: v1.0.0.2
            - Support Android 5.0
        

Multi-port Lib: v0.0.1.26
            -Support Fixed port function.

GPIO [2,1,0]	
0,0,1	COM1
0,1,0	COM2
0,1,1	COM3
1,0,0	COM4
1,0,1	COM5
1,1,0	COM6
1,1,1	COM7
0,0,0	COM8


         Multi-port UART AP: v0.0.0.11
            - Support Fixed port function.

==================================================
Date: 04/23/2015
==================================================
SDK: v1.0.0.13b

1 port Lib: v2.0.12.31
            - Supports Android 5.0

         1 port UART AP: v2.0.2.16
            -  no change

         8 GPIO AP: v1.0.0.3
            - Supports Android 5.0

         ModemStatus AP: v1.0.0.2
            - Supports Android 5.0
        

Multi-port Lib: v0.0.1.20
            -Supports Android 5.0

         Multi-port UART AP: v0.0.0.10
            - Supports Android 5.0

==================================================
Date: 12/09/2014
==================================================
SDK: v1.0.0.12_B1

1 port Lib: v2.0.10.29
            - Add GetSerialNumber

         1 port UART AP: v2.0.2.16
            - Add GetSerialNumber sample code

         8 GPIO AP: v1.0.0.2
            - no change

         ModemStatus AP: v1.0.0.0
            - no change
        

Multi-port Lib: v0.0.1.14
            - Add GetSerialNumber

         Multi-port UART AP: v0.0.0.9
            - no change

==================================================
Date: 09/18/2014
==================================================
SDK: v1.0.0.11

1 port Lib: v2.0.10.28
            - Modify driver version.
            - Added Modem Status

         1 port UART AP: v2.0.2.16
            - remove SetDTR method

         8 GPIO AP: v1.0.0.2
            - no change

         ModemStatus AP: v1.0.0.0
            - Set DTR, Set RTS
            - Get RI, DCD, DSR, CTS..
        

Multi-port Lib: v0.0.1.14
            - Supports up to 10 devices
            - Added Modem Status

         Multi-port UART AP: v0.0.0.9
            - no change

==================================================
Date: 02/12/2014
==================================================
SDK: v1.0.0.10

1 port Lib: v2.0.10.23
            - Modify driver version.

         1 port UART AP: v2.0.2.15
            - no change

         8 GPIO AP: v1.0.0.2
            - no change


Multi-port Lib: v0.0.1.13
            - Support RS485

         Multi-port UART AP: v0.0.0.9
            - no change

==================================================
Date: 12/10/2013
==================================================
SDK: v1.0.0.9

1 port Lib: v2.0.9.22
            - no change

         1 port UART AP: v2.0.2.15
            - no change

         8 GPIO AP: v1.0.0.2
            - no change


Multi-port Lib: v0.0.0.12
            - Fixed PL2303HXD device number issue

         Multi-port UART AP: v0.0.0.9
            - no change
         

==================================================
Date: 11/12/2013
==================================================
SDK: v1.0.0.8

1 port Lib: v2.0.9.22
            - Add PL2303Device_SetCommTimeouts and PL2303Device_GetCommTimeouts.

         1 port UART AP: v2.0.2.15
            - no change

         8 GPIO AP: v1.0.0.2
            - no change


Multi-port Lib: v0.0.0.11
            - Add PL2303Device_SetCommTimeouts and PL2303Device_GetCommTimeouts.
            - Add 8 GPIO

         Multi-port UART AP: v0.0.0.9
            - no change
         

==================================================
Date: 10/29/2013
==================================================
SDK: v1.0.0.7
UART AP: v2.0.2.15
GPIO AP: v1.0.0.2
Lib: v2.0.9.21
1. remove set VID_PID API.
2. Support 8 GPIO.
3. Detecting whether this PL2303 supports this Android OS, only PL2303HXD / PL2303EA / RL2303RA are supported. 
4. Detecting whether this PL2303 devices has permission
5. Detecting whether this Android OS supports USB host API feature


==================================================
Date: 05/13/2013
==================================================
SDK: v1.0.0.6
1. Support VID_PID 
   1.1 ALRT (067B/AAA5)
   1.2 ATEN (0557/2008)
   1.3 YCCable/BestBuy (05AD/0FBA)
2. Modify Android 4.2 USB host API issue

==================================================
Date: 04/1/2013
==================================================
SDK: v1.0.0.5
1. Add set VID_PID

==================================================
Date: 03/21/2013
==================================================
SDK: v1.0.0.4
1. Add loopback function(���b)


==================================================
Date: 03/21/2013
==================================================
SDK: v1.0.0.3
1. Add loopback function.

==================================================
Date: 02/19/2013
==================================================
SDK: v1.0.0.2
Lib: v2.0.2.8
1. Support 4k Buffer size.
2. Suppor WriteTimoutConstant.

AP: v1.0.0.2
1. Modify the sample code flow.


==================================================
Date: 01/18/2013
==================================================
SDK: v1.0.0.1
Lib: v1.0.0.1
1. Support RS485.
2. Support ReadTimeoutConstant

AP: v1.0.0.1
1. Add pop-up function.



DemoAP�����դ�k�p�U:
---------------------
(1) ���դ�k1��loopback����:
1. ��Android ����(�p�غӪ��ܧΪ���3)�[�W�@��PL2303HXD��USB to RS232 cable.
2. �NRS232�ݪ�Tx�MRx�u��.
3. ���}DemoAP��, �bWrite���s���䪺��J��r���V�W���N���r�άO���r�ꡨ��,���UWrite���s.
4. ���۫��URead���s�Y�i�ݨ���ҺV�J�� ���r���άO���r�ꡨ.

(2) ���դ�k2����Ǵ���:
1. ��Android ����(�p�غӪ��ܧΪ���3)�[�W�@��PL2303HXD��USB to RS232 cable.
2. RS232�ݳz�Lnull modem cable �s��PC�W��COM port.
3. �bAndroid OS�W���}DemoAP, �bPC OS�W���}�W�Ų׺ݾ�.
4. Android DemoAP�i�H�]�w9600,19200,115200 bps(�p�n�[baud rate, �Цۤv����doc����k�[�N�i�H�F).
5. ���۶i��Android��ƪ�Read �άOWrite ��t�@��PC��COM port��Ǵ���.

 

========================================
Prolific Technology Inc.
http://www.prolific.com.tw


