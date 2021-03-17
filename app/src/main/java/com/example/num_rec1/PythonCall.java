package com.example.num_rec1;

import android.content.Context;
import android.util.Log;

//import com.chaquo.python.Kwarg;
//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PythonCall {
//    static Python py;
    static void initPython(Context context){
//        if (! Python.isStarted()) {
//            Python.start(new AndroidPlatform(context));
//        }
    }
    static void getPython(){//单例
//        if(py==null){
//            synchronized (PythonCall.class){
//                if(py==null){
//                    py=Python.getInstance();
//                }
//            }
//        }
    }
    static void callPythonCode(byte[] bytes){
//        getPython();
//        // 调用hello.py模块中的greet函数，并传一个参数
//        // 等价用法：py.getModule("hello").get("greet").call("Android");
//        py.getModule("hello").callAttr("greet", "Android");
//
//        // 调用python内建函数help()，输出了帮助信息
//        py.getBuiltins().get("help").call();
//
//        PyObject obj1 = py.getModule("hello").callAttr("add", 2,3);
//        // 将Python返回值换为Java中的Integer类型
//        Integer sum = obj1.toJava(Integer.class);
//        Log.d(TAG,"add = "+sum.toString());
//
//        // 调用python函数，命名式传参，等同 sub(10,b=1,c=3)
//        PyObject obj2 = py.getModule("hello").callAttr("sub", 10,new Kwarg("b", 1), new Kwarg("c", 3));
//        Integer result = obj2.toJava(Integer.class);
//        Log.d(TAG,"sub = "+result.toString());
//
//        // 调用Python函数，将返回的Python中的list转为Java的list
////        PyObject obj3 = py.getModule("hello").callAttr("get_list", 10,"xx",5.6,'c');
//        PyObject obj3 = py.getModule("hello").callAttr("get_list", 1,2,3,bytes);
//
////        List<PyObject> pyList = obj3.asList();
////        List pyList = obj3.toJava(List.class);
//        Log.d(TAG,"get_list = "+obj3.toString());
//
//
//        // 将Java的ArrayList对象传入Python中使用
//        List<PyObject> params = new ArrayList<PyObject>();
//        params.add(PyObject.fromJava("alex"));
//        params.add(PyObject.fromJava("bruce"));
//        py.getModule("hello").callAttr("print_list", params);
    }

    static void incomebytes(byte[] bytes){
//        getPython();
//        PyObject obj3 = py.getModule("hello").callAttr("showInformation", bytes);
    }
    static void imcomeints(int[] ints){
//        getPython();
//        PyObject obj=py.getModule("hello").callAttr("showInts", ints);
    }

    static void callStart(){
//        getPython();
//        PyObject obj=py.getModule("start").callAttr("test");
    }

    static void callgetdataTest(){
//        getPython();
//        PyObject obj=py.getModule("hello").callAttr("getdata");
    }

    static void test1Data(int[] data){
//        getPython();
//        PyObject obj=py.getModule("start").callAttr("test1Data",data);
    }
    static void test1DataCNN(int[] data){
//        getPython();
//        PyObject obj=py.getModule("CNN1").callAttr("test1Data",data);
    }
}

