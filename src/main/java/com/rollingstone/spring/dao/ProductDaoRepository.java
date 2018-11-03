package com.rollingstone.spring.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.rollingstone.spring.model.Product;

public interface ProductDaoRepository extends PagingAndSortingRepository<Product, Long> {

	Page<Product> findAll(Pageable pageable);
}
