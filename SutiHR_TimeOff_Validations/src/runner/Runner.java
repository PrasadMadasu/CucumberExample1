package runner;

import java.io.File;

import org.junit.AfterClass;
import org.junit.runner.RunWith;

import com.cucumber.listener.Reporter;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
				features	=	"src/features",
				glue		=	{"seleniumgluecode"},
				plugin		=	{"com.cucumber.listener.ExtentCucumberFormatter:cucumber-reports/report.html"},
				//plugin	=	{ "pretty", "html:target/htmlreports" },
				monochrome 	=	true
				)
public class Runner {
	@AfterClass
	public static void writeExtentReport() {
		Reporter.loadXMLConfig(new File("config/report.xml"));
	}
}
