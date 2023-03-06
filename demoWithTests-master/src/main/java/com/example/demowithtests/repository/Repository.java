package com.example.demowithtests.repository;

import com.example.demowithtests.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
//@Component
public interface Repository extends JpaRepository<Employee, Integer> {

    Employee findByName(String name);

    public List<Employee> findEmployeeByIsDeletedNull();

    @Query(value = " select e from Employee e where e.country=:country ")
    List<Employee> findEmployeeByCountry(String country);

    @Query(value = "select e from Employee e join e.addresses a where a.city=:city")
    List<Employee> findEmployeeByCity(String city);


    @Query(value = " select * from users e join addresses a  on  e.id = a.employee_id  where a.city=:city", nativeQuery = true)
    List<Employee> findEmployeeByCitySQL(String city);

}
