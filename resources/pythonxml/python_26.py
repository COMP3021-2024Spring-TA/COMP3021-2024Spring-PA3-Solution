def func1():
    func3()
    func7()


def func2():
    func1()
    func6()
    func3()


def func3():
    func2()
    func4()
    func5()
    
    
def func4():
    print('func4')

def func5():
    print('func5')


def func6():
    print('func6')

def func7():
    print('func7')

def func8():
    print('func8')