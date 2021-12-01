package com.dima

import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.concurrent.Flow

@SpringBootApplication
class DemoReactiveSqlApplication

fun main(args: Array<String>) {
	runApplication<DemoReactiveSqlApplication>(*args)
}
@Table
data class Customer(
	@Id
	val id: Long,
	val name: String
)

interface CustomerRepository : ReactiveCrudRepository<Customer, Long>{
	@Query("select * from customer where name=?")
	suspend fun findCustomerByCustomerName(name: String) : Mono<Customer>

}

@RestController
@RequestMapping("/customer")
class CustomerController(val customerRepository: CustomerRepository){

	@GetMapping
	suspend fun getAllCustomer() = customerRepository.findAll().asFlow()

	@GetMapping("/{name}")
	suspend fun getCustomerByName(@PathVariable name: String) : Customer? = customerRepository.findCustomerByCustomerName(name).awaitFirstOrNull()
}