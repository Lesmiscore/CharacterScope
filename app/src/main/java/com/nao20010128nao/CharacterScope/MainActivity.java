package com.nao20010128nao.CharacterScope;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ViewPager pager;
    UsefulPagerAdapter2 adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager=(ViewPager)findViewById(R.id.pager);
        adapter=new UsefulPagerAdapter2(this);
        adapter.addTab(new FirstFragment(),"");
        pager.setAdapter(adapter);
    }

    public static class FirstFragment extends BaseFragment<MainActivity> {
        String savedText="";
        EditText text;
        Button show;

        @Override
        public void onResume() {
            super.onResume();
            text=(EditText)findViewById(R.id.text);
            show=(Button)findViewById(R.id.show);
            text.setText(savedText);
            show.setOnClickListener(v->{
                new AsyncTask<String,Void,List<Fragment>>(){
                    @Override
                    protected List<Fragment> doInBackground(String... strings) {
                        char[] chars=filterCharacters(strings[0]).toCharArray();
                        List<Fragment> fragments=new ArrayList<Fragment>();
                        for(char c:chars){
                            CharFragment cf=new CharFragment();
                            cf.setChar(c);
                            fragments.add(cf);
                        }
                        return fragments;
                    }

                    @Override
                    protected void onPostExecute(List<Fragment> fragments) {
                        List<Map.Entry<Fragment,String>> pages=getParentActivity().adapter.pages;
                        if(pages.size()!=1){
                            Map.Entry<Fragment,String> first=pages.get(0);
                            pages.clear();
                            pages.add(first);
                        }
                        for(Fragment f:fragments){
                            pages.add(new KVP<>(f,""));
                        }
                        getParentActivity().adapter.notifyDataSetChanged();
                    }
                }.execute(text.getText().toString());
            });
        }

        public String filterCharacters(String s){
            StringBuilder sb=new StringBuilder();
            for(char c:s.toCharArray()){
                sb.append(c);
            }
            return sb.toString();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(savedInstanceState!=null){
                savedText=savedInstanceState.getString("savedText");
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("savedText",text.getText().toString());
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.first_fragment,container,false);
        }
    }
    public static class CharFragment extends BaseFragment<MainActivity> {
        TextView charText1;
        FrameLayout textContainer;

        public void setChar(char c){
            Bundle bnd=new Bundle();
            bnd.putChar("char",c);
            setArguments(bnd);
        }

        public char getChar(){
            return getArguments().getChar("char");
        }

        @Override
        public void onResume() {
            super.onResume();
            char show=getChar();
            int maxSize=getScreenMinLength(getActivity());
            charText1=(TextView)findViewById(R.id.charText1);
            textContainer=(FrameLayout)findViewById(R.id.textContainer);
            charText1.setText(String.valueOf(show));
            textContainer.setLayoutParams(new LinearLayout.LayoutParams(maxSize,maxSize));
            charText1.setTextSize(TypedValue.COMPLEX_UNIT_PX,maxSize);
        }

        public static int getScreenMinLength(Context c){
            Point point=getRealSize(c);
            return Math.min(point.x,point.y);
        }

        @SuppressLint("NewApi")
        public static Point getRealSize(Context activity) {

            Display display = ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point point = new Point(0, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // Android 4.2~
                display.getRealSize(point);
                return point;

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                // Android 3.2~
                try {
                    Method getRawWidth = Display.class.getMethod("getRawWidth");
                    Method getRawHeight = Display.class.getMethod("getRawHeight");
                    int width = (Integer) getRawWidth.invoke(display);
                    int height = (Integer) getRawHeight.invoke(display);
                    point.set(width, height);
                    return point;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return point;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.char_fragment,container,false);
        }
    }
}
