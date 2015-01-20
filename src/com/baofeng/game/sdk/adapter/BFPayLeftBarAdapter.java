package com.baofeng.game.sdk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.config.BFGameConfig;

public class BFPayLeftBarAdapter extends BaseAdapter {

	public String[] items = { "信用卡快捷", "支付宝", "储蓄卡", "充值卡",
//			 "点卡",
			// "平台币",
			"银联" };
	public String[] items2 = { "信用卡快捷", "支付宝", "储蓄卡", "充值卡", "点卡",
			// "平台币",
			"银联" };

	/**
	 * 选中下表
	 */
	private int selectIndex = 0;
	/**
	 * 内容tag
	 */
	private String tag_text = "content";
	/**
	 * 上下文
	 */
	private Context context;

	private int defaultBg = 0;
	private int pressedBg = 0;

	private BXOnItemClickListener onItemClickListener;

	public BXOnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(BXOnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public BFPayLeftBarAdapter(Context context) {
		this.context = context;

		selectIndex = 0;

		pressedBg = BFResources.drawable.bx_right_btn_default;
		defaultBg = BFResources.drawable.bx_right_btn_pressed;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if ("0".equals(BFGameConfig.GAME_CARD)) {

			return items.length;
		} else {
			return items2.length;
		}
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
	public View getView(final int position, View convertView,
			ViewGroup parentView) {

		if (convertView == null) {
			LinearLayout layout = new LinearLayout(context);
			layout.setGravity(Gravity.CENTER);

			TextView textView = new TextView(context);

			if ("0".equals(BFGameConfig.GAME_CARD)) {

				textView.setText(items[position]);
			} else {
				textView.setText(items2[position]);
			}
			textView.setTextColor(Color.WHITE);
			textView.setTag(tag_text);
			layout.addView(textView);
			convertView = layout;
		} else {
			TextView textView = (TextView) convertView
					.findViewWithTag(tag_text);

			if ("0".equals(BFGameConfig.GAME_CARD)) {

				textView.setText(items[position]);
			} else {
				textView.setText(items2[position]);
			}
		}

		if (position == selectIndex) {
			convertView.setBackgroundResource(pressedBg);
		} else {
			convertView.setBackgroundResource(defaultBg);
		}

		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (onItemClickListener != null) {
					
					if ("0".equals(BFGameConfig.GAME_CARD)) {
						if (items.length - 1 == position)
							onItemClickListener.onItemClick(position + 1);
						else
							onItemClickListener.onItemClick(position);
					} else {
						if (items2.length - 1 == position)
							onItemClickListener.onItemClick(position + 1);
						else
							onItemClickListener.onItemClick(position);
					}
				}

				selectIndex = position;
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	public interface BXOnItemClickListener {
		public void onItemClick(int position);
	}
}
