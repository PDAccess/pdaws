package com.h2h.pda.api.admin;

import com.h2h.pda.pojo.StatisticsDto;
import com.h2h.pda.service.api.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/statistics")
public class StatisticsController {
    @Autowired
    AdminService adminService;

    @GetMapping()
    public ResponseEntity<StatisticsDto> getStatistics() {
        return new ResponseEntity<>(adminService.statistics(), HttpStatus.OK);
    }
}

