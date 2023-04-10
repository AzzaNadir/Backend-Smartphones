package com.example.service;

import com.example.model.Smartphone;
import com.example.repository.SmartphoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;


@Service
@CrossOrigin
public class SmartphoneService {
    @Autowired
    SmartphoneRepository smartphoneRepository ;

    public Smartphone ajouterSmartphone(Smartphone smartphone) {
        return smartphoneRepository.save(smartphone);
    }


}
