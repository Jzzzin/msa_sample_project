package com.bloknoma.ftgo.accountingservice.web;

// account 응답
public class GetAccountResponse {
    private String accountId;

    public GetAccountResponse() {

    }

    public GetAccountResponse(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
