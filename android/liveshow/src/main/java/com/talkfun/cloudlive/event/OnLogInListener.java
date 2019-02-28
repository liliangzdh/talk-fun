package com.talkfun.cloudlive.event;

public interface OnLogInListener {
	void logInStart();
	void logInCancel();
	void logInCompleted();
}
