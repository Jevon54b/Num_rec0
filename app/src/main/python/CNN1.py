import numpy as np
import numpy
from java import jclass

# neural network class definition
class neuralNetwork:
# initialise the neural network
    def __init__(self , side_len, kernel_len ,outputnodes,learningrate):
        # set number of nodes in each input, hidden, output layer
        self.side_len=side_len #输入图边长
        self.kernel_len= kernel_len #卷积核边长3,池化直接2*2,步长1就好
        self.onodes=outputnodes #输出
        self.lr=learningrate
        self.fullconnodes=side_len*side_len//4 #全连接层应该有的长度
        self.pooling = np.zeros((self.side_len//2,self.side_len//2))

        try:
            self.kernel = getdata2numpy("kernel.csv")
            self.weight_fullConn = getdata2numpy("fullconn.csv")

        except IOError:
            self.kernel = numpy.random.rand(self.kernel_len,self.kernel_len)-0.5
            self.weight_fullConn = numpy.random.rand(self.onodes,self.fullconnodes)-0.5

        #激活函数和反激活函数
        self.activation_function=lambda x: .5 * (1 + np.tanh(.5 * x))#直接定义，双曲正切替代指数函数
        # self.relu=lambda x: np.maximum(0,x)
        # self.inverse_activation_function=lambda x: -numpy.log(1.0/x-1)

        pass

    def conv2d(self,inputs,kernel):#直接把输入和卷积核卷积保存到类里面
        w,h=inputs.shape
        inputs=np.pad(inputs,((1,1),(1,1)),'constant',constant_values=(0,0))
        result=np.zeros((w,h))
        for i in range(1,w):
            for j in range(1,h):
                cur_input=inputs[i-1:i+2,j-1:j+2]
                cur_output=cur_input*kernel
                result[i,j]=np.sum(cur_output)
        return result
        pass#卷积完成，放在self.conv_layer

    def max_pooling(self):
        poollen=self.side_len//2
        for i in range(poollen):
            for j in range(poollen):
                self.pooling[i,j]=max(self.conv_layer[i*2,j*2],\
                self.conv_layer[i*2+1,j*2],self.conv_layer[i*2,j*2+1],self.conv_layer[i*2+1,j*2+1])
        pass

    def cul_conv_error(self,pool_errors):#计算池化层到卷积层输出的error
        pool_len=self.side_len//2
        errors_resize=pool_errors.reshape(pool_len,pool_len)
        conv_errors=np.zeros((self.side_len,self.side_len))
        for i in range(pool_len):
            for j in range(pool_len):
                for x in range(2):
                    for y in range(2):
                        convnum=self.conv_layer[i*2+x,j*2+y]
                        poolnum=self.pooling[i,j]
                        if convnum==poolnum:
                            conv_errors[i*2,j*2]=errors_resize[i,j]
                        #不相等不用管，本来就是0
        return conv_errors
        pass

    def get_kernel_errors(self,origin_graph,conv_error):
        kerrors=np.zeros((self.kernel_len,self.kernel_len))
        for i in range(self.kernel_len):
            for j in range(self.kernel_len):
                product=origin_graph[i,self.side_len-self.kernel_len+i]*conv_error[j,self.side_len-self.kernel_len+j]
                kerrors[i,j]=np.sum(product)
        return kerrors
        pass
    
    # train the neural network
    def train(self, inputs_list, targets_list):
        # convert inputs list to 2d array
        inputs = numpy.array(inputs_list).reshape(self.side_len,self.side_len)#转为二维

        targets = numpy.array(targets_list, ndmin=2).T #这个还是一维
        self.conv_layer=self.conv2d(inputs,self.kernel)
        self.max_pooling()
        pooling_output = self.activation_function(self.pooling)

        final_outputs = numpy.dot(self.weight_fullConn, pooling_output.reshape(self.fullconnodes).T)#矩阵*列向量
        final_outputs_activated = self.activation_function(final_outputs).reshape(self.onodes,1)
        output_errors = targets - final_outputs_activated
        pool_errors = numpy.dot(self.weight_fullConn.T,output_errors)
        conv_errors=self.cul_conv_error(pool_errors)#self.conv_error
        conv_errors=self.conv2d(conv_errors,self.kernel.T)#反卷积
        kernel_errors=self.get_kernel_errors(inputs,conv_errors)

        self.kernel+=self.lr*kernel_errors

        self.weight_fullConn+=\
            self.lr*numpy.dot((output_errors*final_outputs_activated*(1.0-final_outputs_activated)),\
                pooling_output.reshape(1,self.fullconnodes))

        pass

    # query the neural network
    def query(self,inputs_list):
        inputs = numpy.array(inputs_list).reshape(self.side_len,self.side_len)#转为二维
        self.conv_layer=self.conv2d(inputs,self.kernel)
        self.max_pooling()
        pooling_output = self.activation_function(self.pooling)
        
        final_outputs = numpy.dot(self.weight_fullConn, pooling_output.reshape(self.fullconnodes).T)#矩阵*列向量
        final_outputs_activated = self.activation_function(final_outputs).reshape(self.onodes,1)
        
        # printall(self.conv_layer)
        # printall(pooling_output)
        return final_outputs_activated
        pass


    def getresult(self,resultlist):
        resultnum=0
        result=0
        for i in range(10):
            if(resultlist[i]>resultnum):
                resultnum=resultlist[i]
                result=i
        return result
        pass

    def saveresult(self,kernelName="gra/data/kernel.csv",fullConnName="gra/data/fullconn.csv"):
        numpy.savetxt(kernelName, self.kernel, fmt="%f", delimiter=",") #改为保存为浮点数，以逗号分隔 
        numpy.savetxt(fullConnName, self.weight_fullConn, fmt="%f", delimiter=",")  
        pass
    

def trainData(side_len,kernel_len,output_nodes,learning_rate,datalists):
    n=neuralNetwork(side_len,kernel_len,output_nodes,learning_rate)
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
        if(counter%100==0):
            print("已训练",counter)
        if(counter==60000):
            break
    print("完成",counter,"个训练")
    n.saveresult()

def testData(side_len,kernel_len,output_nodes,learning_rate,data_list):
    n=neuralNetwork(side_len,kernel_len,output_nodes,learning_rate)
    testnum,rightnum=0,0
    for testrecord in data_list:
        all_values = testrecord.split(',')
        result=n.query((numpy.asfarray(all_values[1:])))#长度10的那个数组
        resultnum=n.getresult(result)#结果
        if(resultnum==(int)(all_values[0])):
            rightnum+=1
        if(testnum%100==0):
            print ("测试：",testnum," 正确：",rightnum)
        testnum+=1
        pass

    print ("测试：",testnum," 正确：",rightnum)
    print ("正确率:",rightnum/testnum)

def printall(any):
    print(any)
    print(len(any))
    print(type(any))

# if __name__ == '__main__':
#     side_len = 28
#     kernel_len = 3
#     output_nodes = 10
#     learning_rate = 0.05
#     # load the mnist training data CSV file into a list
#     training_data_file = open("gra/mnist_train.csv", 'r')
#     training_data_list = training_data_file.readlines()
#     training_data_file.close()
#     if(True):#是否训练？
#         trainData(side_len,kernel_len,output_nodes,learning_rate,training_data_list)
#         pass
#     if(True):
#         data_file= open("gra/mnist_test.csv", 'r')
#         data_list= data_file.readlines()
#         data_file.close()
#         testData(side_len,kernel_len,output_nodes,learning_rate,data_list)
#
#     pass


def test1Data(data):
    side_len = 28
    kernel_len = 3
    output_nodes = 10
    learning_rate = 0.05
    n=neuralNetwork(side_len,kernel_len,output_nodes,learning_rate)
    result=n.query(np.asarray(list(data)))
    resultnum=n.getresult(result)
    print("CNN")
    print(result)
    print(resultnum)
    return resultnum

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
