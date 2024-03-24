package com.example.custom02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.custom02.view.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlowLayout flowLayout = findViewById(R.id.flow_layout);
        //给 FlowLayout 中设置数据
        List<String> data = new ArrayList<>(Arrays.asList(
                "键盘xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx好xxxxxxxxx好xxxxxxxxxxxx嘻",
                                            "显示器", "鼠标",
                                        "iPad", "air pod", "耳机", "男装", "女鞋",
                                                "医疗", "食品", "百货", "运动",
                                                "电器", "鞋包", "美妆","家具"));
        flowLayout.setTextList(data);
        flowLayout.setOnItemClickListener((v, text) -> Log.d(TAG, text + "被点击了..."));
        flowLayout.setMaxLine(3);
    }
}



