package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.StatusMapper;
import net.aldane.cash_balance.repository.db.StatusDbRepository;
import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance_api_server_java.model.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class StatusServiceImpl implements StatusService {

    private final StatusDbRepository statusDbRepository;
    private final StatusMapper statusMapper;
    private final Logger log = LogManager.getLogger(this.getClass());

    public StatusServiceImpl(StatusDbRepository statusDbRepository, StatusMapper statusMapper) {
        this.statusDbRepository = statusDbRepository;
        this.statusMapper = statusMapper;
    }

    @Override
    public List<Status> getStatus() {
        try {
            var statesList = statusDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return statusMapper.statusDbListToStatusList(statesList);
        } catch (Exception e) {
            log.error("Error when querying status in database");
            return new ArrayList<>();
        }
    }

    @Override
    public Status getStatusById(Long statusId) {
        try {
            var status = statusDbRepository.findById(statusId).orElse(null);
            return statusMapper.statusDbToStatus(status);
        } catch (Exception e) {
            log.error("Error obtaining status with id: {}", statusId);
            return null;
        }
    }

    @Override
    public Status createStatus(Status status) {
        try {
            if(status.getName() == null || status.getName().trim().isBlank()){
                log.info("Status name can't be empty");
                return null;
            }
            StatusDb newStatus = new StatusDb();
            newStatus.setName(status.getName());
            var statusSaved = statusDbRepository.save(newStatus);
            return statusMapper.statusDbToStatus(statusSaved);
        } catch (Exception e) {
            log.error("Status name alredy exists");
            return null;
        }
    }

    @Override
    public boolean deleteStatus(Long id) {
        try {
            var statusDb = statusDbRepository.findById(id).orElse(null);
            if(statusDb != null){
                statusDbRepository.deleteById(id);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting status with id: {}", id);
        }
        return false;
    }

    @Override
    public Status updateStatus(Status status) {
        try {
            var statusDb = statusDbRepository.findById(status.getId()).orElse(null);
            if(statusDb != null){
                if(status.getName() != null || !status.getName().trim().isBlank()){
                    statusDb.setName(status.getName());
                    return statusMapper.statusDbToStatus(statusDbRepository.save(statusDb));
                }
            }
        } catch (Exception e) {
            log.error("Error updating status with id: {}", status.getId());
        }
        return null;
    }
}
