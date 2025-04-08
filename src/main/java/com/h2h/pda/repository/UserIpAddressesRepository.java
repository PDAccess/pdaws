package com.h2h.pda.repository;

import com.h2h.pda.entity.UserIpAddresses;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserIpAddressesRepository extends CrudRepository<UserIpAddresses, Integer> {
    List<UserIpAddresses> findAllByUserId(String userId);
}
