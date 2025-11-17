//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
// Code for Practicum 16 (Short-Circuit Evaluation)
// (TO UPDATE)
import javax.swing.text.Position;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.Scanner;

class LexicalAnalyzer {
    public static int x = 1;
    public static int y = 1;


    public static int nextChar;
    public static BufferedReader reader;

    public static String valData;
    public static String[] misc = {";", ":", "*", "+", "/", "(", ")","<", ">", "=", "-", ","};
    public static String[] conditionals = {"else", "bool", "or", "if", "end", "while", "do", "end"};
    public static String [] keyWords = {"program", "print", "mod", "and", "true", "false", "not"};
    public static String[] relationals = {"=<", ">=", "==", "!=", ":="};

    public static boolean relations = false;
    public static token values = new token();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String fileLocation="";
        while(!fileLocation.equals("quit")){
            System.out.println("Please enter in the location of the file you want to read");
            fileLocation=scanner.nextLine();
            if(fileLocation.equals("quit")){
                break;
            }
            reader = new BufferedReader(new FileReader(fileLocation));
            next(reader);
            System.out.println("POSITION     KIND      Value");
            System.out.printf("| %-10s| %-15s| %-20s%n", Position(), kind(), value());
            while (!kind().equals("END-OF-TEXT")) {
                next(reader);
                System.out.printf("| %-10s| %-15s| %-20s%n", Position(), kind(), value());
            }
            values.setName("");
            values.resetPosition();
            values.resetLine();
        }


    }
    public static void next(BufferedReader reader) throws IOException {
        StringBuilder wordAdd = new StringBuilder();
        boolean tokenEmitted = false;

        while ((nextChar = reader.read()) != -1) {
            reader.mark(1);
            int aheadChar = lookAhead();
            //System.out.println((char) nextChar);


            //Checks if the character is an illegal value (currently not a digit, letter, or included in the arrays above
            //and also checks of aheadChar != -1 to make sure we end it.
            // if it is illegal the char is appended and System Exits while giving positions
            if(aheadChar!=-1 && isIllegal((char) nextChar)){
                //System.out.println("hello");
                if(wordAdd.length()>0){
                    values.setName(wordAdd.toString());
                    values.setLine(values.getLine());
                    break;
                }
                if(relationalTrue((char) nextChar, (char) aheadChar)){
                    wordAdd.append((char) nextChar);
                    wordAdd.append((char) aheadChar);
                    reader.skip(1);
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    tokenEmitted = true;
                    break;
                }
                else{

                    System.out.println("ILLEGAL CHARACTER >>>>>>> "+ (char ) nextChar + " Position "+ Position());
                    System.exit(0);
                    break;
                }
            }
            //checks if the character is a newline
            //if it is and the wordAdd var length is not 0 (or empty) it appends the wordAdd variable to our token object
            //sets the position and line and increments
            //tokenEmitted exists for a possibility that a character is consumed and skipped when this
            //line hits and if its empty it resets X and increments Y

            if ((char) nextChar == '\n') {
                if (wordAdd.length() > 0) {
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    tokenEmitted = true;
                    values.resetPosition();
                    values.incrementY();
                    break;
                } else {
                    values.resetPosition();
                    values.incrementY();
                    continue;
                }
            }
            //checks if nextChar is a tab and if it is it will increment with position tab
            //which increments by 3
            // as said previously if its not empty its adds it to a list and sets the wordAdd var
            if ((char) nextChar == '\t') {
                if (wordAdd.length() > 0) {
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    tokenEmitted = true;
                    values.positionTab();
                    break;
                }
                values.positionTab();
                continue;
            }

            //checks if wordAdd is not empty before reaching end of file to save and append.
            if (aheadChar == -1) {
                if(wordAdd.isEmpty() && !isIllegal((char) nextChar)){
                    wordAdd.append((char) nextChar);
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    tokenEmitted = true;
                    break;
                }
                if (wordAdd.length() > 0) {
                    wordAdd.append((char) nextChar);
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    tokenEmitted = true;
                    break;
                }
                continue;
            }
            //checks if it is a comment and if it is it loops through and increments y and resets x

            if ((char) nextChar == '/' && (char) aheadChar == '/') {
                while ((nextChar = reader.read()) != -1 && (char) nextChar != '\n') {

                }
                values.resetPosition();
                values.incrementY();
                continue;
            }
            //used for things like <= >= etc
            //if the nextChar (current character) and aheadChar (advanced by 1) is true
            // it will append both chars, set the value for token
            //skip by 2 and break;
            if (relationalTrue((char) nextChar, (char) aheadChar)) {
                wordAdd.append((char) nextChar).append((char) aheadChar);
                values.setName(wordAdd.toString());
                reader.skip(1);
                values.setPosition(values.getPosition() + 2);
                values.setLine(values.getLine());
                tokenEmitted = true;
                break;
            }

            //used for things like _ so that bad_expression isnt seperated.
            //while loop done to make sure it can add on more
            if (Character.isLetter((char) nextChar) || (char) nextChar == '_' || (char) nextChar == '.') {
                wordAdd.append((char) nextChar);
                values.incrementX();
                while (aheadChar != -1 && (Character.isLetterOrDigit((char) aheadChar) || (char) aheadChar == '_'|| (char) aheadChar == '.')) {
                    wordAdd.append((char) aheadChar);
                    reader.read();
                    values.incrementX();
                    reader.mark(1);
                    aheadChar = lookAhead();
                }
                values.setName(wordAdd.toString());
                values.setPosition(values.getPosition());
                values.setLine(values.getLine());
                tokenEmitted = true;
                break;
            }


            //checks if the char ahead is a miscellaneous character
            // same thing with wordAdd not being 0
            // if its empty then it will add nextChar and break;
            if (miscTrue((char) nextChar)) {
                if (wordAdd.length() > 0) {
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    tokenEmitted = true;
                    break;
                }
                wordAdd.append((char) nextChar);
                values.setName(wordAdd.toString());
                values.incrementX();
                values.setPosition(values.getPosition());
                values.setLine(values.getLine());
                tokenEmitted = true;
                break;
            }
            //checks for spaces and increments
            //yknow how wordAdd not being length 0 works
            if ((char) nextChar == ' ') {
                if (wordAdd.length() > 0) {
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    values.incrementX();
                    tokenEmitted = true;
                    break;
                }
                values.incrementX();
                continue;
            }
            //checks if the current character is a number
            //appends it, increments x and if the char ahead is not a digit then it will seperate
            //used for things like 5else so 5 is seperated
            if (Character.isDigit((char) nextChar)) {
                wordAdd.append((char) nextChar);
                values.incrementX();
                if (aheadChar == -1 || !Character.isDigit((char) aheadChar)) {
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    tokenEmitted = true;
                    break;
                }
                continue;
            }

            //checks if its a number and if the length is not 0 so it can append and increment x.
            //used also for things like Second_455
            if (Character.isDigit((char) nextChar) && wordAdd.length() > 0) {
                wordAdd.append((char) nextChar);
                values.incrementX();
                if (aheadChar == -1 || !(Character.isLetterOrDigit((char) aheadChar) || (char) aheadChar == '_' ||(char) aheadChar == '.')) {
                    values.setName(wordAdd.toString());
                    values.setPosition(values.getPosition());
                    values.setLine(values.getLine());
                    tokenEmitted = true;
                    break;
                }
                continue;
            }
        }

        //checks for end of line and tokenEmitted being false
        if (nextChar == -1 && !tokenEmitted) {
            values.setName("END-OF-TEXT");
        }
    }
    //looks ahead 1 character
    public static int lookAhead() throws IOException {
        reader.mark(1);
        int ahead = reader.read();
        reader.reset();
        return ahead;

    }
    //returns the position
    public static String Position() {
        return (values.getPosition() + " " + values.getLine());
    }
    //returns the kind with loops and stuff
    public static String kind() {
        if (values.getValue().equals("int")) {
            valData = "";
            return "int";
        }
        for(String key : keyWords){
            if(key.equals(values.getValue())){
                valData = values.getValue();
                return "Keyword";
            }
        }

        for (String cond : conditionals) {
            if (cond.equals(values.getValue())) {
                valData = "";
                return values.getValue();
            }

        }
        if (values.getValue().equals("END-OF-TEXT")) {
            valData = "";
            return "END-OF-TEXT";
        }

        for (String relate : relationals) {
            if (relate.equals(values.getValue())) {
                relations = true;
                valData = "";
                return values.getValue();
            }
        }

        for(int i=0; i<values.getValue().length(); i++){
            if(isIllegal(values.getValue().charAt(i)) && !relations){
                valData = values.getValue();
                System.out.println("ILLEGAL CHARACTER >>>>>>> "+ values.getValue().charAt(i) + " Position "+ Position());
                System.exit(0);
            }
        }

        for(int i=0; i<values.getValue().length(); i++){
            if(Character.isLetter(values.getValue().charAt(i))){
                valData = values.getValue();
                return "ID";
            }
        }
        for(int i=0; i<values.getValue().length(); i++){
            if(Character.isDigit(values.getValue().charAt(i))){
                valData = values.getValue();
                return "NUM";
            }
        }


        for (String s : misc) {
            if (values.getValue().equals(s)) {
                valData = "";
                return values.getValue();
            }
        }

        for(int i=0; i<values.getValue().length(); i++){
            if(values.getValue().equals("_") || values.getValue().equals(".")){
                valData = "";
                return values.getValue();
            }
        }


        return "hi";

    }
    //checks if the char is illegal which was explained earlier
    public static boolean isIllegal(char aheadChar){
        boolean illegalChar=false;
        if(!Character.isLetterOrDigit(aheadChar) && !miscTrue(aheadChar) && !Character.isWhitespace(aheadChar) && !String.valueOf(aheadChar).contains("_") && !String.valueOf(aheadChar).contains(".")){
            //System.out.println("hello");
            return illegalChar=true;
        }
        return illegalChar;
    }
    //checks if the relations is true with the current and aheadchar. ie ==, <=, >= etc
    public static boolean relationalTrue(char current, char ahead) {
        boolean relationalBool = false;
        StringBuilder relationsString=new StringBuilder();
        relationsString.append(current);
        relationsString.append(ahead);
        for (String relate : relationals) {
            if (String.valueOf(relationsString).equals(relate)) {
                relationalBool = true;
                break;
            }
        }
        return relationalBool;
    }
    //checks if its a miscellaneous character
    public static boolean miscTrue(char ahead) {
        boolean miscBool = false;
        for (String m : misc) {
            if (String.valueOf(ahead).equals(m)) {
                miscBool = true;
                break;
            }
        }
        return miscBool;
    }
    //returns the value
    public static String value() {
        return valData;
    }
}
