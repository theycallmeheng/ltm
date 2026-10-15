import socket

SERVER = ("36.50.135.242", 2207)
STUDENT_CODE = "B23DCCN368"  # Thay mã sinh viên
QUESTION_CODE = "WcVGrvbF"  # Thay mã câu hỏi trong đề

with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
    sock.settimeout(10)

    # a. Gửi thông tin đăng ký
    message = f";{STUDENT_CODE};{QUESTION_CODE}"
    sock.sendto(message.encode("utf-8"), SERVER)

    # b. Nhận requestId;n;A1,A2,...,Am
    data, address = sock.recvfrom(65535)
    request_id, n_text, values_text = (
        data.decode("utf-8").strip().split(";", 2)
    )

    n = int(n_text)
    present = {
        int(value)
        for value in values_text.split(",")
        if value.strip()
    }

    # c. Tìm các số còn thiếu từ 1 đến n, theo thứ tự tăng dần
    missing = [str(value) for value in range(1, n + 1)
               if value not in present]

    response = f"{request_id};{','.join(missing)}"
    sock.sendto(response.encode("utf-8"), address)
    print("Đã gửi:", response)

# d. Socket tự đóng khi thoát khối with