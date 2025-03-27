package com.execodex.poc01.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.ChatOptions;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AiCvParserTest {


    @Test
    void parseCv_shouldReturnParsedJson_whenCvIsValid() {

       String cvText = """
               Adrian Vrabie – Java Software Engineer
               +41 762575204 (CH)
               +1 484 6950242 (US)
               +373 79816002 (MD)
               mr.vrabie@gmail.com
               SO: https://stackoverflow.com/users/959876/moldovean
               P. Languages: Java SE/EE (v. 8+), Oracle certified (OCA)
               Java Fram-s: Spring related: Spring-Boot, Spring-Security
               Reactor Webflux, Spring-Data, JMS, Spring MVC,
               JAX-RS, Spring security, Spring cloud; Quarkus.
               Tech stack: Azure, Azure-WS, Cosmos DB, MTLS, Apache Camel,
               JAX-RS, JWT, SAML, OAuth2.0, JSON-B, Junit,
               JSR343-JMS (ActiveMQ, IBM MQ), JSR338-JPA
               (Hibernate), JSR-331 (Constraint Optimization),
               ELK stack, Swagger/Open-api, Rancher,
               Websockets.
               Pack. Mgmt: Maven, Gradle.
               Ver Sys: GIT, SVN
               Database: Oracle, Postgres, H2 (dev); Cassandra, Mongo
               DB.
               C14n: Docker, Podman
               Orchestration: K8s, docker-compose
               Other lan.: Python, C, PL/SQL, Julia, R, SQL, STATA, Kotlin
               First P lang: Turbo Pascal on IBM 386.
               Summary
               • Over 8+ years of experience in Object-Oriented analysis/design and development in Java and
               J2EE (Servlets, JNDI, EJB, RMI, JPA, JMS) using non-blocking I/O features and multithreading.
               • Working experience on complete Project life cycle from Software Requirement Specification
               (SRS) to Operations and Maintenance.
               • Experience in developing plugins for SD-WAN cloud providers.
               • Experience in applying Gradient Descend, Markovian chains, Dynamic Programming
               algorithms.
               • Experience in developing web applications using SOAP- and REST-based Web services
               • Experience in developing applications using web/application servers like Weblogic, JBoss and
               Tomcat.
               • Experience in writing JUnit, Mockito, AssertJ, JBehave test cases and running the test suites
               to determine test coverage.
               • Worked Agile, which involved sprint planning, estimations, dividing stories into tasks and
               tracked issues. (Jenkins, Rally and Bamboo)
               • Good knowledge of code version control systems and best practices in GIT.
               • Avid user of Intellij IDE.
               Experience
               Allegis Group, Switzerland Jun, 2022 -
               Client: Zurich Insurance, Switzerland
               Role: Java Senior Software Engineer
               • Successfully developed Calculator Search product for Zurich Insurance and deployed it to
               Prod. It is currently being used by two external clients. The product allows to search any
               vehicle by the swiss number plate.
               • Main Tech used: Java 11, Reactor Project for async, docker containers, Cosmos DB , H2 used
               as a DS, event listeners, Azure storage.
               • Created pipelines for Azure for deployment, terraform scripts for infrastructure as code.
               • Created Vnets, DZones, private endpoints, learned how to manage them in resource groups,
               contributed to writing terraform groups, adjusted firewall rules to unblock the development.
               Learned about RUs from Azure and how they impact Data loading.
               • Successfully developed and deployed the DataLoader microservice into Azure and exposed
               its API only to internal Zurich network.
               • Took the initiative to revamp the architecture: changed Serverless Functions to a normal
               Srping Boot app in a simple container and understood thoroghly the limits and perills of the
               serverless functions in Azure.
               • Started developing a new product: PvTools. Used Azure Graph API (not GraphQL) to query
               and update actuary models written in Microsoft Excel and expose them as a webservice to
               the outside world. (POC done). Used the same tech stack as above plus Graph Api form
               MSFT.
               EPAM Gmbh, Switzerland Aug, 2020 – Jun, 2022
               Client: UBS AG, Switzerland
               Role: Contractor Software Engineer
               • Successfully developed from zero a product (Resurce provider from OAuth2 flow) for UBS,
               related for the digitalization of documents, and deployed it to Production (PROD: 6/15/2020)
               on PCF *Pivotal Cloud Foundry.
               • Integrated my product to be able to communicate with other components and services at
               UBS: IdP, Authorization providers,  Enerprise Valut Architectures etc.
               • Used spring security and customized it accordingly. *including providing custom
               Authorization and Authentication providers that would be compatible with some UBS
               components that pretend to be using OAuth2.
               • This involved working with trust stores, private keys, establishing mTLS communication
               between services and other overkill security stuff. In case you are wondering, yes, Even
               inside the same network zone.
               • Working now on a different project within UBS, based on a template from UBS. *working on
               Security and data models and schema for the db.
               Ciorici Consulting LLC, PA, USA Oct, 2018 – March
               2020
               Client: Comcast, PA, USA
               Role: Contractor Software Engineer at Comcast
               • Successfully implemented a software product following a provided design for a cloud
               network provider and successfully launched the product: SD-WAN (Software Defined Wide
               Area Network) in production. We used Java SE 8 and the plugin design pattern for the kernel
               services.
               • Successfully proposed improvements in the design to address some production issues:
               implemented using the strategy pattern.
               • Proposed solutions to optimize the flow and reduce time complexity from polynomial time
               to linear for some flows.
               • Software engineered (Designed and implemented) auxiliary Spring Boot applications that
               would automate some of the Service Creations for the main product described above. Tech
               used: Spring Boot, JPA, Rest Controllers, GSON, Junit, docker-compose and other.
               • Proposed GIT strategies to streamline and expediate the development of features.
               • Configured the environments to use mock services in order to perform Integration and
               Component Testing.
               • Always maintained above 90% of code coverage through unit tests using JUnit, Mockito and
               other frameworks
               • Upheld program quality and overall delivery standards and adhered to SOLID principles
               wherever possible.
               • Got acquainted with Network related technologies, Kubernetes, Rancher, Kibana, Versa OS,
               uCPEs, etc
               Endava Inc 2018
               –2018
               Client: UK Financial Services Company
               Role: Software Engineer
               • Interacted with the client and designed Technical design document from Business
               Requirements for the development team.
               • Responsible for Sequence Diagrams and Class Diagrams creation.
               • Utilized Core Java concepts such as multithreading, collections, garbage collection and other
               JEE technologies during development phase and used different design patterns.
               • Implemented Single Sign-On functionality using SAML and OAuth 2.0.
               • Designed and implemented authentication and authorization functionality using SAML and,
               XACML.
               • Designed and implemented SOAP Web Services.
               • Developed server side code layer responsible for the business logic, which included
               integration with external service calls (to get data from legacy systems).
               • Responsible for continuous source code security scans and enforcing security standards
               throughout the entire solution implementation.
               • Unit, integration testing and bug fixing using JUnit, Mockito, AssertJ, JBehave.
               • Upheld program quality and overall delivery standards in developing software solutions.
               • Responsible for fine-tuning the framework to meet the performance standards.
               • Technologies used: Java, PL/SQL, Spring, JPA, Apache Camel, IBM MQ, Hibernate, Flyway,
               Weblogic, Oracle 12c.
               Endava Inc 2016 –
               2018
               Client: Major Global Card Scheme
               Role: Software Engineer
               • Developed the concept for immediate payments within the MasterCard Settlement System.
               • Responsible for the implementation of business stories, writing functional unit tests and
               integration tests using remote webservers.
               • Implemented the designed RESTful services specification APIs and implemented additional
               internationalization functionality.
               • Re-designed and implemented the adapter for the I18n of the PayByBank App, which uses
               inter-account payments under the IPS framework in UK.
               • Contributed to adding new functionality to the synchronous Adapter from ISO8583 to
               ISO020022.
               • Augmented the stub functionalities for the Acquirer and CFI to be able to accept the newly
               implemented CORE features.
               • Generified the classes to be able to accept other currencies.
               • Implemented the possibility for the partial authorization and partial approval.
               • Transitioned the payments system from using the FPAN to DPAN (Digital Personal Account
               Number), which completely eliminated the customer’s sensitive or personal information
               from the transaction flows.
               • Added new validation rules and SOAP web services.
               • Communicated extensively with business analysis to understand the specifications as well as
               testers to understand how to fix the bugs.
               ASCOM Holding SA 2014
               – 2016
               Client: Tristan Oil / Major Insurance Company
               Role: Software Developer, Data Analysis, Consultant
               • Designed and implemented Underwriting service application architecture for enabling
               overall full feature parallel processing for several insurance companies simultaneously.
               • Designed, configured and maintained the development, test automation, defect resolution,
               and release management throughout the entire solution lifecycle.
               • Designed and developed RESTful services APIs for the entire solution.
               • Managed team code reviews throughout the duration of the project.
               • Established continuous source code security scans to ensure compliance with client security
               guidelines.
               • Responsible for two-month long post-production stabilization activities.
               Client: Major Insurance Company
               Role: Software Developer
               • Responsible for development of custom based system that automated insurance products,
               including re-evaluation the price premiums commensurate with variance estimates.
               • Responsible for analysis of functional documentation and technical documentation.
               • Created software requirement specification document for the development team.
               • Reverse-engineered legacy Insurance Agent Management application.
               • Re-designed and implemented new web-based solution complaint with Enterprise
               Architecture Group requirements.
               • Developed and automated Test pipelines, mainly using Spring/JUnit and Cucumber.
               • Developed new custom SOAP web services for integration with legacy mainframe systems.
               • Refactored legacy components and integrated them into newly designed system.
               MoldATSA SE 2010
               – 2014
               Client: MoldATSA, EUROCONTROL, European Commission
               Role: Software Consultant, Head of Strategy and Development
               • Responsible for the integration and interoperability of Air Navigation Equipment, specifically
               ATM systems VCS, DVOR, NDBs, multilateration, ILS (2nd category), SSR, PSR as well as Meteo
               equipment.
               • Team lead in a group of 5 people.
               • Acquainted with the modular design patterns implemented by the Swedish company SiATM
               to develop the best in class software for ATM (Air Traffic Management).
               • Responsible for the design and development of the Business Contingency Plan under the
               constraints of the European Commission and EUROCONTROL using the R and Python
               programming language.
               • Used R for traffic forecast for Chisinau FIR (Flight Information Region) and facilitated the
               division of operations into Cost Centers for the UR (Unit Rate) adjustments.
               • Developed the LSIP plan and provided technical support for the homologation of the
               software to facilitate the digitization of the AIS (Air Information Service) bulletin.
               • Defended the Unit Rates at the European Commission and EUROCONTROL.
               Education:
               • State University of Moldova – Masters of Science in Mathematics. Thesis: “Applications of
               Markovian Processes and Latent Parameters Estimation in Hidden Markov Models".
               • Albert Ludwigs University, Freiburg, Germany – Master’s degree (Computer Science and
               Economics concentration)
               • Clemson University, SC, USA – Exchange Student (2004-2005).
               Publications:
               • A. Vrabie “Estimating the transition probability matrix in the Hidden Markovian Models”,
               Mathematics & Information Technologies: Research and Education (MITRE-2016), pp. 71,
               ISBN: 978-9975-71-794-6, Chisinau, 2016.
               """;
        List<String> strings = splitCvIntoSemanticChunks(cvText, 1000);
        int i = 0;
        for (String string : strings) {
            System.out.println("Chunk " + i++);
            System.out.println(string);
        }
        System.out.println("Total chunks: " + strings.size());

    }

    private List<String> splitCvIntoSemanticChunks(String text, int maxSize) {
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        // Split on section boundaries
        String regex = "(?m)(?i)(?=^\\s*(education|experience|publications|summary)\\b*)";
        String[] sections = text.split(regex);



        for (String section : sections) {
            if (currentChunk.length() + section.length() > maxSize) {
                chunks.add(currentChunk.toString());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(section);
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }
        return chunks;
    }

}
