package ru.indemion.carservice.models.order;

import ru.indemion.carservice.common.Period;

import java.util.Arrays;
import java.util.List;

public final class FilterParams {
    private final List<OrderStatus> statuses;
    private final Integer masterId;
    private final Period estimatedWorkStartInPeriod;

    private FilterParams(Builder builder) {
        this.statuses = builder.statuses;
        this.masterId = builder.masterId;
        this.estimatedWorkStartInPeriod = builder.estimatedWorkStartInPeriod;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<OrderStatus> getStatuses() {
        return statuses;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public Period getEstimatedWorkStartInPeriod() {
        return estimatedWorkStartInPeriod;
    }

    public static class Builder {
        private List<OrderStatus> statuses;
        private Integer masterId;
        private Period estimatedWorkStartInPeriod;

        public Builder statuses(OrderStatus... statuses) {
            this.statuses = Arrays.asList(statuses);
            return this;
        }

        public Builder masterId(Integer masterId) {
            this.masterId = masterId;
            return this;
        }

        public Builder estimatedWorkStartInPeriod(Period period) {
            this.estimatedWorkStartInPeriod = period;
            return this;
        }

        public FilterParams build() {
            return new FilterParams(this);
        }
    }
}
