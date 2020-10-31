import cv2
import numpy as np

def contour(image):
    # 사진 사이즈 조정
    def Resizing(img):
        print(img.shape)
        width = 500
        ratio = width / img.shape[1]  # width * 사진 너비 = 비율
        height = int(ratio * img.shape[0])  # 비율 * 사진 높이
        # 비율 유지한 채로 이미지 Resize
        resize = cv2.resize(img, dsize=(width, height), interpolation=cv2.INTER_AREA)
        print(resize.shape)

        return resize


    # 창 이름 설정

    cv2.namedWindow('image')

    # 이미지 파일 읽기

    base = cv2.imread(image, cv2.IMREAD_COLOR)
    img=cv2.rotate(base,cv2.ROTATE_90_CLOCKWISE)

    # 이미지 사이즈 조정

    img = Resizing(img)

    # 이미지 hsv 색 변경

    img_hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    # 피부색 범위 검출

    img_hsv = cv2.fastNlMeansDenoisingColored(img_hsv, None, 10, 10, 7, 21) # 노이즈제거

    lower = np.array([0, 35, 85], dtype="uint8") # 최소 범위

    upper = np.array([35, 255, 255], dtype="uint8") # 최대 범위

    img_hand = cv2.inRange(img_hsv, lower, upper) # 범위 내 이미지 추출

    # 손 가장자리 외각선 찾기

    contours, hierarchy = cv2.findContours(img_hand, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)

    max = 0

    maxctr = None

    for cnt in contours:

        area = cv2.contourArea(cnt)

        if (max < area):
            max = area

            maxctr = cnt # 손 가장자리 배열

    # 흰색으로 손 내부 다시 칠하기

    mask = np.zeros(img.shape).astype(img.dtype)

    color = [255,255,255]

    img_hand=cv2.fillPoly(mask, [maxctr], color)


    # 손 외각선 그리기

    img_out=cv2.drawContours(img_hand, [maxctr], 0, (0, 0, 255), 3)



    # 이미지 보여주기
    cv2.imshow('image', img_hand)
    cv2.imwrite('contour.png',img_hand)

    # 창 esc 끄기

    return img_hand