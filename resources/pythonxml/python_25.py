def func1(param1, param2):
    param1 = 10
    print(param1)


def func2(param1, param2):
    param1 = param2
    print(param1)

def func3(param1, param2):
    param1 = param1 or "default"

def func4(param1):
    if param1:
        param1 = True 

def func5(param1):
    param1 = max(param1, 0)

def func6(param4, param6):
    print(param4)
    param4 = max(param6 == 1, 0)
    
def func7(param3, param5, param4):
    param3 = max(param5, 0)
    