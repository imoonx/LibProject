package com.imoonx.third.JPush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.imoonx.util.Log4jConfig;
import com.imoonx.util.XLog;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 处理tagalias相关的逻辑
 */
@SuppressWarnings("deprecation")
public class TagAliasOperatorHelper implements TagAliasCallback {

    public static int sequence = 1;
    /**
     * 增加
     */
    public static final int ACTION_ADD = 1;
    /**
     * 覆盖
     */
    public static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    public static final int ACTION_DELETE = 3;
    /**
     * 删除所有
     */
    public static final int ACTION_CLEAN = 4;
    /**
     * 查询
     */
    public static final int ACTION_GET = 5;

    public static final int ACTION_CHECK = 6;

    public static final int DELAY_SEND_ACTION = 1;

    private Context context;
    private int reconnectionTime;

    private static TagAliasOperatorHelper mInstance;

    private TagAliasOperatorHelper() {
    }

    public static TagAliasOperatorHelper getInstance() {
        if (mInstance == null) {
            synchronized (TagAliasOperatorHelper.class) {
                if (mInstance == null) {
                    mInstance = new TagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }

    private void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    private SparseArray<TagAliasBean> tagAliasActionCache = new SparseArray<TagAliasBean>();

    public TagAliasBean get(int sequence) {
        return tagAliasActionCache.get(sequence);
    }

    public TagAliasBean remove(int sequence) {
        return tagAliasActionCache.get(sequence);
    }

    public void put(int sequence, TagAliasBean tagAliasBean) {
        tagAliasActionCache.put(sequence, tagAliasBean);
    }

    @SuppressLint("HandlerLeak")
    private Handler delaySendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    reconnectionTime--;
                    JPushInterface.setAlias(context, (String) msg.obj, TagAliasOperatorHelper.this);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 处理设置标签别名
     */
    public void handleAction(Context context, int sequence, TagAliasBean tagAliasBean) {
        init(context);
        if (tagAliasBean == null) {
            XLog.i(TagAliasOperatorHelper.class, "tagAliasBean was null");
            return;
        }
        if (null == tagAliasBean.reconnectionTime)
            reconnectionTime = 3;
        else reconnectionTime = tagAliasBean.reconnectionTime;

        put(sequence, tagAliasBean);
        XLog.i(TagAliasOperatorHelper.class, "tagAliasBean:" + tagAliasBean.toString());
        if (tagAliasBean.isAliasAction) {
            switch (tagAliasBean.action) {
                case ACTION_GET:
                    JPushInterface.getAlias(context, sequence);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteAlias(context, sequence);
                    break;
                case ACTION_SET:
                    JPushInterface.setAlias(context, tagAliasBean.alias, this);
                    break;
                default:
                    XLog.i(TagAliasOperatorHelper.class, "unsupport alias action type");
                    break;
            }
        } else {
            switch (tagAliasBean.action) {
                case ACTION_ADD:
                    JPushInterface.addTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_SET:
                    JPushInterface.setTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_CHECK:
                    //一次只能check一个tag
                    String tag = (String) tagAliasBean.tags.toArray()[0];
                    JPushInterface.checkTagBindState(context, sequence, tag);
                    break;
                case ACTION_GET:
                    JPushInterface.getAllTags(context, sequence);
                    break;
                case ACTION_CLEAN:
                    JPushInterface.cleanTags(context, sequence);
                    break;
                default:
                    XLog.i(TagAliasOperatorHelper.class, "unsupport tag action type");
                    break;
            }
        }
    }

    private void RetryActionIfNeeded(String alias) {
        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        Message message = new Message();
        message.what = DELAY_SEND_ACTION;
        message.obj = alias;
        delaySendHandler.sendMessageDelayed(message, 1000 * 60);
    }

//    private String getRetryStr(boolean isAliasAction, int actionType, int errorCode) {
//        String str = "Failed to %s %s due to %s. Try again after 60s.";
//        str = String.format(Locale.ENGLISH, str, getActionStr(actionType), (isAliasAction ? "alias" : " tags"), (errorCode == 6002 ? "timeout" : "server too busy"));
//        return str;
//    }

//    private String getActionStr(int actionType) {
//        switch (actionType) {
//            case ACTION_ADD:
//                return "add";
//            case ACTION_SET:
//                return "set";
//            case ACTION_DELETE:
//                return "delete";
//            case ACTION_GET:
//                return "get";
//            case ACTION_CLEAN:
//                return "clean";
//            case ACTION_CHECK:
//                return "check";
//        }
//        return "unkonw operation";
//    }

//    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
//        int sequence = jPushMessage.getSequence();
//        XLog.e(TagAliasOperatorHelper.class, "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
//        XLog.e(TagAliasOperatorHelper.class, "tags size:" + jPushMessage.getTags().size());
//        init(context);
//        //根据sequence从之前操作缓存中获取缓存记录
//        TagAliasBean tagAliasBean = tagAliasActionCache.get(sequence);
//        if (tagAliasBean == null) {
//            XLog.e(TagAliasOperatorHelper.class, "获取缓存记录失败" + sequence);
//            return;
//        }
//        if (jPushMessage.getErrorCode() == 0) {
//            XLog.e(TagAliasOperatorHelper.class, "action - modify tag Success,sequence:" + sequence);
//            tagAliasActionCache.remove(sequence);
//            String logs = getActionStr(tagAliasBean.action) + " tags success";
//            XLog.e(TagAliasOperatorHelper.class, logs);
//        } else {
//            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags";
//            if (jPushMessage.getErrorCode() == 6018) {
//                //tag数量超过限制,需要先清除一部分再add
//                logs += ", tags is exceed limit need to clean";
//            }
//            logs += ", errorCode:" + jPushMessage.getErrorCode();
//            XLog.e(TagAliasOperatorHelper.class, logs);
//        }
//    }

//    /**
//     * 标签操作结果
//     *
//     * @param context      上下文
//     * @param jPushMessage {@link JPushMessage}
//     */
//    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
//        int sequence = jPushMessage.getSequence();
//        XLog.e(TagAliasOperatorHelper.class, "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag());
//        init(context);
//        //根据sequence从之前操作缓存中获取缓存记录
//        TagAliasBean tagAliasBean = tagAliasActionCache.get(sequence);
//        if (tagAliasBean == null) {
//            return;
//        }
//        if (jPushMessage.getErrorCode() == 0) {
//            XLog.e(TagAliasOperatorHelper.class, "tagBean:" + tagAliasBean);
//            tagAliasActionCache.remove(sequence);
//            String logs = getActionStr(tagAliasBean.action) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
//            XLog.e(TagAliasOperatorHelper.class, logs);
//        } else {
//            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags, errorCode:" + jPushMessage.getErrorCode();
//            XLog.e(TagAliasOperatorHelper.class, logs);
//            RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean);
//        }
//    }

//    /**
//     * 别名操作结果
//     *
//     * @param context      上下文
//     * @param jPushMessage {@link JPushMessage}
//     */
//    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
//        int sequence = jPushMessage.getSequence();
//        XLog.e(TagAliasOperatorHelper.class, "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
//        Log4jConfig.info("action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
//        init(context);
//        //根据sequence从之前操作缓存中获取缓存记录
//        TagAliasBean tagAliasBean = tagAliasActionCache.get(sequence);
//        if (tagAliasBean == null) {
//            return;
//        }
//        if (jPushMessage.getErrorCode() == 0) {
//            XLog.e(TagAliasOperatorHelper.class, "action - modify alias Success,sequence:" + sequence);
//            tagAliasActionCache.remove(sequence);
//            String logs = getActionStr(tagAliasBean.action) + " alias success";
//            XLog.e(TagAliasOperatorHelper.class, logs);
//            Log4jConfig.info(logs);
//        } else {
//            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " alias, errorCode:" + jPushMessage.getErrorCode();
//            XLog.e(TagAliasOperatorHelper.class, logs);
//            Log4jConfig.info(logs);
//            RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean);
//        }
//    }

    @Override
    public void gotResult(int code, String alias, Set<String> tags) {
        try {
            String logs;
            switch (code) {
                case 0:
                    logs = alias + "Set tag or alias success";
                    break;
                case 6002:
                    logs = alias + "Failed to set alias and tags due to timeout. Try again after 60s";
                    // 延迟 60 秒来调用 Handler 设置别名
                    if (reconnectionTime > 0)
                        RetryActionIfNeeded(alias);
                    break;
                default:
                    logs = alias + "Failed with errorCode = " + code;
            }
            XLog.e(TagAliasOperatorHelper.class, logs);
        } catch (Exception e) {
            XLog.e(TagAliasOperatorHelper.class, e);
        }
    }

    public static class TagAliasBean {

        public int action;
        public Set<String> tags;
        public String alias;
        public boolean isAliasAction;
        public Integer reconnectionTime;

        @Override
        public String toString() {
            return "TagAliasBean{" +
                    "action=" + action +
                    ", tags=" + tags +
                    ", alias='" + alias + '\'' +
                    ", isAliasAction=" + isAliasAction +
                    '}';
        }
    }
}
