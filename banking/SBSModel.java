package banking;

import java.util.Random;

public class SBSModel {
    //private List<String> cardsBase = new ArrayList<>();
   // private List<String> pinBase = new ArrayList<>();
    //private List<Integer> balanceBase = new ArrayList<>();
    private boolean isAuthorized = false;
    private StringBuilder currentAccount = new StringBuilder();
    private Integer currentAccountBalance;
    private Integer maxAccountIndex;
    private String db_file_name = "db.s3db";

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
        maxAccountIndex++;
        for (int i = 100000000; i > maxAccountIndex; i /= 10) {
            cardNum.append(0);
        }
        //cardNum.append(cardsBase.size() + 1);
        cardNum.append(maxAccountIndex);
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
        SBSDBOperate.insertCard(maxAccountIndex, cardNum, cardPin, 0);
    }

    public boolean logIn(String cardNum, String cardPin) {
        String[] loginData = SBSDBOperate.getCardInfo(cardNum);

        if (cardPin.equals(loginData[1])) {
            isAuthorized = true;
            currentAccount.append(cardNum);
            currentAccountBalance = Integer.parseInt(loginData[2]);
            return true;
        } else {
            return false;
        }
    }

    public void  logOut() {
        currentAccount.delete(0, currentAccount.length());
        currentAccountBalance = null;
        isAuthorized = false;
    }

    public Integer getAccountBalance() {
        currentAccountBalance = SBSDBOperate.getAccountBalance(currentAccount.toString());
        return currentAccountBalance;
    }

    public void addIncome(Integer amount) {
        SBSDBOperate.setAccountBalance(currentAccount.toString(), currentAccountBalance + amount);
        getAccountBalance();
    }

    public String doTransferValidate(String toCardNum) {
        StringBuilder subNum = new StringBuilder(toCardNum.substring(0, toCardNum.length() - 1));
        String[] cardMas = SBSDBOperate.getCardInfo(toCardNum);
        if (toCardNum.equals(currentAccount.toString())) {
            return "You can't transfer money to the same account!";
        } else if (Integer.parseInt(toCardNum.substring(toCardNum.length() - 1)) != (algorithmLuhn(subNum))) {
            return "Probably you made mistake in card number. Please try again!";
        } else if (!toCardNum.equals(cardMas[0])) {
            return "Such a card does not exist.";
        }
        return null;
    }

    public String doTransferSumValidate(Integer transferSum) {
        if (transferSum > currentAccountBalance) {
            return "Not enough money!";
        }
        return null;
    }

    public void doTransfer(String toCardNum, Integer transferSum) {
        Integer toCardBalance = SBSDBOperate.getAccountBalance(toCardNum);
        SBSDBOperate.setAccountBalance(currentAccount.toString(), currentAccountBalance - transferSum);
        SBSDBOperate.setAccountBalance(toCardNum, toCardBalance + transferSum);
        getAccountBalance();
    }

    public void deleteAccount() {
        SBSDBOperate.deleteCard(currentAccount.toString());
    }

    protected void initModel(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if ("-fileName".equals(args[i])) {
                db_file_name = args[i + 1];
            }
        }
        dbInit();
    }

    protected void dbInit() {
        String sql = "CREATE TABLE IF NOT EXISTS card ("
                + "	id INTEGER,"
                + "	number TEXT,"
                + "	pin TEXT,"
                + " balance INTEGER DEFAULT 0"
                + ");";

        //String url = "jdbc:sqlite:C:\\Users\\adenisov\\IdeaProjects\\Simple Banking System\\Simple Banking System\\task\\src\\banking\\" + db_file_name;
        String url = "jdbc:sqlite:" + db_file_name;

        SBSDBOperate.setUrl(url);
        SBSDBOperate.createNewDatabase();
        SBSDBOperate.createNewTable(sql);
        maxAccountIndex = SBSDBOperate.queryMaxId();
    }
}
