package com.rollingstone.spring.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rollingstone.spring.model.Category;

@FeignClient(name = "rollingstone-ecommerce-category-api")
public interface CategoryFeignClient {

	  @GetMapping("/category/{id}")
	   public Category getCategory(@PathVariable("id") long id);
}
