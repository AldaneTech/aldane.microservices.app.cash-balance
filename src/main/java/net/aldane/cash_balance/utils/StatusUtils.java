package net.aldane.cash_balance.utils;

import net.aldane.cash_balance.repository.db.StatusDbRepository;
import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.service.StatusService;
import org.springframework.stereotype.Component;

@Component
public class StatusUtils {

    private StatusService statusService;
    private final StatusDbRepository statusDbRepository;

    public StatusUtils(StatusDbRepository statusDbRepository) {
        this.statusDbRepository = statusDbRepository;
    }

    public StatusDb getActiveStatus() {
        return statusDbRepository.findById(1L).orElse(null);
    }

    public StatusDb getDeletedStatus() {
        return statusDbRepository.findById(2L).orElse(null);
    }
    public StatusDb findStatusDb(Long statusId) {
        return statusDbRepository.findById(statusId).orElse(null);
    }

}
