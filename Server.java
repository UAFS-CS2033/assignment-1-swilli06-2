import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
        OutputStream output = clientSocket.getOutputStream();

        //*** Application Protocol *****
        String buffer = in.readLine();
        System.out.println("Buffer: " + buffer);
        String[] tokens = buffer.split(" ");
        String fileName = tokens[1];

        if(fileName.equals("/")){
            fileName = "/home.html"; //Uses the home.html as the base!
        }

        File file = new File("docroot" + fileName); //Reads the files
        System.out.println(file.toString());
        System.out.println(file.exists());
        if (file.exists()) {
            
            out.printf("HTTP/1.1 200 OK\n");
            out.printf("Content-Length: %d\n", file.length()); //Takes in the exact length of the content
            out.printf("Content-Type: %s\n\n", returnContentType(file.getName()));
            
        } else {
            out.printf("HTTP/1.1 404 Not Found\n");
            out.printf("Content-Length: 0\n");
            out.printf("Content-Type: text/html\n\n");
        }

        FileInputStream inStream = new FileInputStream(file);
        int line = 0;
        while((line = inStream.read()) != -1){
            output.write(line);
        }


        in.close();
        out.close();
    }
    //need to check http://localhost:8080/home.html to see if everything is pulled over

    public String returnContentType(String file){
        if(file.endsWith(".png")){
            return "image/png";
        }
        if(file.endsWith(".css")){
            return "text/css";
        }
        if(file.endsWith(".html")){
            return "text/html";
        }
        return null;
    }
    
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
