package com.jinlinkeji.byetuo.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.jinlinkeji.byetuo.R;
import com.jinlinkeji.byetuo.utils.App;
import com.jinlinkeji.byetuo.utils.BaseActivity;
import com.jinlinkeji.byetuo.utils.MessageHandler;
import com.jinlinkeji.byetuo.utils.RoundedImageView;
import com.jinlinkeji.byetuo.utils.StatusUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangxiaobo on 15/4/16.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, AbsListView.OnScrollListener {

    private TextView userName;
    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String TAG = ChatActivity.class.getSimpleName();
    static final int PAGE_SIZE = 10;
    private AVIMConversation conversation;
    MessageAdapter adapter;
    private EditText messageEditText;
    private ListView listView;
    private ChatHandler handler;
    //Atomic   其基本的特性就是在多线程环境下，当有多个线程同时执行这些类的实例包含的方法时，
    // 具有排他性，即当某个线程进入方法，执行其中的指令时，不会被其他线程打断，而别
    // 的线程就像自旋锁一样，一直等到该方法执行完成，才由JVM从等待队列中选择一个另一个线程进入，
    // 这只是一种逻辑上的理解
    //Atomic初始值默认为false
    private AtomicBoolean isLoadingMessages = new AtomicBoolean(false);
    private int flag = 0;
    private ImageView switchKey, back,contacts;
    private Intent intent;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private String conversationId,meId,otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //TODO
        //这里需要传入当前用户的id和对方的id，用户通过id去查询双方的头像RUI
        intent = getIntent();
        meId = intent.getStringExtra("");
        otherId = intent.getStringExtra("");
        userName = (TextView)findViewById(R.id.userName);
        //TODO
        //这里可以从上个页面传递对方的用户名过来，然后在这里设置
        userName.setText("路人甲");
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        switchKey = (ImageView) findViewById(R.id.switchKey);
        switchKey.setOnClickListener(this);
        intent = getIntent();
        timeSwapBuff = intent.getLongExtra("timeSwapBuff", 0);
        conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        timerValue = (TextView) findViewById(R.id.time);
        contacts = (ImageView)findViewById(R.id.contacts);
        contacts.setOnClickListener(this);
        // init component
        listView = (ListView) findViewById(R.id.listview);
        messageEditText = (EditText) findViewById(R.id.message);
        adapter = new MessageAdapter(ChatActivity.this, App.getClientIdFromPre());
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);
        // get argument
        Log.d(TAG, "会话 id: " + conversationId);
        // register callback
        handler = new ChatHandler(adapter);
        conversation = App.getIMClient().getConversation(conversationId);
        findViewById(R.id.send).setOnClickListener(this);
        loadMessagesWhenInit();
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hour = mins / 60;
            secs = secs % 60;
            timerValue.setText("0" + hour + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MessageHandler.setActivityMessageHandler(handler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageHandler.setActivityMessageHandler(null);
    }

    private List<AVIMTypedMessage> filterMessages(List<AVIMMessage> messages) {
        List<AVIMTypedMessage> typedMessages = new ArrayList<AVIMTypedMessage>();
        for (AVIMMessage message : messages) {
            if (message instanceof AVIMTypedMessage) {
                typedMessages.add((AVIMTypedMessage) message);
            }
        }
        return typedMessages;
    }

    private void loadMessagesWhenInit() {
        if (isLoadingMessages.get()) {
            return;
        }
        isLoadingMessages.set(true);
        conversation.queryMessages(PAGE_SIZE, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
                if (filterException(e)) {
                    List<AVIMTypedMessage> typedMessages = filterMessages(list);
                    adapter.setMessageList(typedMessages);
                    adapter.notifyDataSetChanged();
                    scrollToLast();
                }
                isLoadingMessages.set(false);
            }
        });
    }

    private void loadOldMessages() {
        if (isLoadingMessages.get() || adapter.getMessageList().size() < PAGE_SIZE) {
            return;
        } else {
            isLoadingMessages.set(true);
            AVIMTypedMessage firstMsg = adapter.getMessageList().get(0);
            long time = firstMsg.getTimestamp();
            conversation.queryMessages(null, time, PAGE_SIZE, new AVIMMessagesQueryCallback() {
                @Override
                public void done(List<AVIMMessage> list, AVIMException e) {
                    if (filterException(e)) {
                        List<AVIMTypedMessage> typedMessages = filterMessages(list);
                        if (typedMessages.size() == 0) {
                            toast("无更早的消息了");
                        } else {
                            List<AVIMTypedMessage> newMessages = new ArrayList<AVIMTypedMessage>();
                            newMessages.addAll(typedMessages);
                            newMessages.addAll(adapter.getMessageList());
                            //避免载入消息时，重复载入最后一条消息
                            if(typedMessages.get(0).getContent().toString().equals(adapter.messageList.get(0).getContent().toString())){
                                adapter.setMessageList(adapter.getMessageList());
                            }else{
                                adapter.setMessageList(newMessages);
                            }
                            adapter.notifyDataSetChanged();
                            listView.setSelection(typedMessages.size() - 1);
                        }
                    }
                    isLoadingMessages.set(false);
                }
            });
        }
    }

    //收到消息时，消息列表滚动到最后一条
    private void scrollToLast() {
        listView.smoothScrollToPosition(listView.getCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVIMMessageManager.unregisterMessageHandler(AVIMTypedMessage.class, handler);
    }

    public static void startActivity(Context context, String conversationId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if(messageEditText.getText().toString().length() == 0){
                    Toast.makeText(this,"请不要发送空消息哦~",Toast.LENGTH_SHORT).show();
                }else{
                    sendText();
                }
                break;
            case R.id.switchKey:
                if (flag == 0) {
                    timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);
                    flag = 1;
                    switchKey.setImageResource(R.mipmap.begin_icon);
                } else {
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                    switchKey.setImageResource(R.mipmap.pause);
                    flag = 0;
                }
                break;
            case R.id.back:
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
                intent = new Intent();
                intent.setAction("android.intent.action.TaskProceed");
                intent.putExtra("timeSwapBuff", timeSwapBuff);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.contacts:
                intent = new Intent();
                intent.setAction("android.intent.action.Contacts");
                intent.putExtra("conversation_id",conversationId);
                intent.putExtra("timeSwapBuff", timeSwapBuff);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
        }
    }

    public void sendText() {
        final AVIMTextMessage message = new AVIMTextMessage();
        message.setText(messageEditText.getText().toString());
        conversation.sendMessage(message, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (null != e) {
                    e.printStackTrace();
                } else {
                    adapter.addMessage(message);
                    finishSend();
                }
            }
        });
    }

    public void finishSend() {
        messageEditText.setText(null);
        scrollToLast();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //view.getChildCount显示的是当前页面显示的子视图的项数
            if (view.getChildCount() > 0) {
                View first = view.getChildAt(0);
                if (first != null && view.getFirstVisiblePosition() == 0 && first.getTop() == 0) {
                    loadOldMessages();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public class ChatHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {
        private MessageAdapter adapter;

        public ChatHandler(MessageAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
            if (!(message instanceof AVIMTextMessage)) {
                return;
            }
            if (conversation.getConversationId().equals(ChatActivity.this.conversation.getConversationId())) {
                adapter.addMessage((AVIMTextMessage) message);
                scrollToLast();
            }
        }
    }

    //处理手机返回键跳转问题
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 4){
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            intent = new Intent();
            intent.setAction("android.intent.action.TaskProceed");
            intent.putExtra("timeSwapBuff", timeSwapBuff);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //消息处理适配器
    public class MessageAdapter extends BaseAdapter {

        private Context context;
        private String selfId;
        List<AVIMTypedMessage> messageList = new LinkedList<AVIMTypedMessage>();

        public MessageAdapter(Context context, String selfId) {
            this.context = context;
            this.selfId = selfId;
        }

        public void setMessageList(List<AVIMTypedMessage> messageList) {
            this.messageList = messageList;
        }

        public List<AVIMTypedMessage> getMessageList() {
            return messageList;
        }

        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Object getItem(int position) {
            return messageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RelativeLayout other, me;
            TextView message_other,message_me;
            RoundedImageView portrait_me,portrait_other;

            convertView = LayoutInflater.from(context).inflate(R.layout.message, null);
            me = (RelativeLayout) convertView.findViewById(R.id.me);
            other = (RelativeLayout) convertView.findViewById(R.id.other);
            message_me = (TextView) convertView.findViewById(R.id.message_me);
            message_other = (TextView) convertView.findViewById(R.id.message_other);
            portrait_me = (RoundedImageView)convertView.findViewById(R.id.portrait_me);
            portrait_other = (RoundedImageView)convertView.findViewById(R.id.portrait_other);
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ChatActivity.this));
            //TODO
            //第一个是自己的头像URI，第二个是对方的头像URI,可以将从intent获取的参数，在这里赋值。
            ImageLoader.getInstance().displayImage("http://ac-mrq9g7oc.clouddn.com/DOER6FTVED3ADZB1Pey4VhuxkhTbOSGiaKF3g4wk", portrait_me, StatusUtils.normalImageOptions);
            ImageLoader.getInstance().displayImage("http://ac-mrq9g7oc.clouddn.com/DOER6FTVED3ADZB1Pey4VhuxkhTbOSGiaKF3g4wk", portrait_other, StatusUtils.normalImageOptions);
            AVIMTypedMessage message = messageList.get(position);
            String text;
            if (AVIMReservedMessageType.getAVIMReservedMessageType(message.getMessageType()) == AVIMReservedMessageType.TextMessageType) {
                AVIMTextMessage textMessage = (AVIMTextMessage) message;
                text = textMessage.getText();
            } else {
                text = message.getContent();
            }
            Date date = new Date(message.getTimestamp());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
            String time = dateFormat.format(date);
            String messageText = text;
            message_me.setText(messageText);
            message_other.setText(messageText);
            if (message.getFrom().equals(selfId)) {
                other.setVisibility(View.GONE);
                me.setVisibility(View.VISIBLE);
            } else {
                me.setVisibility(View.GONE);
                other.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        public void addMessage(AVIMTextMessage message) {
            messageList.add(message);
            notifyDataSetChanged();
        }
    }

}
