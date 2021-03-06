package cn.aura.feimayun.vhall.chat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vhall.business.ChatServer;
import com.vhall.business.VhallSDK;
import com.vhall.business.data.UserInfo;
import com.vhall.business.data.source.UserInfoDataSource;

import java.util.ArrayList;
import java.util.List;

import cn.aura.feimayun.R;
import cn.aura.feimayun.application.MyApplication;
import cn.aura.feimayun.util.Util;
import cn.aura.feimayun.vhall.util.VhallUtil;
import cn.aura.feimayun.vhall.util.emoji.EmojiUtils;
import cn.aura.feimayun.vhall.watch.WatchActivity;

import static android.app.Activity.RESULT_OK;

/**
 * 聊天页的Fragment
 */
public class ChatFragment extends Fragment implements ChatContract.ChatView {

    public static final int CHAT_EVENT_CHAT = 1;
    //    public static final int CHAT_EVENT_QUESTION = 2;

    public final int RequestLogin = 0;
    ListView lv_chat;
    //    List<ChatServer.ChatInfo> chatData = new ArrayList<>();
    List<MessageChatData> chatInfoList = new ArrayList<>();
    List<ChatServer.ChatInfo> questionData = new ArrayList<ChatServer.ChatInfo>();
    ChatAdapter chatAdapter = new ChatAdapter();
    //    QuestionAdapter questionAdapter = new QuestionAdapter();
    boolean isquestion = false;
    int status = -1;
    TextView test_send_custom;
    private String vhall_account = "1";
    private ChatContract.ChatPresenter mPresenter;
    private WatchActivity mActivity;
    private ImageView iv_emoji;
    private TextView text_chat_content;
    private boolean isVisiable = false;

