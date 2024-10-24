package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j  // Utilise SLF4J pour le logging
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public ResponseEntity<?> createRole(Role role){
        Role newRole = roleRepository.findByRoleName(role.getRoleName());
        if(newRole!=null){
            return ResponseEntity.badRequest().body("Role already exists");
        }
       Role save= roleRepository.save(role);
       return new ResponseEntity<>(save,HttpStatus.CREATED);
}

    // find all users
    public List<Role> getAllRole(){
      log.info("Fetching all roles.");
      List<Role> roleList = roleRepository.findAll();
      log.info("Found {} roles.", roleList.size());
      return roleList;
    }

    //find role by Id
    public ResponseEntity<Role> getRoleById(int id){
        log.info("Fetching role by ID: {}", id);
        return roleRepository.findById(id).map(role -> {
                    log.info("Role found with ID: {}", id);
                    return ResponseEntity.ok(role);
                })
                .orElseGet(() -> {
                    log.error("role not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    //Update role
    public ResponseEntity<?> UpdateRole(int id, Role role){
        log.info("Updating role with ID: {}", id);
        return roleRepository.findById(id).map(existingRole-> {
            existingRole.setRoleName(role.getRoleName());
            log.info("Role with ID: {} updated successfully", id);
            roleRepository.save(existingRole);
            return ResponseEntity.ok("Role update sucessfully");
        }).orElseGet(()-> {
            log.error("Role not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    //delete role
    public ResponseEntity<?> deleteRole(int id){
        log.info("Deleting role with ID: {}", id);
        return roleRepository.findById(id).map(role -> {
            roleRepository.delete(role);
            log.info("Role with ID: {} deleted successfully", id);
            return ResponseEntity.ok("Role deleted successfully");
        }).orElseGet(() -> {
            log.error("Role not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }


}
