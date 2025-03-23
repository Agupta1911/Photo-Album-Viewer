//A  Java program that simulates a system that supports the functions of an photo album viewer.

import javax.swing.*;
import java.awt.*;
//import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

//Sets up the user interface panel, contains methods for loading and displaying the images, along with the implementation of the four buttons as well as a shuffle method.
public class PhotoAlbumViewer extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(ImageDisplayStarter.class.getName());
    private static List<File> imageFiles;
    private static JLabel imageLabel;
    private static Node current;
    private static JLabel statusLabel;
    private static LinkedList images;

    private int count = 0;
    
    //Sets up the user interface
    public ImageDisplayStarter(String directoryPath) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Image Display");
        setLayout(new BorderLayout());

        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton button3 = new JButton("First");
        JButton button4 = new JButton("Last");
        
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(button3);
        buttonPanel.add(button4);
        
        add(buttonPanel, BorderLayout.SOUTH);

        statusLabel = new JLabel("Status: ");
        add(statusLabel, BorderLayout.NORTH);

        prevButton.addActionListener(e -> showPreviousImage());
        nextButton.addActionListener(e -> showNextImage());
        button3.addActionListener(e -> showFirstImage());
        button4.addActionListener(e -> showLastImage());

        images = new LinkedList();
        loadImagesFromDirectory(directoryPath);
        if (images.head!=null) {
            current = images.getHead();
            displayImage(current);
        } else {
            statusLabel.setText("Folder is empty");
        }

        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    //the shuffle function
    private void shuffle(File[] array) {
    Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
        int index = random.nextInt(i + 1);
        File temp = array[index];
        array[index] = array[i];
        array[i] = temp;
        }
    }
    //loads the images
    private void loadImagesFromDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            // Convey error message to the viewer 
            statusLabel.setText("No directory found");
            return;
        }

        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".gif") || lowercaseName.endsWith(".png");
            }
        });

        if (files == null || files.length == 0) {
            // Inform the user that no files were found in the specified folder 
            statusLabel.setText("No files were found in the specified folder, or no files with suitable formats were found");
            imageFiles = new ArrayList<>();
        } else {
            // This should be replaced with your own method that iterates over the files in the folder and 
            // adds each image to your Linked List
            shuffle(files);
            imageFiles = new ArrayList<>(Arrays.asList(files)); 
            for (File file: files){
                try{
                String name = file.getName();
                String path = file.getCanonicalPath();
                long size = file.length();
                images.addNode(count, name, path, size);
                count++;
                }catch(IOException e){
                    System.out.println("IOException");
                }
            


            }
    
            LOGGER.log(Level.INFO, "Found {0} image files in directory: {1}", new Object[]{files.length, directoryPath});
            statusLabel.setText("Status: Found " + files.length + " image(s).");
        }
    }

    //Displays the images
    private static void displayImage(Node node) {
        if (node != null) {
            try {
                File imageFile = new File (node.getPath());
                Image temp = ImageIO.read(imageFile);
                Image image = temp.getScaledInstance(700, 500, Image.SCALE_SMOOTH);
                if (image != null) {
                    imageLabel.setIcon(new ImageIcon(image));
                    statusLabel.setText("Status: Displaying image " + (node.getIndex()+1) + " of " + imageFiles.size());
                } else {
                    LOGGER.log(Level.WARNING, "Failed to read image file: {0}", node.getPath());
                    statusLabel.setText("Status: Failed to read image file.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading image", e);
                statusLabel.setText("Status: Error loading image - " + e.getMessage());
            }
        } else {
            LOGGER.log(Level.WARNING, "Invalid image index: {0}");
            statusLabel.setText("Status: No image to display.");
        }
    }
    //shows the previous image
    private static void showPreviousImage() {
        System.out.println("Previous button clicked!");
        if (current!= null && current.prev != null) {
        current= current.prev;
        displayImage(current);
        }
    }
    // shows the next image
    private static void showNextImage() {
        System.out.println("Next button clicked!");
        if (current!= null && current.next != null) {
            current = current.next;
            displayImage(current);
        } else {
            statusLabel.setText("No Next image.");
        }
        }
    //shows first image
    private static void showFirstImage() {
        System.out.println("First button clicked!");
       current = images.getHead();
        displayImage(current);
    }
    //shows last image
    private static void showLastImage() {
        System.out.println("Last button clicked!");
       current = images.getEnd();
        displayImage(current);
    }


    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            if (args.length < 1) {
                System.out.println("Please input path to folder containing images");
                return;
            }
            String directoryPath = args[0];
            ImageDisplayStarter display = new ImageDisplayStarter(directoryPath);
            display.setVisible(true);
        });
    }

class Node {
    int data;
    Node next;
    Node prev;
    private String name;
    private String path;
    private long size;
    //initializes the variables
    public Node(String name, String path, long size, int imageIndex) {
        this.data = imageIndex;
        this.next = null;
        this.prev = null;
        this.name = name;
        this.path = path;
        this.size = size;
    }
    //the following methods in the class return the specified data
    public int getIndex() {
        return data;
    }
    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }
}



    public class LinkedList {
    public Node head=null;
    public Node end;

    public static int length=0;
    //adds a node
    public void addNode(int data,String name, String path, long size){

        Node newNode = new Node(name, path, size, data); 

        if(head == null){
            head = newNode; 
            end = newNode;
            System.out.println("In head - Created a node with data");
            length++;
            return; 
        }

        Node current = head; // i = 0 
        while(current.next!=null){
            current = current.next; // i = i + 1 
            System.out.println("Navigating to the end of the list!");
        }
        current.next = newNode;
        newNode.prev = current;
        end = newNode;
        length++;
    }
    //returns the first node
     public Node getHead() {
            return head;
        }
    //returns the last node.
    public Node getEnd() {
            return end;
        }
    }

}
