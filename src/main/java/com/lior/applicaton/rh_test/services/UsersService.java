package com.lior.applicaton.rh_test.services;

import com.lior.applicaton.rh_test.model.User;
import com.lior.applicaton.rh_test.repos.UsersRepository;
import com.lior.applicaton.rh_test.util.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//TODO
@Service
@Transactional
public class UsersService {
    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public Optional<User> findUserByUsername(String username){
        return usersRepository.findByUsername(username);
    }

    public User findOne(int id) {
        return usersRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void save(User user) {
       usersRepository.save(user);
    }

    @Transactional
    public void update(int id, User updatedUser) {
        updatedUser.setId(id);
        usersRepository.save(updatedUser);
    }

    @Transactional
    public void delete(int id) {
        usersRepository.deleteById(id);
    }
}
