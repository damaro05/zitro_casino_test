package org.zitroprueba.casino.model;

import java.time.LocalDateTime;
import java.util.Random;

public class Slot implements CasinoGame {
	public static final int MAX_BET = 50;
	public static final String GAME_NAME = "SLOT";
	public static final double AWARD_PROBABILITY = 0.03;
	private static final long MAX_USER_GAME_TIME_HOURS = 2;
	//private static final long MAX_USER_GAME_TIME_MINUTES = 120;
	
	private String gameType;
	
	public Slot() {}
	public Slot(String gameType) {
		this.setGameType(gameType);
	}

	@Override
	public double play(double bet, LocalDateTime startTime) {
		LocalDateTime userMaxBetTime = startTime.plusHours(MAX_USER_GAME_TIME_HOURS);
		
		if (LocalDateTime.now().isAfter(userMaxBetTime) || bet < MIN_BET || bet > MAX_BET) {
			return -1;
		}
		
		double reward = 0;
		if (new Random().nextDouble() <= AWARD_PROBABILITY ) {
			reward = bet * 1.1;
		}
		return reward;
	}
	
	
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

}
