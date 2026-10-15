import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class UDPdt {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            byte[] message = ";B23DCCN368;WcVGrvbF"
                    .getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    message, message.length, server, 2207));

            byte[] buffer = new byte[65535];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String received = new String(
                    packet.getData(), packet.getOffset(),
                    packet.getLength(), StandardCharsets.UTF_8);

            String[] parts = received.trim().split(";", 3);
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1].trim());

            boolean[] present = new boolean[n + 1];
            for (String value : parts[2].split(",")) {
                if (!value.trim().isEmpty()) {
                    int number = Integer.parseInt(value.trim());
                    if (number >= 1 && number <= n) {
                        present[number] = true;
                    }
                }
            }

            StringJoiner missing = new StringJoiner(",");
            for (int i = 1; i <= n; i++) {
                if (!present[i]) {
                    missing.add(Integer.toString(i));
                }
            }

            String response = requestId + ";" + missing;
            byte[] result = response.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    result, result.length,
                    packet.getAddress(), packet.getPort()));

            System.out.println("Đã gửi: " + response);
        }
    }
}