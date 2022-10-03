package intCount.model;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author
 */
public enum PaymentMode {
    CASH("cash"),
    CHEQUE("cheque"),
    DD("dd"),
    BANKTRANSFER("banktransfer");

    private final String value;

    private PaymentMode(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case CASH:
                return "SPS/ CARTE";
            case CHEQUE:
                return "CHEQUE";
            case DD:
                return "EFFET";
            case BANKTRANSFER:
                return "VIREMENT";
            default:
                return "Valeur Inconnue";
        }
    }

}
