package org.zitroprueba.casino.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.zitroprueba.casino.client.SlotSvcApi;
import org.zitroprueba.casino.client.SecuredRestBuilder;
import org.zitroprueba.casino.repository.ExternalUser;
import org.zitroprueba.casino.repository.Transaction;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;

public class SlotTest {

	private class ErrorRecorder implements ErrorHandler {

		private RetrofitError error;

		@Override
		public Throwable handleError(RetrofitError cause) {
			error = cause;
			return error.getCause();
		}

		public RetrofitError getError() {
			return error;
		}
	}

	private final String TEST_URL = "http://localhost:8080";
	private final String USERNAME1 = "admin";
	private final String USERNAME2 = "user0";
	private final String PASSWORD = "pass";
	private final String CLIENT_ID = "mobile";
	
	private SlotSvcApi readWriteSlotSvcUser = new SecuredRestBuilder()
			.setEndpoint(TEST_URL)
			.setLoginEndpoint(TEST_URL + SlotSvcApi.TOKEN_PATH)
			.setLogLevel(LogLevel.NONE)
			.setUsername(USERNAME1).setPassword(PASSWORD).setClientId(CLIENT_ID)
			.build().create(SlotSvcApi.class);
	
	
	/** Test to get the game configuration */
	@Test 
	public void getGameConfiguration() throws Exception {
		Object response = readWriteSlotSvcUser.getGameConfiguration();
		System.out.println("Game Configuration: " + response);
		
		assertTrue(response.toString().contains("game_name"));
		assertTrue(response.toString().contains("award_probability"));
		assertTrue(response.toString().contains("maximum_bet"));
		assertTrue(response.toString().contains("minimum_bet"));
	}
	
	/** Test to check previous stored transactions */
	@Test
	public void persistenceTransaction() throws Exception {
		ExternalUser user = new ExternalUser("user2", 250, "BWIN");
		ExternalUser received = readWriteSlotSvcUser.startGame(user);
		Transaction transaction = readWriteSlotSvcUser.playGame(received.getId(), 40);
		Transaction transaction2 = readWriteSlotSvcUser.playGame(received.getId(), 35);
		Transaction transactionReceived = readWriteSlotSvcUser.getTransactionById(transaction.getId());

		assertEquals(transaction, transactionReceived);
	}
	
	/** Test to validate multiple players at the same time */
	@Test
	public void playSlotMultiUsers() throws Exception {
		ExternalUser user1 = new ExternalUser("user1", 100, "PokerStars");
		ExternalUser user2 = new ExternalUser("user2", 300, "BWIN");
		ExternalUser received = readWriteSlotSvcUser.startGame(user1);
		ExternalUser received2 = readWriteSlotSvcUser.startGame(user2);
		
		Transaction transaction = readWriteSlotSvcUser.playGame(received.getId(), 20);
		Transaction transaction2 = readWriteSlotSvcUser.playGame(received2.getId(), 50);
		Transaction transaction3 = readWriteSlotSvcUser.playGame(received.getId(), 40);
		
		System.out.println("Response: " + transaction.toString());
		System.out.println("Response: " + transaction2.toString());
		System.out.println("Response: " + transaction3.toString());
		Collection<Transaction> stored = readWriteSlotSvcUser.getTransactionList();
		assertTrue(stored.contains(transaction));
		assertTrue(stored.contains(transaction2));
		assertTrue(stored.contains(transaction3));
	}
	
	/** Test to block users that do not start a game previously */
	@Test 
	public void playWithoutStartingGame() throws Exception {
		try {
			ExternalUser user = new ExternalUser("user", 100, "PokerStars");
			user.setId(99);
			readWriteSlotSvcUser.playGame(user.getId(), 30);

			fail("The server let us play the game and doesn't exist without returning a 404.");
		} catch (RetrofitError e) {
			// Make sure we got a 404
			assertEquals(404, e.getResponse().getStatus());
		}
	}
	
	/** Test to validate user balance */
	@Test
	public void insufficientBalance() throws Exception {

		ExternalUser user = new ExternalUser("user", 50, "PokerStars");
		ExternalUser received = readWriteSlotSvcUser.startGame(user);
		readWriteSlotSvcUser.playGame(received.getId(), 40);

		try {
			// Play again.
			readWriteSlotSvcUser.playGame(received.getId(), 30);
			fail("The server let us play without returning a 400");
		} catch (RetrofitError e) {
			// Make sure we got a 400 Bad Request
			assertEquals(400, e.getResponse().getStatus());
		}

		// We play again
		Transaction transaction = readWriteSlotSvcUser.playGame(received.getId(), 10);
		Transaction transactionReceived = readWriteSlotSvcUser.getTransactionById(transaction.getId());
		assertEquals(transaction, transactionReceived);
	}
	
	/** Test to validate security (OAuth) */
	@Test
	public void testDenyGameWithoutOAuth() throws Exception {
		ErrorRecorder error = new ErrorRecorder();

		// Create an insecure version
		SlotSvcApi insecurevideoService = new RestAdapter.Builder()
				.setEndpoint(TEST_URL).setLogLevel(LogLevel.NONE)
				.setErrorHandler(error).build().create(SlotSvcApi.class);
		try {
			ExternalUser user1 = new ExternalUser("user1", 100, "PokerStars");
			insecurevideoService.startGame(user1);

			fail("Yikes, the security setup is horribly broken and didn't require the user to authenticate!!");

		} catch (Exception e) {
			assertEquals(HttpStatus.SC_UNAUTHORIZED, error.getError()
					.getResponse().getStatus());
		}
	}
}
