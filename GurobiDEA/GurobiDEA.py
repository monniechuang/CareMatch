# -*- coding: utf-8 -*-
"""
Created on Thu Jan  3 09:13:29 2019

@author: Moya
"""
import numpy as np
from gurobipy import *


import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


# 初始化firebase，注意不能重複初始化

#firebase_admin.initialize_app(cred)

# 初始化firestore

cred = credentials.Certificate('/Users/jasonroy7dct/Documents/carematch-c8be7-firebase-adminsdk-w2so4-3c14348738.json')
db = firestore.client() 
path = "Human"

collection_ref = db.collection(path)

docs = collection_ref.get()

x_temp = []
y_temp = []

ans = []

for doc in docs:
    
    array1 = ([(doc.to_dict()['H_sex'])
    ,(doc.to_dict()['H_age'])
    ,(doc.to_dict()['H_educate'])
    ,(doc.to_dict()['H_years'])
    ,(doc.to_dict()['H_exp'])
    ,(doc.to_dict()['H_score'])])
    
    
    print(array1)
    
    
    array2=[]
    
    #性別分數
    score_sex = 0
    if(array1[0]=='女'):
        score_sex+=20
    if(array1[0]=='男'):
        score_sex+=10
                
        #print(score_sex)
        
    array2.append(score_sex)
        
    #年紀分數
    score_age = 0
    if(array1[1]>'20' and array1[1]<='25'):
        score_age+=10
    if(array1[1]>'25' and array1[1]<='30'):
        score_age+=20
    if(array1[1]>'30' and array1[1]<='35'):
        score_age+=30
    if(array1[1]>'35' and array1[1]<='40'):
        score_age+=40
    if(array1[1]>'40' and array1[1]<='45'):
        score_age+=50
    if(array1[1]>'45' and array1[1]<='50'):
        score_age+=60
    if(array1[1]>'50' and array1[1]<='55'):
        score_age+=70
    if(array1[1]>'55' and array1[1]<='60'):
        score_age+=80
    if(array1[1]>'60'):
        score_age+=90
        #print(score_age)
        
    array2.append(score_age)
    
    #教育分數
    score_edu = 0
    if(array1[2]=='小學'):
        score_edu+=10
    if(array1[2]=='國中'):
        score_edu+=20
    if(array1[2]=='高中'):
        score_edu+=30
    if(array1[2]=='專科'):
        score_edu+=40
    if(array1[2]=='大學'):
        score_edu+=50
    
    array2.append(score_edu)
    
    
    #年資分數
    score_years = 0
    if(array1[3]<'3'):
        score_years+=10
    if(array1[3]>'3' and array1[3]<='5'):
        score_years+=20
    if(array1[3]>'5' and array1[3]<='8'):
        score_years+=30
    if(array1[3]>'8' and array1[3]<='11'):
        score_years+=40
    if(array1[3]>'11' and array1[3]<='14'):
        score_years+=50
    if(array1[3]>'14' and array1[3]<='17'):
        score_years+=60
    if(array1[3]>'17' and array1[3]<='20'):
        score_years+=70
    if(array1[3]>'20' and array1[3]<='23'):
        score_years+=80
    if(array1[3]>'23'):
        score_years+=90
    
    array2.append(score_years)
    
    #經驗分數
    score_exp = 0
    if('1' or '2' or '3' in array1[4]):
        score_exp+=1
    if('4' or '5' or '6' in array1[4]):
        score_exp+=2
    if('7' or '8' in array1[4]):
        score_exp+=3
    if('9' in array1[4]):
        score_exp+=4
    if('10' in array1[4]):
        score_exp+=5
    if('11' in array1[4]):
        score_exp+=6
    if('12' in array1[4]):
        score_exp+=7
    if('13' in array1[4]):
        score_exp+=8
    if('14' in array1[4]):
        score_exp+=9
    if('15' in array1[4]):
        score_exp+=10
    if('16' in array1[4]):
        score_exp+=11
    if('17' or '18' or '19' in array1[4]):
        score_exp+=11
    if('20' or '21' or '' in array1[4]):
        score_exp+=11
    
    array2.append(score_exp)

    
    #評價分數
    score_score = 0
    if(array1[5]=='1'):
        score_score+=10
    if(array1[5]=='2'):
        score_score+=20
    if(array1[5]=='3'):
        score_score+=30
    if(array1[5]=='4'):
        score_score+=40
    if(array1[5]=='5'):
        score_score+=50
        
    array2.append(score_score)


    print(array2)
    
    x_temp.append(array2[:3])
    y_temp.append(array2[3:6])

for j in range(len(x_temp)):
    try:
    # Create a new model
        no_input_factors=2; 
        no_output_factors=2;
        no_units=3;
        m = Model('CareMatch DEA') #自己給定的模型名稱
    
    
    
        x = np.array(x_temp)  
    
        y = np.array(y_temp)             
        #print(x)
        #print('\n',y)  
        
    
    
    
    #print('\n',array2)   
    
    # Create variables
    
        v = m.addVars(no_input_factors, vtype=GRB.CONTINUOUS, name='v')
        u = m.addVars(no_output_factors, vtype=GRB.CONTINUOUS,name='u')    
    # Integrate new variables
        m.update()
    
    
    # Set objective  Maximize
        
        obj = 0
                
        for r in range(no_output_factors):
            obj+=y[j][r]*u[r]
        
        #這裡要設置我們的目標式
        m.setObjective(obj, GRB.MAXIMIZE)     
        # Set constraints
        constraint_1=0
        for s in range(no_input_factors):
            constraint_1+=x[j][s]*v[s]
       #這裡要設置我們的條件式
        m.addConstr(constraint_1==1) 
    
        for k in range(no_units):
            rhs_const=lhs_const=0
            for r in range(no_output_factors):
                lhs_const+=y[k][r]*u[r]
                for s in range(no_input_factors):
                    rhs_const+=x[k][s]*v[s]
                    m.addConstr(lhs_const<=rhs_const)   #這裡要設置我們的條件式  
    
        m.optimize()
    
        m.write('mip1.lp')
     
        for uv in m.getVars():
            print(uv.varName,'=', uv.x)
        print(j)
        
        ans.append(m.objVal)
            
    
    except GurobiError:
        print('Encountered a Gurobi error')
        
        

    
for i in range(len(ans)):
    print("第", i+1, "筆資料最佳化結果:", ans[i])
    #print("年紀:", x_temp[i][1])
    print(y_temp[i])
    
    


#for doc in docs:
    
    doc = {
  'H_score': ans[i]
}
    
    # 語法
    # collection_ref = db.collection("集合路徑")
    
    
    doc_ref = db.collection("Test").document(id)
    
    doc_ref.update(doc)
    


        

 

