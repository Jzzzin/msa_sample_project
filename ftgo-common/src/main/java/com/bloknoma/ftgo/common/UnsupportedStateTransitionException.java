package com.bloknoma.ftgo.common;

// state 변경 실패 예외처리
public class UnsupportedStateTransitionException extends RuntimeException {
    public UnsupportedStateTransitionException(Enum state) {
        super("current state: " + state);
    }
}
