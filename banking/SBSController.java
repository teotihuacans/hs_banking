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
        view.print("2. Log out");
        view.print("0. Exit");
    }

    public void menuHandler(int point) {
        view.print("");
        if (point == 1 && !model.authorized()) {
            String cardNum = model.createCard();
            String cardPin = model.generatePin(cardNum);
            view.print("Your card have been created");
            view.print("Your card number:");
            view.print(cardNum);
            view.print("Your card PIN:");
            view.print(cardPin);
            model.saveNewAccount(cardNum, cardPin);
        } else if (point == 2 && !model.authorized()) {
            Scanner in = new Scanner(System.in);
            view.print("Enter your card number:");
            String inCard = in.nextLine();
            view.print("Enter your PIN:");
            String inPin = in.nextLine();
            if (model.logIn(inCard, inPin)) {
                view.print("You have successfully logged in!");
            } else {
                view.print("Wrong card number or PIN!");
            }
            in.close();
        } else if (point == 1 && model.authorized()) {
            view.print("Balance: " + model.getAccountData());
        } else if (point == 2 && model.authorized()) {
            model.logOut();
            view.print("You have successfully logged out!");
        }

        view.print("");
        if (model.authorized()) {
            mainMenu();
        } else {
            InitMenu();
        }
    }
}
