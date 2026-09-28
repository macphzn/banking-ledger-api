package com.enoch.bankledger.services;

import com.enoch.bankledger.dto.customer.CustomerRequest;
import com.enoch.bankledger.dto.customer.CustomerResponse;
import com.enoch.bankledger.entity.Customer;
import com.enoch.bankledger.entity.User;
import com.enoch.bankledger.repository.CustomerRepository;
import com.enoch.bankledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerResponse createCustomer(CustomerRequest request) {
        User currentUser = getCurrentUser();

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .bvn(request.getBvn())
                .nin(request.getNin())
                .user(currentUser)
                .build();

        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    public List<CustomerResponse> getMyCustomers() {
        User currentUser = getCurrentUser();
        return customerRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CustomerResponse getCustomerById(Long id) {
        User currentUser = getCurrentUser();
        Customer customer = customerRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapToResponse(customer);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .bvn(customer.getBvn())
                .nin(customer.getNin())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}