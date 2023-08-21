package fpt.aptech.api.service;

import fpt.aptech.api.models.Region;
import fpt.aptech.api.respository.RegionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RegionGetListService {
    @Autowired
    private RegionRepository repository;
    
    public List<Region> getAllRegion(){
        return repository.findAll();
    }
            
    
}
