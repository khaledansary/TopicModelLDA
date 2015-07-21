/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topicmodellda;

/**
 *
 * @author khaledd
 */
import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class TopicModelLDA {
    public static final String CSV_DEL = ";";
    public static void LDAModel(int numofK,int numbofIteration,int numberofThread,String outputDir,InstanceList instances) throws Exception
    {
        

        

        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        int numTopics = numofK;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(numberofThread);

        // Run the model for 50 iterations and stop (this is for testing only, 
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(numbofIteration);
        model.estimate();
        // Show the words and topics in the first instance

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++) {
           // out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
             out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
             
        }
        System.out.println(out);
        //System.out.println("Hi 1:"+out);
        
        // Estimate the topic distribution of the first instance, 
        // given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        
        
        
        // Show top 10 words in topics with proportions for the first document
        String topicsoutput="";
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            
            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
            int rank = 0;
            while (iterator.hasNext() && rank < 10) {
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                //out.format("%s ", dataAlphabet.lookupObject(idCountPair.getID()));
                rank++;
            }
            System.out.println(out);
            topicsoutput+=out+"\n";
            
            //System.out.println("Hi 2:"+out);
        }
        

        // Create a new instance with high probability of topic 0
        StringBuilder topicZeroText = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

        int rank = 0;
        while (iterator.hasNext() && rank < 10) {
            IDSorter idCountPair = iterator.next();
            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
            rank++;
        }

        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
        System.out.println("0\t" + testProbabilities[0]);
        
        
        File csvDir = new File(outputDir + File.separator+ "NumofTopics"+numTopics);	//FIXME replace all strings with constants
	csvDir.mkdir();
        String csvDirPath = csvDir.getPath();
        String stateFile = csvDirPath+File.separator+"output_state.gz";
        String outputDocTopicsFile = csvDirPath+File.separator+"output_doc_topics.txt";
        String topicKeysFile = csvDirPath+File.separator+"output_topic_keys";
        PrintWriter writer=null;
        String topicKeysFile_fromProgram = csvDirPath+File.separator+"output_topic";
        
        try {
            writer = new PrintWriter(topicKeysFile_fromProgram, "UTF-8");
            writer.print(topicsoutput);
            writer.close();
        } catch (Exception e) {
                e.printStackTrace();
        }
        
        model.printTopWords(new File(topicKeysFile), 11, false);           
        model.printDocumentTopics(new File (outputDocTopicsFile));
        //model.printState(new File (stateFile));
       // GunZipper g = new GunZipper(new File(stateFile));
      //  g.unzip(new File(csvDirPath+File.separator+"output_state"));
      //  outputCsvFiles(csvDirPath,false,numofK);
        
      //  System.out.println("Mallet Output files written in " + csvDirPath + " ---> " + stateFile + " , " +
        //        topicKeysFile );
        System.out.println("Output files written in " + outputDocTopicsFile);
        //System.out.println("Html Output files written in " + csvDirPath + File.separator+ "output_html");
       // clearExtrafiles(csvDirPath);
        
        
        //System.out.println("Hi 3:"+testProbabilities[0]);
    }
    private static void outputCsvFiles(String outputDir,Boolean htmlOutputFlag,int numberofTopics)
    {

            CsvBuilder cb = new CsvBuilder();
            cb.createCsvFiles(numberofTopics, outputDir);

            if(htmlOutputFlag){
                //HtmlBuilder hb = new HtmlBuilder(cb.getNtd(),new File("E:\\Thesis Data\\DataForLDA\\freshnewData\\cleanTweets.txt"));
                //hb.createHtmlFiles(new File(outputDir));
            }
            clearExtrafiles(outputDir);
    }
    private static void clearExtrafiles(String outputDir)
    {
            String[] fileNames = {"topic-input.mallet","output_state.gz",
                                                            "output_state"};
            for(String f:fileNames){
                    if(!(new File(outputDir,f).canWrite())){
                            System.out.println(f);
                            }
                    Boolean b = new File(outputDir,f).delete();

            }
    }
    public static void main(String[] args) throws Exception{
        
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("H:\\Data\\stoplists\\en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );
        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        Reader fileReader = new InputStreamReader(new FileInputStream(new File("E:\\Thesis Data\\DataForLDA\\freshnewData\\cleanTweets.txt")), "UTF-8");
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                                               3, 2, 1)); // data, label, name fields

        int numberofTopic=5;
        int numberofIteration=500;
        int numberofThread=6;
        String outputDir="E:\\Thesis Data\\LDAData\\";
        
        //int numberofTopic=5;
        //LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        //TimeUnit.SECONDS.sleep(30);
       /* numberofTopic=100;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=69;
       LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=71;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=73;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=76;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
       
        numberofTopic=77;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=78;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=81;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=83;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);*/
        
        numberofTopic=85;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=86;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=87;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=88;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=89;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=91;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
                
        
        numberofTopic=93;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        numberofTopic=97;
        LDAModel(numberofTopic,numberofIteration,numberofThread,outputDir,instances); 
        TimeUnit.SECONDS.sleep(30);
        
        
        
        
    }
}

