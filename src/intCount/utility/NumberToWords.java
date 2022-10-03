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
public abstract class NumberToWords {

        private static String numberToWords(int number) {
            //if this number is 75, then this function should return seventy five
            int intQuotient = number / 10;
            StringBuilder word = new StringBuilder();

            if (intQuotient > 0) {

                if (intQuotient == 1 && (number % 10) > 0) {
                    word.append(wordInTens(number % 10));
                    return word.toString();
                }
                word.append(wordInTenMultiples(intQuotient));
            }
            int remainder = number % 10;
            if (remainder > 0) {
                if (word.length() > 0) {
                    word.append(" ");
                }
                word.append(numberInWords(remainder));
            }

            return word.toString();

        }

        private static String wordInTenMultiples(int number) {
            String[] words = {"Dix", "Vingt", "Trente", "Quarante", "Cinquante", "Soixante",
                "Soixante-dix", "Quatre-vingts", "Quatre vingt dix"};
            return words[number - 1];
        }

        private static String wordInTens(int number) {
            String words[] = {"Onze", "Douze", "Treize", "Quatorze", "Quinze", "Seize",
                "Dix-sept", "Dix-huit", "Dix-neuf"};
            return words[number - 1];

        }

        private static String numberInWords(int number) {
            String[] words = {"Un", "Deux", "Trois", "Quatre", "Cinq", "Six", "Sept",
                "Huit", "Neuf"};
            return words[number - 1];
        }

        public static String convertNumberToWords(BigDecimal number, boolean prefix, boolean suffix) {
            String numberInWords = convertNumberToWords(number);
            if (prefix) {
                if (number.compareTo(BigDecimal.ONE) == 0) {
                    numberInWords =  numberInWords+ " "+ "Dirham ";
                } else {
                    numberInWords =  numberInWords+ " "+ "Dirhams " ;
                }
            }

            if (suffix) {
                numberInWords += "#";
            }
            return numberInWords;
        }

        public static String convertNumberToWords(BigDecimal parameter) {

            StringBuilder word = new StringBuilder();
            String numberString = parameter.setScale(2,
                    RoundingMode.HALF_UP).toPlainString();

            double number = Double.parseDouble(numberString);

            int quotient = (int) (number / 10000000);
            if (quotient > 0) {
                word.append(convertNumberToWords(new BigDecimal(quotient))
                        + " Crore ");
            }

            number = number % 10000000;

            quotient = (int) (number / 100000);
            if (quotient > 0) {
                word.append(numberToWords(quotient) + " Cent ");
            }
            number = number % 100000;

            quotient = (int) (number / 1000);
            if (quotient > 0) {
                word.append(numberToWords(quotient) + " Mille ");
            }

            number = number % 1000;

            quotient = (int) (number / 100);
            if (quotient > 0) {
                word.append(numberToWords(quotient) + " Cent ");
            }

            number = number % 100;
            if (number != 0) {
                word.append(numberToWords((int) number) + " ");
            }

            int decimal = 0;
            String val;
            if (number % 1 != 0) {
                String decimalInWords = Double.toString(number);
                int index = decimalInWords.indexOf(".");
                decimalInWords = decimalInWords.substring(index + 1);
                if (decimalInWords.length() > 2) {
                    val = decimalInWords.substring(0, 2);
                    decimal = Integer.parseInt(val);
                    if (Integer.parseInt(decimalInWords.substring(2, 3)) > 5) {
                        decimal++;
                    }
                } else {
                    decimal = Integer.parseInt(decimalInWords);
                }
                if (decimalInWords.length() == 1) {
                    decimal *= 10;
                }
                word.append("Virgule " + numberToWords(decimal));
                if (decimal > 1) {
                    word.append(" ");
                } else {
                    word.append(" ");
                }
            }

            if (word.toString().trim().length() == 0) {
                return "Zero";
            }
            return word.toString().trim();

        }
    } // end of class definition - NumberToWords
