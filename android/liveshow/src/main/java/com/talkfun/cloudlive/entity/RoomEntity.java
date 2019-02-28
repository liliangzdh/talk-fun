package com.talkfun.cloudlive.entity;

public class RoomEntity {
	private String token;
	private String roomName;
	private boolean isPlayback; //是否点播回放，false为直播，true为点播放

	public RoomEntity(){
	}
	
	public RoomEntity(String token, String roomName){
		this(token,roomName,false);
	}

	public RoomEntity(String token, String roomName, boolean isPlayback){
		this.token = token;
		this.roomName = roomName;
		this.isPlayback = isPlayback;
	}	
	
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	/**
	 * 获取是否点播回放
	 * @return
	 */
	public boolean getIsPlayback() {
		return isPlayback;
	}
	
	/**
	 * 设置是否点播回放
	 * @return
	 */
	public void setIsPlayback(boolean isPlayback) {
		this.isPlayback = isPlayback;
	}

	
	@Override
	public String toString() {
		return roomName;
	}
}
