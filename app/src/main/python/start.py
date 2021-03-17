from java import jclass
import numpy as np
import numpy
import os
import cv2


# neural network class definition
class neuralNetwork:
# initialise the neural network
    def __init__(self , inputnodes, hiddennodes, outputnodes,learningrate):
        # set number of nodes in each input, hidden, output layer
        self . inodes = inputnodes
        self . hnodes = hiddennodes
        self . onodes = outputnodes
        try:
            # self.wih = numpy.loadtxt("datawih1.txt", dtype=float, delimiter=",")
            # self.who = numpy.loadtxt("datawho1.txt", dtype=float, delimiter=",")
            self.wih = getdata2numpy("datawih1.txt")
            self.who = getdata2numpy("datawho1.txt")
        except IOError:
            self.wih = (numpy.random.rand(self.hnodes, self.inodes) - 0.5)
            self.who = (numpy.random.rand(self.onodes, self.hnodes) - 0.5)

        self.lr = learningrate
        #激活函数和反激活函数
        # self.activation_function=lambda x: scipy.special.expit(x)#使用scipy
        self.activation_function=lambda x: .5 * (1 + np.tanh(.5 * x))#直接定义，双曲正切替代指数函数
        # self.inverse_activation_function=lambda x: scipy.special.logit(x)
        self.inverse_activation_function=lambda x: -numpy.log(1.0/x-1)

        # self.activation_function=lambda x:  x/2
        # self.inverse_activation_function=lambda x:  x*2
        pass
    
    # train the neural network
    def train(self, inputs_list, targets_list):
        # convert inputs list to 2d array
        inputs = numpy.array(inputs_list, ndmin=2).T
        targets = numpy.array(targets_list, ndmin=2).T
        hidden_inputs = numpy.dot(self.wih, inputs)
        hidden_outputs = self.activation_function(hidden_inputs)
        final_inputs = numpy.dot(self.who, hidden_outputs)
        final_outputs = self.activation_function(final_inputs)
        
        output_errors = targets - final_outputs
        hidden_errors = numpy.dot(self.who.T, output_errors)
        self.who+=self.lr*numpy.dot((output_errors*final_outputs*(1.0-final_outputs)),numpy.transpose(hidden_outputs))
        self.wih+=self.lr*numpy.dot((hidden_errors*hidden_outputs*(1.0-hidden_outputs)), numpy.transpose(inputs))
        pass

    # query the neural network
    def query(self,input_list):
        inputs=numpy.array(input_list,ndmin=2).T
        hidden_inputs = numpy.dot(self.wih, inputs)
        hidden_outputs = self.activation_function(hidden_inputs)
        final_inputs = numpy.dot(self.who, hidden_outputs)
        final_outputs = self.activation_function(final_inputs)
        return final_outputs
        pass

    def backquery(self, targets_list):#反向传递
        # transpose the targets list to a vertical array
        final_outputs = numpy.array(targets_list, ndmin=2).T
        # calculate the signal into the final output layer
        final_inputs = self.inverse_activation_function(final_outputs)
        # calculate the signal out of the hidden layer
        hidden_outputs = numpy.dot(self.who.T, final_inputs)
        # scale them back to 0.01 to .99
        hidden_outputs -= numpy.min(hidden_outputs)
        hidden_outputs /= numpy.max(hidden_outputs)
        hidden_outputs *= 0.98
        hidden_outputs += 0.01
        hidden_inputs = self.inverse_activation_function(hidden_outputs)
        # calculate the signal out of the input layer
        inputs = numpy.dot(self.wih.T, hidden_inputs)
        inputs -= numpy.min(inputs)
        inputs /= numpy.max(inputs)
        inputs *= 0.98
        inputs += 0.01
        
        return inputs


    def getresult(self,resultlist):
        resultnum=0
        result=0
        for i in range(10):
            if(resultlist[i]>resultnum):
                resultnum=resultlist[i]
                result=i
        return result
        pass

    def saveresult(self,whoName="gra/datawho1,txt",wihName="gra/datawih1.txt"):
        numpy.savetxt(whoName, self.who, fmt="%f", delimiter=",") #改为保存为浮点数，以逗号分隔 
        numpy.savetxt(wihName, self.wih, fmt="%f", delimiter=",")  
        pass

