import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InvertedIndex {
    HashMap<String, DictEntry> termIndex;
    int collection=10;
    InvertedIndex() {
        termIndex = new HashMap<>();
    }
    public void createIndex(String[] files) throws IOException {
        int documentId = 1;
        try {
            for (String filename : files) {
                int wordCount = 0;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] words = line.split("\\W+");//(//W+)
                    for (String word : words) {
                        wordCount += 1;
                        word = word.toLowerCase();
                        if (!termIndex.containsKey(word))//search for term in inverted index
                        {
                            termIndex.put(word, new DictEntry(documentId));
                            termIndex.get(word).term_freq++;
                            termIndex.get(word).doc_freq++;
                            termIndex.get(word).plist.positions.add(wordCount);
                        } else {
                            termIndex.get(word).term_freq++;
                            Poisting poistinglist = termIndex.get(word).plist;
                            while (poistinglist != null) {
                                if (poistinglist.docId == documentId) {
                                    poistinglist.positions.add(wordCount);
                                    poistinglist.termFrequency++;
                                    break;
                                }
                                if (poistinglist.next == null) {
                                    poistinglist.next = new Poisting(documentId);
                                    poistinglist.next.positions.add(wordCount);
                                    termIndex.get(word).plist.documentfrequency++;
                                    break;
                                }
                                poistinglist = poistinglist.next;
                            }
                        }
                    }
                }
                documentId++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    // Term ---> poistingList
    public void printPoistinglist() {
        TreeMap<String, DictEntry> sortedDict = new TreeMap<>();
        sortedDict.putAll(termIndex);
        for (Map.Entry<String, DictEntry> term : sortedDict.entrySet()) {
            Poisting poistlist = term.getValue().plist;
            System.out.print(term.getKey());
            System.out.print("-------->{");
            while (poistlist != null) {
                if (poistlist.next == null) {
                    System.out.print(poistlist.docId+" : "+ poistlist.termFrequency);
                    break;
                }
                //term frequency
                System.out.print(poistlist.docId + " : "+ poistlist.termFrequency+",");
                poistlist = poistlist.next;
            }
            System.out.println("}---------->DocumentFrequncey : " + term.getValue().plist.documentfrequency);
        }
    }
    public void print() {
        TreeMap<String, DictEntry> sortedDict = new TreeMap<>();
        sortedDict.putAll(termIndex);
        for (Map.Entry<String, DictEntry> dictonary : sortedDict.entrySet()) {
            System.out.println(dictonary.getKey() + "    DocumentFrequency : " + dictonary.getValue().plist.documentfrequency + "   TermFrequency : " + dictonary.getValue().term_freq);
        }
    }
    public void printPositionlIndex() {
        TreeMap<String, DictEntry> sortedDict = new TreeMap<>();
        sortedDict.putAll(termIndex);

        for (Map.Entry<String, DictEntry> term : sortedDict.entrySet()) {
            Poisting poistlist = term.getValue().plist;
            System.out.print(term.getKey());
            System.out.print("-------->{");
            while (poistlist != null) {

                System.out.print(poistlist.docId + ":");
                System.out.print(poistlist.positions);
                poistlist = poistlist.next;
            }
            System.out.println();
        }
    }
    // calculate Cosine Similarity with TF_IDF for Document
    // change 10 with num of document
    public void calculateTF_IDF(String phrase) throws IOException {
        String[] query = phrase.split("\\W+");
        int len = query.length;
        double[][] matrix = new double[10][termIndex.size()];
        TreeMap<String, DictEntry> sortedDict = new TreeMap<>();
        sortedDict.putAll(termIndex);
        int j=0;
        for (int i = 0; i < 10; i++) {
            j=0;
            for (Map.Entry<String, DictEntry> term : sortedDict.entrySet()) {
                Poisting poistinglist = term.getValue().plist;
                while (poistinglist != null) {
                    if (i + 1 == poistinglist.docId) {
                        double  tf=poistinglist.termFrequency;
                        double idf=Math.log10((double)collection/ termIndex.get(term.getKey()).plist.documentfrequency);
                        matrix[i][j] = (1+Math.log10(tf))*idf;
                    }
                    poistinglist = poistinglist.next;
                }
                j++;
            }
        }
        double[]matrixQuery=queryTF_idf(phrase);
        double querySimilarity = 0;
        for(int i=0;i<matrixQuery.length;i++)
        {
            querySimilarity+=matrixQuery[i]*matrixQuery[i];
        }
        ArrayList<Double> finalSimilarity=similarty(matrix,matrixQuery,querySimilarity);
        /*for(int i=0;i<finalSimilarity.size();i++)
        {
            System.out.println("The simalirty for document "+(i+1)+" : "+finalSimilarity.get(i));
        }*/
        ranksimilarity(finalSimilarity);
    }
    //  calculate Cosine Similarity with TF_IDF for Query
    public double[]queryTF_idf(String phrase)
    {
        String[] query = phrase.split("\\W+");
        int len = query.length;
        TreeMap<String, DictEntry> sortedDict = new TreeMap<>();
        sortedDict.putAll(termIndex);
        double[]matrixQuery=new double[termIndex.size()];
        int count=0, countquery=0,countmatrix=0;
        for (Map.Entry<String, DictEntry> term : sortedDict.entrySet()) {
            while(countquery<len)
            {
                if(term.getKey().equals(query[countquery]))
                    count++;
                countquery++;
            }
            if(count==0)
                matrixQuery[countmatrix]=0;
            else
            {
                double tff=(double)count;
                Poisting poistinglist = term.getValue().plist;
                double idf=Math.log10((double)collection/termIndex.get(term.getKey()).plist.documentfrequency );
                matrixQuery[countmatrix] = (1+Math.log10(tff))*idf;
            }
            countmatrix++;
            count=0;countquery=0;
        }
        return matrixQuery;
    }
    // calculate Cosine Similarity with TF
    // change 10 with num of document
    public void calculateTf(String phrase) throws IOException {
        String[] query = phrase.split("\\W+");
        int len = query.length;
        double[][] matrix = new double[10][termIndex.size()];
        TreeMap<String, DictEntry> sortedDict = new TreeMap<>();
        sortedDict.putAll(termIndex);
        int j=0;
        for (int i = 0; i < 10; i++) {
            j=0;
            for (Map.Entry<String, DictEntry> term : sortedDict.entrySet()) {
                Poisting poistinglist = term.getValue().plist;
                while (poistinglist != null) {
                    if (i + 1 == poistinglist.docId) {
                        matrix[i][j] = poistinglist.termFrequency;
                    }
                    poistinglist = poistinglist.next;
                }
                j++;
            }
        }
        double[]matrixQuery=queryTf(phrase);
        double querySimilarity = 0;
        for(int i=0;i<matrixQuery.length;i++)
        {
            querySimilarity+=matrixQuery[i]*matrixQuery[i];
        }
        ArrayList<Double> finalSimilarity=similarty(matrix,matrixQuery,querySimilarity);

        /*for(int i=0;i<finalSimilarity.size();i++)
        {
            System.out.println("The simalirty for document "+(i+1)+" : "+finalSimilarity.get(i));
        }*/
        ranksimilarity(finalSimilarity);
    }
    public double[]queryTf(String phrase)
    {
        String[] query = phrase.split("\\W+");
        int len = query.length;
        TreeMap<String, DictEntry> sortedDict = new TreeMap<>();
        sortedDict.putAll(termIndex);
        double[]matrixQuery=new double[termIndex.size()];
        int count=0, countquery=0,countmatrix=0;
        for (Map.Entry<String, DictEntry> term : sortedDict.entrySet()) {
            while(countquery<len)
            {
                if(term.getKey().equals(query[countquery]))
                    count++;
                countquery++;
            }
            if(count==0)
                matrixQuery[countmatrix]=0;
            else
                matrixQuery[countmatrix]=count;
            countmatrix++;
            count=0;countquery=0;
        }
        return matrixQuery;
    }
    public int lengthDocument(String pathDocument) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(pathDocument));
        String line;
        int length=0;
        while ((line = bufferedReader.readLine()) != null) {
            String[]words = line.split("\\W+");//(//W+)
            length+= words.length;
        }
        return length;
    }
    public ArrayList<Double>similarty(double[][]matrixdocuments,double[]query,double querySimilarty)
    {
        ArrayList<Double> similarity = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            double documentSimiliraty = 0;
            double similaritytTest = 0;
            for (int  k= 0; k < termIndex.size(); k++) {
                similaritytTest += matrixdocuments[i][k] * query[k];
                documentSimiliraty += (matrixdocuments[i][k]*matrixdocuments[i][k]);
            }
            similarity.add(similaritytTest/ (Math.sqrt(documentSimiliraty) * Math.sqrt(querySimilarty)));
        }
        return similarity;
    }
    public void ranksimilarity(ArrayList<Double>cosinesimilarity)
         {
             HashMap<Integer,Double>rankDocument=new HashMap<Integer,Double>();
             int i=1;
             for(double similarty:cosinesimilarity)
             {
                 rankDocument.put(i,similarty);
                 i++;
             }
             Map<Integer,Double> sortedMap = sortByValueDesc(rankDocument);

             for (Map.Entry<Integer,Double> entry : sortedMap.entrySet()) {
                 System.out.print("Document Number : " + entry.getKey() + " with cosine similarty : "); System.out.format("%.4f",entry.getValue());
                 System.out.println();
             }
        }
    public static Map< Integer,Double> sortByValueDesc(Map< Integer,Double> map) {
        List<Map.Entry< Integer,Double>> list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry< Integer,Double>>() {
            @Override
            public int compare(Map.Entry< Integer,Double> o1, Map.Entry< Integer,Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map< Integer,Double> result = new LinkedHashMap<>();
        for (Map.Entry<Integer,Double> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
