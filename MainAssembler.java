//Names: Amani Arora, Hank Wai
//File Description: This file acts as the "main" for the entire program

import java.io.*;
import java.util.*;

public class MainAssembler{

    public static ArrayList<String> execInstructions = new ArrayList<>();
    public static ArrayList<RegisterNode> registerInfo = new ArrayList<>();

    static int[] MemArray = new int[8192];            // int array of size 8192 for Memory Segment (Must Have)
    static int pc = 0;                                // program counter
    static String[] arguments;                        // String array for arguments, if any
    static String promptInput;                        // String for user prompt input
    static String[] scriptArray;                      // String Array for each line in Script

    public static class RegisterNode{
        String register;
        String binary;
        int num;
        int curValue;

        public RegisterNode(String register, int num, String binary, int curValue){
            this.register = register;
            this.binary = binary;
            this.num = num;
            this.curValue = curValue;
        }

    }

    public static void initialiseRegisters(){
        registerInfo.add(new RegisterNode("$zero", 0, "00000", 0));
        registerInfo.add(new RegisterNode("$0", 1, "00000", 0));
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
        registerInfo.add(new RegisterNode("$sp", 26, "11101", 0));
        registerInfo.add(new RegisterNode("$ra", 27, "11111", 0));
    }

    public static void main(String[] args) throws Exception {

        initialiseRegisters();

        execInstructions = Compiler.compile(args);
        //execInstructions now holds all executable instructions


        int scriptStart = 0;
        int scriptEnd = 0;
        //String arguments[];                         // String array for arguments, if any
        boolean mulArg = false;                       // flag for multiple arguments
        boolean scriptFlag = false;                   // flag for script mode, default to interactive

        // if script file is used, set scriptFlag and read script into string array
        if (args.length > 1){                           //BUG EXSISTS HERE
            // extract filename and lines
            List<String> scriptStrings = new ArrayList<>();
            BufferedReader bf = new BufferedReader(new FileReader(args[1]));
            String line = bf.readLine();
            while(line != null){
                scriptStrings.add(line);
                line = bf.readLine();
            }
            bf.close();
            scriptArray = scriptStrings.toArray(new String[0]);
            scriptFlag = true;
        }

        Scanner in = new Scanner(System.in);
        if (scriptFlag){
            scriptEnd = scriptArray.length;
        }

        while(1 == 1){

            System.out.print("mips>");
            if (scriptFlag){
                if (scriptStart <= scriptEnd){
                    in.close();
                    promptInput = scriptArray[scriptStart];
                    promptInput = promptInput.trim();
                    System.out.print(" " + promptInput + "\n");
                    scriptStart++;
                }
                else{
                    scriptFlag = false;
                }
            }
            else{
                System.out.print(" ");
                promptInput = in.nextLine().trim();           // no trailing white space :D
            }

            // if more than one argument
            if (promptInput.length() > 1){
                arguments = promptInput.split(" ");
                mulArg ^= true;
            }
            promptInput = promptInput.substring(0,1);

            // logic: action based on 1st char of user input
            switchCase(promptInput,mulArg);
            mulArg = false;                     // not good at static and variable scopes
            //System.out.println(mulArg);
        }
    }

    public static void switchCase(String promptInput, boolean mulArg){
        //System.out.println(mulArg);
        switch(promptInput){
            case "h":                   // show help
                showHelp();
                break;

            case "d":                   // dump register state
                showReg();
                break;

            case "s":                   // single step through the program
                if(mulArg){
                    mulArg ^= true;     // clear flag of multiple arguments
                    int linesToDo = Integer.parseInt(arguments[1]);
                    launchExecute(linesToDo);
                    System.out.format("\t%d instruction(s) executed\n", linesToDo);
                }
                else{
                    launchExecute(1);
                    System.out.format("\t%d instruction(s) executed\n", 1);
                }
                break;

            case "r":                   // run until the program ends
                while(pc != execInstructions.size()){
                    executeLine(execInstructions.get(pc));
                }
                break;

            case "m":                   // display memory address from num1 to num2
                //mulArg ^= true;         // clear flag of multiple arguments
                System.out.println();
                int startCnt = Integer.parseInt(arguments[1]);
                int endCnt = Integer.parseInt(arguments[2]);
                for (int i = startCnt; i <= endCnt; i++){
                    System.out.format("[%d] = %d\n", i, MemArray[i]);
                }
                System.out.println();
                break;

            case "c":                   // clear all registers, memory, and pc
                System.out.println("\tSimulator Reset");
                System.out.println();
                pcReset();
                regReset();
                memReset();
                break;

            case "q":                   // exit the program
                exit();
                break;

            default:
                System.out.println("\nPerhaps Try Again?");
                break;
        }
    }

    public static void showHelp(){
        System.out.println();
        System.out.println("h = show help\n" +
                "d = dump register state\n" +
                "s = single step through the program (i.e. execute 1 instruction and stop)\n" +
                "s num = step through num instructions of the program\n" +
                "r = run until the program ends\n" +
                "m num1 num2 = display data memory from location num1 to num2\n" +
                "c = clear all registers, memory, and the program counter to 0\n" +
                "q = exit the program");
        System.out.println();
    }

