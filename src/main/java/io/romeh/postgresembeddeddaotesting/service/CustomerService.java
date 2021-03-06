package io.romeh.postgresembeddeddaotesting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.romeh.postgresembeddeddaotesting.dao.CustomerRepository;
import io.romeh.postgresembeddeddaotesting.domain.Customer;

/**
 * the main customer service
 */
@Service
public class CustomerService {


	private final CustomerRepository customerRepository;

	@Autowired
	public CustomerService(CustomerRepository customerRepository) {

		this.customerRepository = customerRepository;
	}

	public Customer findCustomerById(long id) {
		return customerRepository.findById(id).orElseThrow(() -> new IllegalStateException("the customer is not there"));
	}

	public Customer findCustomerByName(String name) {
		return customerRepository.findCustomerByName(name).orElseThrow(() -> new IllegalStateException("the customer is not there"));
	}

	public void createCustomer(Customer customer) {
		customerRepository.save(customer);
	}
}
