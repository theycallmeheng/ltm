import UDP.Customer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class object2 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // 1. Gửi mã sinh viên và mã câu hỏi
            byte[] registration = ";B23DCCN368;FT53brW3"
                    .getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                    registration, registration.length, server, 2209));

            // 2. Nhận 8 byte requestId và đối tượng Customer
            byte[] buffer = new byte[65535];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            int offset = packet.getOffset();
            if (packet.getLength() <= 8) {
                throw new IOException("Gói tin không chứa Customer");
            }

            Customer customer;
            try (ObjectInputStream input = new ObjectInputStream(
                    new ByteArrayInputStream(
                            packet.getData(),
                            offset + 8,
                            packet.getLength() - 8))) {
                customer = (Customer) input.readObject();
            }

            // 3. Xử lý tên gốc
            String[] words = customer.name.trim()
                    .toLowerCase(Locale.ROOT)
                    .split("\\s+");

            int last = words.length - 1;

            // nguyen van hai duong -> nv h + duong -> nvhduong
            StringBuilder userName = new StringBuilder();
            for (int i = 0; i < last; i++) {
                userName.append(words[i].charAt(0));
            }
            userName.append(words[last]);
            customer.userName = userName.toString();

            // nguyen van hai duong -> DUONG, Nguyen Van Hai
            StringBuilder name = new StringBuilder(
                    words[last].toUpperCase(Locale.ROOT));

            if (last > 0) {
                name.append(", ");
                for (int i = 0; i < last; i++) {
                    if (i > 0) {
                        name.append(' ');
                    }
                    name.append(
                            words[i].substring(0, 1)
                                    .toUpperCase(Locale.ROOT))
                            .append(words[i].substring(1));
                }
            }
            customer.name = name.toString();

            // mm-dd-yyyy -> dd/mm/yyyy
            String[] date = customer.dayOfBirth.trim().split("-");
            customer.dayOfBirth = String.format(
                    Locale.ROOT, "%02d/%02d/%04d",
                    Integer.parseInt(date[1]),
                    Integer.parseInt(date[0]),
                    Integer.parseInt(date[2]));

            // 4. Giữ nguyên 8 byte requestId, nối Customer đã sửa
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(packet.getData(), offset, 8);

            try (ObjectOutputStream objectOutput = new ObjectOutputStream(output)) {
                objectOutput.writeObject(customer);
                objectOutput.flush();
            }

            byte[] response = output.toByteArray();
            socket.send(new DatagramPacket(
                    response,
                    response.length,
                    packet.getAddress(),
                    packet.getPort()));

            System.out.println("Tên: " + customer.name);
            System.out.println("Ngày sinh: " + customer.dayOfBirth);
            System.out.println("Tài khoản: " + customer.userName);
            System.out.println("Đã gửi kết quả.");
        }
    }
}