    public static void showReg(){
        System.out.println();
        System.out.println("pc = " + pc);
        System.out.printf("%-14s %-14s %-14s %-14s\n", "$0 = " + registerInfo.get(1).curValue
                                                     , "$v0 = " + registerInfo.get(2).curValue
                                                     , "$v1 = " + registerInfo.get(3).curValue
                                                     , "$a0 = " + registerInfo.get(4).curValue);
        System.out.printf("%-14s %-14s %-14s %-14s\n", "$a1 = " + registerInfo.get(5).curValue
                                                     , "$a2 = " + registerInfo.get(6).curValue
                                                     , "$a3 = " + registerInfo.get(7).curValue
                                                     , "$t0 = " + registerInfo.get(8).curValue);
        System.out.printf("%-14s %-14s %-14s %-14s\n", "$t1 = " + registerInfo.get(9).curValue
                                                     , "$t2 = " + registerInfo.get(10).curValue
                                                     , "$t3 = " + registerInfo.get(11).curValue
                                                     , "$t4 = " + registerInfo.get(12).curValue);
        System.out.printf("%-14s %-14s %-14s %-14s\n", "$t5 = " + registerInfo.get(13).curValue
                                                     , "$t6 = " + registerInfo.get(14).curValue
                                                     , "$t7 = " + registerInfo.get(15).curValue
                                                     , "$s0 = " + registerInfo.get(16).curValue);
        System.out.printf("%-14s %-14s %-14s %-14s\n", "$s1 = " + registerInfo.get(17).curValue
                                                     , "$s2 = " + registerInfo.get(18).curValue
                                                     , "$s3 = " + registerInfo.get(19).curValue
                                                     , "$s4 = " + registerInfo.get(20).curValue);
        System.out.printf("%-14s %-14s %-14s %-14s\n", "$s5 = " + registerInfo.get(21).curValue
                                                     , "$s6 = " + registerInfo.get(22).curValue
                                                     , "$s7 = " + registerInfo.get(23).curValue
                                                     , "$t8 = " + registerInfo.get(24).curValue);
        System.out.printf("%-14s %-14s %-14s\n"      , "$t9 = " + registerInfo.get(25).curValue
                                                     , "$sp = " + registerInfo.get(26).curValue
                                                     , "$ra = " + registerInfo.get(27).curValue);
        System.out.println();
    }

    public static void pcReset(){
        pc = 0;
    }

    public static void regReset(){
        for (int i = 0 ; i < registerInfo.size() - 1 ; i++){
            RegisterNode rNode = registerInfo.get(i);
            rNode.curValue = 0;
            registerInfo.set(i,rNode);
        }
    }

    public static void memReset(){
        // all memory reset to 0
        Arrays.fill(MemArray, 0);
    }

    public static void exit(){
        System.exit(0);
    }

    public static void launchExecute(int linesToExecute){
        for (int i = 0 ; i < linesToExecute ; i++){
            executeLine(execInstructions.get(pc));
        }
    }

    public static void executeLine(String instruction){
        pc++;
        String[] strArr = instruction.split(" ");
        String type = categorize(strArr);
        switch(type){
            case "R":
                operateR(strArr);
                break;
            case "I":
                operateI(strArr);
                break;
            case "JR":
                operateJR();
                break;
            case "J":
                operateJ(strArr);
                break;
            default:
                System.out.println("EXECUTE LINE SWITCH CASE DEFAULT");
        }
    }

    public static void operateJR(){
        pc = ALU.jr(registerInfo);
    }

    public static String categorize(String[] str){
        String k;
        switch(str.length){
            case 6:
                k = "R";
                break;
            case 4:
                if (str[0].equals("000000")){
                    k = "JR";
                }
                else{
                    k = "I";
                }
                break;
            case 2:
                k = "J";
                break;
            default:
                k = "Invalid";
                break;
        }
        return k;
    }

    public static void operateR(String[] str){
        String rs = str[1];
        String rt = str[2];
        String rd = str[3];
        String shamt = str[4];
        String funct = str[5];
        int rsVal = 0;
        int rtVal = 0;
        switch(funct){
            case "100000":
                ALU.add(rs, rt, rd, rsVal, rtVal, registerInfo);
                break;
            case "100010":
                ALU.sub(rs, rt, rd, rsVal, rtVal, registerInfo);
                break;
            case "100100":
                ALU.and(rs, rt, rd, rsVal, rtVal, registerInfo);
                break;
            case "100101":
                ALU.or(rs, rt, rd, rsVal, rtVal, registerInfo);
                break;
            case "101010":
                ALU.slt(rs,rt,rd,rsVal,rtVal,registerInfo);
                break;
            case "000000":
                ALU.sll(rt, rd, rtVal, shamt, registerInfo);
                break;
            default:
                System.out.println("GOING TO DEFAULT FOR OPERATE R");
        }
    }

    public static void operateI(String[] str){
        String opcode = str[0];
        String rs = str[1];
        String rt = str[2];
        String immediate = str[3];
        switch(opcode){
            case "001000":
                ALU.addi(rs, rt, immediate, registerInfo);
                break;
            case "000101":
                pc = ALU.bne(rs,rt,immediate,pc, registerInfo);
                break;
            case "000100":
                pc = ALU.beq(rs,rt,immediate,pc,registerInfo);
                break;
            case "100011":
                ALU.lw(rs,rt,immediate,registerInfo,MemArray);
                break;
            case "101011":
                ALU.sw(rs,rt,immediate,registerInfo,MemArray);
                break;
            default:
                System.out.println("ENTERING OPERATE I SWITCH DEFAULT");
        }
    }

    public static void operateJ(String str[]){
        String opcode = str[0];
        String address = str[1];
        switch(opcode){
            case "000010":
                pc = ALU.j(address);
                break;
            case "000011":
                pc = ALU.jal(address,pc,registerInfo);
                break;
            default:
                System.out.println("GOING TO OPERATE J DEFAULT");
        }
    }
}
