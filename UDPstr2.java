import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.StringJoiner;

public class UDPstr2 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            byte[] message = ";B23DCCN368;NprUOXyZ"
                    .getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    message, message.length, server, 2208));

            byte[] buffer = new byte[65535];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String received = new String(
                    packet.getData(), packet.getOffset(),
                    packet.getLength(), StandardCharsets.UTF_8);

            String[] parts = received.split(";", 2);
            String requestId = parts[0];
            String data = parts[1].trim().toLowerCase(Locale.ROOT);

            StringJoiner normalized = new StringJoiner(" ");
            if (!data.isEmpty()) {
                for (String word : data.split("\\s+")) {
                    normalized.add(
                            word.substring(0, 1).toUpperCase(Locale.ROOT)
                                    + word.substring(1));
                }
            }

            String response = requestId + ";" + normalized;
            byte[] result = response.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    result, result.length,
                    packet.getAddress(), packet.getPort()));

            System.out.println("Đã gửi: " + response);
        }
    }
}