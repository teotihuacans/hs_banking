package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SBSModel {
    private List<String> cardsBase = new ArrayList<>();
    private List<String> pinBase = new ArrayList<>();
    private List<Integer> balanceBase = new ArrayList<>();
    private boolean isAuthorized = false;
    private StringBuilder currentAccount = new StringBuilder();
    private Integer currentAccountIndex;

    public boolean authorized() {
        return isAuthorized;
    }

    public String createCard() {
        //(6) Issuer Identification Number (IIN) + (9-12) customer account number + (1) Checksum
        //Visa: 4*****
        //AMEX: 34**** or 37****
        //Mastercard: 51**** to 55****
        //We use fixed: 400000
        StringBuilder cardNum = new StringBuilder();
        cardNum.append("400000");
        for (int i = 100000000; i > cardsBase.size() + 1; i /= 10) {
            cardNum.append(0);
        }
        cardNum.append(cardsBase.size() + 1);
        //cardNum.append(8); //Checksum
        cardNum.append(algorithmLuhn(cardNum));

        return cardNum.toString();
    }

    private int algorithmLuhn(StringBuilder partCardNum) {
        int sum = 0;
        for (int i = 0; i < partCardNum.length(); i++) {
            int k = Integer.parseInt(Character.toString(partCardNum.charAt(i)));
            if ((i + 1) % 2 > 0) {
                if (k * 2 > 9) {
                    sum += (k * 2) - 9;
                } else {
                    sum += k * 2;
                }
            } else {
                sum += k;
            }
        }
        if (sum % 10 > 0) {
            return 10 - (sum % 10);
        } else {
            return 0;
        }
    }

    public String generatePin(String cardNum) {
        StringBuilder result = new StringBuilder();
        Random rdPin = new Random(Long.parseLong(cardNum)); //The same pin for card
        result.append(rdPin.nextInt(9999));
        for (int i = 0; i < 4 - result.length(); i++) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    public void saveNewAccount(String cardNum, String cardPin) {
        cardsBase.add(cardNum);
        pinBase.add(cardPin);
        balanceBase.add(0);
    }

    public boolean logIn(String cardNum, String cardPin) {
        int k = cardsBase.indexOf(cardNum);
        if (k >= 0 && cardPin.equals(pinBase.get(k))) {
            isAuthorized = true;
            currentAccount.append(cardNum);
            currentAccountIndex = k;
            return true;
        } else {
            return false;
        }
    }

    public void  logOut() {
        currentAccount.delete(0, currentAccount.length());
        currentAccountIndex = null;
        isAuthorized = false;
    }

    public Integer getAccountData() {
        return balanceBase.get(currentAccountIndex);
    }
}
