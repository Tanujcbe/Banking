package com.example.Banking.repo;

import com.example.Banking.model.TransactionModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepo extends MongoRepository<TransactionModel,String> {
    List<TransactionModel> findByYourAccountAndDateBetween(String accountId, Date startDate, Date endDate);
    List<TransactionModel> findByTargetAccountAndDateBetween(String accountId, Date startDate, Date endDate);
}
