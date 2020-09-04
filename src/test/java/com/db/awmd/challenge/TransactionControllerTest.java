package com.db.awmd.challenge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void prepareMockMvc() {
	this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

	// Reset the existing accounts before each test.

	accountsService.getAccountsRepository().clearAccounts();

    }

    @Test
    public void transferAmount() throws Exception {
	String uniqueFromAccountId = "Id-123";
	String uniqueToAccountId = "Id-234";
	Account account = new Account(uniqueFromAccountId, new BigDecimal("1230.45"));
	this.accountsService.createAccount(account);
	Account account2 = new Account(uniqueToAccountId, new BigDecimal("123.45"));
	this.accountsService.createAccount(account2);

	this.mockMvc
		.perform(put("/bank/transfers").contentType(MediaType.APPLICATION_JSON)
			.content("{\"accountToId\":\"Id-234\",\"accountFromId\":\"Id-123\",\"amount\":1000}"))
		.andExpect(status().isOk());
    }

    @Test
    public void transferAmountZeroAmount() throws Exception {

	this.mockMvc
		.perform(put("/bank/transfers").contentType(MediaType.APPLICATION_JSON)
			.content("{\"accountToId\":\"Id-234\",\"accountFromId\":\"Id-123\",\"amount\":0}"))
		.andExpect(status().isBadRequest());
    }

    @Test
    public void transferAmountEmptyToAccount() throws Exception {

	this.mockMvc
		.perform(put("/bank/transfers").contentType(MediaType.APPLICATION_JSON)
			.content("{\"accountToId\":\"\",\"accountFromId\":\"Id-123\",\"amount\":0}"))
		.andExpect(status().isBadRequest());
    }

    @Test
    public void transferAmountEmptyFromAccount() throws Exception {

	this.mockMvc
		.perform(put("/bank/transfers").contentType(MediaType.APPLICATION_JSON)
			.content("{\"accountToId\":\"Id-234\",\"accountFromId\":\"\",\"amount\":0}"))
		.andExpect(status().isBadRequest());
    }

}
