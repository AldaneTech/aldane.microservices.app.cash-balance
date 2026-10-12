package net.aldane.cash_balance.controller;
import net.aldane.cash_balance.service.DashboardService;
import net.aldane.cash_balance_api_server_java.api.DashboardApi;
import net.aldane.cash_balance_api_server_java.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
public class DashboardController implements DashboardApi {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) { this.dashboardService = dashboardService; }
    @Override public ResponseEntity<DashboardSummary> getDashboardSummary(Long walletId, LocalDate from, LocalDate to) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(walletId, from, to));
    }
    @Override public ResponseEntity<List<CategoryTotals>> getDashboardByCategory(Long walletId, LocalDate from, LocalDate to) {
        return ResponseEntity.ok(dashboardService.getDashboardByCategory(walletId, from, to));
    }
    @Override public ResponseEntity<List<MonthlyTotals>> getDashboardMonthly(Long walletId, LocalDate from, LocalDate to) {
        return ResponseEntity.ok(dashboardService.getDashboardMonthly(walletId, from, to));
    }
}
