package io.romeh.postgresembeddeddaotesting.rest.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import io.romeh.postgresembeddeddaotesting.domain.Customer;

/**
 * @author romeh
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

	CustomerDto mapCustomerToDto(Customer customer);

	Customer mapeDtoToCustomer(CustomerDto customerDto);
}
