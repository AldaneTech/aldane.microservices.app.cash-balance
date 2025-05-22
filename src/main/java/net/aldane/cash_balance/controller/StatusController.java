package net.aldane.cash_balance.controller;

import jakarta.validation.Valid;
import net.aldane.cash_balance_api_server_java.api.StatusApi;
import net.aldane.cash_balance_api_server_java.model.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatusController implements StatusApi {
    @Override
    public ResponseEntity<Status> createStatus(@Valid Status status) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteStatus(Long aLong) {
        return null;
    }

    @Override
    public ResponseEntity<List<Status>> getStatus() {
        return null;
    }

    @Override
    public ResponseEntity<Status> getStatusById(Long aLong) {
        return null;
    }

    @Override
    public ResponseEntity<Status> updateStatus(@Valid Status status) {
        return null;
    }
}
