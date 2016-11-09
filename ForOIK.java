package com.john_pank;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForOIK {

    public ArrayList<String> loadFile(String fileName) throws IOException{
        ArrayList <String> source = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName + ".txt")));
        String line;
        while((line = reader.readLine())!=null)
            source.add(line);
        reader.close();
        return source;
    }

    public void saveFile(String fileName,ArrayList<String> result) throws IOException{
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".clc")));

        for (String line : result)
            writer.println(line);

        writer.println();
        writer.println(";Создано в программе oikYrdGen");

        writer.flush();
        writer.close();
    }


    public ArrayList<String> logicCore(ArrayList<String> source){
        ArrayList <String> res = new ArrayList<>();
        String substation = "";
        int label1 = -1;
        int label2 = 0;
        int channel= 0;
        int kp = 0;
        int ts = 0;

        for (String line : source){
            if(line.matches("[a-zA-Z]+"))
                substation = line;

            if(line.matches(".+[0-9]+.[0-9]+.[0-9]")){
                Pattern p = Pattern.compile("[0-9]+");
                Matcher m = p.matcher(line);
                m.find();
                channel = Integer.parseInt(m.group());
                m.find();
                kp = Integer.parseInt(m.group());
                m.find();
                ts = Integer.parseInt(m.group());
                res.add("if GETFLAG(TM_STATUS," + channel + "," + kp + "," + ts + ",F_ABNORMAL) THEN goto M" + label1);
            }

            if(line.matches("[0-9]+")) {

                if(label1 == -1){

                    res.add(";Обработка индикаторов " + substation + " ВЛ №" + line);
                    res.add("");

                    label1 += 2;
                    label2 += 2;
                }
                else{
                    //lamp on/off
                    res.add("");
                    res.add("#TC" + channel + ":" + kp + ":200 = 1");
                    res.add("goto M" + label2);
                    res.add("");
                    res.add("M" + label1 + ": #TC" + channel + ":" + kp + ":200 = 0");
                    res.add("M" + label2 + ":");
                    res.add("");

                    label1 += 2;
                    label2 += 2;


                    res.add("");
                    res.add(";Обработка индикаторов " + substation + " ВЛ №" + line);
                    res.add("");
                }


            }
        }
        res.add("");
        res.add("#TC" + channel + ":" + kp + ":200 = 1");
        res.add("goto M" + label2);
        res.add("");
        res.add("M" + label1 + ": #TC" + channel + ":" + kp + ":200 = 0");
        res.add("M" + label2 + ":");
        res.add("");
        res.add("sleep(1000)");

        return res;
    }

    public static void main(String[] args) throws IOException {
        ForOIK foik = new ForOIK();
        ArrayList <String> prog;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String fileName;
        System.out.println("How many files you have?");
        fileName = reader.readLine();
        int max = Integer.parseInt(fileName);
        for(int i = 0; i < max; i++) {
            try {
                System.out.println("Input file name:");
                fileName = reader.readLine();
                prog = foik.loadFile(fileName);
                prog = foik.logicCore(prog);
                //System.out.println(prog);
                foik.saveFile(fileName, prog);
            } catch (IOException e) {
                System.out.println("File not found!!!");
                i--;
            }
        }

        //credits
        System.out.println();
        System.out.println("*************************");
        System.out.println("  (c)Pankrushov Evgenij");
        System.out.println("*************************");
        System.out.println("v1.02");

    }
}

/*
    public ArrayList<String> consoleInput() throws IOException{
        ArrayList <String> source = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = " ";
        while((line = reader.readLine())!=null)
            source.add(line);
        reader.close();
        return source;
    }
*/

