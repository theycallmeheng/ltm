import socket
from collections import Counter

SERVER = ("36.50.135.242", 2208)
STUDENT_CODE = "B23DCCN368"
QUESTION_CODE = "ih2dwWtj"  # Kiểm tra đúng mã trên đề

with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
    sock.settimeout(10)

    # a. Gửi mã sinh viên và mã câu hỏi
    message = f";{STUDENT_CODE};{QUESTION_CODE}"
    sock.sendto(message.encode("utf-8"), SERVER)

    # b. Nhận dữ liệu, giữ nguyên nội dung chuỗi
    packet, address = sock.recvfrom(65535)
    request_id, data = packet.decode("utf-8").split(";", 1)

    # c. Tìm ký tự xuất hiện nhiều nhất
    counts = Counter(data)
    character = max(counts, key=counts.get)

    positions = "".join(
        f"{i}," for i, char in enumerate(data, start=1)
        if char == character
    )

    response = f"{request_id};{character}:{positions}"
    sock.sendto(response.encode("utf-8"), address)

    print("Nhận:", data)
    print("Gửi:", response)

# d. Socket tự đóng