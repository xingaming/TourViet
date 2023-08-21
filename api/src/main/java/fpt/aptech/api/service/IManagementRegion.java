package fpt.aptech.api.service;

import fpt.aptech.api.models.Region;


public interface IManagementRegion {
    
    Region getRegionById(int regionId);
    Region createRegion(Region region);
    Region editRegion(Region region);
    Region deleteRegion(int regionId);
}
