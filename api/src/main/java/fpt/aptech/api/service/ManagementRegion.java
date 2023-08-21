package fpt.aptech.api.service;

import fpt.aptech.api.models.Region;
import fpt.aptech.api.respository.RegionRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementRegion implements IManagementRegion {

    @Autowired
    RegionRepository regionRepository;

    @Override
    public Region createRegion(Region region) {
        try {
            return regionRepository.save(region);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Region editRegion(Region region) {
        try {
            return regionRepository.save(region);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Region deleteRegion(int regionId) {
        // Trước khi xóa, truy vấn đối tượng Region bằng regionId
        Optional<Region> regionToDelete = regionRepository.findById(regionId);

        // Lưu thông tin của đối tượng Region vào biến
        Region deletedRegion = null;
        if (regionToDelete.isPresent()) {
            deletedRegion = regionToDelete.get();

            // Xóa đối tượng Region
            regionRepository.deleteById(regionId);
        }

        // Trả về thông tin của đối tượng Region đã lưu
        return deletedRegion;
    }

    @Override
    public Region getRegionById(int regionId) {
        try {
            return regionRepository.findById(regionId).get();
        } catch (Exception e) {
            return null;
        }
    }

}
