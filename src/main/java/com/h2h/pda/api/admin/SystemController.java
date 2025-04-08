package com.h2h.pda.api.admin;

import com.h2h.pda.pojo.LogFileEntry;
import com.h2h.pda.pojo.VersionData;
import com.h2h.pda.pojo.metric.Counters;
import com.h2h.pda.pojo.vault.SealStatusResponse;
import com.h2h.pda.service.api.MetricService;
import com.h2h.pda.service.api.VaultService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.vault.VaultException;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    public static final String LOGS = "/logs/";
    public static final String STATUS_TOKEN = "TGsckNf7Vmsgd2bTTxVq";


    @Autowired
    EntityManager entityManager;

    @Autowired
    VaultService vaultService;

    @Autowired
    MetricService metricService;

    @Value("${service.commit-id}")
    String commitId;

    @Autowired
    JmsTemplate jmsTemplate;

    @JmsListener(destination = "pdaccess.jms.select1", containerFactory = "queueListenerFactory")
    public void echo(Message message) throws JMSException {
        jmsTemplate.send(message.getJMSReplyTo(), session -> {
            Message responseMsg = session.createTextMessage("pong");
            responseMsg.setJMSCorrelationID(message.getJMSCorrelationID());
            return responseMsg;
        });
    }

    @GetMapping(path = "/status/{token}")
    public ResponseEntity<String> status(@PathVariable String token) {
        if (!STATUS_TOKEN.equalsIgnoreCase(token)) {
            metricService.getCounter(Counters.SYSTEM_STATUS_COUNT).increment("token invalid");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return status();
    }

    @GetMapping(path = "/status")
    public ResponseEntity<String> status() {
        Session session = entityManager.unwrap(Session.class);
        Executor executor = (command) -> ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Operation timeout");
        try {
            session.doWork(connection -> {
                connection.setNetworkTimeout(executor, 10 * 1000);
                PreparedStatement statement = connection.prepareStatement("select 1");
                statement.setQueryTimeout(10);
                statement.execute();
                statement.close();
            });
        } catch (HibernateException he) {
            metricService.getCounter(Counters.SYSTEM_STATUS_COUNT).increment("db failed status");
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(he.getMessage());
        }

        try {
            jmsTemplate.setReceiveTimeout(5000);
            Message pong = jmsTemplate.sendAndReceive("pdaccess.jms.select1",
                    (session2) -> {
                        TextMessage ping = session2.createTextMessage("ping");
                        ping.setJMSCorrelationID("ping");
                        return ping;
                    }
            );
            if (pong == null)
                throw new UncategorizedJmsException("time out");
        } catch (JmsException jmsException) {
            metricService.getCounter(Counters.SYSTEM_STATUS_COUNT).increment("jms failed status");
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(jmsException.getMessage());
        }

        try {
            SealStatusResponse sealStatusResponse = vaultService.doWithVault(null, restOperations -> {
                ResponseEntity<SealStatusResponse> responseEntity = restOperations.getForEntity("/sys/seal-status"
                        , SealStatusResponse.class);
                return responseEntity.getBody();
            });

            if (sealStatusResponse.isSealed()) {
                throw new VaultException("Vault is sealed");
            }

        } catch (VaultException ve) {
            metricService.getCounter(Counters.SYSTEM_STATUS_COUNT).increment("vault failed status");
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ve.getMessage());
        }

        metricService.getCounter(Counters.SYSTEM_STATUS_COUNT).increment("success status");
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/version")
    public ResponseEntity<VersionData> version() {
        return new ResponseEntity<>(new VersionData().setVersion(commitId).setTag("1.5"), HttpStatus.OK);
    }

    @GetMapping(path = "/log/list")
    public ResponseEntity<List<LogFileEntry>> listLogs() {
        try (Stream<Path> collect = Files.walk(Paths.get(LOGS)).
                filter(Files::isRegularFile).sorted((p1, p2) -> (p1.toFile().lastModified() < p2.toFile().lastModified() ? 1 : -1))) {

            return new ResponseEntity<>(collect.map(p -> new LogFileEntry(p)).collect(Collectors.toList()), HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
    }

    @PostMapping(path = "/log/read")
    public ResponseEntity<String> readLog(@RequestBody String file) {
        return readLog(file, 1000);
    }

    @PostMapping(path = "/log/read/{line}")
    public ResponseEntity<String> readLog(@RequestBody String file, @PathVariable Integer n_lines) {
//        file = file.replace("..", "");
//
//        try (ReversedLinesFileReader object = new ReversedLinesFileReader(new File(LOGS, file), Charset.forName("ASCII"))) {
//            List<String> list = new ArrayList<>();
//
//            for (int i = 0; i < n_lines; i++) {
//                String line = object.readLine();
//                if (line == null)
//                    break;
//                list.add(line);
//            }
//            Collections.reverse(list);
//            String collect = list.stream().collect(Collectors.joining("\n"));
//
//            return new ResponseEntity<>(collect, HttpStatus.OK);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
//        }
        return ResponseEntity.ok("empty");
    }

    @PostMapping(path = "/log/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadLog(@RequestBody String file)
            throws IOException {
        file = file.replace("..", "");
        File file1 = new File(LOGS, file);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        InputStreamResource inputStreamReader = new InputStreamResource(new FileInputStream(file1));
        return ResponseEntity.ok().headers(headers).contentLength(file1.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(inputStreamReader);
    }
}
