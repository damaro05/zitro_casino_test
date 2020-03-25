package org.zitroprueba.casino.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.common.base.Objects;

@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String username;
	private String game;
	private double bet;
	private double increase;
	
	public Transaction() {}
	public Transaction(String username, String game, double bet, double increase) {
		super();
		this.username = username;
		this.game = game;
		this.bet = bet;
		this.increase = increase;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getGame() {
		return game;
	}
	public void setGame(String game) {
		this.game = game;
	}
	public double getBet() {
		return bet;
	}
	public void setBet(double bet) {
		this.bet = bet;
	}
	public double getIncrease() {
		return increase;
	}
	public void setIncrease(double increase) {
		this.increase = increase;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(username, game, bet);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Transaction) {
			Transaction other = (Transaction) obj;
			
			return Objects.equal(username, other.username)
					&& Objects.equal(game, other.game)
					&& bet == other.bet;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "Transaction: " + id +
				", User: " + username +
				", Bet: " + bet +
				", Increase: " + increase +
				", Game: " + game; 
	}	
}
