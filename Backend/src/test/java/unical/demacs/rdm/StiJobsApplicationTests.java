package unical.demacs.rdm;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import unical.demacs.rdm.controller.JobControllerTest;
import unical.demacs.rdm.controller.MachineControllerTest;
import unical.demacs.rdm.controller.ScheduleControllerTest;
import unical.demacs.rdm.service.JobServiceImplTest;
import unical.demacs.rdm.service.MachineServiceImplTest;
import unical.demacs.rdm.service.ScheduleServiceImplTest;

@Suite
@SelectClasses({
		JobControllerTest.class,
		MachineControllerTest.class,
		ScheduleControllerTest.class,

		JobServiceImplTest.class,
		MachineServiceImplTest.class,
		ScheduleServiceImplTest.class
})
@SpringBootTest
class StiJobsApplicationTests {

	@Test
	void contextLoads() {
	}

}