import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HuffmanEncoderGUI {
    private JButton CompressBtn, DecompressBtn, ShowContentBtn, OpenBtn, SelectBtn, SaveBtn;
    private JLabel HeadingLabel, SelectFileLabel, SelectCompressedFileLabel;
    private JTextField FilePathField, CompressedFilePathField;
    private JPanel jPanel1;
    private static boolean hasBeenCompressed = false;
    private static boolean fileCompressed = false;
    private String src = new String(), dst = new String();
    private static HuffmanEncoder encoder;
    private static Message msg;


    HuffmanEncoderGUI() {
        JFrame f = new JFrame();
        f.setTitle("Huffman - Text File ");
        f.setSize(600, 400); // Set window size
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new GridBagLayout());
        f.addWindowListener(new HeadingSlider());
        f.setLocationRelativeTo(null); // Center the frame

        initComponents(f); // Pass the JFrame to initComponents for layout
        f.setVisible(true); // Make frame visible after setting up components
    }

    private void initComponents(JFrame f) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // Heading Label
        HeadingLabel = new JLabel("Huffman Encoding Text File");
        HeadingLabel.setFont(new Font("Serif", Font.BOLD, 20));
        HeadingLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        f.add(HeadingLabel, gbc);

        // Panel background
        jPanel1 = new JPanel();
        jPanel1.setBackground(new Color(31, 40, 51));
        f.getContentPane().setBackground(new Color(31, 40, 51));

        // Select file to compress
        SelectFileLabel = new JLabel("Select a File to Compress:");
        SelectFileLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        SelectFileLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        f.add(SelectFileLabel, gbc);

        FilePathField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        f.add(FilePathField, gbc);

        OpenBtn = new JButton("Browse");
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        f.add(OpenBtn, gbc);

        ShowContentBtn = new JButton("Show Content");
        gbc.gridx = 2; gbc.gridy = 2;
        f.add(ShowContentBtn, gbc);

        // Select file to decompress
        SelectCompressedFileLabel = new JLabel("Select a File to Decompress:");
        SelectCompressedFileLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        SelectCompressedFileLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        f.add(SelectCompressedFileLabel, gbc);

        CompressedFilePathField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        f.add(CompressedFilePathField, gbc);

        SelectBtn = new JButton("Select Compressed File");
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1;
        f.add(SelectBtn, gbc);

        // Compress and Decompress buttons
        CompressBtn = new JButton("Compress");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        f.add(CompressBtn, gbc);

        DecompressBtn = new JButton("Decompress");
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 1;
        f.add(DecompressBtn, gbc);

        OpenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 openButtonAction();
            }
        });

        SelectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCompressedFile();
            }
        });

        ShowContentBtn.addActionListener(new ShowAction());

         CompressBtn.addActionListener(new compressButtonActionPeformed());
        DecompressBtn.addActionListener(new DecompressButtonActionPerformed());
        SelectBtn.addActionListener(evt -> selectButtonActionPerformed(evt));
    }

    class compressButtonActionPeformed implements ActionListener{
        public void actionPerformed (ActionEvent e){
        StartCompression();
    }
    }
    class DecompressButtonActionPerformed implements ActionListener{
        public void actionPerformed (ActionEvent e)
        {
            startDecompression();
        }    }
    private void StartCompression() {
        if(dst.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please specify a location to save compressed file");
            return;
        }
        new ProgressBar().setVisible(true);
        HuffCompression.compress(src, dst);
        fileCompressed = true;
    }

    private void startDecompression() {
        String path = CompressedFilePathField.getText();
        if(!path.isEmpty()) {
            File f = new File(path);
            String ext;
            int i;
            for(i = path.length()-1; i >= 0 ; i--) {
                if(path.charAt(i) == '.') break;
            }
            ext = path.substring(i, path.length());
            System.out.println(ext);
            String dir = f.getParent() + "\\decompressed" + ext;
            HuffCompression huffCompression = new HuffCompression();
            huffCompression.decompress(path, dir);
            JOptionPane.showMessageDialog(null, "File has been decompressed and saved at" +
                    " the location: " + dir);
        } else JOptionPane.showMessageDialog(null, "no file has been selected for decompression");
    }
 /*   private void openButtonAction() {
        JFileChooser fchooser = new JFileChooser();
        fchooser.showOpenDialog(null);
        if(fchooser.getSelectedFile() != null) {
            File f0 = fchooser.getSelectedFile();

            String filename = f0.getAbsolutePath();

            String dstDirectory = f0.getParent(); // Get the directory (parent folder) of the file
            JOptionPane.showConfirmDialog(null,dstDirectory);
            String baseName = f0.getName().substring(0, f0.getName().lastIndexOf(".")); // Get file name without extension
            // Create a new file in the same directory with '_compressed' suffix
            File compressedFile = new File(dstDirectory, baseName + "_compressed.txt");
            dst = compressedFile.getAbsolutePath();
            FilePathField.setText(filename);
            if(f0.exists() && filename.substring(filename.length()-3, filename.length()).equals("txt")) {
            //   msg = new Message(convertFileToString(f0));
            }
        }
    }*/
    private void openButtonAction() {
        JFileChooser fchooser = new JFileChooser();
        fchooser.showOpenDialog(null);

        if (fchooser.getSelectedFile() != null) {
            File f0 = fchooser.getSelectedFile();  // Get the selected file
            String filename = f0.getAbsolutePath();  // Get the full file path

            String dstDirectory = f0.getParent();  // Get the directory (parent folder) of the file
            JOptionPane.showMessageDialog(null, "Directory: " + dstDirectory);  // Display the directory path for confirmation

            String baseName = f0.getName().substring(0, f0.getName().lastIndexOf("."));  // Get file name without extension

            // Create a new file in the same directory with '_compressed' suffix
            File compressedFile = new File(dstDirectory, baseName + "_compressed.txt");

            try {
                // Check if the file does not exist and create a new one
                if (compressedFile.createNewFile()) {
                    JOptionPane.showMessageDialog(null, "File created: " + compressedFile.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(null, "File already exists: " + compressedFile.getAbsolutePath());
                }
                dst = compressedFile.getAbsolutePath();
                FilePathField.setText(filename);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (f0.exists() && filename.endsWith(".txt")) {
                // msg = new Message(convertFileToString(f0)); // Handle .txt file if needed
            }
        }
    }

    private void openCompressedFile(){
        JFileChooser fchooser = new JFileChooser();
        fchooser.showOpenDialog(null);
        if(fchooser.getSelectedFile() != null)
        {
            File temp=fchooser.getSelectedFile();
            String fName=temp.getAbsolutePath();
            CompressedFilePathField.setText(fName);

        }
    }
    private void ShowActionFunction(){
        if(!FilePathField.getText().isEmpty()) {
            try {
                String pathfile = FilePathField.getText();
                File f = new File(pathfile);
                if(f.exists()) {
                    BufferedReader bufReader = new BufferedReader(new FileReader(f));
                    String str = new String();
                    Displayer area = new Displayer();
                    while((str = bufReader.readLine()) != null) {
                        area.displayArea.append(str + '\n');
                    }
                }
            } catch(IOException io) {
                JOptionPane.showMessageDialog(null, "Invalid File", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please fill the text field", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showContentButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Action for Show Content button
    }



    private void decompressButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Action for Decompress button
    }

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Action for Select Compressed File button
    }

    class HeadingSlider extends WindowAdapter {
        public void windowOpened(WindowEvent e) {
            ThreadedText T1 = new ThreadedText("Huffman Encoding Text File", HeadingLabel);
            T1.t.start();
        }
    }
    class ShowAction implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            ShowActionFunction();
        }
    }


    private static class HuffCode extends JFrame {
        JTable codes;
        JScrollPane scrlPane;
        String[] headings = {"Character", "Frequency", "Bit Sequence"};

        HuffCode() {
            setTable();
            setSize(600, 550);
            setTitle("Huff Code");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
        }

        public void setTable() {
            CharLinkedList charset = encoder.get_charset();
            String[][] records = new String[charset.size()][headings.length];
            CharNode node = charset.getLink();
            for(int i = 0; i < charset.size(); i++) {
                if(node != null) {
                    records[i][0] = node.ch + "";
                    records[i][1] = Integer.toString(node.frequency);
                    records[i][2] = node.bit_size;
                }
                node = node.next;
            }
            codes = new JTable(records, headings);
            codes.setEnabled(false);
            scrlPane = new JScrollPane(codes);
            add(scrlPane);
        }
    }

    private static class OrgCode extends JFrame {
        JTable codes;
        JScrollPane scrlPane;
        String[] headings = {"Character", "Frequency", "Bit Sequence"};

        OrgCode() {
            setTable();
            setSize(600, 550);
            setTitle("Original Code");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
        }

        public void setTable() {
            String[][] records = new String[msg.getCharacters().length][headings.length];
            char[] chars = msg.getCharacters();
            for(int i = 0; i < chars.length; i++) {
                records[i][0] = chars[i] + "";
                records[i][1] = Integer.toString(msg.calcFrequencyOfChar(chars[i]));
                records[i][2] = msg.convertBinary(i);
            }
            codes = new JTable(records, headings);
            codes.setEnabled(false);
            scrlPane = new JScrollPane(codes);
            add(scrlPane);
        }
    }

   /* private boolean ensureValidityOfPath() {
       String path = jTextField2.getText();
       return !path.isEmpty() && new File(path).exists();
    }*/

    private String convertFileToString(File f0) {
        String str, contents = "";
        boolean first = true;
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(f0));
            while((str = bufReader.readLine()) != null) {
                if(!first) contents += '\n';
                contents += str;
                first = false;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return contents;
    }

  /*  private void showBytesActionPerformed(ActionEvent evt) {
        if(!jTextField2.getText().isEmpty()) {
            File f = new File(jTextField2.getText());
            JOptionPane.showMessageDialog(null, "Current Size: " + (f.length() * 0.001) + " KB");
        } else {
            JOptionPane.showMessageDialog(null, "No file has been selected");
        }
    }

    private void compBytesActionPerformed(ActionEvent evt) {
        if(!jTextField3.getText().isEmpty() && fileCompressed) {
            File f = new File(jTextField3.getText());
            JOptionPane.showMessageDialog(null, "Compressed Size: " + (f.length() * 0.001) + " KB");
        } else {
            JOptionPane.showMessageDialog(null, "No file has been selected");
        }
    }

    private void compressBitsActionPerformed(ActionEvent evt) {
        if(!ensureValidityOfPath())
            JOptionPane.showMessageDialog(null, "Invalid Path", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        else {
            File f0 = new File(jTextField2.getText());
            String contents = convertFileToString(f0);
            encoder = new HuffmanEncoder(new Message(contents));
            encoder.compress();
            hasBeenCompressed = true;
            JOptionPane.showMessageDialog(null, "Compression done succesfully");
        }
    }
*/

    public static void main(String[] args) {
        new HuffmanEncoderGUI();
    }
}
