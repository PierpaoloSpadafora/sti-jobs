package unical.demacs.rdm;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import unical.demacs.rdm.controller.ScheduleControllerTest;
import unical.demacs.rdm.service.ScheduleServiceImplTest;

@Suite
@SelectClasses({
		ScheduleControllerTest.class,
		ScheduleServiceImplTest.class
})
@SpringBootTest
class StiJobsApplicationTests {

	@Test
	void contextLoads() {
	}

}