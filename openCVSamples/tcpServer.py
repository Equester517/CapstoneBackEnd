import socket
import main2
import cv2
import numpy
import compare
import main
import time
# 이미지 파일 저장경로
src = "./temp"

BUFF_SIZE = 65535
isCorrect = 12

# 서버 소켓 오픈
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 2)
server_socket.bind(('221.167.232.253', 8080))
server_socket.listen(1)
file_recive_cnt = 0

print("TCPServer Waiting for client on port 8080")

def access():
    # 클라이언트 요청 대기중 .
    client_socket, address = server_socket.accept()

    # 연결 요청 성공
    print("I got a connection from ", address)

    sum_data = bytearray()
    sum_data2 = bytearray()
    # Data 수신
    while True:
        length = client_socket.recv(4)
        leng = int.from_bytes(length, byteorder='big', signed=False)
        print(leng)
        img_data = client_socket.recv(1024)
        sum_data.extend(img_data)
        while img_data:
            hexSum = sum_data.hex()
            size = len(hexSum) / 2
            if leng == size:
                break
            img_data = client_socket.recv(65535)
            sum_data.extend(img_data)
        else:
            break
        break
    print("finish img recv1")
    img_fileName = 'image' + ".png"
    print(img_fileName)
    img_file = open(img_fileName, "wb")
    img_file.write(sum_data)
    img_file.close()
    ###############################################################

    while True:
        length = client_socket.recv(4)
        leng = int.from_bytes(length, byteorder='big', signed=False)
        print(leng)
        img_data = client_socket.recv(1024)
        sum_data2.extend(img_data)
        while img_data:
            hexSum = sum_data2.hex()
            size = len(hexSum) / 2
            if leng == size:
                break
            img_data = client_socket.recv(65535)
            sum_data2.extend(img_data)
        else:
            break
        break
    print("finish img recv2")
    img_fileName2 = 'image2' + ".png"
    print(img_fileName2)
    img_file2 = open(img_fileName2, "wb")
    img_file2.write(sum_data2)
    img_file2.close()
    ###################################################################
    contour_hand = main.contour('image.png')
    contour_hand2 = main2.contour('image2.png')
    ###################################################################
    isCorrect = compare.score(contour_hand)
    isCorrect2=compare.score(contour_hand2)
    ###################################################################
    print('알고리즘 처리 완료')
    sumCorrect=bytearray()
    sumCorrect.extend(isCorrect.to_bytes(1, byteorder='little'))
    sumCorrect.extend(isCorrect2.to_bytes(1,byteorder='little'))
    client_socket.send(sumCorrect)

    frame = cv2.imread('contour.png')
    #추출한 이미지를 String 형태로 변환(인코딩)시키는 과정
    encode_param=[int(cv2.IMWRITE_JPEG_QUALITY),90]
    result, imgencode = cv2.imencode('.jpg', frame, encode_param)
    data = numpy.array(imgencode)
    stringData = data.tostring()
    print(str(len(stringData)).ljust(16))
    client_socket.send(stringData)
    time.sleep(3)
    frame2 = cv2.imread('contour2.png')
    #추출한 이미지를 String 형태로 변환(인코딩)시키는 과정
    encode_param2=[int(cv2.IMWRITE_JPEG_QUALITY),90]
    result2, imgencode2 = cv2.imencode('.jpg', frame2, encode_param2)
    data2 = numpy.array(imgencode2)
    stringData2 = data2.tostring()
    print(str(len(stringData2)).ljust(16))
    client_socket.send(stringData2)

while True:
    access()
