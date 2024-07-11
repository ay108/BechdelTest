import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;
import javafoundations.*;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.*;

public class HollywoodGraph extends AdjListsGraph<String> {
    private Vector<LinkedList<String>> edges; // Separate storage for edges
    private Vector<String> vertices;
    private String fileName;
    private LinkedList<String> movies;
    private LinkedList<String> actors;
    private Vector<Integer> passedResults;

    public HollywoodGraph(String fileName) {
        super();
        this.edges = new Vector<>();
        this.vertices = new Vector<>();
        this.fileName = fileName;
        movies = new LinkedList<String>();
        actors = new LinkedList<String>();
        passedResults = new Vector<Integer>();
        readFromFile(); // Automatically read from file upon object creation
    }

    /**
     * Reads from a file, adding the actors and actors from the file into their respective data structures
     */
    public void readFromFile() {
       
        try {
            Scanner scan = new Scanner(new File(fileName));
            scan.nextLine(); 
            while (scan.hasNext()) {
                String line = scan.nextLine();

                if (line.length() == 0) {
                    continue;
                }

                String[] s = line.split(",");
                String movie = s[0].trim().replaceAll("\"", "");
                String actor = s[1].trim().replaceAll("\"", "");

                // Add the movie and actor vertices if not already added
                addVertex(movie);
                addVertex(actor);

                addVertex(movies, movie);//the other addVertex method
                actors.add(actor); //adds actors 

                // Add edges between the movie and actor
                addEdge(movie, actor);
            }
            scan.close(); // Close the scanner
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }catch (NoSuchElementException e){
            System.out.println(e);
        }
    }

