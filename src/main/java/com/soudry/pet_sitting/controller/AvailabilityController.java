package com.soudry.pet_sitting.controller;

import com.soudry.pet_sitting.Services.AvailabilityService;
import com.soudry.pet_sitting.objects.DailyAvailability;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/availability")
@CrossOrigin(origins = "http://localhost:5173")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController( AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/getDates")
    public Map<LocalDate, DailyAvailability> getAvailabilityForMonth(@RequestParam int year, @RequestParam int month) throws IOException {
        return availabilityService
                .getAvailabilityForMonth(year, month);
    }
}