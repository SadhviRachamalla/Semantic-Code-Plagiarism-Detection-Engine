def get_fib(val):
    """
    Calculates the Nth value in the Fibonacci series.
    """
    if val <= 0:
        return 0
    elif val == 1:
        return 1
    
    first = 0
    second = 1
    for index in range(2, val + 1):
        tmp = first + second
        first = second
        second = tmp
    return second
