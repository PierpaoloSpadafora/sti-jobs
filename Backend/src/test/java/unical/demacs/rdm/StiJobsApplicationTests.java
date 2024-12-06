package unical.demacs.rdm;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import unical.demacs.rdm.controller.*;
import unical.demacs.rdm.service.*;
import unical.demacs.rdm.utils.SchedulerTest;

@Suite
@SelectClasses({
		JobControllerTest.class,
		JsonControllerTest.class,
		MachineControllerTest.class,
		MachineTypeControllerTest.class,
		ScheduleControllerTest.class,
		UserControllerTest.class,

		JobServiceImplTest.class,
		JsonServiceImplTest.class,
		MachineServiceImplTest.class,
		MachineTypeServiceImplTest.class,
		ScheduleServiceImplTest.class,
		UserServiceImplTest.class,

		SchedulerTest.class
})
@SpringBootTest
class StiJobsApplicationTests {

	@Test
	void contextLoads() {
	}

}