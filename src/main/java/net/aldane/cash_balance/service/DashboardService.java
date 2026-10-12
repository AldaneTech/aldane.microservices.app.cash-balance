package net.aldane.cash_balance.service;

import net.aldane.cash_balance_api_server_java.model.CategoryTotals;
import net.aldane.cash_balance_api_server_java.model.DashboardSummary;
import net.aldane.cash_balance_api_server_java.model.MonthlyTotals;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    DashboardSummary getDashboardSummary(Long walletId, LocalDate from, LocalDate to);
    List<CategoryTotals> getDashboardByCategory(Long walletId, LocalDate from, LocalDate to);
    List<MonthlyTotals> getDashboardMonthly(Long walletId, LocalDate from, LocalDate to);
}
