package lucene;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

public class QueryIndex
{

    // the location of the search index
    private static String INDEX_DIRECTORY = "D:\\MICHAELMANS SEMESTER (1)\\Modules\\Information retrieval and web search\\Assignments\\Index";

    //Queries file path
    private static String qryPath = "D:\\MICHAELMANS SEMESTER (1)\\Modules\\Information retrieval and web search\\Assignments\\Data\\cran.qry";

    //Output result
    private static String outputResultsPath = "D:\\MICHAELMANS SEMESTER (1)\\Modules\\Information retrieval and web search\\Assignments\\Outputs";

    // Limit the number of search results we get
    private static int MAX_RESULTS = 1400;

    public static void main(String[] args) throws IOException, ParseException
    {
        // Analyzer used by the query parser.
        // Must be the same as the one used when creating the index
        Analyzer analyzer = new StandardAnalyzer();

        // Open the folder that contains our search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // ArrayList of documents in the corpus
        ArrayList<Document> documents = new ArrayList<Document>();

        // create objects to read and search across the index
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        isearcher.setSimilarity(new BM25Similarity());


        // Create parser
        MultiFieldQueryParser multiFieldQP = new MultiFieldQueryParser(new String[] { "Title", "Word" }, analyzer);

        //Read the qry file
        Path file = Paths.get(qryPath);
        InputStream stream = null;
        try {
            stream = Files.newInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line = null;
        StringBuilder fileContent = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));

        try {
            while((line = in.readLine()) != null) {
                fileContent.append(line + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String Content = fileContent.toString();
        String[] eachContent = Content.split(".I");
        
        // remove the first line since it's empty
        eachContent = Arrays.copyOfRange(eachContent, 1, eachContent.length);

        Path outputFile = Paths.get(outputResultsPath);
        List<String> queryResults = new ArrayList<String>();

        //Split content and add index field
        for (int i = 0; i < eachContent.length; i++)
        {
            int index_W = eachContent[i].indexOf(".W");
            // cut the contents of each filed
            String queryText = eachContent[i].substring(index_W + 2).trim().replaceAll(" +", " ").replaceAll("\\?", "");
            int queryId = i + 1;
            Query query = multiFieldQP.parse(queryText);
            TopDocs results = isearcher.search(query, MAX_RESULTS);
            ScoreDoc[] hits = results.scoreDocs;
            for (int j = 0; j < hits.length; j++) {
                Document hit = isearcher.doc(hits[j].doc);
                queryResults.add(queryId + " Q0 " + hit.get("Id") + " " + (j + 1) + " " + hits[j].score + " STANDARD");
            }
        }
        Files.write(outputFile, queryResults, Charset.forName("UTF-8"));
        ireader.close();
    }
}
