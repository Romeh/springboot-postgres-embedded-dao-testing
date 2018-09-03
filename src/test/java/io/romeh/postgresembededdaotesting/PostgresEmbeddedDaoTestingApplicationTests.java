package io.romeh.postgresembededdaotesting;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.romeh.postgresembeddeddaotesting.dao.CustomerRepository;
import io.romeh.postgresembeddeddaotesting.domain.Customer;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DbConfig.class})
@ActiveProfiles("DaoTest")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:dao/TestData.sql")
public class PostgresEmbeddedDaoTestingApplicationTests {

	@Autowired
	private CustomerRepository customerRepository;

	@Test
	public void contextLoads() {

		customerRepository.save(Customer.builder()
				.id(new Random().nextLong())
				.address("brussels")
				.name("TestName")
				.build());

		Assert.assertTrue(customerRepository.findCustomerByName("TestName") != null);
	}


}
