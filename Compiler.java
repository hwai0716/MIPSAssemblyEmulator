//Names: Amani Arora, Hank Wai
//File Description: This file compiles the instructions to machine code


import java.io.*;
import java.util.Scanner;
import java.util.*;
import java.io.IOException;


public class Compiler {
    //global variable for address


    public static ArrayList<String> finalInstructions = new ArrayList<>();

    public static ArrayList<String> invalidInstruct = new ArrayList<>();
    static List<Integer> evaluateList = new ArrayList<Integer>();     // List to store line type
    static HashMap<String, Integer> labelMap = new HashMap<>();       // Stores label and address as key,value pair
    private static int addrCnt = 0; // Address Counter offset count
    public static int curAddr = 0;

    static ArrayList<String> instructionList = new ArrayList<>(); //Stores all valid instructions


    public static ArrayList<String> compile(String[] args) throws Exception {

        initialiseINode();

        InputStream inputStream = new FileInputStream(args[0]); //CHANGE THIS INPUT TO ARGS[0]
        Scanner sc = new Scanner(inputStream);
        while (sc.hasNext()) {
            firstPassLineProcessing(sc);
        }
        sc.close();

        InputStream inputStream2 = new FileInputStream(args[0]); //CHANGE THIS INPUT TO ARGS[0]
        Scanner sc2 = new Scanner(inputStream2);
        int ind = 0;
        while (sc2.hasNext()) {
            secondPassLineProcessing(sc2, ind);
            ind++;
        }

        sc2.close();
        return finalInstructions;

    }


    public static void initialiseINode(){
        instructionList.add("and"); //R //0
        instructionList.add("or"); //R  //1
        instructionList.add("addi"); //I //2
        instructionList.add("jal "); //J //3
        instructionList.add("jr"); //R //4
        instructionList.add("j "); //J //5
        instructionList.add("add"); //R //6
        instructionList.add("sll"); //R //7
        instructionList.add("sub"); //R //8
        instructionList.add("slt"); //R //9
        instructionList.add("beq"); //I //10
        instructionList.add("bne"); //I //11
        instructionList.add("lw"); //I //12
        instructionList.add("sw"); //I //13
    }

    public static void secondPassLineProcessing(Scanner sc2, int ind){
        String currentLine = sc2.nextLine();
        int lineType = evaluateList.get(ind);

        if (lineType == 2 || lineType == 3){
            curAddr++;
            currentLine = clearWhiteSpaces(currentLine);
            String operand = isolateOperand(currentLine);
            ArrayList<String> registers = new ArrayList<>();
            registers = isolateRegisters(currentLine, operand, registers);
            int valid = checkInstructionValidity(operand);
            if (valid != -1){
                Instruction in = createInstruction(valid, operand);
                in = in.readInstruction(registers, labelMap, curAddr);
                finalInstructions.add(in.toString());
            }
        }
    }

    public static int checkInstructionValidity(String operand){
        int save = -1;
        for (int i = 0 ; i < 14 ; i ++){
            if (operand.equals(instructionList.get(i))){
                save = i;
                break;
            }
        }
        return save;
    }

    public static String isolateOperand(String line){
        line = line.substring(line.lastIndexOf(":") + 1);
        // this clause catches j and jal instructions
        if (line.contains(" ")){
            String[] temp = line.split(" ");
            String temp2 = temp[0] + " ";
            return temp2;
        }
        // this clause generaters all other instructions
        String operand = "";
        for (int i = 0 ; i < line.length() ; i++){
            String c = line.charAt(i) + "";
            if (!c.equals("$")){
                operand = operand.concat(c);
            }
            else{
                break;
            }
        }
        return operand;
    }

