/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.baofeng.game.sdk.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

//	//合作身份者id，以2088开头的16位纯数字
//	public static final String DEFAULT_PARTNER = "2088111065531263";
//
//	//收款支付宝账号
//	public static final String DEFAULT_SELLER = "2088111065531263";
//
//	//商户私钥，自助生成
//	public static final String PRIVATE = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAN/Xu69LHnWZCr5AGE8V8voxHgi6987zMpc9TTz8/VYEQvQTeaavmfNN2iX/mtSFhiVIcer/VJh4UeMdBsCK66bRuQ8CKDm2TqM1K5KmQhGJn2GJQWKgB8Uzs1XK/08FtpHTamLqw0S2jbSJnhpTGDr5XRUHmbOcsLWmcbKZ9jqHAgMBAAECgYEArbBzhRiP4QZI7MlFU5Cfgn28ZV/Wy/Gv7uT1Wv6gAS+7WDMIcUvO9JkTTerhlEirh6CILKXFWa37NHSgeaUSJmKTJldIo7mZgmN29XAcsY4p1qHDMupPGIR0bCK8HeUTMsf74QhrCBgh+t7tPP94NQEtr1/WNFQQd1CeK38MyTECQQD8LOGa6A3tcjMAOPt/4i+jhHbcm9r5o1pNfFlaj14bMt8oxNUNan+QZCuAN7rAc0DDtwUE3m8G4T+bKGZSw7GPAkEA4zzYWYcbU65FPaJ/g60czHrAkjApOj0856Eyrlh4Bpq28dX7d0jj0jU4LykHJ7GOJS8DT98Vt1lF8VKrwQv7iQJBAKJYBnmicOzvOxQmmwquQ7sCWT8W+zcBSe5eYmvhplTeQDpP1VUIvczoH/Uwa4Lf787PsWR8CoAkXL13mcAoidMCQQCQDaBxkO7H67BFVGBVz0Z0clTl19Yq72tOw6fnUno0ZZwYTzis8+5I8if97Zp1lV+xPs5wsdGIe/WU+H2dUcVRAkEAnM6i1Xi0r41qapoByShYdKMltj7r6mZrKo3efCKtJxL2DUxwEF4EjPJJ9slCdnDI8+qYR1viHyxBh6cbNEPBDg==";
//
//	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	
	//合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088411705925145";

	//收款支付宝账号
	public static final String DEFAULT_SELLER = "2088411705925145";

	//商户私钥，自助生成
	public static final String PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANfNNxwJ3ba6mrFNMS0gs9SmIKDpJI4ky/mXVFoNCPJUNdBAJ2GqwA7T3CIeOLF0ZGacPv3kxAvIHSBKhwsgtYO1T/sab6O+2xA94Zwzganft+pe6SjdyXMJIH5ZgpEu0OmX9E5v2srrhfuibnHDjZXCKbP5jL9cQc1kmopVucMbAgMBAAECgYEA11Ikja5ucbepSAV8bsm1hRUpc0SNO+MNPwG2oY9mANzzQNHyOWo07daIS+aZrL40u4lk9NIAprzKCwEx03GUiXwK9yxzjod/Wf2HxQYF30z5kdVzSdpRclujSnrEWlSoksCQ1g2kbc7DYUx9qSlasO6QZq1x+Ut4bgEDTHFqbOECQQDvl+PYtkTT9I0aFZjdXPPiCoP9lg4CrKyGUzEmujnEagSH4Vi3cq9w7eZOWnzqFF/81cIpt0iZgQQwbrW5L05RAkEA5pRAvc3rk4Qi7mwFaYbLONsMbVrL6Mo5E5ur+GdxbyCq3/ohCXaJM4ynyypy7+ALu0Lo6/O9KzA219Q5iP2DqwJAZIOOPM5KlbkUsQq6dLOYRQ4wTWR0QD78qeWgyyR5M6NefzrUozFj1LaZVem0WeduVX2/1QxlUrDDdyYa6rOj4QJBAI6wN9A9WgcTwjohBshi7eflRi91/LG2UtPbhfRWr0/Bu3yXYVJl1EneRQfo4O+oihn8Mf+x+eJ8F7RAVMp9d7kCQCN3h8M/yoMoX5J8o7dcu7bH+SfhsrfllsXsHln2HcyuuwVivWRueMTWPAmmXh0oxLi1I/6YEyCVuZ+LLZUKPP8=";

	//支付宝公钥
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	
	
}