def showReverse(input_nodes,hidden_nodes,output_nodes,learning_rate):#反向查找
    n=neuralNetwork(input_nodes,hidden_nodes,output_nodes,learning_rate)
    for label in range(10):
        targets = numpy.zeros(output_nodes) + 0.01
        targets[label] = 0.99
        image_data=n.backquery(targets).T
        cv2.imshow("SHOW",image_data.reshape(28,28))
        matplotlib.pyplot.imshow(image_data.reshape(28,28), cmap='Greys', interpolation='None')
        matplotlib.pyplot.show()

def trainData(input_nodes,hidden_nodes,output_nodes,learning_rate,datalists):
    n=neuralNetwork(input_nodes,hidden_nodes,output_nodes,learning_rate)
    training_data_list=datalists
    counter=0
    for record in training_data_list:
        counter+=1
        all_values = record.split(',')
        #数据读入并处理为0-1
        inputs = (numpy.asfarray(all_values[1:]) / 255.0 * 0.99) + 0.01
        targets = numpy.zeros(output_nodes) + 0.01
        # all_values[0] is the target label for this record
        targets[int(all_values[0])] = 0.99
        n.train(inputs, targets)
        if(counter%10000==0):
            print("已训练",counter)
    print("完成",counter,"个训练")
    n.saveresult()


def testData(input_nodes,hidden_nodes,output_nodes,learning_rate,data_list):
    n=neuralNetwork(input_nodes,hidden_nodes,output_nodes,learning_rate)
    testnum,rightnum=0,0
    for testrecord in data_list:
        all_values = testrecord.split(',')
        result=n.query((numpy.asfarray(all_values[1:])))#长度10的那个数组
        resultnum=n.getresult(result)#结果
        if(resultnum==(int)(all_values[0])):
            rightnum+=1
        testnum+=1
        if(testnum%500==0):
            print ("测试：",testnum," 正确：",rightnum)
        pass

    print ("测试：",testnum," 正确：",rightnum)
    print ("正确率:",rightnum/testnum)

def test1Data(data):
    input_nodes = 784
    hidden_nodes = 100
    output_nodes = 10
    learning_rate = 0.1
    n=neuralNetwork(input_nodes,hidden_nodes,output_nodes,learning_rate)
    result=n.query(np.asarray(list(data)))
    resultnum=n.getresult(result)
    print(result)
    print(resultnum)
    return resultnum

def test():

    input_nodes = 784
    hidden_nodes = 100
    output_nodes = 10
    learning_rate = 0.1

    if(True):
        data_list= getdata2lines("mnist_test.csv")
        testData(input_nodes,hidden_nodes,output_nodes,learning_rate,data_list)
    pass



def getdata2numpy(filename):
    assetCtrl=jclass("com.example.num_rec1.AssetCtrl")
    ac=assetCtrl()
    ac.openFile(filename)

    s=ac.readLine()
    arr=[]
    if s :
        arr=np.array(s.split(","),dtype=float,ndmin=2)
        while True :
            s=ac.readLine()
            if not s:
                break
            srr=np.array(s.split(","),dtype=float,ndmin=2)
            arr=np.append(arr,srr,axis=0)
    return arr

def getdata2lines(filename):
    AssetCtrl=jclass("com.example.num_rec1.AssetCtrl")
    ac=AssetCtrl()
    ac.openFile(filename)
    strs=[]
    while(True):
        str=ac.readLine()
        if str:
            strs.append(str)
        else :
            break
    return strs