    public static ChatFragment newInstance(int status, boolean isquestion, String vhall_account) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("question", isquestion);
        bundle.putInt("state", status);
        bundle.putString("vhall_account", vhall_account);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (WatchActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vhall_chat_fragment, null);
        lv_chat = view.findViewById(R.id.lv_chat);
        test_send_custom = view.findViewById(R.id.test_send_custom);
        iv_emoji = view.findViewById(R.id.iv_emoji);
        text_chat_content = view.findViewById(R.id.text_chat_content);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        test_send_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VhallSDK.isLogin()) {
                    Toast.makeText(mActivity, "请输入信息", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, R.string.vhall_login_first, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        isquestion = getArguments().getBoolean("question");
        if (getArguments() != null) {
            status = getArguments().getInt("state");
            vhall_account = getArguments().getString("vhall_account");
        }

        iv_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!VhallSDK.isLogin()) {
                    //登录聊天账号
                    String uid = Util.getUid();
                    if (!uid.equals("")) {
                        //登录微吼账号，用于聊天
                        String username = vhall_account.equals("1") ? "wxh" + uid : "sch" + uid;
                        String userpass = "1q2w3e4r5t6y7u8i9o";
                        VhallSDK.login(username, userpass, new UserInfoDataSource.UserInfoCallback() {
                            @Override
                            public void onSuccess(UserInfo userInfo) {
//                                Toast.makeText(MyApplication.context, "登录聊天服务器成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int errorCode, String reason) {
//                                Toast.makeText(MyApplication.context, "登录聊天服务器失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                mPresenter.showChatView(true, null, 0);
            }
        });
        text_chat_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!VhallSDK.isLogin()) {
                    //登录聊天账号
                    String uid = Util.getUid();
                    if (!uid.equals("")) {
                        //登录微吼账号，用于聊天
                        String username = vhall_account.equals("1") ? "wxh" + uid : "sch" + uid;
                        String userpass = "1q2w3e4r5t6y7u8i9o";

                        VhallSDK.login(username, userpass, new UserInfoDataSource.UserInfoCallback() {
                            @Override
                            public void onSuccess(UserInfo userInfo) {
//                                Toast.makeText(MyApplication.context, "登录聊天服务器成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int errorCode, String reason) {
//                                Toast.makeText(MyApplication.context, "登录聊天服务器失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                mPresenter.showChatView(false, null, 0);
            }
        });

        if (!isquestion) {
            lv_chat.setAdapter(chatAdapter);
//            lv_chat.setAdapter(questionAdapter);
        }
//        else {
//            lv_chat.setAdapter(chatAdapter);
//        }
    }

    private static final String TAG = "ChatFragment";

    @Override
    public void notifyDataChangedChat(MessageChatData data) {
        Log.d(TAG, "notifyDataChangedChat: 1");
        if (MyApplication.APP_STATUS == MyApplication.APP_STATUS_NORMAL) {
            if (isVisiable) {
                if (chatInfoList.size() > 10)
                    chatInfoList.remove(0);
                chatInfoList.add(data);
                if (!isquestion) {
                    chatAdapter.notifyDataSetChanged();
//            questionAdapter.notifyDataSetChanged();
                }
//                else {
//                    chatAdapter.notifyDataSetChanged();
//                }
            }
        }
    }

    @Override
    public void notifyDataChangedChat(int type, List<MessageChatData> list) {
        Log.d(TAG, "notifyDataChangedChat: 2");
        chatInfoList.addAll(list);
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDataChangedQe(ChatServer.ChatInfo data) {
//        if (questionData.size() > 10) {
//            questionData.remove(0);
//        }
//        questionData.add(data);
//        questionAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDataChangedQe(int type, List<ChatServer.ChatInfo> list) {
//        if (questionData.size() > 10) {
//            questionData.remove(0);
//        }
//        questionData.addAll(list);
//        questionAdapter.notifyDataSetChanged();
    }


    @Override
    public void showToast(String content) {
        if (this.isAdded())
            Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearChatData() {
        if (questionData != null) {
            questionData.clear();
        }
        if (chatInfoList != null) {
            chatInfoList.clear();
        }
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    @Override
    public void performSend(String content, int chatEvent) {
        switch (status) {
            case VhallUtil.BROADCAST://直播界面只能发聊天
                mPresenter.sendChat(content);
                break;
            case VhallUtil.WATCH_LIVE://观看直播界面发聊天和问答
                if (chatEvent == ChatFragment.CHAT_EVENT_CHAT) {
                    mPresenter.sendChat(content);
                }
//                else if (chatEvent == ChatFragment.CHAT_EVENT_QUESTION) {
//                    mPresenter.sendQuestion(content);
//                }
                break;
            case VhallUtil.WATCH_PLAYBACK://回放界面只能发评论(发评论必须保证登陆)
                mPresenter.sendChat(content);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RequestLogin == requestCode) {
            if (resultCode == RESULT_OK) {
                mPresenter.onLoginReturn();
            }
        }
    }

    @Override
    public void setPresenter(ChatContract.ChatPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatInfoList != null) {
            chatInfoList.clear();
            chatAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        isVisiable = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isVisiable = false;
    }

    //    class QuestionAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return chatData.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return chatData.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            Holder viewHolder;
//            if (convertView == null) {
//                convertView = View.inflate(getActivity(), R.layout.chat_question_item, null);
//                viewHolder = new Holder();
//                viewHolder.iv_question_avatar = convertView.findViewById(R.id.iv_question_avatar);
//                viewHolder.tv_question_content = convertView.findViewById(R.id.tv_question_content);
//                viewHolder.tv_question_name = (TextView) convertView.findViewById(R.id.tv_question_name);
//                viewHolder.tv_question_time = (TextView) convertView.findViewById(R.id.tv_question_time);
//
//                viewHolder.ll_answer = (LinearLayout) convertView.findViewById(R.id.ll_answer);
//                viewHolder.iv_answer_avatar = (ImageView) convertView.findViewById(R.id.iv_answer_avatar);
//                viewHolder.tv_answer_content = (TextView) convertView.findViewById(R.id.tv_answer_content);
//                viewHolder.tv_answer_name = (TextView) convertView.findViewById(R.id.tv_answer_name);
//                viewHolder.tv_answer_time = (TextView) convertView.findViewById(R.id.tv_answer_time);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (Holder) convertView.getTag();
//            }
//            ChatServer.ChatInfo data = chatData.get(position);
//            ChatServer.ChatInfo.QuestionData questionData = data.questionData;
//            if (questionData != null && !TextUtils.isEmpty(questionData.avatar)) {
//                Glide.with(getActivity()).load(questionData.avatar).placeholder(R.drawable.icon_vhall).into(viewHolder.iv_question_avatar);
//            }
//            //TODO 头像设置
//            viewHolder.tv_question_name.setText(questionData.nick_name);
//            viewHolder.tv_question_time.setText(questionData.created_at);
//            viewHolder.tv_question_content.setText(EmojiUtils.getEmojiText(mActivity, questionData.content), TextView.BufferType.SPANNABLE);
//            if (questionData.answer != null) {
//                viewHolder.ll_answer.setVisibility(View.VISIBLE);
//                viewHolder.tv_answer_content.setText(EmojiUtils.getEmojiText(mActivity, questionData.answer.content), TextView.BufferType.SPANNABLE);
//                viewHolder.tv_answer_name.setText(questionData.answer.nick_name);
//                viewHolder.tv_answer_time.setText(questionData.answer.created_at);
//                Glide.with(getActivity()).load(questionData.answer.avatar).placeholder(R.drawable.icon_vhall).into(viewHolder.iv_answer_avatar);
//                Glide.with(getActivity()).load(questionData.avatar).placeholder(R.drawable.icon_vhall).into(viewHolder.iv_question_avatar);
//            } else {
//                Glide.with(getActivity()).load(data.avatar).placeholder(R.drawable.icon_vhall).into(viewHolder.iv_question_avatar);
//                viewHolder.ll_answer.setVisibility(View.GONE);
//            }
//            return convertView;
//        }
//    }

    static class ViewHolder {
        ImageView iv_chat_avatar;
        TextView tv_chat_content;
        TextView tv_chat_name;
        TextView tv_chat_time;
    }

//    static class ChatSurveyHolder {
//        TextView tv_join;
//    }

//    static class Holder {
//        ImageView iv_question_avatar;
//        TextView tv_question_content;
//        TextView tv_question_time;
//        TextView tv_question_name;
//
//        LinearLayout ll_answer;
//        ImageView iv_answer_avatar;
//        TextView tv_answer_content;
//        TextView tv_answer_time;
//        TextView tv_answer_name;
//    }

    //    class TestRunnable implements Runnable {
//
//        @Override
//        public void run() {
//            while (flag) {
//                try {
//                    Thread.sleep(500);
//                    mPresenter.sendChat("lalala");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
    public static final int CHAT_NORMAL = 0x00;
    public static final int CHAT_SURVEY = 0x01;

    class ChatAdapter extends BaseAdapter {

        @Override
        public int getItemViewType(int position) {
            if ("survey".equals(chatInfoList.get(position).event)) {
                return CHAT_SURVEY;
            } else {
                return CHAT_NORMAL;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return chatInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return chatInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            ChatSurveyHolder surveyHolder;
            final MessageChatData data = chatInfoList.get(position);
            switch (getItemViewType(position)) {
                case CHAT_NORMAL:
                    if (convertView == null) {
                        convertView = View.inflate(getActivity(), R.layout.vhall_chat_item, null);
                        viewHolder = new ViewHolder();
                        viewHolder.iv_chat_avatar = (ImageView) convertView.findViewById(R.id.iv_chat_avatar);
                        viewHolder.tv_chat_content = (TextView) convertView.findViewById(R.id.tv_chat_content);
                        viewHolder.tv_chat_name = (TextView) convertView.findViewById(R.id.tv_chat_name);
                        viewHolder.tv_chat_time = (TextView) convertView.findViewById(R.id.tv_chat_time);
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolder) convertView.getTag();
                    }

                    String avatar = data.getAvatar();
                    if (!TextUtils.isEmpty(avatar) && !avatar.startsWith("http")) {
                        avatar = String.format("http:%s", avatar);
                    }
                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.live_userimg);
                    if (Util.isOnMainThread()) {
                        Glide.with(MyApplication.context).load(avatar).apply(options).into(viewHolder.iv_chat_avatar);
                    }
                    switch (data.event) {
                        case MessageChatData.eventMsgKey:
                            if (data.getType().equals("image")) {
                                if (!TextUtils.isEmpty(data.getImage_url())) {
                                    viewHolder.tv_chat_content.setText(String.format("收到图片---%s", data.getImage_url()));
                                } else if (data.getImage_urls() != null) {
                                    viewHolder.tv_chat_content.setText(String.format("收到%d张图片", data.getImage_urls().size()));
                                }
                            } else {
                                viewHolder.tv_chat_content.setText(EmojiUtils.getEmojiText(mActivity, data.getText_content()), TextView.BufferType.SPANNABLE);
                            }
                            viewHolder.tv_chat_content.setVisibility(View.VISIBLE);
                            viewHolder.tv_chat_name.setText(data.getNickname());
                            break;
                        case MessageChatData.eventCustomKey:
                            viewHolder.tv_chat_content.setVisibility(View.VISIBLE);
                            viewHolder.tv_chat_content.setText(EmojiUtils.getEmojiText(mActivity, data.getText_content()), TextView.BufferType.SPANNABLE);
                            viewHolder.tv_chat_name.setText(data.getNickname());
                            break;
                        case MessageChatData.eventOnlineKey:
                            viewHolder.tv_chat_name.setText(String.format("%s上线了！", data.getNickname()));
                            viewHolder.tv_chat_content.setVisibility(View.INVISIBLE);
                            break;
                        case MessageChatData.eventOfflineKey:
                            viewHolder.tv_chat_name.setText(String.format("%s下线了！", data.getNickname()));
                            viewHolder.tv_chat_content.setVisibility(View.INVISIBLE);
                            break;
                        default:
                            break;
                    }
                    viewHolder.tv_chat_time.setText(data.getTime());
                    break;
                case CHAT_SURVEY:
//                    if (convertView == null) {
//                        convertView = View.inflate(getActivity(), R.layout.chat_item_survey, null);
//                        surveyHolder = new ChatSurveyHolder();
//                        surveyHolder.tv_join = (TextView) convertView.findViewById(R.id.tv_join);
//                        convertView.setTag(surveyHolder);
//                    } else {
//                        surveyHolder = (ChatSurveyHolder) convertView.getTag();
//                    }
//                    surveyHolder.tv_join.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (TextUtils.isEmpty(data.getUrl())) {
//                                mPresenter.showSurvey(data.getId());
//                            } else {
//                                mPresenter.showSurvey(data.getUrl(), "");
//                            }
//                        }
//                    });
                    break;
                default:
                    break;
            }
            return convertView;
        }
    }

    static class ChatSurveyHolder {
        TextView tv_join;
    }
}

