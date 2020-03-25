package org.zitroprueba.casino.repository;

import java.util.Collection;

import org.zitroprueba.casino.client.SlotSvcApi;
import org.zitroprueba.casino.repository.Transaction;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path=SlotSvcApi.SLOT_SVC_PATH)
public interface TransactionRepository extends CrudRepository<Transaction, Long>{

	public Collection<Transaction> findByUsername(@Param("username") String username);
	
	public Collection<Transaction> findByGame(@Param("game") String game);
	
	public Collection<Transaction> findByBetLessThan(@Param("bet") double maxbet);
}
