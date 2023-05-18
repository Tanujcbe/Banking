package com.example.Banking.repo;

import com.example.Banking.model.BalanceModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AccountRepo extends MongoRepository<BalanceModel,String> {
    List<BalanceModel> findAllByAccountIdAndDateBetween(String accountId, Date startDate, Date endDate);

}
