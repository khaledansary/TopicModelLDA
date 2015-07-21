/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topicmodellda;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author khaledd
 */
public class DocumentDistance {
    public static void main(String[] args)  {
        DocumentDistance obj=new DocumentDistance();
        obj.DocumentRead(46);
    }
    public static double calculateDistance(double[] array1, double[] array2)
    {
        double Sum = 0.0;
        for(int i=0;i<array1.length;i++) {
           Sum = Sum + Math.pow((array1[i]-array2[i]),2.0);
        }
        return Math.sqrt(Sum);
    }
    public void DocumentRead(int numberofTopic)
    {
        String Dir="E:\\Thesis Data\\LDAData\\NumofTopics"+numberofTopic+"\\";
        BufferedReader br = null;
        NumberFormat formatter = new DecimalFormat("#0.00");    
        try {
          br = new BufferedReader(new FileReader(Dir+"output_doc_topics.txt"));
          PrintWriter writer = new PrintWriter(Dir+"document_distance.txt");
          String line = null;
          int i=0;
          int index=0;
          
          List<double[]> documentArray=new ArrayList<double[]>();
          
          while((line = br.readLine()) != null){
              double docArray[] =new double[numberofTopic];
              String[] proportion=line.split("\\t");
              
             
                  if(i==0)
                  {
                      writer.println("Doc,Topic,Distribution");
                  }
                  else
                  {
                    int topicCount=0;                    
                    for(int j=3;j<proportion.length-1;j=j+3)
                    {
                        
                        double dist=Double.parseDouble(proportion[j]);
                        docArray[topicCount]=dist;
                        topicCount++;
                        
                        
                        //System.out.println(proportion[0]+","+proportion[2]+","+proportion[3]);
                    }
                    documentArray.add(docArray);

                  }
                  i++;
             
           }
           for(int p=0;p<documentArray.size()-1;p++)
           {
             for(int q=1;q<documentArray.size()-2;q++)
             {
                //System.out.println("Distance of doc: "+p +"and doc: "+q+" is:"+ calculateDistance(documentArray.get(p),documentArray.get(q)));
                writer.println(p+","+q+","+calculateDistance(documentArray.get(p),documentArray.get(q)));
             }
           }
            br.close();
            System.out.println("Total Doc: "+index);
            writer.close();
        }catch(Exception e)
        {
            System.out.println(e);
        }
  
    }
}
