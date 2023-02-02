//Names: Amani Arora, Hank Wai
//File Description: Instruction.java handles the instructions

import java.lang.reflect.Array;
import java.util.*;

public class Instruction{

    // Class attribute declaractions
    private char instType;

    // *** R - format
    // op = 6 bits, rs = 5 bits, rt = 5 bits
    // rd = 5 bits, shamt = 5 bits, funct = 6 bits
    public String op;
    public String rs, rt, rd, shamt, funct;   // Arithmetic instructions

    // *** I - format
    public String addrImm;        // Transfer, Branch, Immediate format

    // *** J - format
    public String tarAddr;       // Jump instruction only

    private class InstructionNode{
        String operand;
        String binary;
        String shammt;
        String func;

        public InstructionNode(String operand, String binary, String shammt, String func){
            this.operand = operand;
            this.binary = binary;
            this.shammt = shammt;
            this.func = func;
        }

    }

    public class RegisterNode{
        String register;
        int num;
        String binary;
        int curValue;

        public RegisterNode(String register, int num, String binary, int curValue){
            this.register = register;
            this.num = num;
            this.binary = binary;
            this.curValue = curValue;
        }

    }

    public ArrayList<InstructionNode> instructionInfo = new ArrayList<>();
    public ArrayList<RegisterNode> registerInfo = new ArrayList<>();

    // Constructors Methods
    // R - format
    public Instruction(String op, String rs, String rt, String rd, String shamt, String funct){
        this.instType = 'R';
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
        this.shamt = shamt;
        this.funct = funct;
    }

    // I - format
    public Instruction(String op, String rs, String rt, String addrImm){
        this.instType = 'I';
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.addrImm = addrImm;
    }

    // J - format
    public Instruction(String op, String tarAddr){
        this.instType = 'J';
        this.op = op;
        this.tarAddr = tarAddr;
    }

    public void initialiseInstructionInfo(){
        instructionInfo.add( new InstructionNode("and", "000000", "00000","100100"));
        instructionInfo.add( new InstructionNode("or", "000000", "00000","100101"));
        instructionInfo.add( new InstructionNode("add", "000000", "00000","100000"));
        instructionInfo.add( new InstructionNode("sll", "000000", "00000","000000"));
        instructionInfo.add( new InstructionNode("sub", "000000", "00000","100010"));
        instructionInfo.add( new InstructionNode("slt", "000000", "00000","101010"));
        instructionInfo.add( new InstructionNode("jr", "000000", "00000","001000"));
        instructionInfo.add( new InstructionNode("beq", "000100", "00000","000000"));
        instructionInfo.add( new InstructionNode("bne", "000101", "00000","000000"));
        instructionInfo.add( new InstructionNode("addi", "001000", "00000","000000"));
        instructionInfo.add( new InstructionNode("lw", "100011", "00000","000000"));
        instructionInfo.add( new InstructionNode("sw", "101011", "00000","000000"));
        instructionInfo.add( new InstructionNode("j ", "000010", "00000","000000"));
        instructionInfo.add( new InstructionNode("jal ", "000011", "00000","000000"));
    }

    public void initialiseRegisterInfo(){
        registerInfo.add(new RegisterNode("$zero", 0, "00000", 0));
        registerInfo.add(new RegisterNode("$0", 0, "00000", 0));
        registerInfo.add(new RegisterNode("$v0", 2, "00010", 0));
        registerInfo.add(new RegisterNode("$v1", 3, "00011", 0));
        registerInfo.add(new RegisterNode("$a0", 4, "00100", 0));
        registerInfo.add(new RegisterNode("$a1", 5, "00101", 0));
        registerInfo.add(new RegisterNode("$a2", 6, "00110", 0));
        registerInfo.add(new RegisterNode("$a3", 7, "00111", 0));
        registerInfo.add(new RegisterNode("$t0", 8, "01000", 0));
        registerInfo.add(new RegisterNode("$t1", 9, "01001", 0));
        registerInfo.add(new RegisterNode("$t2", 10, "01010", 0));
        registerInfo.add(new RegisterNode("$t3", 11, "01011", 0));
        registerInfo.add(new RegisterNode("$t4", 12, "01100", 0));
        registerInfo.add(new RegisterNode("$t5", 13, "01101", 0));
        registerInfo.add(new RegisterNode("$t6", 14, "01110", 0));
        registerInfo.add(new RegisterNode("$t7", 15, "01111", 0));
        registerInfo.add(new RegisterNode("$s0", 16, "10000", 0));
        registerInfo.add(new RegisterNode("$s1", 17, "10001", 0));
        registerInfo.add(new RegisterNode("$s2", 18, "10010", 0));
        registerInfo.add(new RegisterNode("$s3", 19, "10011", 0));
        registerInfo.add(new RegisterNode("$s4", 20, "10100", 0));
        registerInfo.add(new RegisterNode("$s5", 21, "10101", 0));
        registerInfo.add(new RegisterNode("$s6", 22, "10110", 0));
        registerInfo.add(new RegisterNode("$s7", 23, "10111", 0));
        registerInfo.add(new RegisterNode("$t8", 24, "11000", 0));
        registerInfo.add(new RegisterNode("$t9", 25, "11001", 0));
        registerInfo.add(new RegisterNode("$k0", 26, "11010", 0));
        registerInfo.add(new RegisterNode("$k1", 27, "11011", 0));
        registerInfo.add(new RegisterNode("$ra", 31, "11111", 0));
        registerInfo.add(new RegisterNode("$sp", 29, "11101", 0));
    }

