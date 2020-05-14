package com.bloknoma.ftgo.common;

import javax.persistence.Embeddable;

// 사람 이름 정보
@Embeddable
public class PersonName {
    private String firstName;
    private String lastName;

    private PersonName() {
    }

    public PersonName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
