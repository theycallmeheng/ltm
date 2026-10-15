import UDP.Student;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

public class object1 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2209;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // a. Gửi mã sinh viên và mã câu hỏi
            byte[] registration = ";B23DCCN368;MDD1boBB".getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                    registration, registration.length, server, port));

            // b. Nhận 8 byte requestId và đối tượng Student
            byte[] buffer = new byte[65535];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            int offset = packet.getOffset();
            if (packet.getLength() <= 8) {
                throw new IOException("Gói tin không chứa đối tượng Student");
            }

            Student student;
            try (ObjectInputStream input = new ObjectInputStream(
                    new ByteArrayInputStream(
                            packet.getData(),
                            offset + 8,
                            packet.getLength() - 8))) {
                student = (Student) input.readObject();
            }

            // c. Chuẩn hóa tên
            String[] words = student.name.trim()
                    .toLowerCase(Locale.ROOT)
                    .split("\\s+");

            StringBuilder normalizedName = new StringBuilder();
            for (String word : words) {
                if (normalizedName.length() > 0) {
                    normalizedName.append(' ');
                }
                normalizedName.append(
                        word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
            }
            student.name = normalizedName.toString();

            // Email: tên + chữ cái đầu của họ và tên đệm
            // nguyen van tuan nam -> namnvt@ptit.edu.vn
            StringBuilder email = new StringBuilder(
                    removeAccents(words[words.length - 1]));
            for (int i = 0; i < words.length - 1; i++) {
                email.append(removeAccents(words[i]).charAt(0));
            }
            student.email = email + "@ptit.edu.vn";

            // Giữ nguyên 8 byte requestId, nối đối tượng đã sửa
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(packet.getData(), offset, 8);

            try (ObjectOutputStream objectOutput = new ObjectOutputStream(output)) {
                objectOutput.writeObject(student);
                objectOutput.flush();
            }

            byte[] response = output.toByteArray();
            socket.send(new DatagramPacket(
                    response,
                    response.length,
                    packet.getAddress(),
                    packet.getPort()));

            System.out.println("Tên: " + student.name);
            System.out.println("Email: " + student.email);
            System.out.println("Đã gửi kết quả.");
        }
        // d. Socket tự đóng
    }

    private static String removeAccents(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
    }
}