How to Enable Service Registration for a Microservice

1. Be clear in our minds about what Service Registry brings to the table

2. Develop a Service Registry Server with Eureka

3. Make sure the service registry microservice is compliant with Spring Cloud latest version

4. Build the Service Registry Microservice to obtain a jar with the command : mvn clean package

5. Run the service registry application with the command : java -jar target/<your jar>.jar

6. Visit the site : http://localhost:8761 and check if the sites comes up

7. *************************************Client side*********************************

8. Open the pom.xml of the client microservice which in our case is the product-api

9. Visit the Spring Cloud site to find the latest version 

10. First include spring cloud maven dependency

<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>Finchley.SR1</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
</dependencyManagement>
	
11. Add spring-cloud-erke dependency for the client side

<!-- Only for Service Discovery -->

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

12. Add a bootstram.yml under src/main/resources and add the property 

spring:
  application:
    name: rollingstone-ecommerce-product-api
    
13. Add the following property to application.yml

eureka:
  client:
    serviceUrl: 
      defaultZone:  http://localhost:8761/eureka/

14. Add the following annotation to the spring boot main program

@EnableDiscoveryClient
      
15. Build the product-api with : mvn clean package

16. Run the product-api with : java -jar target<your-har>.jar

17. Visit http://localhost:8761 to see if product api has registered

How to add Config Client Feature to the Product Microservice

1. Add the following dependencies to your pom.xml

	<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>
		
	<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-client</artifactId>
	</dependency>
		
2. Add the following to your bootstrap.yml

  cloud:
    config:
      uri: http://localhost:9000
      
3. Rebuild and restart

4. Test

How to add Declarative Client Feign to the ProductAPI Microservice

1. Add this maven dependency to your pom.xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

2. Add this annotation to the SpringBoot Main Class

@EnableFeignClients

3. Create a new java package called com.rollingstone.spring.service.feign

4. Add a new java interface named as CategoryFeignInterface with the following code

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

5. Review How the CategoryController works as we will need the following method in our Feign Interface

 /*---Get a Category by id---*/
   @GetMapping("/category/{id}")
   @ResponseBody
   public Category getCategory(@PathVariable("id") long id) {
	  Optional<Category> returnedCategory = CategoryService.get(id);
	  Category Category  = returnedCategory.get(); 
	  
	  CategoryEvent CategoryCreatedEvent = new CategoryEvent("One Category is retrieved", Category);
      eventPublisher.publishEvent(CategoryCreatedEvent);
      return Category;
   }
   
6. Add dependency to ProductServiceImplk

  @Autowired
   CategoryFeignClient categoryClient;
   
7. Change ProductServiceImpl.java save method to look like this

	@Override
	public Product save(Product product) {

		Category category = null;
		Category parentCategory = null;
		
		if (product.getCategory() == null) {
			logger.info("Product Category is null :");
			throw new HTTP400Exception("Bad Request as Category Can not be empty");
		} else {
			logger.info("Product Category is not null :" + product.getCategory());
			logger.info("Product Category is not null ID :" + product.getCategory().getId());
			try {
				category = categortyClient.getCategory(product.getCategory().getId());
			}
			catch(Exception e) {
				logger.info("Product Category Does not Exist :" + product.getCategory().getId());
				throw new HTTP400Exception("Bad Request as the Category Provided is an Invalid one");
			}
		}
		if (product.getParentCategory() == null) {
			logger.info("Product Parent Category is null :");
			throw new HTTP400Exception("Bad Request as Parent Category Can not be empty");
		} else {
			logger.info("Product Parent Category is not null :" + product.getParentCategory());
			logger.info("Product Parent Category is not null Id :" + product.getParentCategory().getId());
			try {
				category = categortyClient.getCategory(product.getParentCategory().getId());
			}
			catch(Exception e) {
				logger.info("Product Parent Category Does not Exist :" + product.getParentCategory().getId());
				throw new HTTP400Exception("Bad Request as the Parent Category Provided is an Invalid one");
			}

		}

		return productDao.save(product);
	}
	   
8. Build Run and Test in order

	A. Service Discovery
	B. Config Server
	C. CategoryServer
	D. ProductServer
		   
9. Test Data

URL : http://localhost:8081/product

Body for Happy Path Everything wroks

{
  "productCode": "P1249493777",
  "productName": "Girls New Formal Leather Shoes",
  "shortDescription": "Girls Formal Leather Dress Shoes",
  "longDescription": "Girls's Blue Formal Dress Shoes with multile sizes",
  "canDisplay": true,
  "parentCategory": {
    "id": 3,
    "categoryName": "Men's Clothing",
    "categoryDescription": "Men's Branded Designer Clothing"
  },
  "category": {
    "id": 4,
    "categoryName": "Young Men's Clothing",
    "categoryDescription": "Young Men's Branded Designer Clothing"
  },
  "deleted": false,
  "automotive": false,
  "international": false
}

Category Invalid

{
  "productCode": "P1249493777",
  "productName": "Girls New Formal Leather Shoes",
  "shortDescription": "Girls Formal Leather Dress Shoes",
  "longDescription": "Girls's Blue Formal Dress Shoes with multile sizes",
  "canDisplay": true,
  "parentCategory": {
    "id": 3,
    "categoryName": "Men's Clothing",
    "categoryDescription": "Men's Branded Designer Clothing"
  },
  "category": {
    "id": 12,
    "categoryName": "Young Men's Clothing",
    "categoryDescription": "Young Men's Branded Designer Clothing"
  },
  "deleted": false,
  "automotive": false,
  "international": false
}

Parent Category Invalid

{
  "productCode": "P1249493777",
  "productName": "Girls New Formal Leather Shoes",
  "shortDescription": "Girls Formal Leather Dress Shoes",
  "longDescription": "Girls's Blue Formal Dress Shoes with multile sizes",
  "canDisplay": true,
  "parentCategory": {
    "id": 12,
    "categoryName": "Men's Clothing",
    "categoryDescription": "Men's Branded Designer Clothing"
  },
  "category": {
    "id": 4,
    "categoryName": "Young Men's Clothing",
    "categoryDescription": "Young Men's Branded Designer Clothing"
  },
  "deleted": false,
  "automotive": false,
  "international": false
}

How to Add Hystrix Circuit Breaker 

1. Add Hystrix starter and dashboard dependencies.

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
		</dependency>

2. Add @EnableCircuitBreaker annotation

3. Add annotation @HystrixCommand(fallbackMethod = "saveWithoutValidation")

5. Define fallback method 

	public Product saveWithoutValidation(Product product) {
		logger.info("Cirquit Breaker Enabled Saving Product without Category Validaton:");

		return productDao.save(product);
	}
	
6. Test

	A. With invalid category and parent category and watch the Database SQL Foreign Key Exception
	B. Stop Category Server
	C. Test with valid category and parent category and watch the success
	

