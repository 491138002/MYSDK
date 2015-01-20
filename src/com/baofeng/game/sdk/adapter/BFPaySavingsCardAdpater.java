package com.baofeng.game.sdk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.baofeng.game.sdk.BFResources;

public class BFPaySavingsCardAdpater extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context context;
	private int selectIndex;
	private int[] rIds = {
			BFResources.drawable.bx_btn_bank_js, BFResources.drawable.bx_btn_bank_ny,
			BFResources.drawable.bx_btn_bank_cx, BFResources.drawable.bx_btn_bank_zs, 
			BFResources.drawable.bx_btn_bank_gs, BFResources.drawable.bx_btn_bank_jt, 
			BFResources.drawable.bx_btn_bank_zg, BFResources.drawable.bx_btn_bank_pf, 
	};
	
	public BFPaySavingsCardAdpater(Context context){
		this.context = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		selectIndex = 0;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rIds.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = mInflater.inflate(BFResources.layout.bx_pay_center_game_card_item, null);
		}
		
		ImageView iconIv = (ImageView) convertView.findViewById(BFResources.id.bx_game_card_icon_iv);
		ImageView selectIv = (ImageView) convertView.findViewById(BFResources.id.bx_game_card_selected_iv);
		
		iconIv.setBackgroundResource(rIds[position]);
		selectIv.setVisibility(View.INVISIBLE);
		
		convertView.setBackgroundColor(Color.parseColor("#f9f9f9"));
		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
			}
		});
		
		return convertView;
	}

	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}
}
