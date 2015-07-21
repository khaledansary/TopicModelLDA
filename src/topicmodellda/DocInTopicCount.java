/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topicmodellda;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author khaledd
 */
public class DocInTopicCount {
    public static void main(String[] args) throws IOException {
 
        DocInTopicCount obj = new DocInTopicCount();
    
        obj.run();
 
    }
    public void run()
    {
        try {
            printUrl("J:\\Topics\\NumofTopics5\\output_csv\\DocsInTopics.csv","J:\\Doc\\output.txt");
        }catch(Exception e)
        {
           System.out.println(e);
        }

    }
    public static Map<String, Integer> getWordCount(String fileName){

      FileInputStream fis = null;
      DataInputStream dis = null;
      BufferedReader br = null;
      Map<String, Integer> wordMap = new HashMap<String, Integer>();
      try {
          fis = new FileInputStream(fileName);
          dis = new DataInputStream(fis);
          br = new BufferedReader(new InputStreamReader(dis));
          String line = null;
          while((line = br.readLine()) != null){
              String[] topic=line.split(";");
              if(wordMap.containsKey(topic[0])){
                  wordMap.put(topic[0], wordMap.get(topic[0])+1);
              } else {
                  wordMap.put(topic[0], 1);
              }

          }
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      } finally{
          try{if(br != null) br.close();}catch(Exception ex){}
      }
      return wordMap;
    }
    public static void printUrl(String filename,String outputfile) throws FileNotFoundException, UnsupportedEncodingException
    {
        Map<String, Integer> wordMap = getWordCount(filename);
        List<Map.Entry<String, Integer>> list = sortByValue(wordMap);
        PrintWriter writer = new PrintWriter(outputfile, "UTF-8");
        for(Map.Entry<String, Integer> entry:list){
            System.out.println(entry.getKey()+" ==== "+entry.getValue());
            writer.print("Topic: "+entry.getKey()+" ("+entry.getValue()+")");
            writer.println();
        }
        writer.close();

    }

    public static List<Map.Entry<String, Integer>> sortByValue(Map<String, Integer> wordMap){

        Set<Map.Entry<String, Integer>> set = wordMap.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
        return list;
    }

    
}
