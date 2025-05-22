package net.aldane.cash_balance.controller;

import jakarta.validation.Valid;
import net.aldane.cash_balance.service.StatusService;
import net.aldane.cash_balance_api_server_java.api.StatusApi;
import net.aldane.cash_balance_api_server_java.model.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StatusController implements StatusApi {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @Override
    public ResponseEntity<Status> createStatus(@Valid Status status) {
        var result = statusService.createStatus(status);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteStatus(Long statusId) {
        var status = statusService.deleteStatus(statusId);
        return status ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Status> getStatusById(Long statusId) {
        var status = statusService.getStatusById(statusId);
        return status != null ? ResponseEntity.ok(status) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<Status>> getStatus() {
        var states = statusService.getStatus();
        return ResponseEntity.ok(states);
    }

    @Override
    public ResponseEntity<Status> updateStatus(@Valid Status status) {
        var result = statusService.updateStatus(status);
        return  result != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
    }
}