    public Instruction readInstruction(ArrayList<String> registers, HashMap<String, Integer> labelMap, int curAddr){
        initialiseInstructionInfo();
        initialiseRegisterInfo();
        if (this.op.equals("jr")){
            this.analyseJRType();
        }
        else if (this.op.equals("lw") || this.op.equals("sw")){
            this.analyseLWSWType(registers);
        }
        else if (this.instType == 'R'){
            this.analyseRType(registers);
        }
        else if (this.instType == 'I'){
            this.analyseIType(registers, labelMap, curAddr);
        }
        else{
            this.analyseJType(registers, labelMap);
        }
        registerInfo.clear();
        instructionInfo.clear();
        return this;
    }

    public void analyseJType(ArrayList<String> registers, HashMap<String, Integer> labelMap){
        for (InstructionNode m : instructionInfo){
            if (m.operand.contains(this.op)){
                this.op = m.binary;
                break;
            }
        }
        for (Map.Entry<String, Integer> entry : labelMap.entrySet()){
            String key = entry.getKey();
            if (key.equals(registers.get(0))){
                Integer value = entry.getValue();
                this.tarAddr = decimalToBinary(value,26);
                break;
            }
        }
    }

    public void analyseIType(ArrayList<String> registers, HashMap<String, Integer> labelMap, int curAddr){
            for (InstructionNode m : instructionInfo){ //Goes through each possible instruction to find which instruction it is
                if (m.operand.equals(this.op)){
                    this.op = m.binary; //Assigns the binary
                    break;
                }
            }
            for (RegisterNode a : registerInfo){
                if (registers.get(0).equals(a.register)){
                    this.rt = a.binary;
                    break;
                }
            }
            for (RegisterNode a : registerInfo){
                if (registers.get(1).equals(a.register)){
                    this.rs = a.binary;
                    break;
                }
            }
            //If operand == bne or beq this IF-statement is entered
            //Otherwise it goes through the ELSE portion
            //000100 is the binary for beq while 000101 is the binary for bne
            if (this.op.equals("000100") || this.op.equals("000101")){
                //this for loop searches through all the entries of a hashmap
                for (Map.Entry<String, Integer> entry : labelMap.entrySet()){
                    String key = entry.getKey();
                    if (key.equals(registers.get(2))){
                        Integer target = entry.getValue();
                        int offset = target - curAddr;
                        this.addrImm = decimalToBinary(offset,16);
                        break;
                    }
                }

            }
            else{
                int temp = Integer.parseInt(registers.get(2));
                this.addrImm = decimalToBinary(temp, 16);
            }
    }

    public void analyseJRType(){
        InstructionNode temp = instructionInfo.get(6);
        this.op = temp.binary;
        this.rs = "00000";
        this.rt = "11111";
        this.rd = "00000";
        this.shamt = temp.shammt;
        this.funct = temp.func;
    }

    public void analyseLWSWType(ArrayList<String> registers){
        for (InstructionNode m : instructionInfo){ //Goes through each possible instruction to find which instruction it is
            if (m.operand.equals(this.op)){
                this.op = m.binary; //Assigns the binary operand
                break;
            }
        }
        for (RegisterNode a : registerInfo){ //For loop to search for first register from registerInfo arraylist
            if (registers.get(0).equals(a.register)){
                this.rt = a.binary;
                break;
            }
        }
        for (RegisterNode a : registerInfo){ //For loop to search for first register from registerInfo arraylist
            if (registers.get(1).equals(a.register)){
                this.rs = a.binary;
                break;
            }
        }
        int temp = Integer.parseInt(registers.get(2));
        this.addrImm = decimalToBinary(temp,16);
    }

