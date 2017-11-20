import xlsxwriter
from enum import Enum
from decimal import Decimal

bigDividerString = "-----------------------------------"
smallDividerString = "------------------------"
BICStartText = "BIC"
EPICStartText = "EPIC"
differenceString = "difference in solutions:"
valueString = "Optiimal Value:" #typo in original generation oops

class resultSection(Enum):
    ratio = 1
    problemDesc = 2
    BIC = 3
    EPIC = 4


class RowObj(object):

    def __init__(self):
        self.rowCounter = 1
        self.emptyData()

    def emptyData(self):
        self.ratio = -1
        self.zeroRow = "No"
        self.problemDescription = []#list of strings, will be joined when entered into the excel spreadsheet
        self.EPICSolutionVal = -1
        self.EPICSolution = ["EPIC SOLUTION\n"] #list of strings, will be joined when entered into the excel spreadsheet
        self.BICSolutionVal = -1
        self.BICSolution = ["BIC SOLUTION\n"]#list of strings, will be joined when entered into the excel spreadsheet


    def writeToExcelFile(self,worksheet):
        problemDescText = ''.join(self.problemDescription)
        BICDescText = ''.join(self.BICSolution)
        EPICDescText = ''.join(self.EPICSolution)

        worksheet.write_number(self.rowCounter, 0, self.ratio)
        worksheet.write_string(self.rowCounter, 1, self.zeroRow)
        worksheet.write_string(self.rowCounter, 2, problemDescText)
        worksheet.write_number(self.rowCounter, 3, self.BICSolutionVal)
        worksheet.write_string(self.rowCounter, 4, BICDescText)
        worksheet.write_number(self.rowCounter, 5, self.EPICSolutionVal)
        worksheet.write_string(self.rowCounter, 6, EPICDescText)

        self.rowCounter += 1


# read result by result
    # parse ratio - as double
    # determine if there is a distribution row of all zeros - 0 or 1?
    # record problem description in full as string
    # record EPIC solution value as double
    # write full EPIC solution as string
    # record BIC solution value as double
    # record full BIC solution as string

workbook = xlsxwriter.Workbook('SimulationOutcomesNoZeroRows.xlsx')
worksheet = workbook.add_worksheet()

worksheet.write_string(0, 0, "Ratio BIC/EPIC")
worksheet.write_string(0, 1, "zero row in distribution?")
worksheet.write_string(0, 2, "Problem Description")
worksheet.write_string(0, 3, "BIC Solution Value")
worksheet.write_string(0, 4, "BIC Solution")
worksheet.write_string(0, 5, "EPIC Solution Value")
worksheet.write_string(0, 6, "EPIC Solution")

with open("SimulationOutcomes"+".txt","r") as rf:
    section = resultSection.ratio
    thisRow = RowObj()
    for line in rf:
        if section == resultSection.ratio:

            if differenceString in line:
                #start of problem description
                section = resultSection.problemDesc
            elif "difference=" in line:
                try:
                    ratioRaw = line.split("difference=",1)[1]
                    thisRow.ratio = float(ratioRaw)
                except IndexError:
                    print("error in line selection")


        elif section == resultSection.problemDesc:

            if "0.0 0.0 0.0 " in line:
                thisRow.zeroRow = "Yes"

            if BICStartText in line:
                #start of BIC
                section = resultSection.BIC

            if "-" not in line:
                thisRow.problemDescription.append(line)

        elif section == resultSection.BIC:

            if valueString in line:
                BICValRaw = line.split(valueString,1)[1]
                thisRow.BICSolutionVal = float(BICValRaw)

            if EPICStartText in line:
                #start of EPIC
                section = resultSection.EPIC

            if "-" not in line:
                thisRow.BICSolution.append(line)

        elif section == resultSection.EPIC:

            if valueString in line:
                EPICValRaw = line.split(valueString,1)[1]
                thisRow.EPICSolutionVal = float(EPICValRaw)

            if bigDividerString in line:
                #end of this problem description
                #write rowobj using xlsx writer
                if thisRow.zeroRow is "No":
                    thisRow.writeToExcelFile(worksheet)
                thisRow.emptyData()
                section = resultSection.ratio

            if "-" not in line:
                thisRow.EPICSolution.append(line)

        else:
            #not in any section?
            print("error not in any section")
            pass


workbook.close()
