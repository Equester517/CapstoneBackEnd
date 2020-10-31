import socket
import sys
import time
import main
import cv2
import compare


# 이미지 파일 저장경로
src = "./temp"

BUFF_SIZE = 65535
isCorrect=12

def fileName():
    dte = time.localtime()
    Year = dte.tm_year
    Mon = dte.tm_mon
    Day = dte.tm_mday
    WDay = dte.tm_wday
    Hour = dte.tm_hour
    Min = dte.tm_min
    Sec = dte.tm_sec
    imgFileName = src + str(Year) + '_' + str(Mon) + '_' + str(Day) + '_' + str(Hour) + '_' + str(Min) + '_' + str(Sec)
    return imgFileName


# 서버 소켓 오픈
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 2)
server_socket.bind(('221.167.232.253', 8080))
server_socket.listen(1)
file_recive_cnt = 0

print("TCPServer Waiting for client on port 8080")

# 클라이언트 요청 대기중 .
client_socket, address = server_socket.accept()

# 연결 요청 성공
print("I got a connection from ", address)

sum_data = bytearray()
img_fileName = fileName()
#client_socket.send((1).to_bytes(4,byteorder='little'))
# Data 수신
while True:
    rev_path = 'C://Users/JoonHo/Desktop/image'
    img_data = client_socket.recv(65535)
    sum_data.extend(img_data)
    # img_data = client_socket.recv(65535)
    # if img_data:
    while img_data:
        img_data = client_socket.recv(1024)
        sum_data.extend(img_data)
    else:
        break
    print("finish img recv")
    print(sys.getsizeof(sum_data))

if file_recive_cnt == 0:
    img_fileName = 'image' + ".png"
print(img_fileName)
print(len(sum_data))
img_file = open(img_fileName, "wb")
img_file.write(sum_data)
img_file.close()

contour_hand=main.contour('image.png')
isCorrect=compare.score(contour_hand)
while(True):
    data2 = int(input("보낼 값 : "))
    client_socket.send(data2.to_bytes(4, byteorder='little'))
    #client_socket.send(isCorrect.to_bytes(4, byteorder='little'))
    print("보냄")
print(isCorrect)
print('server off')


