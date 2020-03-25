package org.zitroprueba.casino;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zitroprueba.casino.client.SlotSvcApi;
import org.zitroprueba.casino.model.Slot;
import org.zitroprueba.casino.repository.ExternalUser;
import org.zitroprueba.casino.repository.Transaction;
import org.zitroprueba.casino.repository.TransactionRepository;

import com.google.common.collect.Lists;


@Controller
public class SlotSvc {
	
	@Autowired
	private TransactionRepository transactions;
	
	private static final AtomicLong currentId = new AtomicLong(0L);
	private Map<Long, LocalDateTime> usersTime = new HashMap<Long, LocalDateTime>();
	private Map<Long, ExternalUser> users = new HashMap<Long, ExternalUser>();
	
	private void checkAndSetId(ExternalUser entity) {
		if (entity.getId() == 0)
			entity.setId(currentId.incrementAndGet());
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value=SlotSvcApi.SLOT_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Object getGameConfiguration() {
		
		JSONObject data = new JSONObject();
		data.put("game_name", Slot.GAME_NAME);
		data.put("minimum_bet", Slot.MIN_BET);
		data.put("maximum_bet", Slot.MAX_BET);
		data.put("award_probability", Slot.AWARD_PROBABILITY);
		return data;
	}
	
	@RequestMapping(value=SlotSvcApi.SLOT_SVC_PATH + "/bet", method=RequestMethod.GET)
	public @ResponseBody Collection<Transaction> getTransactionList() {
		return Lists.newArrayList(transactions.findAll());
		
	}
	
	@RequestMapping(value=SlotSvcApi.SLOT_SVC_PATH + "/{id}/bet", method=RequestMethod.GET)
	public @ResponseBody Transaction getTransactionById(@PathVariable("id") long id) {
		return transactions.findOne(id);
	}

	
	@RequestMapping(value=SlotSvcApi.SLOT_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody ExternalUser startGame(@RequestBody ExternalUser user) {
		checkAndSetId(user);
		if (!usersTime.containsKey(user.getId()))
			usersTime.put(user.getId(), LocalDateTime.now());
		
		users.put(user.getId(), user);
		return user;
	}

	@RequestMapping(value=SlotSvcApi.SLOT_SVC_PATH + "{id}/bet", method=RequestMethod.POST)
	public @ResponseBody Transaction playGame(@PathVariable("id") long id, @RequestParam(SlotSvcApi.BET_PARAMETER) double bet) throws UserNotFound, NotEnoughBalanceAccount, GameException {

		ExternalUser user = users.get(id);
		if (user == null)
			throw new UserNotFound();
		
		if (user.getBalance() < bet || user.getBalance() == 0)
			throw new NotEnoughBalanceAccount();
	
		Slot slot = new Slot("Test game");
		double reward = slot.play(bet, usersTime.get(id));
		if (reward == -1 )
			throw new GameException();
		
		double currentIncrease = - bet + reward;
		double currentBalance = user.getBalance() + currentIncrease;
		user.setBalance(currentBalance);
		users.put(user.getId(), user);
		
		Transaction transaction = new Transaction(user.getNickname(), Slot.GAME_NAME, bet, currentIncrease);
		transactions.save(transaction);
		return transaction;
	}
	
	
	@ResponseStatus(value=HttpStatus.NOT_FOUND, reason = "This user is not registered as an active player!")
	public class UserNotFound extends Exception {
		public UserNotFound() {}
	}
	
	@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason = "The user does not have enough balance!")
	public class NotEnoughBalanceAccount extends Exception {
		public NotEnoughBalanceAccount() {}
	}
	
	@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason = "Wrong parameters!, see the game configuration")
	public class GameException extends Exception {
		public GameException() {}
	}

}
