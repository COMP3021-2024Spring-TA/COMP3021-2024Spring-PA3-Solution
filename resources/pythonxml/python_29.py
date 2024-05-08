
def shadow_bad():
    a = open()
    b = open()
    c = open()
    a = c
    a.close()
    b.close()
    c.close()


def shadow_good():
    a = open()
    b = open()
    c = open()
    d = a
    d.close()
    d = b
    d.close()
    d = c
    d.close()