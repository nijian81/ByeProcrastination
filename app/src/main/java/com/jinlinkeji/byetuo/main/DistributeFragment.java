package com.jinlinkeji.byetuo.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jinlinkeji.byetuo.R;
import com.jinlinkeji.byetuo.utils.DistributeEntity;
import com.jinlinkeji.byetuo.utils.DistributeView;

import java.util.ArrayList;
import java.util.List;

public class DistributeFragment extends Fragment implements View.OnClickListener {

    private List<DistributeEntity> list;
    private DistributeView distributeView;
    private RelativeLayout distribute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.distribute_fragment, container,
                false);

        distribute = (RelativeLayout) rootView.findViewById(R.id.distribute);
        list = new ArrayList<>();
        //type分为1，2，3分别代表正在进行，待完成，已完成，紧急程度和难度决定了圆点的位置
        list.add(new DistributeEntity(1, 3, 6, "我是第一个内容"));
        list.add(new DistributeEntity(2, 6, 2, "我是第二个内容"));
        list.add(new DistributeEntity(1, 3, 4, "我是第三个内容"));
        list.add(new DistributeEntity(3, 9, 3, "我是第四个内容"));
        list.add(new DistributeEntity(1, 3, 8, "我是第五个内容"));
        list.add(new DistributeEntity(3, 4, 1, "我是第六个内容"));
        list.add(new DistributeEntity(1, 1, 8, "我是第七个内容"));
        list.add(new DistributeEntity(2, 3, 9, "我是第八个内容"));
        for (int i = 0; i < list.size(); i++) {
            distributeView = new DistributeView(getActivity());
            distributeView.setType(list.get(i).getType());
            distributeView.setDifficulty(list.get(i).getDifficulty());
            distributeView.setEmergency(list.get(i).getEmergency());
            distributeView.setTag(i);
            distributeView.setContent("我是第" + i + "个圆点");
            distributeView.setOnClickListener(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(90, 90);
            layoutParams.leftMargin = (int) (list.get(i).getEmergency() / (float) 10.0 * 700 + 250);
            layoutParams.topMargin = (int) (list.get(i).getDifficulty() / (float) 10.0 * 800 + 150);
            distribute.addView(distributeView, layoutParams);
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch ((int) view.getTag()) {
            case 0:
                Toast.makeText(getActivity(), list.get(0).getContent(), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getActivity(), list.get(1).getContent(), Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getActivity(), list.get(2).getContent(), Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getActivity(), list.get(3).getContent(), Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(getActivity(), list.get(4).getContent(), Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(getActivity(), list.get(5).getContent(), Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(getActivity(), list.get(6).getContent(), Toast.LENGTH_SHORT).show();
                break;
            case 7:
                Toast.makeText(getActivity(), list.get(7).getContent(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

}