import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPdt2 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            byte[] message = ";B23DCCN368;u8heSQM2"
                    .getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    message, message.length, server, 2207));

            byte[] buffer = new byte[65535];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String received = new String(
                    packet.getData(), packet.getOffset(),
                    packet.getLength(), StandardCharsets.UTF_8);

            String[] parts = received.trim().split(";", 2);
            String requestId = parts[0];

            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            for (String value : parts[1].split(",")) {
                int number = Integer.parseInt(value.trim());
                min = Math.min(min, number);
                max = Math.max(max, number);
            }

            String response = requestId + ";" + max + "," + min;
            byte[] result = response.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    result, result.length,
                    packet.getAddress(), packet.getPort()));

            System.out.println("Đã gửi: " + response);
        }
    }
}