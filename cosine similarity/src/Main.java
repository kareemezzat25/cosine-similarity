import java.io.*;
import java.io.IOException;
import java.util.*;

    public class Main {
        public static void main(String args[])  throws IOException{
            Scanner input=new Scanner(System.in);
            InvertedIndex invertedIndex=new InvertedIndex();
            String []documents=new String[10];
            documents=new String[]
            {
                    "D:\\Documents/Doc1.txt","D:\\Documents/Doc2.txt","D:\\Documents/Doc3.txt",
                    "D:\\Documents/Doc4.txt","D:\\Documents/Doc5.txt", "D:\\Documents/Doc6.txt",
                    "D:\\Documents/Doc7.txt","D:\\Documents/Doc8.txt","D:\\Documents/Doc9.txt",
                    "D:\\Documents/Doc10.txt"
            };
            invertedIndex.createIndex(documents);//create index
            //invertedIndex.printPoistinglist();
            System.out.println("Enter The word you want : ");
            String term=input.nextLine();
            System.out.println("Enter 1 to Calculate Term Frequency : ");
            System.out.println("Enter 2 to Calculate TF-IDF : ");
            int number= input.nextInt();
            if(number==1)
                invertedIndex.calculateTf(term);
            else if(number==2)
                invertedIndex.calculateTF_IDF(term);
            else
                System.out.println("Invalid Number Enter 1 or 2");
        }
    }


