def chain_bad():
    a = open()
    b = open()
    c = a
    d = b
    e = c
    f = d
    g = e
    g = f
    g.close()

def chain_good():
    a = open()
    b = open()
    c = a
    d = b
    e = c
    f = d
    g = e
    f.close()
    g.close()
