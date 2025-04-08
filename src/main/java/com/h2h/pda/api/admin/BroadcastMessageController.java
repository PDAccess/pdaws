package com.h2h.pda.api.admin;

import com.h2h.pda.entity.BroadcastMessageEntity;
import com.h2h.pda.pojo.BroadcastMessageWrapper;
import com.h2h.pda.service.api.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/broadcast")
public class BroadcastMessageController {
    private static final Logger log = LoggerFactory.getLogger(BroadcastMessageController.class);
    private static final String HEX_WEB_COLOR_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";
    private static final Pattern pattern = Pattern.compile(HEX_WEB_COLOR_PATTERN);

    @Autowired
    AdminService adminService;

    @PostMapping()
    public ResponseEntity<String> createMessage(@RequestBody BroadcastMessageWrapper data) {

        BroadcastMessageEntity messageEntity = new BroadcastMessageEntity();

        if (data.getMessage() == null || data.getMessage().equals(""))
            return new ResponseEntity<>("Message value cannot be null!", HttpStatus.BAD_REQUEST);

        if (data.getDateRange() == null || data.getDateRange().getStart() == null || data.getDateRange().getEnd() == null)
            return new ResponseEntity<>("Date range cannot be empty!", HttpStatus.BAD_REQUEST);

        Date dateStart = data.getDateRange().getStart();
        Date dateEnd = data.getDateRange().getEnd();
        if (dateStart.after(dateEnd) || dateEnd.before(new Date()))
            return new ResponseEntity<>("Message start date cannot be after end date!", HttpStatus.BAD_REQUEST);

        if (data.getBackgroundColor() == null || data.getFontColor() == null || !this.isValidColor(data.getBackgroundColor()) || !this.isValidColor(data.getFontColor()))
            return new ResponseEntity<>("Please enter the correct format for the background or font color.", HttpStatus.BAD_REQUEST);

        try {
            Integer.parseInt(data.getFontsize());
            Timestamp timestampStart = new Timestamp(dateStart.getTime());
            Timestamp timestampEnd = new Timestamp(dateEnd.getTime());

            messageEntity.setMessage(data.getMessage());
            messageEntity.setBackgroundColor(data.getBackgroundColor());
            messageEntity.setFontColor(data.getFontColor());
            messageEntity.setFontSize(data.getFontsize());
            messageEntity.setEndDate(timestampEnd);
            messageEntity.setStartDate(timestampStart);

            adminService.newBroadcastMessage(messageEntity);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NumberFormatException numberFormatException) {
            return new ResponseEntity<>("Please enter numeric value in the font size.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<BroadcastMessageWrapper>> getMessages() {
        List<BroadcastMessageWrapper> collect = adminService.activeMessages().stream().map(m -> new BroadcastMessageWrapper().wrap(m))
                .collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }

    @GetMapping(path = "/list")
    public ResponseEntity<List<BroadcastMessageWrapper>> getList() {
        List<BroadcastMessageEntity> messages = adminService.allMessages();

        return new ResponseEntity<>(messages.stream()
                .map(m -> new BroadcastMessageWrapper().wrap(m)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePortal(@PathVariable String id) {
        adminService.deleteMessage(id);
        return ResponseEntity.ok().build();
    }

    public boolean isValidColor(final String colorCode) {
        Matcher matcher = pattern.matcher(colorCode);
        return matcher.matches();
    }
}
