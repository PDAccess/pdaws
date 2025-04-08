package com.h2h.test.it;


import com.h2h.pda.pojo.BroadcastMessageWrapper;
import com.h2h.pda.pojo.DateRange;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BroadcastControllerTest extends BaseIntegrationTests {

    BroadcastMessageWrapper data;

    @Test
    @Order(200)
    public void broadcastMessageCRUDTest() {
        loginWithDefaultUserToken();

        data = new BroadcastMessageWrapper();

        data.setMessage("test message added");
        data.setFontsize("15");
        data.setDateRange(new DateRange());
        LocalDateTime start = LocalDateTime.now();
        data.getDateRange().setStart(Timestamp.valueOf(start));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(Timestamp.valueOf(start));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        LocalDateTime end = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
        data.getDateRange().setEnd(Timestamp.valueOf(end));
        data.setIsDeleted(false);
        data.setFontColor("#D1ECF1");
        data.setBackgroundColor("#D1ECF1");

        // Success message added
        ResponseEntity<String> call = call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Has a message been added?
        ResponseEntity<BroadcastMessageWrapper[]> response = call("/api/v1/broadcast/list", HttpMethod.GET, BroadcastMessageWrapper[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        BroadcastMessageWrapper b = response.getBody()[0];
        assertThat(b.getMessage()).isEqualTo(data.getMessage());
        assertThat(b.getBackgroundColor()).isEqualTo(data.getBackgroundColor());
        assertThat(b.getFontColor()).isEqualTo(data.getFontColor());
        assertThat(b.getFontsize()).isEqualTo(data.getFontsize());
        assertThat(b.getIsDeleted()).isEqualTo(data.getIsDeleted());

        // Is the added message active?
        response = call("/api/v1/broadcast", HttpMethod.GET, BroadcastMessageWrapper[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()[0].getMessageid()).isEqualTo(b.getMessageid());

        // delete added message
        call = call("/api/v1/broadcast/" + b.getMessageid(), HttpMethod.DELETE, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Is the added message delete?
        response = call("/api/v1/broadcast/list", HttpMethod.GET, BroadcastMessageWrapper[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();

        // Is the deleted message active?
        response = call("/api/v1/broadcast", HttpMethod.GET, BroadcastMessageWrapper[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();

        // add an inactive message
        start = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        end = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
        data.getDateRange().setStart(Timestamp.valueOf(start));
        data.getDateRange().setEnd(Timestamp.valueOf(end));
        call = call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        // get message id
        response = call("/api/v1/broadcast/list", HttpMethod.GET, BroadcastMessageWrapper[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        b.setMessageid(response.getBody()[0].getMessageid());

        // Is the deleted message active?
        response = call("/api/v1/broadcast", HttpMethod.GET, BroadcastMessageWrapper[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();

        // delete added message
        call = call("/api/v1/broadcast/" + b.getMessageid(), HttpMethod.DELETE, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Add a message with the wrong date
        data.getDateRange().setEnd(Timestamp.valueOf(LocalDateTime.of(2021, 4, 27, 0, 0, 0)));
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        // Add a message with the wrong font or background color
        data.getDateRange().setEnd(Timestamp.valueOf(end));
        data.setFontColor("#D1ECF");
        data.setBackgroundColor("#D1ECF");
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.setFontColor(null);
        data.setBackgroundColor(null);
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        // Add a message with the wrong font size
        data.setFontColor("#D1ECF1");
        data.setBackgroundColor("#D1ECF1");
        data.setFontsize("test");
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        // Add a message with the null message
        data.setFontsize("1");
        data.setMessage(null);
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.setMessage("");
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        // Add a message with the null date
        data.setMessage("test");
        data.setDateRange(null);
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.setDateRange(new DateRange());
        data.getDateRange().setStart(null);
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.getDateRange().setEnd(Timestamp.valueOf(end));
        data.getDateRange().setEnd(null);
        try {
            call("/api/v1/broadcast", HttpMethod.POST, data, String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

}
