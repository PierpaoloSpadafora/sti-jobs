package unical.demacs.rdm;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import unical.demacs.rdm.controller.*;
import unical.demacs.rdm.service.*;

@Suite
@SelectClasses({
		JobControllerTest.class,
		MachineControllerTest.class,
		ScheduleControllerTest.class,
		MachineTypeControllerTest.class,
		UserControllerTest.class,

		JobServiceImplTest.class,
		MachineServiceImplTest.class,
		MachineTypeServiceImplTest.class,
		ScheduleServiceImplTest.class,
		UserServiceImplTest.class
})
@SpringBootTest
class StiJobsApplicationTests {

	@Test
	void contextLoads() {
	}

}