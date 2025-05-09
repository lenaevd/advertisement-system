package com.lenaevd.advertisements.model;

import java.time.Period;

public enum PremiumPlan {
    DAY {
        @Override
        public Period getPeriod() {
            return Period.ofDays(1);
        }
    },
    WEEK {
        @Override
        public Period getPeriod() {
            return Period.ofWeeks(1);
        }
    },
    MONTH {
        @Override
        public Period getPeriod() {
            return Period.ofMonths(1);
        }
    };

    public abstract Period getPeriod();
}
