# assumes list of numbers where each number d satisfies 0<=d<=1
def calcLastDistValue(numList,denominator):
    
    listSum = sum(numList)
    if listSum > denominator:
        raise Exception("invalid num list")
    lastVal = denominator - listSum

    return lastVal

# allows for duplicates values in different conigurations
"""
def DistGenerator2x2(denominator):

    if type(denominator) is not int:
        raise Exception("invalid denominator argument") 

    with open("2by2Distribution_denominator"+str(denominator)+".txt","w") as f:
        f.write("{")
        for i in range(denominator+1):
            for j in range(denominator+1):
                for k in range(denominator+1):
                    if (i+j+k) <= denominator:
                        last = calcLastDistValue([i,j,k],denominator)
                        x = (i/denominator)
                        y = (j/denominator)
                        z = (k/denominator)
                        lastD = last/denominator
                        print([x,y,z,lastD])
                        f.write("{"+str(x)+","+str(y)+","+str(z)+","+str(lastD)+"};")
        f.write("}") 

    print("finished with dist generation")
"""


def create3x3(incAmount):

    with open("3x3dist"+".txt","w") as f:
        rowTotals = threeValueManager(1,incAmount)
        while rowTotals.hasNext:
            rowTList = rowTotals.getListAndUpdate()
            row1 = threeValueManager(rowTList[0],incAmount)
            while row1.hasNext:
                row1vals = row1.getListAndUpdate()
                row2 = threeValueManager(rowTList[1],incAmount)
                while row2.hasNext:
                    row2vals = row2.getListAndUpdate()
                    row3 = threeValueManager(rowTList[2],incAmount)
                    while row3.hasNext:
                        row3vals = row3.getListAndUpdate()

                        def strList(x):
                            y = map(str,x)
                            return ','.join(y)

                        f.write(strList(row1vals)+";"+strList(row2vals)+";"+strList(row3vals)+"\n")

    print("finished with dist generation")


class threeValueManager():
    """docstring for threeValueManager"""
    def __init__(self, max_cap,incAmount):
        self.hasNext = True
        self.maxCap = max_cap
        self.midCap = max_cap/2
        self.incAmount = incAmount
        self.list = [0,self.midCap,self.maxCap-self.midCap]

    def getNext(self):
        oldList = self.list
        newList = self.list
        incAmount = self.incAmount

        if(oldList[1] >= incAmount):
            newList[1] = oldList[1] - incAmount
        else:
            if(oldList[0] <= self.midCap - incAmount):
                newList[0] = oldList[0] + incAmount
                newList[1] = self.midCap - newList[0]
            else:
                self.hasNext = False

        newList[2] = self.maxCap - newList[0] - newList[1]

        self.list = newList

    def getListAndUpdate(self):
        oldList = self.list
        self.getNext()
        return oldList


#could be extended to be done from command line, but anyone working with this should be able to modify the code, very simple
def main():
    create3x3(.1)

if __name__ == "__main__":
    main()
