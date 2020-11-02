import cv2
import numpy as np
from matplotlib import pyplot as plt

# def shed(image):
# 사진 사이즈 조정
def Resizing(img):
    width = 500
    ratio = width / img.shape[1]  # width * 사진 너비 = 비율
    height = int(ratio * img.shape[0])  # 비율 * 사진 높이
    # 비율 유지한 채로 이미지 Resize
    resize = cv2.resize(img, dsize=(width, height), interpolation=cv2.INTER_AREA)
    print(resize.shape)

    return resize


# 창 이름 설정
image='hand.png'
# cv2.namedWindow('image')
# 이미지 파일 읽기
base = cv2.imread(image)
gray = cv2.cvtColor(base, cv2.COLOR_BGR2GRAY)
ret , thresh= cv2.threshold(gray, 90, 255, cv2.THRESH_BINARY_INV+cv2.THRESH_OTSU)
kernel = np.ones((3,3), np.uint8)
opening = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, kernel, iterations=2)
sure_bg = cv2.dilate(opening, kernel, iterations=3)


dist_transform = cv2.distanceTransform(opening, cv2.DIST_L2, 5)
ret, sure_fg = cv2.threshold(dist_transform, 0.5*dist_transform.max(), 255, 0)
sure_fg = np.uint8(sure_fg)
# 배경과 전경을 제외한 영역 곳을 확보
unknown = cv2.subtract(sure_bg, sure_fg)
# 마커 생성 작성
ret, markers = cv2.connectedComponents(sure_fg)
markers = markers + 1
markers[unknown == 255] = 0
# 앞서 생성한 마커를 이용해 Watershed 알고리즘을 적용
markers = cv2.watershed(base, markers)
base[markers == -1] = [255,0,0]

#fg = cv2.erode(binary, (-1, -1), 12)
#bg = cv2.dilate(binary, (-1, -1), 40)
#cv2.threshold(bg, bg, 1, 128, cv2.THRESH_BINARY_INV)
#markers = fg + bg
cv2.imshow('1', base)
cv2.imshow('2', gray)
cv2.imshow('3', ret)
cv2.imshow('4',thresh)
cv2.imshow('5',ret)
#cv2.imshow('4', fg)
#cv2.imshow('5', bg)
#cv2.imshow('6', markers)
cv2.waitKey()
cv2.destroyAllWindows()