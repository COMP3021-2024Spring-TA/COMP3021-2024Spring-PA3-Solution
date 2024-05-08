def reuse_bad():
    a = open()
    a.close()
    a = open()
    a = open()
    a.close()

def reuse_good():
    a = open()
    a.close()
    a = open()
    a.close()
    a.open()
    a.close()

