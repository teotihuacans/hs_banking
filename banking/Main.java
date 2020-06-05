package banking;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        SBSModel model = new SBSModel();
        SBSView view = new SBSView();
        SBSController controller = new SBSController(view, model);
        int menuPoint;

        while ((menuPoint = input.nextInt()) != 0) {
            controller.menuHandler(menuPoint);
        }
        input.close();

        view.print("\nBye!");
    }
}
