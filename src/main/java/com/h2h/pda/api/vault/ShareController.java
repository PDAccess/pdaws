package com.h2h.pda.api.vault;

import com.h2h.pda.entity.BreakTheGlassShareEntity;
import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.pojo.BreakTheGlassShareParams;
import com.h2h.pda.pojo.CredentialParams;
import com.h2h.pda.pojo.CredentialShareResponse;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.service.api.CredentialManager;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.util.RequestUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/share")
public class ShareController {

    public static String SHARE_LINK = "api/v1/share/link/%s";

    public static final String FORMAT_JSON = "json";
    public static final String FORMAT_XML = "xml";
    public static final String FORMAT_CSV = "csv";

    @Autowired
    CredentialManager credentialManager;

    @Autowired
    UsersOps usersOps;

    @GetMapping(value = "link/{share_id}")
    public ResponseEntity<Object> getSharedLink(@PathVariable("share_id") String shareId, @RequestParam(value = "format", required = false) String format, HttpServletRequest request) {
        BreakTheGlassShareEntity breakTheGlassShareEntity = credentialManager.getSharedLink(shareId);
        if (breakTheGlassShareEntity == null || breakTheGlassShareEntity.getCredentialEntity() == null || (breakTheGlassShareEntity.getAllowIpAddress() != null && !breakTheGlassShareEntity.getAllowIpAddress().isEmpty() && !breakTheGlassShareEntity.getAllowIpAddress().equals(RequestUtil.remoteAddress(request)))) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        CredentialParams credentialParams = credentialManager.breakCredentialUsingRootToken(breakTheGlassShareEntity.getCredentialEntity().getCredentialId(), breakTheGlassShareEntity.getDescription(), breakTheGlassShareEntity.getUserEntity(), breakTheGlassShareEntity.getUsers(), RequestUtil.remoteAddress(request), true);
        if (credentialParams == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        if (format == null) {
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        } else {
            switch (format) {
                case FORMAT_XML:
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
                    break;
                case FORMAT_CSV:
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, "text/csv");
                    ByteArrayInputStream byteArrayOutputStream;

                    try (
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            CSVPrinter csvPrinter = new CSVPrinter(
                                    new PrintWriter(out),
                                    CSVFormat.DEFAULT.withHeader("Credential ID", "Username", "Password", "Key", "PPKey", "Key Values")
                            );
                    ) {
                        csvPrinter.printRecord(credentialParams.getCredentialId(), credentialParams.getUsername(), credentialParams.getPassword(), credentialParams.getKey(), credentialParams.getPpKey(), credentialParams.getKeyValues());
                        csvPrinter.flush();
                        byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

                    httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=credentials.csv");

                    return new ResponseEntity<>(
                            fileInputStream,
                            httpHeaders,
                            HttpStatus.OK
                    );
                default:
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    break;
            }
        }

        return new ResponseEntity<>(
                new CredentialShareResponse().wrap(credentialParams),
                httpHeaders,
                HttpStatus.OK
        );
    }

    @DeleteMapping("{share_id}")
    public ResponseEntity<Void> revokeSharedLink(@PathVariable("share_id") String shareId) {
        credentialManager.revokeSharedLink(shareId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BreakTheGlassShareParams> createSharedLink(@RequestBody BreakTheGlassShareParams shareParams) {
        CredentialEntity credentialEntity = credentialManager.getCredential(shareParams.getCredentialId());
        if (credentialEntity == null || credentialEntity.isCheckStatus()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        BreakTheGlassShareEntity breakTheGlassShareEntity = new BreakTheGlassShareEntity();
        breakTheGlassShareEntity.setCredentialEntity(credentialEntity);
        breakTheGlassShareEntity.setDescription(shareParams.getDescription());
        breakTheGlassShareEntity.setExpiredAt(shareParams.getExpiredAt());
        breakTheGlassShareEntity.setAllowIpAddress(shareParams.getAllowIpAddress());

        if (shareParams.getUsers() != null) {
            breakTheGlassShareEntity.setUsers(shareParams.getUsers().stream().map(UserDTO::unWrap).collect(Collectors.toList()));
        }

        breakTheGlassShareEntity = credentialManager.createSharedLink(breakTheGlassShareEntity);

        shareParams.setShareLink(String.format(SHARE_LINK, breakTheGlassShareEntity.getId()));
        return ResponseEntity.ok(shareParams);
    }

}
