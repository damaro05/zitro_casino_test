package org.zitroprueba.casino.repository;

public class ExternalUser {
	private long id;
	private String nickname;
	private double balance;
	private String userProvider;
	
	public ExternalUser() {}
	public ExternalUser(String nickname, long balance, String userProvider) {
		super();
		this.nickname = nickname;
		this.balance = balance;
		this.userProvider = userProvider;
	}
	
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getUserProvider() {
		return userProvider;
	}
	public void setUserProvider(String userProvider) {
		this.userProvider = userProvider;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
