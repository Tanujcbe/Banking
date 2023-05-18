package com.example.Banking.repo;

import com.example.Banking.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<UserModel,String> {
    public boolean existsUserModelByUserEmail(String userEmail);
    UserModel findByUserEmail(String userEmail);
}
