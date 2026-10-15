import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class UDPstr {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            byte[] message = ";B23DCCN368;ih2dwWtj"
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
            String data = parts[1];

            Map<Character, Integer> counts = new LinkedHashMap<>();
            for (char c : data.toCharArray()) {
                counts.put(c, counts.getOrDefault(c, 0) + 1);
            }

            char mostFrequent = '\0';
            int maxCount = 0;
            for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    mostFrequent = entry.getKey();
                    maxCount = entry.getValue();
                }
            }

            StringBuilder response = new StringBuilder(requestId)
                    .append(';').append(mostFrequent).append(':');

            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == mostFrequent) {
                    response.append(i + 1).append(',');
                }
            }

            byte[] result = response.toString()
                    .getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    result, result.length,
                    packet.getAddress(), packet.getPort()));

            System.out.println("Đã gửi: " + response);
        }
    }
}