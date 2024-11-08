package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.mentor.GetScheduleResponseDTO;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.ScheduleStatus;
import com.swd392.mentorbooking.entity.Schedule;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AccountRepository accountRepository;

    public Response<List<GetScheduleResponseDTO>> getMentorActiveSchedules(long mentorId) {

        Account mentorAccount = accountRepository.findById(mentorId).orElse(null);

        if (mentorAccount == null) {
            return new Response<>(204, "No mentor with such ID!", null);
        }

        List<Schedule> listSchedule = scheduleRepository.findAllByAccountAndStatusAndIsDeletedFalse(mentorAccount, ScheduleStatus.ACTIVE)
                .orElseGet(Collections::emptyList);

        if (listSchedule.isEmpty()) {
            return new Response<>(204, "This mentor have no active schedules", null);
        }

        List<GetScheduleResponseDTO> data = listSchedule.stream()
                .map(schedule -> GetScheduleResponseDTO.builder()
                        .scheduleId(schedule.getId())
                        .date(schedule.getDate())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .status(schedule.getStatus())
                        .createdAt(schedule.getCreatedAt())
                        .isDeleted(schedule.getIsDeleted())
                        .build())
                .collect(Collectors.toList());

        return new Response<>(200, "Retrieved data successfully!", data);
    }
}
