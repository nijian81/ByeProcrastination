package com.jinlinkeji.byetuo.main;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.jauker.widget.BadgeView;
import com.jinlinkeji.byetuo.R;
import com.jinlinkeji.byetuo.utils.AnyEventType;
import com.jinlinkeji.byetuo.utils.App;
import com.jinlinkeji.byetuo.utils.ContactItem;
import com.jinlinkeji.byetuo.utils.MessageHandler;
import com.jinlinkeji.byetuo.utils.RoundedImageView;
import com.jinlinkeji.byetuo.utils.StatusUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Handler;

import java.util.logging.LogRecord;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
//import com.avos.avoscloud.AVObject;

public class ContactsActivity extends Activity implements View.OnClickListener {

    public EventBus eventBus;
    public Handler handler = new Handler();
    private SwipeRefreshLayout swipeRefreshLayout;
    private static int showNum;
    private Intent intent;
    private ImageView back, set;
    long timeSwapBuff = 0L;
    private List<AVIMConversation> list;
    private List<ContactItem> listContact;
    private MyAdapter myAdapter;
    private ListView listView;
    private ProgressDialog progressDialog;
    private int i;
    private String conversation_id;
    private EditText search;
    private PopDialog popDialog;
    //设置是否允许消息推送、定位、联系人提醒等功能，“1”代表允许，“0”代表禁止
    private String isAllowLocation, isAllowMessage, isAllowRemind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        EventBus.getDefault().register(this);
        intent = getIntent();
        timeSwapBuff = intent.getLongExtra("timeSwapBuff", 0);
        conversation_id = intent.getStringExtra("conversation_id");
        search = (EditText) findViewById(R.id.search);
        // 实现搜索框改变监听器
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                if (s.toString().length() >= 0) {
                    filterData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        set = (ImageView) findViewById(R.id.set);
        set.setOnClickListener(this);
        list = new ArrayList<>();
        listContact = new ArrayList<>();
        myAdapter = new MyAdapter();
        myAdapter.setList(list);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter);
        progressDialog =
                ProgressDialog.show(this, "", "数据加载中，请稍后...", true);
        progressDialog.setCancelable(true);
        new RemoteDataTask().execute();
    }

    // 输入字符过滤药品监视器 尼见 2015-03-10
    private void filterData(String filterStr) {
        List<AVIMConversation> filterDateList = new ArrayList<>();
        String upper = filterStr.toUpperCase();
        if (TextUtils.isEmpty(upper)) {
            filterDateList = list;
            myAdapter.setList(filterDateList);
            myAdapter.notifyDataSetChanged();
        } else {
            filterDateList.clear();
            for (AVIMConversation avimConversation : list) {
                String otherId;
                if (avimConversation.getMembers().get(0).equals(App.getInstance().getDeviceId())) {
                    otherId = avimConversation.getMembers().get(1);
                } else {
                    otherId = avimConversation.getMembers().get(0);
                }
                if (otherId.contains(upper)
                        || otherId.contains(filterStr)) {
                    filterDateList.add(avimConversation);
                }
            }
            myAdapter.setList(filterDateList);
            myAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {

        // 设置ListView
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showNum = 0;
                new RemoteDataTask().execute();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        myAdapter.setList(list);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AVIMConversation avObject = (AVIMConversation) adapterView.getItemAtPosition(position);
                intent = new Intent();
                intent.setAction("android.intent.action.Chat");
                intent.putExtra("conversation_id", avObject.getConversationId());
                intent.putExtra("timeSwapBuff", timeSwapBuff);
                startActivity(intent);
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set:
                progressDialog =
                        ProgressDialog.show(this, "", "数据加载中，请稍后...", true);
                AVQuery<AVUser> query = AVUser.getQuery();
                query.whereEqualTo("username", App.deviceId);
                query.findInBackground(new FindCallback<AVUser>() {
                    public void done(List<AVUser> avObjects, AVException e) {
                        if (e == null) {
                            Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                            progressDialog.dismiss();
                            isAllowLocation = avObjects.get(0).get("isAllowLocation").toString();
                            isAllowMessage = avObjects.get(0).get("isAllowMessage").toString();
                            isAllowRemind = avObjects.get(0).get("isAllowRemind").toString();
                            popDialog = new PopDialog(ContactsActivity.this, R.style.PauseDialog);
                            popDialog.setTitle(null);
                            popDialog.show();
                            WindowManager.LayoutParams lp = popDialog.getWindow().getAttributes();
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT; //设置宽度
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.gravity = Gravity.BOTTOM;
                            popDialog.getWindow().setAttributes(lp);
                        } else {
                            Log.d("失败", "查询错误: " + e.getMessage());
                        }
                    }
                });

                break;
            case R.id.back:
                intent = new Intent();
                intent.setAction("android.intent.action.Chat");
                intent.putExtra("timeSwapBuff", timeSwapBuff);
                intent.putExtra("conversation_id", conversation_id);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
        }
    }

    //会话列表适配器
    public class MyAdapter extends BaseAdapter {

        private int messageNum[] = new int[100];
        List<AVIMConversation> list;
        private Context context;

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int index) {
            return list.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {

            view = LayoutInflater.from(ContactsActivity.this).inflate(R.layout.contact_item, null);

            final RoundedImageView photo = (RoundedImageView) view.findViewById(R.id.photo);
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ContactsActivity.this));
            ImageLoader.getInstance().displayImage("http://ac-mrq9g7oc.clouddn.com/DOER6FTVED3ADZB1Pey4VhuxkhTbOSGiaKF3g4wk", photo, StatusUtils.normalImageOptions);
            TextView name = (TextView) view.findViewById(R.id.name);
            final TextView content = (TextView) view.findViewById(R.id.content);
            final TextView time = (TextView) view.findViewById(R.id.time);
            String otherId = null;
            if (this.list.get(position).getMembers().get(0).equals(App.getInstance().getDeviceId())) {
                otherId = this.list.get(position).getMembers().get(1);
            } else {
                otherId = this.list.get(position).getMembers().get(0);
            }
            name.setText(otherId);
            list.get(position).queryMessages(100000, new AVIMMessagesQueryCallback() {
                @Override
                public void done(List<AVIMMessage> list, AVIMException e) {
                    if (e == null) {
                        if (messageNum[position] != 0) {
                            BadgeView badgeView = new BadgeView(ContactsActivity.this);
                            badgeView.setTargetView(photo);
                            badgeView.setBadgeCount(list.size() - messageNum[position]);
                        }
                        String a = ((AVIMTextMessage) list.get(list.size() - 1)).getText();
                        content.setText(a);
                        Date date = new Date(list.get(list.size() - 1).getTimestamp());
                        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
                        String time_param = format.format(date);
                        time.setText(time_param);
                        if (messageNum[position] == 0) {
                            messageNum[position] = list.size();
                        }
                    }
                }
            });

            return view;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setList(List<AVIMConversation> list) {
            this.list = list;
        }
    }

    //在工作线程中刷新会话列表
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            findAVObjects();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            // 设置初期数据
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
            myAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    AVIMConversation avObject = (AVIMConversation) adapterView.getItemAtPosition(position);
                    intent = new Intent();
                    intent.setAction("android.intent.action.Chat");
                    intent.putExtra("conversation_id", avObject.getConversationId());
                    intent.putExtra("timeSwapBuff", timeSwapBuff);
                    startActivity(intent);
                }
            });
        }
    }

    //查询和本人相关的会话记录
    public void findAVObjects() {
        // 搜索 Tom 参与的所有群组对话
        List<String> clients = new ArrayList<String>();
        clients.add(App.getInstance().getDeviceId());
        //Todo
        //这个的ID应该是传入的本人的ID
        AVIMClient imClient = AVIMClient.getInstance(App.getInstance().deviceId);
        final AVIMConversationQuery conversationQuery = imClient.getQuery();
//        conversationQuery.containsMembers(clients);
        conversationQuery.whereContains("m",App.getInstance().getDeviceId());
        // 之前有常量定义：
        // const int ConversationType_OneOne = 0;
        // const int ConversationType_Group = 1;
        conversationQuery.whereEqualTo("attr.type", 0);
        //返回 20 个结果，意思是返回符合要求的会话个数
        conversationQuery.setLimit(100);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> conversations, AVIMException e) {
                if (null != e) {
                    // 出错了。。。
                    e.printStackTrace();
                } else {
                    if (null != conversations) {
                        //查询结果正确，自己定义自己的逻辑
                        list = conversations;
                        myAdapter.setList(list);
                    } else {

                    }
                }
            }
        });
    }

    //定制弹出对话框的内容
    public class PopDialog extends Dialog implements View.OnClickListener {

        private RelativeLayout one, two, three, cancel;
        private TextView setLocation, setMessage, setRemind;

        public PopDialog(Context context) {
            super(context);
        }

        public PopDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_item);

            setLocation = (TextView) findViewById(R.id.setLocation);
            setMessage = (TextView) findViewById(R.id.setMessage);
            setRemind = (TextView) findViewById(R.id.setRemind);
            if (isAllowLocation.equals("1")) {
                setLocation.setText("别人不可见我的定位");
            } else {
                setLocation.setText("别人可见我的定位");
            }
            if (isAllowMessage.equals("1")) {
                setMessage.setText("关闭消息提醒");
            } else {
                setMessage.setText("打开消息提醒");
            }
            if (isAllowRemind.equals("1")) {
                setRemind.setText("关闭联系人提醒");
            } else {
                setRemind.setText("打开联系人提醒");
            }
            one = (RelativeLayout) findViewById(R.id.one);
            one.setOnClickListener(this);
            two = (RelativeLayout) findViewById(R.id.two);
            two.setOnClickListener(this);
            three = (RelativeLayout) findViewById(R.id.three);
            three.setOnClickListener(this);
            cancel = (RelativeLayout) findViewById(R.id.cancel);
            cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.one:
                    progressDialog =
                            ProgressDialog.show(ContactsActivity.this, "", "数据加载中，请稍后...", true);
                    AVQuery<AVUser> query = AVUser.getQuery();
                    query.whereEqualTo("username", App.deviceId);
                    query.findInBackground(new FindCallback<AVUser>() {
                        public void done(List<AVUser> avObjects, AVException e) {
                            if (e == null) {
                                Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                                if (avObjects.get(0).get("isAllowLocation").toString().equals("1")) {
                                    avObjects.get(0).put("isAllowLocation", "0");
                                    avObjects.get(0).saveInBackground();
                                } else {
                                    avObjects.get(0).put("isAllowLocation", "1");
                                    avObjects.get(0).saveInBackground();
                                }
                                progressDialog.dismiss();
                                dismiss();
                            } else {
                                Log.d("失败", "查询错误: " + e.getMessage());
                            }
                        }
                    });
                    break;
                case R.id.two:
                    progressDialog =
                            ProgressDialog.show(ContactsActivity.this, "", "数据加载中，请稍后...", true);
                    AVQuery<AVUser> query1 = AVUser.getQuery();
                    query1.whereEqualTo("username", App.deviceId);
                    query1.findInBackground(new FindCallback<AVUser>() {
                        public void done(List<AVUser> avObjects, AVException e) {
                            if (e == null) {
                                Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                                if (avObjects.get(0).get("isAllowMessage").toString().equals("1")) {
                                    avObjects.get(0).put("isAllowMessage", "0");
                                    avObjects.get(0).saveInBackground();
                                } else {
                                    avObjects.get(0).put("isAllowMessage", "1");
                                    avObjects.get(0).saveInBackground();
                                }
                                progressDialog.dismiss();
                                dismiss();
                            } else {
                                Log.d("失败", "查询错误: " + e.getMessage());
                            }
                        }
                    });
                    break;
                case R.id.three:
                    progressDialog =
                            ProgressDialog.show(ContactsActivity.this, "", "数据加载中，请稍后...", true);
                    AVQuery<AVUser> query2 = AVUser.getQuery();
                    query2.whereEqualTo("username", App.deviceId);
                    query2.findInBackground(new FindCallback<AVUser>() {
                        public void done(List<AVUser> avObjects, AVException e) {
                            if (e == null) {
                                Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                                if (avObjects.get(0).get("isAllowRemind").toString().equals("1")) {
                                    avObjects.get(0).put("isAllowRemind", "0");
                                    avObjects.get(0).saveInBackground();
                                } else {
                                    avObjects.get(0).put("isAllowRemind", "1");
                                    avObjects.get(0).saveInBackground();
                                }
                                progressDialog.dismiss();
                                dismiss();
                            } else {
                                Log.d("失败", "查询错误: " + e.getMessage());
                            }
                        }
                    });
                    break;
                case R.id.cancel:
                    dismiss();
                    break;
            }
        }
    }

    //用于消息处理Handler收到新消息时，动态刷新联系人页面，添加小红点用的。
    //后台服务器存储了当前用户的操作请求，默认是允许IM通信的，如果用户设置了拒绝IM通信的话，
    //则不会收到新消息通知，这个通过后台服务器可以查询当前用户的设置
    public void onEventMainThread(AnyEventType event) {
        //这里先查询后台服务器看用户的设置是否允许收到消息通知
        AVQuery<AVUser> query = AVUser.getQuery();
        query.whereEqualTo("username", App.deviceId);
        query.findInBackground(new FindCallback<AVUser>() {
            public void done(List<AVUser> objects, AVException e) {
                if (e == null) {
                    // 查询成功
                    //如何用户设置禁止提醒，则不会提醒
                    if (objects.get(0).get("isAllowMessage").toString().equals("0") || objects.get(0).get("isAllowRemind").toString().equals("0")) {

                    } else {
                        Toast.makeText(ContactsActivity.this, "您收到新消息了哦~", Toast.LENGTH_LONG).show();
                        myAdapter.notifyDataSetChanged();
                    }
                } else {
                    // 查询出错
                }
            }
        });
    }

}
