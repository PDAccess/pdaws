package com.h2h.test.it;

import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.auth.AuthenticationAttemptEntityWrapper;
import com.h2h.test.util.PageHelper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerTests extends BaseIntegrationTests {

    @Test
    @Order(500)
    public void getAllAuthenticationTest() {

        loginWithDefaultUserToken();

        Pagination page = new Pagination();
        page.setFilter("sametk");
        page.setCurrentPage(0);
        page.setPerPage(15);
        page.setSort("created");

        ResponseEntity<PageHelper<AuthenticationAttemptEntityWrapper>> callVoid =
                call("/api/v1/auths/portal", HttpMethod.POST, page,
                        ParameterizedTypeReference.forType(PageHelper.class));

        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
