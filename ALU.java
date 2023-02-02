//Names: Amani Arora, Hank Wai
//File Description: ALU.java holds all the operands

import java.util.ArrayList;

public class ALU {

    public static void add(String rs, String rt, String rd,
                           int rsVal, int rtVal, ArrayList<lab3.RegisterNode> registerInfo){  //add: R[rd] = R[rs] + R[rt]
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
            }
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
            }
        }
        int rdVal = rsVal + rtVal;                      // perform addition, result stored in rdVal
        int count = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to set rd register to rdVal

            if (rNode.binary.equals(rd)){
                rNode.curValue = rdVal;
                registerInfo.set(count,rNode);
                break;
            }
            count++;
        }
    }

    public static void sub(String rs, String rt, String rd,
                           int rsVal, int rtVal, ArrayList<lab3.RegisterNode> registerInfo){  //add: R[rd] = R[rs] - R[rt]
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
            }
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
            }
        }
        int rdVal = rsVal - rtVal;                      // perform subtraction, result stored in rdVal
        int count = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to set rd register to rdVal
            if (rNode.binary.equals(rd)){
                rNode.curValue = rdVal;
                registerInfo.set(count,rNode);
                break;
            }
            count++;
        }
    }

    public static void and(String rs, String rt, String rd,  //R[rd] = R[rs] & R[rt]
                           int rsVal, int rtVal, ArrayList<lab3.RegisterNode> registerInfo){
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
            }
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
            }
        }
        int rdVal = rsVal & rtVal;
        int count = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to set rd register to rdVal
            if (rNode.binary.equals(rd)){
                rNode.curValue = rdVal;
                registerInfo.set(count,rNode);
                break;
            }
            count++;
        }
    }

    public static void or(String rs, String rt, String rd,  //R[rd] = R[rs] | R[rt]
                          int rsVal, int rtVal, ArrayList<lab3.RegisterNode> registerInfo){
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
            }
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
            }
        }
        int rdVal = rsVal | rtVal;
        int count = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to set rd register to rdVal
            if (rNode.binary.equals(rd)){
                rNode.curValue = rdVal;
                registerInfo.set(count,rNode);
                break;
            }
            count++;
        }
    }

    public static void slt(String rs, String rt, String rd,     //R[rd] = (R[rs] < R[rt]) ? 1 : 0
                           int rsVal, int rtVal,
                           ArrayList<lab3.RegisterNode> registerInfo){
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
            }
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
            }
        }
        int rdVal;
        if (rsVal < rtVal){
            rdVal = 1;
        }
        else{
            rdVal = 0;
        }
        int count = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to set rd register to rdVal
            if (rNode.binary.equals(rd)){
                rNode.curValue = rdVal;
                registerInfo.set(count,rNode);
                break;
            }
            count++;
        }
    }

    public static void sll(String rt, String rd,  //R[rd] = R[rt] << shamt
                           int rtVal, String shamt, ArrayList<lab3.RegisterNode> registerInfo){
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
                break;
            }
        }
        int shift = binaryToDecimal(shamt);
        int rdVal = rtVal << shift;                      // perform shift, result stored in rdVal
        int count = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to set rd register to rdVal
            if (rNode.binary.equals(rd)){
                rNode.curValue = rdVal;
                registerInfo.set(count,rNode);
                break;
            }
            count++;
        }
    }

    public static void addi(String rs, String rt, String imm,           //addi: R[rt] = R[rs] + SignExtImm
                            ArrayList<lab3.RegisterNode> registerInfo){
        int rsVal = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
                break;
            }
        }
        int rtVal = rsVal + binaryToDecimal(imm);                      // perform addition, result stored in rdVal
        int count = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to set rd register to rdVal
            if (rNode.binary.equals(rt)){
                rNode.curValue = rtVal;
                registerInfo.set(count,rNode);
                break;
            }
            count++;
        }
    }

    public static int bne(String rs, String rt, String offset, int pc,
                          ArrayList<lab3.RegisterNode> registerInfo){
        int rsVal = 0, rtVal = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
            }
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
            }
        }
        if (rsVal != rtVal){
            pc = pc + binaryToDecimal(offset);
        }
        return pc;
    }

    public static int beq(String rs, String rt, String offset, int pc,
                          ArrayList<lab3.RegisterNode> registerInfo){
        int rsVal = 0, rtVal = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
            }
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
            }
        }
        if (rsVal == rtVal){
            pc = pc + binaryToDecimal(offset);
        }
        return pc;
    }

    public static void lw(String rs, String rt, String offset,     //R[rt] = M[R[rs] + immediate]
                         ArrayList<lab3.RegisterNode> registerInfo,
                         int[] memory){
        int rsVal = 0, rtVal;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
                break;
            }
        }
        int index = rsVal + binaryToDecimal(offset);
        rtVal = memory[index];
        int count = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to set rd register to rdVal
            if (rNode.binary.equals(rt)){
                rNode.curValue = rtVal;
                registerInfo.set(count,rNode);
                break;
            }
            count++;
        }
    }

    public static void sw(String rs, String rt, String offset,     // M[R[rs] + immediate] = R[rt]
                          ArrayList<lab3.RegisterNode> registerInfo,
                          int[] memory){
        int rsVal = 0, rtVal = 0;
        for (lab3.RegisterNode rNode : registerInfo){    // parse register arraylist to extract rs, rt values
            if (rNode.binary.equals(rt)){
                rtVal = rNode.curValue;
            }
            if (rNode.binary.equals(rs)){
                rsVal = rNode.curValue;
            }
        }
        int index = rsVal + binaryToDecimal(offset);
        memory[index] = rtVal;
    }

    public static int j(String address){
        return binaryToDecimal(address);
    }

    public static int jal(String address, int pc,
                          ArrayList<lab3.RegisterNode> registerInfo){          //R[31]=PC+4;PC=JumpAddr
        lab3.RegisterNode rNode = registerInfo.get(registerInfo.size() - 1);
        rNode.curValue = pc;
        registerInfo.set(registerInfo.size() - 1,rNode);
        pc = binaryToDecimal(address);
        return pc;
    }

    public static int jr(ArrayList<lab3.RegisterNode> registerInfo){
        lab3.RegisterNode rNode = registerInfo.get(registerInfo.size() - 1);
        return (rNode.curValue);
    }

    public static int binaryToDecimal(String binary){
        boolean negative = false;
        if(binary.charAt(0) == '1'){
            StringBuffer input = new StringBuffer(binary);
            binary = arithmeticNegation(input);
            negative = true;
        }
        int num = 0;
        int value;
        for (int i = binary.length() - 1 ; i > -1 ; i--){
            value = Integer.parseInt(binary.charAt(binary.length() - i - 1) + "");

            num += value * Math.pow(2,i);
        }
        if (negative){
            num = num * -1;
        }
        return num;
    }

    public static String arithmeticNegation(StringBuffer num)
    {
        int len = num.length();
        int i;
        for (i = len-1 ; i >= 0 ; i--){
            if (num.charAt(i) == '1'){
                break;
            }
        }
        if (i == -1){
            return "1" + num;
        }
        int temp = i - 1;
        while(temp > -1){
            if (num.charAt(temp) == '1') {
                num.replace(temp, temp + 1, "0");
            }
            else{
                num.replace(temp, temp + 1, "1");
            }
            temp--;
        }
        return num.toString();
    }
}
