import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class main {

    private Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        new main().client();
    }

    public void client() throws IOException {

        System.out.println("-----------=+=-----------");
        System.out.println("Modern Art Multiplayer (Client)");

        System.out.print("Enter IP Address: ");
        String hostname = in.nextLine();

        Socket socket = new Socket(hostname,9090);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        boolean connected = true;
        while (connected) {
            String line = in.readLine();
            String[] command = line.split(",");
            int id = Integer.parseInt(command[0].trim());

            /*
            Command formats:
                1, message // print message
                2, question, max number // ask for number between 0 and max
                3, question // asks for boolean
                4, question // ask for string
                0, // exit program

             */
            boolean valid;
            switch (id) {
                case 1: // print message
                    System.out.println(command[1]);
                    break;
                case 2: // Ask for integer input
                    valid = false;
                    int output = 0;
                    while (!valid) {
                        System.out.print(command[1]); // Ask question
                        output = this.in.nextInt();
                        if (output < 0) {
                            continue;
                        }
                        if (output > Integer.parseInt(command[2])) {
                            continue;
                        }
                        valid = true;
                    }
                    out.println(output);
                    break;
                case 3:
                    valid = false;
                    while (!valid) {
                        System.out.print(command[1]);
                        char answer = this.in.nextLine().charAt(0);

                        if (answer == 'y' || answer == 'Y') {
                            out.println("true");
                            valid = true;
                            continue;
                        }
                        if (answer == 'n' || answer == 'N') {
                            out.println("false");
                            valid = true;
                            continue;
                        }
                        System.out.println();
                    }
                    break;
                case 4:
                    System.out.print(command[1]);
                    String answer = this.in.nextLine();
                    out.println(answer);
                    break;
                case 5:
                    System.out.println();
                    break;
                case 0: // exit
                    socket.close();
                    connected = false;
                    break;
                default:
                    break;
            }
        }
    }
}