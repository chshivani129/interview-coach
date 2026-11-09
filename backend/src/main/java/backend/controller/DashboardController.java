package backend.controller;

import backend.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{jobId}/dashboard")
    public DashboardService.DashboardResponse getDashboard(
            @PathVariable Long jobId) {

        return dashboardService.getDashboard(jobId);
    }
}