package com.aleef.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.aleef.models.TokenInfo;

@Service
public interface TokenInfoRepository extends CrudRepository<TokenInfo, Integer> {

}
