package com.baofeng.game.sdk.type;

import java.util.HashMap;

public class SMSCode {

	public static HashMap<Integer, Integer> SMS_CODE = new HashMap<Integer, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put(5, 4);
			put(10, 7);
			put(15, 8);
			put(30, 10);
		}
	};
}
