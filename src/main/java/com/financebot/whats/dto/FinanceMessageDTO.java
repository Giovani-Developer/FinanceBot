package com.financebot.whats.dto;


public class FinanceMessageDTO {
        private String message;
        private String user;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
}