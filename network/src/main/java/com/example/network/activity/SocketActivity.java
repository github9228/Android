package com.example.network.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.example.network.R;
import com.example.network.thread.MessageTransmit;
import com.example.network.util.DateUtil;

public class SocketActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "SocketActivity";
    private EditText et_socket;
    private static TextView tv_socket;
    private MessageTransmit mTransmit;      //消息传输对象


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        et_socket = findViewById(R.id.et_socket);
        tv_socket = findViewById(R.id.tv_socket);
        mTransmit = new MessageTransmit();      //创建消息传输对象
        findViewById(R.id.btn_socket).setOnClickListener(this);
        new Thread(mTransmit).start();      //启动消息传输的线程
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_socket) {
            //获得一个默认的消息对象
            Message msg = Message.obtain();
            Log.d(TAG, "msg" + et_socket.getText().toString());
            msg.obj = et_socket.getText().toString();
            //通过线程的发送处理器，向后端发送消息
            mTransmit.mSendHandler.sendMessage(msg);
        }
    }

    //创建一个主线程的接收处理器，专门处理服务器发来的消息
    public static Handler mHandler = new Handler() {
        //在收到消息时触发
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: " + msg.obj);
            if (tv_socket != null) {
                String desc = String.format("%s 收到服务器的应答消息：%s", DateUtil.getNowTime(), msg.obj.toString());
                tv_socket.setText(desc);
            }
        }
    };
}