    public void addVertex(String vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
            edges.add(new LinkedList<String>()); // Add empty list for edges
        }
    }

    public void addVertex(LinkedList linked , String vertex){
        //New addVertex for movies linkedlist
        if (!linked.contains(vertex)) {
            linked.add(vertex);
        }
    }

    public void addEdge(String vertex1, String vertex2) {
        if (!vertices.contains(vertex1)) {
            addVertex(vertex1);
        }
        if (!vertices.contains(vertex2)) {
            addVertex(vertex2);
        }
        int index1 = vertices.indexOf(vertex1);
        int index2 = vertices.indexOf(vertex2);

        edges.get(index1).add(vertex2);
        edges.get(index2).add(vertex1);
    }

    public String toString() {
        String result = "Vertices:\n";
        String actorVertices = "Actors: ";
        String movieVertices = "Movies: ";

        for (int i = 0; i < vertices.size(); i++) {
            String vertex = vertices.get(i);
            if (isActor(vertex)) {
                actorVertices += vertex + ", ";
            } else {
                movieVertices += vertex + ", ";
            }
        }
        actorVertices = actorVertices.substring(0, actorVertices.length() - 2);
        movieVertices = movieVertices.substring(0, movieVertices.length() - 2);

        result += actorVertices + "\n" + movieVertices + "\n\nEdges:\n";
        for (int i = 0; i < vertices.size(); i++) {
            String vertex = vertices.get(i);
            if (!isActor(vertex)) {
                result += "from " + vertex + ": " + edges.get(i) + "\n";
            }
        }

        return result;
    }   

    private boolean isActor(String vertex) {
        return actors.contains(vertex);
    }

    public int moviesSeparatingActors(String actor1, String actor2) {
        // Check if the actors exist in the graph
        if (!vertices.contains(actor1) || !vertices.contains(actor2)) {
            System.out.println("One or both actors not found in the graph.");
            return -1; 
        }

        // Find the indices of the actors in the vertices list
        int index1 = vertices.indexOf(actor1);
        int index2 = vertices.indexOf(actor2);

        // Use BFS to find the shortest path between the two actors
        LinkedList<String> queue = new LinkedList<>();
        boolean[] visited = new boolean[vertices.size()];
        int[] distance = new int[vertices.size()];

        // Initialize arrays
        for (int i = 0; i < vertices.size(); i++) {
            visited[i] = false;
            distance[i] = -1;
        }

        // Enqueue actor1 and mark it as visited
        queue.add(actor1);
        visited[index1] = true;
        distance[index1] = 0;

        while (!queue.isEmpty()) {
            String currentActor = queue.getFirst(); // Remove the front element of the queue
            int currentIndex = vertices.indexOf(currentActor);
            //System.out.println("Exploring actor: " + currentActor + ", Distance: " + distance[currentIndex]);

            // Explore (movies) of the current actor
            for (String movie : edges.get(currentIndex)) {
                // Find adjacent actors in the movie
                for (String adjacentActor : edges.get(vertices.indexOf(movie))) {
                    int adjacentIndex = vertices.indexOf(adjacentActor);

                    if (!visited[adjacentIndex]) {
                        visited[adjacentIndex] = true;
                        distance[adjacentIndex] = distance[currentIndex] + 1; // Increment the distance
                        queue.add(adjacentActor);
                        // System.out.println("Adjacent actor: " + adjacentActor + ", Distance: " + (distance[currentIndex] + 1));

                        // If we reach actor2, return the distance
                        if (adjacentActor.equals(actor2)) {
                            return distance[adjacentIndex]-1;
                        }
                    }
                }
            }
        }

        // If no path is found between the actors
        System.out.println("No path found between " + actor1 + " and " + actor2);
        return -1;
    }

    public LinkedList<String> moviesByActor(String actorName) {
        LinkedList<String> moviesPlayed = new LinkedList<>();
        int actorIndex = vertices.indexOf(actorName);

        if (actorIndex != -1) {
            LinkedList<String> actorEdges = edges.get(actorIndex);
            moviesPlayed.addAll(actorEdges);
        } else {
            System.out.println("No movies found for " + actorName);
        }

        return moviesPlayed;
    }

    public List<String> actorsFromMovie(String movieName){
        List<String> actors = new ArrayList<>();
        try{
            Scanner scan = new Scanner(new File(fileName));
            scan.nextLine();
            while (scan.hasNext()){
                String line = scan.nextLine();
                String[] s = line.split(",");
                String movies = s[0].trim().replaceAll("\"", "");
                if(movies.equals(movieName)){
                    actors.add(s[1].trim());
                }
            }
            scan.close();
        }catch(IOException e){
            System.out.println(e);
        }
        return actors;
    }

    private Vector<Integer> numOfActsPerMovie(){ //helper method
        Vector<Integer> lengthsOfMovies = new Vector<Integer>();
        for(String movie : movies){
            List<String> m = (actorsFromMovie(movie));
            int size = m.size(); 
            lengthsOfMovies.add(size);
        }
        return lengthsOfMovies;
    }

    /**
     * a new movie test, passes if at least half leading roles are women
     * 
     */
    public void FAMEcalc() {
        try {
            Vector<Integer> lengths = numOfActsPerMovie();
            Scanner scan = new Scanner(new File(fileName));
            scan.nextLine();
            for (int i = 0; i < lengths.size(); i++) {
                int steps = lengths.get(i);
                int womenCount = 0;
                int count = 0;
                int passed = 0;
                for (int j = 0; j < steps; j++) {
                    String line = scan.nextLine();
                    if (line.length() == 0) {
                        continue;
                    }

                    String[] s = line.split(",");
                    String movie = s[0];
                    String characterType = s[3];
                    String gender = s[5];
                    if (characterType.contains("Lead") && gender.contains("F")) {
                        womenCount++;
                    }
                    if (characterType.contains("Lead")) {
                        count++;
                    }
                    
                }
                if (count > 0 && (double) womenCount / count >= 0.5) {
                    passed = 1;
                }
                passedResults.add(passed);            
            }
            scan.close();
            System.out.println(movies);
            System.out.println(passedResults);
            writeFAMEToFile("BechdelTesting.txt");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Writes our results of the movie test FAME to a file 
     * @param file to be written to (output)
     */
    public void writeFAMEToFile(String output){
        try{
            PrintWriter writer  = new PrintWriter(new File(output));
            writer.println("Movie, FAME Results");
            int i = 0;
            for (int j = 0; j< movies.size(); j++) {
                String movie = movies.get(j);
                int result = passedResults.get(i);
                writer.print(movie);
                writer.print(", "+result);
                writer.println();
                i++;
            }
            writer.close();
        }catch(IOException e ){
            System.out.println(e);
        }
    }
    
    /**
     * Writes our file in the form of a TGF file
     * @param fileName to be reading from
     */
       public void saveAsTGF(String fileName) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(fileName + ".tgf"));

            // Write vertices
            for (String vertex : vertices) {
                writer.println(vertices.indexOf(vertex) + 1 + " " + vertex);
            }
            writer.println("#"); // Indicate the end of vertices and start of edges

            // Write edges
            for (int i = 0; i < edges.size(); i++) {
                LinkedList<String> edgeList = edges.get(i);
                for (String edge : edgeList) {
                    int sourceIndex = i + 1;
                    int targetIndex = vertices.indexOf(edge) + 1;
                    writer.println(sourceIndex + " " + targetIndex);
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        HollywoodGraph graph1 = new HollywoodGraph("small_castGender.txt");
        System.out.println(graph1);
        HollywoodGraph graph2 = new HollywoodGraph ("nextBechdel_castGender.txt");
        System.out.println(graph2);

        graph1.saveAsTGF("nextBechdel_castGender.tgf");
        graph2.saveAsTGF("nextBechdel_castGender2.tgf");

        System.out.println(graph1.actorsFromMovie("The Jungle Book"));
        System.out.println("Actors in the movie 'The Jungle Book': ");

        //String actorName = "Jennifer Lawrence";
        //LinkedList<String> movies = graph2.moviesByActor(actorName);
       // System.out.println("Movies played by " + actorName + ": " + movies);

        int separation1 = graph2.moviesSeparatingActors("Megan Fox", "Tyler Perry");
        System.out.println("Separation between Megan Fox and Tyler Perry: " + separation1);

        int separation2 = graph2.moviesSeparatingActors("Tyler Perry","Nick Arapoglou");
        System.out.println("Separation between Nick Arapoglou and Tyler Perry: " + separation2);
        
        graph2.FAMEcalc();
    }
}