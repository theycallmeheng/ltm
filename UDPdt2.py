import socket

SERVER = ("36.50.135.242", 2207)
STUDENT_CODE = "B23DCCN368"
QUESTION_CODE = "u8heSQM2"

with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
    sock.settimeout(10)

    # a. Gửi mã sinh viên và mã câu hỏi
    message = f";{STUDENT_CODE};{QUESTION_CODE}"
    sock.sendto(message.encode("utf-8"), SERVER)

    # b. Nhận requestId;a1,a2,...,a50
    packet, address = sock.recvfrom(65535)
    request_id, data = packet.decode("utf-8").strip().split(";", 1)
    numbers = [int(value) for value in data.split(",")]

    # c. Gửi requestId;max,min
    response = f"{request_id};{max(numbers)},{min(numbers)}"
    sock.sendto(response.encode("utf-8"), address)

    print("Nhận:", packet.decode("utf-8"))
    print("Đã gửi:", response)

# d. Socket tự đóng