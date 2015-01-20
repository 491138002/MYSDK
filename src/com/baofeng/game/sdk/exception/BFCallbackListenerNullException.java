package com.baofeng.game.sdk.exception;

public class BFCallbackListenerNullException extends Exception {


	private static final long serialVersionUID = -8831463422132317310L;      

	public BFCallbackListenerNullException(String detailMessage, Throwable throwable)
	  {
	    super(detailMessage, throwable);
	  }

	  public BFCallbackListenerNullException(String detailMessage)
	  {
	    super(detailMessage);
	  }

	  public BFCallbackListenerNullException(Throwable throwable)
	  {
	    super(throwable);
	  }

	  public BFCallbackListenerNullException()
	  {
	  }
}
