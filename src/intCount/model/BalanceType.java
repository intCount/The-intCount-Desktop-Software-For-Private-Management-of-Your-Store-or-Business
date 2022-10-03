/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

/**
 * @author
 */
public enum BalanceType {
    CREDIT,
    DEBIT;

    @Override
    public String toString() {
        switch (this) {
            case CREDIT:
                return "CR.";
            case DEBIT:
                return "DR.";
            default:
                return "unknown value";
        }
    }


}
