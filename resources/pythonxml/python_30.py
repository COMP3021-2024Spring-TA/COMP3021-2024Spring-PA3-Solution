def swap_bad():
    a = open()
    b = open()
    a = b
    b = a
    a.close()
    b.close()


def swap_good():
    a = open()
    b = open()
    c = a
    a = b
    b = c
    a.close()
    b.close()