package banking;

import java.util.Scanner;

public class SBSController {
    private SBSView view;
    private SBSModel model;

    public SBSController (SBSView view, SBSModel model) {
        this.view = view;
        this.model = model;
        InitMenu();
    }

    private void InitMenu() {
        view.print("1. Create an account");
        view.print("2. Log into account");
        view.print("0. Exit");
    }

    private void mainMenu() {
        view.print("1. Balance");
        view.print("2. Add income");
        view.print("3. Do transfer");
        view.print("4. Close account");
        view.print("5. Log out");
        view.print("0. Exit");
    }

    public void menuHandler(String point, Scanner in) {
        view.print("");
        in.nextLine();
        if ("1".equals(point) && !model.authorized()) {
            String cardNum = model.createCard();
            String cardPin = model.generatePin(cardNum);
            view.print("Your card have been created");
            view.print("Your card number:");
            view.print(cardNum);
            view.print("Your card PIN:");
            view.print(cardPin);
            model.saveNewAccount(cardNum, cardPin);
        } else if ("2".equals(point) && !model.authorized()) {
            view.print("Enter your card number:");
            String inCard = in.nextLine();
            view.print("Enter your PIN:");
            String inPin = in.nextLine();
            if (model.logIn(inCard, inPin)) {
                view.print("You have successfully logged in!");
            } else {
                view.print("Wrong card number or PIN!");
            }
        } else if ("1".equals(point) && model.authorized()) {
            view.print("Balance: " + model.getAccountBalance());
        } else if ("2".equals(point) && model.authorized()) {
            view.print("Enter income:");
            model.addIncome(in.nextInt());
            view.print("Income was added!");
        } else if ("3".equals(point) && model.authorized()) {
            view.print("Transfer");
            view.print("Enter card number:");
            String toCardNum = in.nextLine();
            String validateResult = model.doTransferValidate(toCardNum);
            if (validateResult == null) {
                view.print("Enter how much money you want to transfer:");
                Integer transferSum = in.nextInt();
                String validateSumResult = model.doTransferSumValidate(transferSum);
                if (validateSumResult == null) {
                    model.doTransfer(toCardNum, transferSum);
                    view.print("Success!");
                } else {
                    view.print(validateSumResult);
                }
            } else {
                view.print(validateResult);
            }
        } else if ("4".equals(point) && model.authorized()) {
            model.deleteAccount();
            view.print("The account has been closed!");
        } else if ("5".equals(point) && model.authorized()) {
            model.logOut();
            view.print("You have successfully logged out!");
        } else {
            view.print("Wrong command! (" + point + ")");
        }

        view.print("");
        if (model.authorized()) {
            mainMenu();
        } else {
            InitMenu();
        }
    }
}
