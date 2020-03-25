package org.zitroprueba.casino.client;

import java.util.Collection;

import org.zitroprueba.casino.repository.Transaction;
import org.zitroprueba.casino.repository.ExternalUser;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface SlotSvcApi {
	
	public static final String SLOT_SVC_PATH = "/slot";
	
	public static final String TOKEN_PATH = "/oauth/token";
	
	public static final String BET_PARAMETER = "bet";
	
	@GET(SLOT_SVC_PATH)
	public Object getGameConfiguration();
	
	@GET(SLOT_SVC_PATH + "/bet")
	public Collection<Transaction> getTransactionList();
	
	@GET(SLOT_SVC_PATH + "/{id}/bet")
	public Transaction getTransactionById(@Path("id") long id);
	
	
	@POST(SLOT_SVC_PATH)
	public ExternalUser startGame(@Body ExternalUser user);	
	
	@POST(SLOT_SVC_PATH + "{id}/bet")
	public Transaction playGame(@Path("id") long id, @Query(BET_PARAMETER) double bet);
	
	
	


}
