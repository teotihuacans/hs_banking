package banking;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        SBSModel model = new SBSModel();
        model.initModel(args);
        SBSView view = new SBSView();
        SBSController controller = new SBSController(view, model);
        String menuPoint;

        while (!(menuPoint = input.next()).equals("0")) {
            controller.menuHandler(menuPoint, input);
        }
        input.close();

        view.print("\nBye!");
    }
}
