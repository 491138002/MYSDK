package com.baofeng.game.sdk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.game.sdk.util.BXDialogUtil.OnAlertSelectId;
import com.baofeng.game.sdk.util.DeviceUtil;

public class BFSearchAdapter extends BaseAdapter implements Filterable{
	
	private List<String> baseList = new ArrayList<String>();
	private List<String> dataList = new ArrayList<String>();
	private MyFilter myFilter;
	private AutoCompleteTextView editText;
	Context context;
	
//	private Handler handler = new Handler();
	private OnAlertSelectId onSelectListener;
	
	@SuppressLint("NewApi") public BFSearchAdapter(final Context context, final AutoCompleteTextView editText, List<String> dataList, OnAlertSelectId onSelectListener){
		this.editText = editText;
		this.context = context;
		this.dataList = dataList;
		this.onSelectListener = onSelectListener;
		baseList.addAll(dataList);
		
		editText.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					editText.setDropDownWidth(((View)editText.getParent()).getWidth());
					editText.setDropDownHorizontalOffset(editText.getWidth()-((View)editText.getParent()).getWidth()+DeviceUtil.dip2px(context, 10));
					editText.setDropDownVerticalOffset(DeviceUtil.dip2px(context, 4));
					editText.showDropDown();
				}
				return false;
			}
		});
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			
			LinearLayout layout = new LinearLayout(context);
			layout.setPadding(10, 10, 10, 10);
			layout.setGravity(Gravity.CENTER_VERTICAL);
			TextView textView = new TextView(context);
			layout.addView(textView);
			convertView = layout;
			holder.item_text = textView;

			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}
		
		holder.item_text.setTextColor(Color.BLACK);
		holder.item_text.setText(dataList.get(position));
		
		if(position % 2 == 0){
			convertView.setBackgroundColor(Color.WHITE);
		}else{
			convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
		}
		
		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(onSelectListener != null){
					editText.setText(dataList.get(position));
					onSelectListener.onClick(position, dataList.get(position));
//					handler.postDelayed(new Runnable() {
//						public void run() {
//						}
//					}, 50);
					editText.dismissDropDown();
					
				}
			}
		});
		
		return convertView;
	}
	
	class Holder {
		TextView item_text;
	}

	@Override
	public Filter getFilter() {  
        if (myFilter == null) {  
        	myFilter = new MyFilter();  
        }  
        return myFilter;  
    }  

	class MyFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();  
			List<String> list = new ArrayList<String>();
			list.addAll(baseList);
			
			results.count = list.size();
			results.values = list;
			
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			dataList = (List<String>) results.values;  
            if (results.count > 0) {  
                notifyDataSetChanged();  
            } else {  
                notifyDataSetInvalidated();  
            }  
//            editText.showDropDown(); 
            
//            handler.postDelayed(new Runnable() {
//				public void run() {
//				}
//			}, 0);
		}
		
	}

	public List<String> getDataList() {
		return dataList;
	}

	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
		notifyDataSetChanged();
	}
}
