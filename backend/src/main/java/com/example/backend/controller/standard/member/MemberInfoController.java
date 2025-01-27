package com.example.backend.controller.standard.member;

import com.example.backend.dto.standard.employee.Employee;
import com.example.backend.service.standard.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/info")
public class MemberInfoController {
    private final InfoService service;

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Employee> getMemberInfo(@PathVariable String id, Authentication auth) {
        System.out.println("auth = " + auth.getName());
        if (service.hasAccess(id, auth)) {
            return ResponseEntity.ok(service.getId(id));
        } else {
            System.out.println("안됨");
            return ResponseEntity.status(403).build();
        }
//        return service.getMemberInfo();
    }

    @PutMapping("update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Employee> updateMemberInfo(@RequestBody Employee employee, Authentication auth) {
        System.out.println("employee = " + employee);
        return null;
    }
}
