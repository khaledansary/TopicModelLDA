/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topicmodellda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author khaledd
 */
public class TopicQuality {
    public static void main(String[] args)  {
 
	TopicQuality obj = new TopicQuality();
        
        double threshold=05;
        System.out.println("Threshold: "+threshold);
        /*for(int i=25;i<=100;i++)
        {
            String Dir="E:\\Thesis Data\\LDAData\\NumofTopics"+i+"\\";
            File f = new File(Dir);
            if (f.exists()) {
                    obj.findTopicDocuments(1000, i, Dir);
            }
            else{
                System.out.println("No exist yet"+i);
            }
            
            
        }*/
        int numberofTopic=77;
        String Dir="E:\\Thesis Data\\LDAData\\NumofTopics"+numberofTopic+"\\";
        obj.findTopicDocuments(1000, numberofTopic, Dir);
	
        
        //obj.test();
 
    }
    
    public void test()
    {
        Map<String, Integer> map = new HashMap <>();
        map.put("1", 0);
        map.put("2", 1);
        map.put("4", 2);
        map.put("7", 3);
        int data1=map.get("1");
       // System.out.println(data1);
        data1=map.get("7");
       // System.out.println(data1);
        data1=map.get("4");
        //System.out.println(data1);
    }
     public static String getParenthesesContent(String str){
        return str.substring(str.indexOf('(')+1,str.indexOf(')'));
    }
    
