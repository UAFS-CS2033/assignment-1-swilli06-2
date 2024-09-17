import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class Server{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int portNo;

    public Server(int portNo){
        this.portNo=portNo;
    }

    private void processConnection() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //look at the file read in, and see if it exists
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

        //*** Application Protocol *****
        String buffer = in.readLine();
        while(buffer != null && buffer.length()!=0){
            System.out.println(buffer);
            buffer = in.readLine();
        }

        File file = new File("/docroot/home.html"); //Reads the html
        if (file.exists()) {
            String content = new String(Files.readAllBytes(file.toPath()));


            out.printf("HTTP/1.1 200 OK\n");
            out.printf("Content-Length: %d\n", content.length()); //Takes in the exact length of the content
            out.printf("Content-Type: text/html\n\n");
            out.print(content);
        } else {
            out.printf("HTTP/1.1 404 Not Found\n");
            out.printf("Content-Length: 0\n");
            out.printf("Content-Type: text/html\n\n");
        }


        in.close();
        out.close();
    }
    //need to check http://localhost:8080/home.html to see if everything is pulled over
    
    public void run() throws IOException{
        boolean running = true;
       
        serverSocket = new ServerSocket(portNo);
        System.out.printf("Listen on Port: %d\n",portNo);
        while(running){
            clientSocket = serverSocket.accept();
            //** Application Protocol
            processConnection();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static void main(String[] args0) throws IOException{
        Server server = new Server(8080);
        server.run();
    }
    
}
