package fpt.aptech.api.resource.admin;

import fpt.aptech.api.models.Region;
import fpt.aptech.api.service.IManagementRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api")
public class ManagementRegionResource {

    @Autowired
    IManagementRegion service;
    
    @PostMapping("/create-region")
    public ResponseEntity<Region> createRegion(@RequestBody Region region) {
        Region createdRegion = service.createRegion(region);
        if (createdRegion != null) {
            return ResponseEntity.ok(createdRegion);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("edit-region/{regionId}")
    public ResponseEntity<Region> editRegion(@PathVariable int regionId, @RequestBody Region region) {
        region.setId(regionId);
        Region editedRegion = service.editRegion(region);
        if (editedRegion != null) {
            return ResponseEntity.ok(editedRegion);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("delete-region/{regionId}")
    public ResponseEntity<Region> deleteRegion(@PathVariable int regionId) {
        Region deletedRegion = service.deleteRegion(regionId);
        if (deletedRegion != null) {
            return ResponseEntity.ok(deletedRegion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("region/{regionId}")
    public ResponseEntity<Region> getRegionById(@PathVariable int regionId) {
        Region region = service.getRegionById(regionId);
        if (region != null) {
            return ResponseEntity.ok(region);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