    public void findTopicDocuments(int numofDocument,int numofTopic,String Dir) 
    {
        BufferedReader br = null;
        NumberFormat formatter = new DecimalFormat("#0.00");    
        try {
          br = new BufferedReader(new FileReader(Dir+"output_doc_topics.txt"));
          PrintWriter writer = new PrintWriter(Dir+"topic_distribution.txt");
          String line = null;
          int i=0;
          int index=0;
           ArrayList<Cluster> cluster = new ArrayList<Cluster>();
           Map<String, Integer> map = new HashMap <>();
          while((line = br.readLine()) != null){
              
              String[] proportion=line.split("\\t");
             // if(i<numofDocument)
              //{
                  if(i==0)
                  {
                      writer.println("Doc,Topic,Distribution");
                  }
                  else
                  {
                                        
                    if(Double.parseDouble(proportion[3])>0.5)
                    {
                        String topic=proportion[2];
                        String doc= proportion[0];
                        double dist=Double.parseDouble(proportion[3]);
                        Cluster clusteritem= new Cluster();          
                        clusteritem.setTopic(Integer.parseInt(topic));
                        clusteritem.setDoc(Integer.parseInt(doc));
                        clusteritem.setDistribution(dist);
                        cluster.add(clusteritem);
                        map.put(doc, index);
                        index++;
                        writer.println(proportion[0]+","+proportion[2]+","+proportion[3]);
                        //System.out.println(proportion[0]+","+proportion[2]+","+proportion[3]);
                    }

                  }
                  i++;
               //}
              //else
              //{
                //  break;
              //}
           }
          br.close();
          System.out.println("Total Doc: "+index);
        writer.close();
        writer = new PrintWriter(Dir+"topic_cluster.txt");
        //PrintWriter writer2 = new PrintWriter(Dir+"topic_cluster1.txt");
        
        
        List<Cluster> toRemove = new ArrayList<Cluster>();
        StringBuilder output = new StringBuilder();
        Collections.sort(cluster, new Comparator<Cluster>() {
        public int compare(Cluster c1, Cluster c2) {
          if (c1.topic < c2.topic) return -1;
          if (c1.topic > c2.topic) return 1;
          return 0;
        }});
        
        
        //int nTopic[]=new int[numofTopic];
        
        int countdoc=0;  
        double avg=0.0;
        HashMap<Integer, List<Cluster>> hashMap=new HashMap<Integer, List<Cluster>>();
        

        String docs="";
        String printDocs="";
        String tempTopic="0";
        for (Cluster p: cluster) {
            if(!hashMap.containsKey(p.getTopic())){
                List<Cluster> list= new ArrayList<Cluster>();
                list.add(p);
                hashMap.put(p.getTopic(),list);
            }
            else
            {
                hashMap.get(p.getTopic()).add(p);
            }    

             
        }
        List<ClusterAvgCount> topicAvg= new ArrayList<ClusterAvgCount>();  
        for(int k=0;k<numofTopic;k++)
        {
            ClusterAvgCount clusterAvg= new ClusterAvgCount();
            System.out.println(hashMap.get(k).size());
            writer.print(k+"\t"+hashMap.get(k).size()+"\t");
            avg=0.0;
            
            clusterAvg.setTopic(""+k);
          //  writer2.print(k+"\t");
            for(int l=0;l<hashMap.get(k).size();l++)
            {
                //System.out.println(hashMap.get(k).get(l).getDoc());
                avg+=hashMap.get(k).get(l).getDistribution();
                //docs+=hashMap.get(k).get(l).getDoc()+",";
                //printDocs+=hashMap.get(k).get(l).getDoc()+"("+formatter.format(hashMap.get(k).get(l).getDistribution())+"),";
                writer.print(hashMap.get(k).get(l).getDoc()+"("+formatter.format(hashMap.get(k).get(l).getDistribution())+")");
            //    writer2.print(hashMap.get(k).get(l).getDoc());
                if(l<hashMap.get(k).size()-2)
                {
                    writer.print(",");
              //      writer2.print(",");
                }
            }   
            writer.print("\t"+avg/hashMap.get(k).size());
            //writer2.print("\t"+avg/hashMap.get(k).size());
            clusterAvg.setAvg(avg/hashMap.get(k).size());
            topicAvg.add(clusterAvg);
            System.out.println("Topic write: "+k);
            
            writer.println();
            //writer2.println();
            
        }
        System.out.println("Size of Topic: "+topicAvg.size());
        writer.close();
        //writer2.println();
       /* double totalsilhoutte=0.0;
        int totalDocs=0;
        br = new BufferedReader(new FileReader(Dir+"topic_cluster1.txt"));
        while((line = br.readLine()) != null)
        {
            String[] topicCluster=line.split("\\t");
            double onebyOneTopicSilhoutte=0.0;
            
            int t=Integer.parseInt(topicCluster[0]);
            System.out.println("Topic: "+t);
            String getDocs[]=topicCluster[1].split(","); //get number of documents in a topic
            
            
            //System.out.println(line);
            
            
            for(int p=0;p<getDocs.length;p++)   // each document in a topic
            {
                
                double avgSimilarity=0.0;
                double similarity=0.0;
                int numofDocsInTopic=0;
                totalDocs++;
                for(int q=p+1;q<getDocs.length-1;q++) //compaare each document in same cluster
                {
                    //System.out.println("Avg Similarity of Doc: "+p+"& "+q+" is "+getEuclidianDistance(cluster.get(map.get(getDocs[p])).getDistribution(),cluster.get(map.get(getDocs[q])).getDistribution()));
                    similarity+=getEuclidianDistance(cluster.get(map.get(getDocs[p])).getDistribution(),cluster.get(map.get(getDocs[q])).getDistribution());
                   // System.out.println("similarity here: "+similarity );
                    numofDocsInTopic++;
                            
                }
                avgSimilarity=similarity/getDocs.length;
                
                //System.out.println("a: "+avgSimilarity);
                //System.out.println("Avg Similarity of Doc: "+p+"is "+avgSimilarity);
                double dissimilairty=0.0;
                double avgDisSimilarity=0.0;
                int numOfTopicCluster=0;
                double distance =1.0;
                for(int tp=0;tp<numofTopic;tp++) //find dissimilarity of different cluster for each document
                {
                    if(t!=tp)
                    {
                       // System.out.println("Topic AVG: "+p+" :  "+cluster.get(map.get(getDocs[p])).getDistribution());
                        double temp=getEuclidianDistance(cluster.get(map.get(getDocs[p])).getDistribution(),Double.parseDouble(topicCluster[2]));//red
                        if(temp<distance)
                        {
                            distance=temp;
                        }
                    }
                 }   
                avgDisSimilarity=distance;
                //System.out.println("b: "+avgDisSimilarity);
                double docSilhoutte=0.0;
                if(avgSimilarity<avgDisSimilarity)
                {
                    docSilhoutte=1 -(avgSimilarity/avgDisSimilarity);
                }
                else if(avgSimilarity>avgDisSimilarity)
                {
                    docSilhoutte=(avgDisSimilarity/avgSimilarity)-1;
                }
                else{
                    docSilhoutte=avgDisSimilarity=avgSimilarity;
                }
                    
                //System.out.println("Silhoutte: "+p+"is "+docSilhoutte);
                onebyOneTopicSilhoutte+=docSilhoutte;
                //System.out.println("Avg Similarity of Doc: "+p+"is "+avgDisSimilarity);
                
            }
            totalsilhoutte+=onebyOneTopicSilhoutte;
            System.out.println("total for topic "+ t +" : "+totalsilhoutte);
        }
        System.out.println("Total No. of Docs"+totalDocs);
        double ldaModel=totalsilhoutte/totalDocs;        
        System.out.println("LDA Quality: "+ldaModel);*/
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      } finally{
          try{if(br != null) br.close();}catch(Exception ex){}
      }
        
        
    }
    public double getEuclidianDistance(double x,double y)
    {
        double euclidian, diff,sqr;
        diff= x-y;
        sqr = Math.pow(diff,2);
        euclidian= Math.sqrt(sqr);
        
        return euclidian;
    }
    
   
} 


