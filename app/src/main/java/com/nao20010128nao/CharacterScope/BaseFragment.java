package com.nao20010128nao.CharacterScope;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.view.*;

public abstract class BaseFragment<Parent extends FragmentActivity> extends Fragment {
	public Parent getParentActivity(){
		return (Parent)getActivity();
	}
	
	public View findViewById(int id){
		return getView().findViewById(id);
	}
}
