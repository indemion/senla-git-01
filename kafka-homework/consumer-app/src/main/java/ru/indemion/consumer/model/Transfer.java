package ru.indemion.consumer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    private UUID id;
    @Column(name = "from_bank_account_id")
    private Integer fromBankAccountId;
    @Column(name = "to_bank_account_id")
    private Integer toBankAccountId;
    private Integer amount;
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    public Transfer() {
    }

    public Transfer(UUID id, Integer fromBankAccountId, Integer toBankAccountId, Integer amount, TransferStatus status) {
        this.id = id;
        this.fromBankAccountId = fromBankAccountId;
        this.toBankAccountId = toBankAccountId;
        this.amount = amount;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Integer getFromBankAccountId() {
        return fromBankAccountId;
    }

    public Integer getToBankAccountId() {
        return toBankAccountId;
    }

    public Integer getAmount() {
        return amount;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFromBankAccountId(Integer fromBankAccountId) {
        this.fromBankAccountId = fromBankAccountId;
    }

    public void setToBankAccountId(Integer toBankAccountId) {
        this.toBankAccountId = toBankAccountId;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }
}
