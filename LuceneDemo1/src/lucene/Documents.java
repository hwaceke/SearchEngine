package lucene;

import java.io.BufferedReader;
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
 
public class Documents{
	// Directory where the search index will be saved
	private static String INDEX_DIRECTORY = "LuceneDemo1/Index";
	// Location of the data to be searched
	private static String filePath = "LuceneDemo1/Data/cran.all.1400";
	
	public static void main(String[] args) throws IOException
	{
		// Make sure we were given something to index
		if (args.length <= 0)
		{
            System.out.println("Expected corpus as input");
            System.exit(1);            
        }

		// Analyzer that is used to process TextField
		Analyzer analyzer = new StandardAnalyzer();
		
		// ArrayList of documents in the corpus
		ArrayList<Document> documents = new ArrayList<Document>();

		// Open the directory that contains the search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		// Set up an index writer to add process and save documents to the index
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iwriter = new IndexWriter(directory, config);
		
		//Read the file
		Path file = Paths.get(filePath);
        InputStream stream = null;
        try {
            stream = Files.newInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line = null;
        StringBuilder fileContent = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        // read in file
        try {
            while((line = in.readLine()) != null) {
                fileContent.append(line + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String Content = fileContent.toString();
        String[] eachContent = Content.split(".I");
        
        // To remove the first line, it's empty
        eachContent = Arrays.copyOfRange(eachContent, 1, eachContent.length);

		
		//Split content and index fields
        for (String arg : eachContent)
        {
            int index_T = arg.indexOf(".T");
            int index_A = arg.indexOf(".A");
            int index_B = arg.indexOf(".B");
            int index_W = arg.indexOf(".W");
            // cut the contents of each filed
            String content_I = arg.substring(0, index_T).trim();
            String content_T = arg.substring(index_T + 2, index_A).trim();
            String content_A = arg.substring(index_A + 2, index_B).trim();
            String content_B = arg.substring(index_B + 2, index_W).trim();
            String content_W = arg.substring(index_W + 2).trim();

            System.out.println(content_I);
            System.out.println(content_T);
            
            System.exit(1);
			// Load the contents of the file
			System.out.printf("Indexing \"%s\"\n", arg);
			String content = new String(Files.readAllBytes(Paths.get(arg)));

			// Create a new document and add the file's contents
			Document doc = new Document();
			doc.add(new StringField("filename", arg, Field.Store.YES));
			doc.add(new TextField("content", content, Field.Store.YES));

			// Add the file to our linked list
			documents.add(doc);
		}

		// Write all the documents in the linked list to the search index
		iwriter.addDocuments(documents);

		// Commit everything and close
		iwriter.close();
		directory.close();
	}
}
