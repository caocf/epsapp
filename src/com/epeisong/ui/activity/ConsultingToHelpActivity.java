package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.AdjustHeightExpandableListView;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetCustomService;
import com.epeisong.data.net.parser.QuestionParser;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.QuestionReq.Builder;
import com.epeisong.logistics.proto.Eps.QuestionResp;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Question;
import com.epeisong.model.User;
import com.epeisong.net.request.NetQuestion;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 
 * @author 孙灵洁 咨询帮助
 * 
 */
public class ConsultingToHelpActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    int position = -1;
    private MyAdapter mAdapter;

    private class MyAdapter extends BaseExpandableListAdapter {

        private List<String> titles;
        private List<List<String>> lists;

        @SuppressWarnings("unused")
        public MyAdapter(List<String> titles, List<List<String>> lists) {
            this.titles = titles;
            this.lists = lists;
        }

        public void replace(List<String> titles, List<List<String>> lists) {
            this.titles = titles;
            this.lists = lists;
            this.notifyDataSetChanged();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return lists.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return lists.get(groupPosition).size();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {

            String string = lists.get(groupPosition).get(childPosition);
            TextView tv_content;

            View view = null;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.item_consulting_to_help_child, null);
                view.setBackgroundColor(Color.parseColor("#f4f8fa"));
            } else {
                view = convertView;
            }
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_content.setText(string);
            tv_content.setTextSize(16);
            tv_content.setTextColor(Color.parseColor("#7d7e80"));
            return view;

        }

        @Override
        public String getGroup(int groupPosition) {
            return titles.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return titles.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        public int getGroupIndex(String title) {
            return titles.indexOf(title);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            String s = titles.get(groupPosition);
            TextView tv_problem;
            ImageView iv;

            View view = null;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.item_consulting_to_help, null);
                view.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                view = convertView;
            }
            tv_problem = (TextView) view.findViewById(R.id.tv_problem);
            tv_problem.setTextSize(18);
            iv = (ImageView) view.findViewById(R.id.iv);
            tv_problem.setText(s);

            if (groupPosition == position) {
                listview.expandGroup(groupPosition);
                iv.setImageResource(R.drawable.blue_up);
            } else {
                listview.collapseGroup(groupPosition);
                iv.setImageResource(R.drawable.hui_down);
            }

            return view;

        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    public static final String EXTRA_ID = "id";

    private ScrollView mScrollView;
    private Button bt_click;
    private AdjustHeightExpandableListView listview;
//    private TextView tv_number;
    //private Button bt_call;

    private String id_consultation = "7";

    // List<String> list = new ArrayList<String>();

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    protected TitleParams getTitleParams() {
        // TODO Auto-generated method stub
        return new TitleParams(getDefaultHomeAction(), "咨询帮助", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulting_to_help);
        mScrollView = (ScrollView) findViewById(R.id.sv);
        bt_click = (Button) findViewById(R.id.bt_click);

        listview = (AdjustHeightExpandableListView) findViewById(R.id.listview);

        List<String> titles = new ArrayList<String>();
        List<List<String>> lists = new ArrayList<List<String>>();

        listview.setAdapter(mAdapter = new MyAdapter(titles, lists));

        listview.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listview.isGroupExpanded(groupPosition)) {
                    listview.collapseGroup(groupPosition);
                    position = -1;

                } else {
                    if (position > -1) {
                        listview.collapseGroup(position);
                    }
                    listview.expandGroup(groupPosition);
                    position = groupPosition;
                    listview.setSelectedGroup(position);
                }
                // mAdapter.notifyDataSetChanged();
                return true;
            }
        });
//        tv_number = (TextView) findViewById(R.id.tv_number);
//        tv_number.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_number.getText()));
//                startActivity(intent);
//            }
//        });

        bt_click.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getCustomService();
            }
        });

        requestData();
    }

    private void getCustomService() {
        showPendingDialog(null);
        AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                NetCustomService net = new NetCustomService();
                try {
                    QuestionResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        return UserParser.parse(resp.getCustomerService());
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(User result) {
                dismissPendingDialog();
                if (result != null) {
                    Intent intent = new Intent(ConsultingToHelpActivity.this, ChatRoomActivity.class);
                    intent.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, result.getId());
                    intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE, ChatMsg.business_type_normal);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast("系统繁忙");
                }
            }
        };
        task.execute();
    }

    private void requestData() {

        AsyncTask<Void, Void, List<Question>> task = new AsyncTask<Void, Void, List<Question>>() {

            @Override
            protected List<Question> doInBackground(Void... arg0) {
                // TODO Auto-generated method stub
                NetQuestion net = new NetQuestion() {

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        // TODO Auto-generated method stub
                        req.setStatus(Properties.QUESTION_IS_SHOWN);
                        return true;
                    }

                };

                try {
                    QuestionResp.Builder resp = net.request();
                    if (resp == null) {
                        return null;
                    }
                    if (resp.getResult().equals("SUCC")) {

                        return QuestionParser.parse(resp);
                    }

                } catch (NetGetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Question> result) {
                HandlerUtils.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bt_click.setVisibility(View.VISIBLE);
                        mScrollView.scrollTo(0, 0);
                    }
                }, 100);
                List<String> titles = new ArrayList<String>();
                List<List<String>> lists = new ArrayList<List<String>>();
                if (result == null) {
                    return;
                }
                for (Question q : result) {
                    titles.add(q.getTitle());
                    List<String> list = new ArrayList<String>();
                    list.add(q.getContent());
                    lists.add(list);
                }
                mAdapter.replace(titles, lists);

            }
        };
        task.execute();

    }
}
