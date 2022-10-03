/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.utility;

import java.math.BigDecimal;

import java.math.RoundingMode;

/**
 *
 * @author 
 */
 public abstract class IndianCurrencyFormatting {

        private static String applyFormatting(String amount) {

            boolean negativeNumber = false;
            if (amount.subSequence(0, 1).equals("-")) {
                negativeNumber = true;
                amount = amount.replaceFirst("-", "");
            }

            int decimalIndex = amount.indexOf(",");
            StringBuilder sb = new StringBuilder();

            if (decimalIndex == -1) {
                //Check that the string is atleast 4 characters long
                if (amount.length() < 4) {
                    return amount; // return the source string as it is i.e. without any change
                }
                sb.append(amount);
                sb.insert(sb.length() - 3, "");
            } else if (decimalIndex < 4) {
                return amount; //return the source string as it is
            } else {
                sb.append(amount);
                sb.insert(decimalIndex - 3, "");
            }

            int index = 0;
            while ((index = sb.indexOf("")) >= 3) {
                sb.insert(index - 2, " ");
            }

            if (negativeNumber) {
                return "-" + sb.toString();
            } else {
                return sb.toString();
            }

        }

        public static String applyFormatting(BigDecimal amount) {

            if (amount == null) {
                return "";
            }

            amount = amount.setScale(2, RoundingMode.HALF_UP);
            return applyFormatting(amount.toPlainString());
        }
    } // end of class definition