    public static ArrayList<String> isolateRegisters(String line, String operand, ArrayList<String> registers){
        // Remove labels, if any
        line = line.substring(line.lastIndexOf(":") + 1);
        // Remove operand with nothing
        line = line.replace(operand, "");


        // Check operand, perform actions to read register/addr/immd info into ArrayList<String>
        switch(operand){

            case "j ": case "jal ": case "jr":

                registers.add(line);
                break;

            case "lw": case "sw":

                String[] temp = line.split(",", 2);
                registers.add(temp[0]);
                temp = temp[1].split("\\(", 2);
                temp[1] = temp[1].substring(0, temp[1].length() - 1);
                registers.add(temp[1]);
                registers.add(temp[0]);
                break;

            default:

                String[] temp3 = line.split(",", 3);
                registers.add(temp3[0]);
                registers.add(temp3[1]);
                registers.add(temp3[2]);
                break;
        }

        for (int i = 0 ; i < registers.size() ; i++){
            if (registers.get(i).equals("$zero")){
                registers.set(i, "$0");
            }
        }

        // return ArrayList<String>
        return registers;
    }

    public static Instruction createInstruction(int index, String operand){
        Instruction instruc;
        if (index == 0 || index == 1 || index == 4 || index == 6 || index == 7 || index == 8 || index == 9 ){
            //Builds instruction using R-Type constructor
            instruc = new Instruction(operand, "0","0","0","0","0");
            return instruc;
        }
        else if (index == 2 || index > 9){
            //Means that the instruction is a I-Type
            //Builds instruction using I-Type constructor
            instruc = new Instruction(operand, "0","0","0");
            return instruc;
        }
        else{
            //Means that the instruction is a J-Type
            //Builds instruction using J-Type constructor
            instruc = new Instruction(operand, "0");
            return instruc;
        }
    }
    //Adds to hash map
    //Creates arraylist of line types
    public static void firstPassLineProcessing(Scanner sc) {
        String currentLine = sc.nextLine();
        int k = evaluateLine(currentLine);

        switch (k) {
            // whitespaces/tabs/comments ignored - no effect on counter
            case 0:
                break;

            // label only - put label in hashmap, do not increment counter
            case 1:
                String[] temp = currentLine.split(":");
                labelMap.put(temp[0], addrCnt);
                break;

            // instruction only - increment addrCnt
            case 2:
                addrCnt++;
                break;

            // label + instructions - put label in hashmap and increment addrCnt
            case 3:
                String[] temp1 = currentLine.split(":");
                labelMap.put(temp1[0], addrCnt);
                addrCnt++;
        }
        evaluateList.add(k);
    }
    //Identifies line type
    public static int evaluateLine(String line){
        line = line.trim();
        String[] parsed = line.split("#", 2);
        line = parsed[0];
        if (line.contains(":")){
            int index = line.indexOf(':');
            try{
                char temp = line.charAt(index + 1);
                return 3;
            }
            catch(Exception StringIndexOutOfBoundsException){
                return 1;
            }
        }
        for (int i = 0 ; i < line.length() ; i++){
            char c = line.charAt(i);
            if (c != '\n' && c != ' ' && c != '\t'){
                return 2;
            }
        }
        return 0;
    }

    public static String clearWhiteSpaces(String parsee){
        parsee = parsee.split("#")[0];
        String str = "";
        int skip = 0;
        for (int i = 0 ; i < parsee.length() ; i++){
            if (skip > 0){
                skip -= 1;
                continue;
            }
            char c = parsee.charAt(i);
            if (c != ' ' && c != 'j' && c != '\t' && c != '\n'){
                String t = "" + c;
                str = str.concat(t);
            }
            else if (c == 'j'){
                char temp = parsee.charAt(i+1);
                if (temp == ' '){
                    str = str.concat("j ");
                    skip = 1;
                }
                else if (temp == 'r'){
                    str = str.concat("jr");
                    skip = 2;
                }
                else if (temp == 'a'){
                    char temp2 = parsee.charAt(i+2);
                    if (temp2 == 'l'){
                        char temp3 = parsee.charAt(i + 3);
                        if (temp3 == ' '){
                            str = str.concat("jal ");
                            skip = 3;
                        }
                    }
                }
            }
        }
        return str;
    }

}

