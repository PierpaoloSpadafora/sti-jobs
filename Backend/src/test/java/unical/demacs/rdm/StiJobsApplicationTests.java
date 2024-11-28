package unical.demacs.rdm;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import unical.demacs.rdm.controller.UserControllerTest;
import unical.demacs.rdm.service.UserServiceImplTest;

@Suite
@SelectClasses({
		JobControllerTest.class,
		MachineControllerTest.class,
		ScheduleControllerTest.class,
		UserControllerTest.class,

		JobServiceImplTest.class,
		MachineServiceImplTest.class,
		ScheduleServiceImplTest.class,
		UserServiceImplTest.class
})
@SpringBootTest
class StiJobsApplicationTests {

	@Test
	void contextLoads() {
	}

}