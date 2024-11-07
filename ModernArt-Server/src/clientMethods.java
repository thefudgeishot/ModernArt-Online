import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class clientMethods {

    public clientMethods() {

    }

    public void broadcastMessageToSockets(Player[] players, String message) throws IOException {
        System.out.println(message); // send message to host
        for (int i = 0; i != players.length; i++) {
            Player player = players[i];
            if (player == null) {
                continue;
            }
            if (!(player.getSocket() == null)) {
                PrintWriter out = new PrintWriter(player.getSocket().getOutputStream(), true);

                out.println("1," + message);
            }
        }
    }

    public void broadcastNewLineToSockets(Player[] players) throws  IOException {
        System.out.println(); // send newLine to host
        for (int i = 0; i != players.length; i++) {
            Player player = players[i];
            if (player == null) {
                continue;
            }
            if (!(player.getSocket() == null)) {
                PrintWriter out = new PrintWriter(player.getSocket().getOutputStream(), true);

                out.println("5,");
            }
        }
    }

    public void broadcastMessageToSocket(Socket socket, String message) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String format = "1," + message.trim() + " ";
        out.println(format);
        out.flush();
    }

    public int getIntFromSocket(Socket socket, String question, int maxValue) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("2," + question + "," + maxValue); // send question to client
        boolean response = false;
        while (!response) {
            int result = Integer.parseInt(in.readLine());
            if (result < 0) {
                continue;
            }
            if (maxValue < result) {
                continue;
            }
            response = true;
            return result;
        }
        return 0;
    }

    public boolean getBooleanFromSocket(Socket socket, String question) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        boolean response = false;
        while (!response) {
            out.println("3," + question); // send question to client
            boolean result = Boolean.getBoolean(in.readLine());

            if (result) {
                response = true;
            }
            if (!result) {
                response = true;
            }
            return result;
        }
        return false;
    }

    public String getStringFromSocket(Socket socket, String question) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String result = "";
        boolean response = false;
        while (!response) {
            out.println("3," + question); // send question to client
            result = in.readLine();

            if (!Objects.equals(result, "")) {
                response = true;
            }
        }
        return result;
    }
}
