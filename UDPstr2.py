import socket

SERVER = ("36.50.135.242", 2208)
STUDENT_CODE = "B23DCCN368"
QUESTION_CODE = "NprUOXyZ"

with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
    sock.settimeout(10)

    # a. Gửi mã sinh viên và mã câu hỏi
    message = f";{STUDENT_CODE};{QUESTION_CODE}"
    sock.sendto(message.encode("utf-8"), SERVER)

    # b. Nhận requestId;data
    packet, address = sock.recvfrom(65535)
    request_id, data = packet.decode("utf-8").split(";", 1)

    # c. Chuẩn hóa từng từ, loại bỏ khoảng trắng thừa
    normalized = " ".join(word.capitalize() for word in data.split())

    response = f"{request_id};{normalized}"
    sock.sendto(response.encode("utf-8"), address)

    print("Nhận:", data)
    print("Đã gửi:", response)

# d. Socket tự đóng