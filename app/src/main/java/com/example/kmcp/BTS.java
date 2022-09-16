package com.example.kmcp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BTS extends Thread{

    //Bluetooth 관련
    public static BluetoothAdapter mBluetoothAdapter;
    public static Set<BluetoothDevice> mPairedDevices;
    public static List<String> mListPairedDevices;
    public static Handler mBluetoothHandler;
    public static BluetoothDevice mBluetoothDevice;
    public static BluetoothSocket mBluetoothSocket;
    public static Context context;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static BluetoothSocket mmSocket;
    public static InputStream mmInStream;
    public static OutputStream mmOutStream;

    public static BTS bts = null;


    public void BluetoothThread(BluetoothSocket socket) {   // 데이터 전송 및 수신하는 길을 만들어주는 작업
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch(IOException e) {
            Toast.makeText(context,"소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }


    public void run() {     // 수신받은 데이터는 언제 들어올지 모르니 항상 확인해야한다. 그에 따라 while 반복문 처리로 데이터가 존재한다면 데이터를 읽어오는 작업을 해준다.
        byte[] buffer = new byte[1024]; //버퍼의 크기를 설정하시오
        int bytes;

        while(true) {
            try {
                //bytes = mmInStream.read(buffer);
                bytes = mmInStream.available();
                if(bytes != 0) {
                    SystemClock.sleep(100);
                    bytes = mmInStream.available();
                    bytes = mmInStream.read(buffer, 0, bytes);
                    //bts.getInstance().mBluetoothHandler.obtainMessage(bts.getInstance().BT_MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                    BTS.getInstance().mBluetoothHandler.obtainMessage(BTS.getInstance().BT_MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                }
            }catch (IOException e) {
                //Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    public void write(String str) {
        byte[] bytes = str.getBytes();
        try {
            mmOutStream.write(bytes);
        }catch (IOException e) {
            Toast.makeText(context,"데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel() {
        try{
            mmSocket.close();

            BTS.getInstance();

        } catch (IOException e) {
            Toast.makeText(context,"소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    // Constructor
    BTS(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mPairedDevices = mBluetoothAdapter.getBondedDevices();

        mListPairedDevices = new ArrayList<>();
        for (BluetoothDevice device : mPairedDevices) {
            mListPairedDevices.add(device.getName());
        }
    }


    public static synchronized BTS getInstance() {
        if(bts==null){
            bts = new BTS();
        }
        return bts;
    }


}
