package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.Status;

import java.util.List;

public interface StatusService {

    List<Status> getStatus();

    Status getStatusById(Long statusId);

    Status createStatus(Status status);

    boolean deleteStatus(Long id);

    Status updateStatus(Status status);

}
