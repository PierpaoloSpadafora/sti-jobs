package unical.demacs.rdm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.nio.file.Paths;

@SpringBootApplication
@EnableScheduling
public class StiJobsApplication {


	private static void createFolder(String path) {
		File logFolder = new File(path);
		if (!logFolder.exists()) {
			if (logFolder.mkdirs()) {
				System.out.println("Cartella di log in: " + path + " creata con successo.");
			} else {
				System.err.println("Impossibile creare la cartella di log in: " + path + ".");
			}
		}
	}

	public static void main(String[] args) {
		String baseDir;
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			baseDir = "C:\\sti-jobs-logs";
		} else {
			baseDir = "/sti-jobs-logs";
		}

		String backendDir = Paths.get(baseDir, "Backend").toString();

		createFolder(baseDir);
		createFolder(backendDir);
		SpringApplication.run(StiJobsApplication.class, args);
	}

}
