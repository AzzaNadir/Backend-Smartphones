package com.example.repository;

import com.example.model.Smartphone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmartphoneRepository extends JpaRepository<Smartphone, Long> {

}
