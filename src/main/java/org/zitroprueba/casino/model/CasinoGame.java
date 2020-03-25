package org.zitroprueba.casino.model;

import java.time.LocalDateTime;

public interface CasinoGame {
	
	public static final int MIN_BET = 5;
	
	public double play(double bet, LocalDateTime startTime);
	
}