    public void analyseRType(ArrayList<String> registers){
        if(this.op.equals("sll")){
            for (InstructionNode m : instructionInfo){ //Goes through each possible instruction to find which instruction it is
                if (m.operand.equals(this.op)){
                    this.op = m.binary; //Assigns the binary operand
                    this.funct = m.func; //Assigns the func
                    break;
                }
            }
            this.rt = "00000";
            for (RegisterNode a : registerInfo){ //For loop to search for second register from registerInfo arrayList
                if (a.register.equals(registers.get(0))){
                    this.rs = a.binary;
                    break;
                }
            }
            for (RegisterNode a : registerInfo){ //For loop to search for third register from registerInfo arrayList
                if (a.register.equals(registers.get(1))){
                    this.rd = a.binary;
                    break;
                }
            }
            int temp = Integer.parseInt(registers.get(2));
            this.shamt = decimalToBinary(temp,5);

        }
        else{
            for (InstructionNode m : instructionInfo){ //Goes through each possible instruction to find which instruction it is
                if (m.operand.equals(this.op)){
                    this.op = m.binary; //Assigns the binary operand
                    this.shamt = m.shammt; //Assigns the shamt
                    this.funct = m.func; //Assigns the func
                    break;
                }
            }
            for (RegisterNode a : registerInfo){ //For loop to search for first register from registerInfo arraylist
                if (a.register.equals(registers.get(0))){
                    this.rs = a.binary;
                    break;
                }
            }
            for (RegisterNode a : registerInfo){ //For loop to search for second register from registerInfo arrayList
                if (a.register.equals(registers.get(1))){
                    this.rt = a.binary;
                    break;
                }
            }
            for (RegisterNode a : registerInfo){ //For loop to search for third register from registerInfo arrayList
                if (a.register.equals(registers.get(2))){
                    this.rd = a.binary;
                    break;
                }
            }
        }
    }

    public String toString(){
        //the first if statement asks if the operand is == to "jr"
        //000000 is the binary for "jr"

        if (this.instType == 'R'){
            if (this.funct.equals("001000")){
                return JRTypeString();
            }
            else{
                return RTypeString();
            }
        }
        else if (this.instType == 'I'){
            return ITypeString();
        }
        else{
            return JTypeString();
        }
    }

    public String JRTypeString(){

        String result = "";
        result = result.concat(this.op + " ");              // this.op for jr is '000000'
        result = result.concat(this.rt + " ");              // if $ra this.rs should be '11111'
        result = result.concat("000000000000000" + " ");  // 17 zeroes
        result = result.concat(this.funct);                 // this.funct for jr is '001000'
        //result = result.concat("\n");
        return result;
    }

    public String JTypeString(){
        String result = "";
        result = result.concat(this.op + " ");
        result = result.concat(this.tarAddr + "");
        //result = result.concat("\n");
        return result;
    }

    public String RTypeString(){
        String result = "";
        result = result.concat(this.op + " ");
        result = result.concat(this.rt + " ");
        result = result.concat(this.rd + " ");
        result = result.concat(this.rs + " ");
        result = result.concat(this.shamt + " ");
        result = result.concat(this.funct);
        //result = result.concat("\n");
        return result;
    }

    public String ITypeString(){
        String result = "";
        result = result.concat(this.op + " ");
        result = result.concat(this.rs + " ");
        result = result.concat(this.rt + " ");
        result = result.concat(this.addrImm + "");
        //result = result.concat("\n");
        return result;
    }

    public static String decimalToBinary(int num, int digitsRequired){
        boolean negative = false;
        if (num < 0){
            num = num * -1;
            negative = true;
        }
        ArrayList<String> binary = new ArrayList<>();
        for (int i = 0 ; i < 30 ; i++){
            binary.add("0");
        }
        while (num > 0){
            int exp = 0;
            while (true){
                int temp = (int) Math.pow(2, (exp + 1));
                if (temp > num){
                    break;
                }
                exp += 1;
            }
            binary.set((binary.size() - 1 - exp), "1");
            num = num - ((int) Math.pow(2, exp));
        }
        String str = "";
        for (String a : binary){
            str = str.concat(a);
        }
        if (negative){
            StringBuffer input = new StringBuffer(str);
            str = arithmeticNegation(input);
        }
        str = str.substring(str.length()- digitsRequired);
        return str;
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