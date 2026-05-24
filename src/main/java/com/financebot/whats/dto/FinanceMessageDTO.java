package com.financebot.whats.dto;


public class FinanceMessageDTO {
        private String message;
        private String user;
        private Boolean parcelado;
        private Integer totalParcelas;
        private Integer parcelaAtual;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getUser() { return user; }
     public void setUser(String user) { this.user = user; }

        public Boolean getParcelado() { return parcelado; }
        public void setParcelado(Boolean parcelado) { this.parcelado = parcelado; }

        public Integer getTotalParcelas() { return totalParcelas; }
        public void setTotalParcelas(Integer totalParcelas) { this.totalParcelas = totalParcelas; }

        public Integer getParcelaAtual() { return parcelaAtual; }
        public void setParcelaAtual(Integer parcelaAtual) { this.parcelaAtual = parcelaAtual; }

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