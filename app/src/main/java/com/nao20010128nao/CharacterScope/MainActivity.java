package com.nao20010128nao.CharacterScope;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
        public void setChar(char c){
            Bundle bnd=new Bundle();
            bnd.putChar("char",c);
            setArguments(bnd);
        }

        public char getChar(){
            return getArguments().getChar("char");
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            char show=getChar();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
