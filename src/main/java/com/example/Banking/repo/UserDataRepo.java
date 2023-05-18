package com.example.Banking.repo;

import com.example.Banking.model.UserDataModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepo extends MongoRepository<UserDataModel,String> {
    UserDataModel findUserDataModelByUserEmail(String userEmail);

}
