package unical.demacs.rdm.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.dto.ScheduleViewDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.service.implementation.ScheduleServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
public class Scheduler {
    private final ScheduleRepository scheduleRepository;
    private final ModelMapperExtended modelMapperExtended;
    private final JobRepository jobRepository;
    private final ScheduleServiceImpl scheduleServiceImpl;

    //---------------------- WORK IN PROGRESS ---------------------------------------

    private List<ScheduleDTO> getSchedulesDueBefore(LocalDateTime date) {
        List<Schedule> schedules = scheduleServiceImpl.getSchedulesDueAfter(date);
        return schedules.stream()
                .map(schedule -> modelMapperExtended.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList());
    }

    // metodo che, partendo da una lista di Schedule ne salva il contenuto in formato ScheduleDTO e json in un file in ./data/jobScheduled+{type}+.json
    public void saveSchedulesToFile(List<Schedule> schedules, String type) {
        List<ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> modelMapperExtended.map(schedule, ScheduleDTO.class))
                .toList();
        String fileName = "./data/jobScheduled" + type + ".json";

    }

    public void scheduleTesting(String type) {
        if(type.equals("priority")) {
            testSchedulingByPriority();
        }
        else if (type.equals("dueDate")) {
            testSchedulingByDueDate();
        }
    }

    /*
    ogni schedule ha un job, un machineType, un startTime, una durata (end time è implicito), una due date, una priorità e uno status
    possiamo avere più macchine dello stesso tipo, se abbiamo più job con lo stesso machine type ma più macchine dello stesso tipo quei job potranno
    essere eseguiti in parallelo.

    Se abbiamo più job con lo stesso machine type e la stessa priorità, il job che arriva prima viene eseguito prima.
    Se abbiamo più job con lo stesso machine type e priorità diversa, il job con priorità maggiore viene eseguito prima.
    Se abbiamo più job con lo stesso machine type e priorità uguale, il job che arriva prima viene eseguito prima.
    Se abbiamo più job con lo stesso machine type e priorità uguale e stessa data di scadenza, il job che arriva prima viene eseguito prima.
    */
    public void testSchedulingByPriority() {

    }

    public void testSchedulingByDueDate() {

    }





}