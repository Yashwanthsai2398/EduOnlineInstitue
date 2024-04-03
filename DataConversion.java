import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class DataConversion {
    public void graph() {
        String filepath = "D:/MS CSE/Spring2024/Web Semantics/webu/SecondPhase/EduOnline.csv";

        Model courseGraph = ModelFactory.createDefaultModel();

        String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns/";
        String rdfs = "http://www.w3.org/2000/01/rdf-schema/";
        String db = "https://dbpedia.org/page/";
        String dbo = "https://dbpedia.org/ontology/";
        Property belongsTo = courseGraph.createProperty(dbo + "belongsTo");
        Property hasLevel = courseGraph.createProperty(dbo + "hasLevel");
        Property Content_Duration = courseGraph.createProperty(dbo + "Content_Duration");
        Property Link = courseGraph.createProperty(dbo + "Link");
        Property noOfLectures = courseGraph.createProperty(dbo + "noOfLectures");
        Property noOfReviews = courseGraph.createProperty(dbo + "noOfReviews");
        Property price = courseGraph.createProperty(dbo + "price");
        Property publishedTime = courseGraph.createProperty(dbo + "publishedTime");
        Property rdfType = courseGraph.createProperty(rdf + "type");
        Property title = courseGraph.createProperty(rdfs + "title");

        Resource Course = courseGraph.createResource(dbo + "Course");
        Resource Subject = courseGraph.createResource(dbo + "Subject");
        Resource DifficultyLevel = courseGraph.createResource(dbo + "DifficultyLevel");

        try {
            Scanner sc = new Scanner(new File(filepath));
            // Skip header line
            if (sc.hasNextLine()) {
                sc.nextLine();
            }

            int linesProcessed = 0; // Counter to keep track of lines processed
            while (sc.hasNextLine() && linesProcessed < 100) {
                String line = sc.nextLine();
                String[] arrStatement = line.split(",");

                // Check if array length is as expected
                if (arrStatement.length >= 12) {
                    Resource course = courseGraph.createResource(db + arrStatement[1].replace(" ", "_"));
                    Resource subject = courseGraph.createResource(db + arrStatement[11].replace(" ", "_"));
                    Resource difficultyLevel = courseGraph.createResource(db + arrStatement[8].replace(" ", "_"));

                    // Object properties
                    course.addProperty(rdfType, Course);
                    subject.addProperty(rdfType, Subject);
                    difficultyLevel.addProperty(rdfType, DifficultyLevel);
                    course.addProperty(belongsTo, subject);
                    course.addProperty(hasLevel, difficultyLevel);

                    // Data properties
                    course.addProperty(title, arrStatement[1]);
                    subject.addProperty(title, arrStatement[11]);
                    difficultyLevel.addProperty(title, arrStatement[8]);
                    course.addProperty(Content_Duration, arrStatement[9] + "hrs");
                    course.addProperty(Link, arrStatement[2]);
                    course.addProperty(noOfLectures, arrStatement[7]);
                    course.addProperty(noOfReviews, arrStatement[6]);
                    course.addProperty(price, arrStatement[4]);
                    course.addProperty(publishedTime, arrStatement[10]);

                    linesProcessed++;
                } else {
                    // Log or handle incomplete data
                    System.err.println("Incomplete data in line: " + line);
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write RDF data to console and file
        courseGraph.write(System.out, "TURTLE");
        try (Writer wr = new FileWriter("D:/MS CSE/Spring2024/Web Semantics/webu/SecondPhase/Turtlefile.ttl")) {
            courseGraph.write(wr, "TURTLE");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print RDF triples to console
        int counter = 0;
        StmtIterator iter = courseGraph.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.next();
            Resource s = stmt.getSubject();
            Property p = stmt.getPredicate();
            RDFNode o = stmt.getObject();

            counter++;

            System.out.print(counter + ".  ");
            System.out.print(s.toString());
            System.out.print(" " + p.toString() + " ");
            if (o instanceof Resource) {
                System.out.print(o.toString());
            } else {
                System.out.print(" \"" + o.toString() + "\"");
            }

            System.out.println(" .");
        }
    }

    public static void main(String[] args) {
        DataConversion csv = new DataConversion();
        csv.graph();
        System.out.println();
    }
}
