package com.starway.starrobot.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.activity.adapter.CommomListAdapter;
import com.starway.starrobot.bean.AIUIQAData;
import com.starway.starrobot.bean.CommonListItem;
import com.starway.starrobot.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class QAFragment extends Fragment implements AdapterView.OnItemClickListener {

    //AIUI自定义问答ID（需要从地址栏获取）
    private static final String AIUI_QA_ID = "1622c42b509";
    private BaseActivity baseActivity;
    private OnQAItemClickListener listener;
    private ArrayList<CommonListItem> list = new ArrayList<>();
    private CommomListAdapter adapter;
    private HttpUtil httpUtil;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        httpUtil = HttpUtil.getInstance();
        baseActivity = (BaseActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_qa, container, false);

        listView = view.findViewById(R.id.listView);
        adapter = new CommomListAdapter(getContext(), R.layout.item_common, list);

        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        init();
        return view;
    }

    private void init() {
        httpUtil.get(getUrlWithIndex(0), new HttpUtil.ObjectCallback<AIUIQAData>() {

            @Override
            public void onResponse(Call call, AIUIQAData obj) {
                for (AIUIQAData.QAData.QA qa : obj.data.result) {
                    List<String> questionList = qa.questionList;
                    boolean flag = true;
                    String title = "";
                    for (String question : questionList) {
                        if (question.startsWith("/")) {
                            title = question.replace("/","");
                            break;
                        } else if (question.startsWith("*")) {//排除自定义问答
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        list.add(new CommonListItem().setTitle(title).setObject(qa)
                        );
                    }
                }
                adapter.notifyDataSetChanged();
                System.out.println("QA数据加载完毕");
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            baseActivity.setTitle(R.string.menu_qa);
        }
    }

    /**
     * 返回AIUI自定义问答接口地址
     *
     * @param idx 页数（0为一次性获取所有页，idx>0为一页5个）
     * @return
     */
    private String getUrlWithIndex(int idx) {
        // https://aiui.xfyun.cn/aiui/web/qa/queryQaPair?repoId=1622c42b509&query=&pageSize=15&pageIndex=1&queryRange=&timestamp=1535552374597
        return "https://aiui.xfyun.cn/aiui/web/qa/queryQaPair?repoId=" + AIUI_QA_ID + "&query=&queryRange=&pageSize=30&pageIndex=" + idx + "&timestamp=" + System.currentTimeMillis();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            CommonListItem item = list.get(position);
            listener.onItemClick(item.getTitle(),((AIUIQAData.QAData.QA) item.getObject()).answerList.get(0));
        }
    }

    /**
     * 设置列表项点击回调
     *
     * @param listener
     * @return
     */
    public QAFragment setListener(OnQAItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 问答列表点击回调
     */
    public interface OnQAItemClickListener {
        void onItemClick(String title,String text);
    }

}
