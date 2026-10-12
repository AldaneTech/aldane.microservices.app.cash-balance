package net.aldane.cash_balance;

import net.aldane.cash_balance.controller.*;
import net.aldane.cash_balance.controller.admin.AuthController;
import net.aldane.cash_balance.service.*;
import net.aldane.cash_balance_api_server_java.model.*;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EndpointContractTest {
    DashboardService queries = mock(DashboardService.class);
    AccountEntryService entryService = mock(AccountEntryService.class);
    AuthService authService = mock(AuthService.class);
    UserService userService = mock(UserService.class);
    MockMvc mvc;
    @BeforeEach void setup() {
        var entries = new AccountEntryController(entryService);
        var users = new UserController(userService);
        var auth = new AuthController(authService);
        mvc = MockMvcBuilders.standaloneSetup(entries, users, auth, new DashboardController(queries)).build();
    }
    @Test void generatedSearchContractBindsQueryParameters() throws Exception {
        when(entryService.searchAccountEntries(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new AccountEntryPage().items(List.of()).page(2).size(10).totalElements(0L).totalPages(0));
        mvc.perform(get("/account-entries").param("walletId", "7").param("from", "2026-10-01").param("to", "2026-10-12")
                .param("categoryId", "3").param("typeId", "2").param("search", "food").param("page", "2").param("size", "10").param("sortBy", "amount").param("direction", "asc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items").isEmpty()).andExpect(jsonPath("$.page").value(2));
        verify(entryService).searchAccountEntries(7L, LocalDate.of(2026,10,1), LocalDate.of(2026,10,12), 3L, 2L, "food", 2, 10, "amount", "asc");
    }
    @Test void dashboardRoutesSerializeDecimalsAndNullCategory() throws Exception {
        when(queries.getDashboardSummary(null,null,null)).thenReturn(new DashboardSummary().income(new BigDecimal("12.34")).expenses(BigDecimal.ZERO).net(new BigDecimal("12.34")).balance(new BigDecimal("12.34")).entryCount(1L));
        mvc.perform(get("/dashboard/summary")).andExpect(status().isOk()).andExpect(jsonPath("$.income").value(12.34));
        when(queries.getDashboardByCategory(null,null,null)).thenReturn(List.of(new CategoryTotals().categoryId(null).categoryName(null).income(BigDecimal.ZERO).expenses(BigDecimal.ZERO).net(BigDecimal.ZERO).entryCount(0L)));
        mvc.perform(get("/dashboard/by-category")).andExpect(status().isOk()).andExpect(jsonPath("$[0].categoryId").doesNotExist());
        when(queries.getDashboardMonthly(null,null,null)).thenReturn(List.of());
        mvc.perform(get("/dashboard/monthly")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }
    @Test void ownProfileRouteIsImplemented() throws Exception {
        when(userService.getMe()).thenReturn(new User().id(1L).username("alice"));
        mvc.perform(get("/me")).andExpect(status().isOk()).andExpect(jsonPath("$.username").value("alice")).andExpect(jsonPath("$.password").doesNotExist());
    }
    @Test void passwordBodyIsValidatedAndSuccessReturns204() throws Exception {
        mvc.perform(post("/change-password").contentType("application/json").content("{\"currentPassword\":\"old-password\",\"newPassword\":\"new-password\"}"))
                .andExpect(status().isNoContent());
        verify(authService).changePassword(any()); clearInvocations(authService);
        mvc.perform(post("/change-password").contentType("application/json").content("{\"currentPassword\":\"old-password\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(authService);
    }
}
