package carservice.dao;

import carservice.models.master.Master;
import carservice.models.master.MasterStatus;

public record MasterDTO(int id, String firstname, String lastname, String status, Integer orderAtWorkId) {
    public Master toModel() {
        return new Master(id, firstname, lastname, MasterStatus.parse(status), orderAtWorkId);
    }
}
