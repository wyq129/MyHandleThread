package com.example.aiiage.myhandlethread.EnventBus;


import android.content.Context;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.aiiage.myhandlethread.R;

import org.greenrobot.eventbus.EventBus;



/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {
    private final static String[] Books=new String[]{
            "第一行代码",
            "Java编程思想",
            "Android开发艺术探索",
            "算法"
    };
    ListView recyclerView;
    Context context;

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.recycleView_Books);
        ArrayAdapter arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Books);
        recyclerView.setAdapter(arrayAdapter);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventBus.getDefault().post(new ReadBookEvent(Books[i]));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_first, container, false);

        return view;
    }

}
