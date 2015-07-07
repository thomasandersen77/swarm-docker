package swarm;

import lombok.extern.slf4j.Slf4j;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.jaxrs.JAXRSDeployment;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.undertow.UndertowFraction;

import java.io.File;

@Slf4j
public class SwarmMain {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Container container = new Container();

        String logLevel = "INFO";
        container.fraction(new LoggingFraction()
                .formatter("PATTERN", "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n")
                .consoleHandler(logLevel, "PATTERN")
                .rootLogger("CONSOLE", logLevel));
        container.fraction(new UndertowFraction());
        container.start();

        JAXRSDeployment jaxrsDeployment = new JAXRSDeployment(container);
        jaxrsDeployment.setApplication(JaxRsApplication.class);
        jaxrsDeployment.addResource(MyResource.class);
        jaxrsDeployment.getArchive()
                .addPackages(true, "swarm")
                .addAsWebInfResource(new FileAsset(new File("src/main/webapp/WEB-INF/beans.xml")), "beans.xml");

        jaxrsDeployment.getArchive().getContent().forEach((p, r) -> log.debug("\tresource [{}]" + p));

        container.deploy(jaxrsDeployment);
        log.info("Started server in {} .ms", (System.currentTimeMillis() - start));
    }

}
