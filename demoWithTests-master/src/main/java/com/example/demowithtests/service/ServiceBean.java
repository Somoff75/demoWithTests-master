package com.example.demowithtests.service;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.repository.Repository;
import com.example.demowithtests.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
@org.springframework.stereotype.Service
public class ServiceBean implements Service {

    private final Repository repository;

    @Override
    public Employee create(Employee employee) {

        if (employee.getName() == null) {
            log.info("!-- NewUserNameIsNotSetException requested");
            throw new NewUserNameIsNotSetException();
        }
        try {
            repository.save(employee);
        } catch (DataAccessException e) {
            log.info("!-- SAVE operation failed!");
            throw new SaveException();
        }
        return employee;
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employeeList;// = new ArrayList<>();
        try {
            employeeList = repository.findAll();
        } catch (Exception e) {
            log.info("!-- getAll operation failed!");
            throw new GetAllException();
        }
        return employeeList;
    }

    @Override
    public Employee getById(Integer id) {
        Employee employee = repository.findById(id)
                // .orElseThrow(() -> new EntityNotFoundException("Employee not found with id = " + id));
                .orElseThrow(ResourceNotFoundException::new);
        if (employee.getIsDeleted()) {
            log.info("!-- getById: Employee was deleted with id = " + id);
            throw new EntityWasDeletedException();
        }
        return employee;
    }

    @Override
    public Employee updateById(Integer id, Employee employee) {
        return repository.findById(id)
                .map(entity -> {
                    entity.setName(employee.getName());
                    entity.setEmail(employee.getEmail());
                    entity.setCountry(employee.getCountry());
                    return repository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id = " + id));
    }

    @Override
    public void removeById(Integer id) {
        //repository.deleteById(id);
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id = " + id));
        //.orElseThrow(ResourceWasDeletedException::new);
        employee.setIsDeleted(true);
        // repository.delete(employee);
        repository.save(employee);
    }

    @Override
    public void removeAll() {
        repository.deleteAll();

    }

    @Override
    public List<Employee> processor() {
        log.info("Start: replaceNull");
        List<Employee> replaceNull = repository.findEmployeeByIsDeletedNull();
        for (Employee emp : replaceNull) {
            emp.setIsDeleted(Boolean.FALSE);
        }
        log.info("replaceNull = {} ", replaceNull);
        log.info("End: replaceNull");
        return repository.saveAll(replaceNull);

    }

    @Override
    public List<Employee> sendEmailByCountry(String country, String text) {
        List<Employee> employees = repository.findEmployeeByCountry(country);
        mailSender(extracted(employees), text);
        return employees;
    }

    @Override
    public List<Employee> sendEmailByCity(String city, String text) {
        List<Employee> employees = repository.findEmployeeByCity(city);
        mailSender(extracted(employees), text);

        return employees;
    }

    @Override
    public List<Employee> sendEmailByCitySQL(String city, String text) {
        List<Employee> employees = repository.findEmployeeByCitySQL(city);
        mailSender(extracted(employees), text);
        return employees;
    }

    private static List<String> extracted(List<Employee> employees) {
        List<String> emails = new ArrayList<>();
        for (Employee emp : employees) {
            emails.add(emp.getEmail());
        }
        return emails;
    }

    public void mailSender(List<String> emails, String text) {
        log.info("Emails were successfully sent to: " + emails);
    }
}
