package common.dto;

import java.util.UUID;

public class TransferMessage {
    private UUID id;
    private Integer fromId;
    private Integer toId;
    private Integer amount;

    public TransferMessage() {
    }

    public TransferMessage(UUID id, Integer fromId, Integer toId, Integer amount) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
