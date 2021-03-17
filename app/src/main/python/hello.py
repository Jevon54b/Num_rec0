from java import jclass
import cv2
import numpy as np
import os


def greet(name):
    print("--- hello,%s ---" % name)

def add(a,b):
    return a + b

def sub(count,a=0,b=0,c=0):
    return count - a - b -c

def showInformation(a):
    print("TYPE")
    print(type(a))
    print("INFO")
    print(bytes(a))
    pass

def showInts(a):
    # print(os.path.realpath(__file__))
    # file_walk()
    # print(os.path.abspath("/"))
    intsa=np.asarray(list(a)).reshape(28,28)
    print(type(intsa))
    print(intsa)
    pass

def file_name_listdir(file_dir):
    for files in os.listdir(file_dir):  # 不仅仅是文件，当前目录下的文件夹也会被认为遍历到
        print("files", files)

def file_walk():
    path = '/data/data/com.example.num_rec1'

    for root, dirs, files in os.walk(path, True):
        print ('root: %s'% root)
        print ('dirs: %s'% dirs)
        print ('files: %s'% files)
        print ('')

    print(os.path.abspath(__file__))
    print(__package__)

def get_list(a,b,c,d):
    print("LISTFUNC")
    print(bytes(d))
    # img = cv2.imdecode(np.frombuffer(bytes(d), np.uint8), cv2.IMREAD_COLOR)
    # print(img)
    # im_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    return [a,b,c,d]

def print_list(data):
    print(type(data))
    # 遍历Java的ArrayList对象
    for i in range(data.size()):
        print(data.get(i))

# python调用Java类
def get_java_bean():
    JavaBean = jclass("com.example.num_rec0.python.JavaBean")#用自己的包名
    jb = JavaBean("python")
    jb.setData("json")
    jb.setData("xml")
    jb.setData("xhtml")
    return jb

def getdata():
    AssetCtrl=jclass("com.example.num_rec1.AssetCtrl")
    ac=AssetCtrl()
    ac.openFile("datawho1.txt")
    strt=""
    while(True):
        str=ac.readLine()
        if str:
            strt+=str+"\n"
        else :
            break
    print(strt)