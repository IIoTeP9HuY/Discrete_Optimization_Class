#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
from subprocess import Popen, PIPE

def solveIt(n):
    # Writes the inputData to a temporay file

    tmpFileName = 'tmp.data'
    tmpFile = open(tmpFileName, 'w')
    tmpFile.write(str(n))
    tmpFile.close()

    # Runs the command: java Solver -file=tmp.data

    process = Popen([
                        './queensSolver',
                        tmpFileName
                    ],
                    stdout=PIPE)
    (stdout, stderr) = process.communicate()

    # removes the temporay file

    os.remove(tmpFileName)

    return stdout.strip()


# this is a depth first search of all assignments
def tryall(assignment, domains):
    # base-case: if the domains list is empty, all values are assigned
    # check if it is a solution, return None if it is not
    if len(domains) == 0:
        if checkIt(assignment):
            return assignment
        else:
            return None
    
    # recursive-case: try each value in the next domain
    # if we find a solution return it. otherwise, try the next value
    else:
        for v in domains[0]:
            sol = tryall(assignment[:]+[v],domains[1:])
            if sol != None:
                return sol


# checks if an assignment is feasible
def checkIt(sol):
    n = len(sol)
    for i in range(0,n):
        for j in range(i+1,n):
            if sol[i] == sol[j] or \
               sol[i] == sol[j] + (j-i) or \
               sol[i] == sol[j] - (j-i):
                return False
    return True


import sys

if __name__ == "__main__":
    if len(sys.argv) > 1:
        try:
            n = int(sys.argv[1].strip())
        except:
            print sys.argv[1].strip(), 'is not an integer'
        print 'Solving Size:', n
        print(solveIt(n))

    else:
        print('This test requires an instance size.  Please select the size of problem to solve. (i.e. python queensSolver.py 8)')

