package com.example.jobtracker.repository;

import java.time.LocalDate;

public interface WeeklyActivityProjection {

    LocalDate getDate();

    Long getCount();
}
