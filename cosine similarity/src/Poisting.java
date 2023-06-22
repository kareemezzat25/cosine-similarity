import java.util.ArrayList;

public class Poisting {

        public Poisting next=null;
        int docId=0;
        int documentfrequency=1;
        int termFrequency=1;
        ArrayList<Integer>positions=new ArrayList<>();
        Poisting(int Docid)
        {
            this.docId=Docid;
        }

}
