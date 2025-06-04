package com.hamcam.back.controller.unit;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/units")
public class UnitController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<String> getUnits(@RequestParam String subject) {
        String sql = "SELECT unit FROM units WHERE subject = ?";
        return jdbcTemplate.queryForList(sql, String.class, subject);
    }
}
