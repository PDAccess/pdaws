package com.h2h.test.it;

import com.h2h.pda.entity.SnippetEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.SnippetUsername;
import com.h2h.pda.pojo.SnippetWrapper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class SnippetControllerTests extends BaseIntegrationTests{
    final static String URL = "/api/v1/snippet/";
    @Test
    @Order(480)
    public void snippetCrudTest(){
        loginWithDefaultUserToken();
        ResponseEntity<UserEntity> userCall = call("/api/v1/user/who", HttpMethod.GET, UserEntity.class);
        assertThat(userCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserEntity userEntity = userCall.getBody();
        SnippetWrapper snippetWrapper = new SnippetWrapper();
        assert (userEntity != null);
        snippetWrapper.setUserid(userEntity.getUserId());
        snippetWrapper.setTitle("Title for test");
        snippetWrapper.setDescription("Description for test");
        snippetWrapper.setInfo("Info for test");
        snippetWrapper.setOperatingSystemId(randomNumber());
        snippetWrapper.setServiceTypeId(randomNumber());
        ResponseEntity<String> createCall = call(URL, HttpMethod.PUT,snippetWrapper,String.class);
        assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String snippetId = createCall.getBody();
        assertThat(snippetId).isNotEqualTo(null);

        SnippetUsername editedSnippet = new SnippetUsername();
        editedSnippet.setFirstName(userEntity.getFirstName());
        editedSnippet.setLastName(userEntity.getLastName());
        editedSnippet.setUsername(userEntity.getUsername());
        editedSnippet.setSnippetEntity(new SnippetEntity());
        editedSnippet.getSnippetEntity().setSnippetId(snippetId);
        editedSnippet.getSnippetEntity().setTitle("Title for edit test");
        editedSnippet.getSnippetEntity().setDescription("Description for edit test");
        editedSnippet.getSnippetEntity().setInfo("Info for test");
        editedSnippet.getSnippetEntity().setOperatingSystemId(randomNumber());
        editedSnippet.getSnippetEntity().setServiceTypeId(randomNumber());
        editedSnippet.getSnippetEntity().setUserId(userEntity.getUserId());
        ResponseEntity<Void> editCall = call(URL+"edit",HttpMethod.PUT,editedSnippet,Void.class);
        assertThat(editCall.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<SnippetUsername> infoCall = call(URL+"/info/"+ snippetId,HttpMethod.GET,SnippetUsername.class);
        assertThat(infoCall.getStatusCode()).isEqualTo(HttpStatus.OK);

        SnippetUsername snippetInfo = infoCall.getBody();
        assert snippetInfo != null;
        assertThat(snippetInfo.getSnippetEntity().getTitle()).isEqualTo("Title for edit test");


        ResponseEntity<SnippetUsername[]> snippetsCall = call(URL+userEntity.getUserId(),HttpMethod.GET,SnippetUsername[].class);
        assertThat(snippetsCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<SnippetUsername> snippets = Arrays.asList(Objects.requireNonNull(snippetsCall.getBody()));
        assertThat(snippets.isEmpty()).isEqualTo(false);


        ResponseEntity<SnippetUsername[]> snippetsAllCall = call(URL+"name/"+userEntity.getUserId(),HttpMethod.POST,SnippetUsername[].class);
        assertThat(snippetsAllCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<SnippetUsername> allSnippets = Arrays.asList(Objects.requireNonNull(snippetsAllCall.getBody()));
        assertThat(allSnippets.isEmpty()).isEqualTo(false);

        ResponseEntity<Void> deleteCall = call(URL+snippetId,HttpMethod.DELETE,Void.class);
        assertThat(deleteCall.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<SnippetUsername[]> snippetsCallForDelete = call(URL+userEntity.getUserId(),HttpMethod.GET,SnippetUsername[].class);
        assertThat(snippetsCallForDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<SnippetUsername> snippetsForDelete = Arrays.asList(Objects.requireNonNull(snippetsCallForDelete.getBody()));
        assertThat(snippetsForDelete.isEmpty()).isEqualTo(true);
    }

}
