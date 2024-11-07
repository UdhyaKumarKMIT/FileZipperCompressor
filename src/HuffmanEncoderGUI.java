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
    private JButton CompressBtn, DecompressBtn, ShowContentBtn, SelectFileBtn,SelectCompressedFileBtn, SaveBtn;
    private JLabel HeadingLabel, SelectFileLabel, SelectCompressedFileLabel;
    private JTextField FilePathField, CompressedFilePathField;
    private JPanel jPanel1;
    private static boolean hasBeenCompressed = false;
    private static boolean fileCompressed = false;
    private String src = new String(), dst = new String();
    private String srcFile=new String(),destFile=new String();
    private static HuffmanEncoder encoder;
    private static Message msg;
    private String log;

    private JButton compressionRatio;
    HuffmanEncoderGUI() {
        JFrame f = new JFrame();
        f.setTitle("Huffman - Text File ");
        f.setSize(600, 400); // Set window size
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new GridBagLayout());
        f.addWindowListener(new HeadingSlider());
        f.setLocationRelativeTo(null); // Center the frame

        initComponents(f);
        f.setVisible(true); // Make frame visible after setting up components
    }

    private void initComponents(JFrame f) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // Heading Label
        HeadingLabel = new JLabel("");
        HeadingLabel.setFont(new Font("Serif", Font.BOLD, 20));
        HeadingLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        f.add(HeadingLabel, gbc);


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

        SelectFileBtn = new JButton("Browse");
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        f.add(SelectFileBtn, gbc);

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

        SelectCompressedFileBtn = new JButton("Select Compressed File");
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1;
        f.add(SelectCompressedFileBtn, gbc);


        CompressBtn = new JButton("Compress");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        f.add(CompressBtn, gbc);

        DecompressBtn = new JButton("Decompress");
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 1;
        f.add(DecompressBtn, gbc);

        compressionRatio = new JButton("Compression Ratio ");
        gbc.gridx=0; gbc.gridy=6; gbc.gridwidth=1;
        f.add(compressionRatio,gbc);



        SelectFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 openButtonAction();
            }
        });

        SelectCompressedFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCompressedFile();
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
        });

        ShowContentBtn.addActionListener(new ShowAction());
         CompressBtn.addActionListener(new compressButtonActionPeformed());
        DecompressBtn.addActionListener(new DecompressButtonActionPerformed());
        compressionRatio.addActionListener(new showPrevCompressionDetails());
   //     SelectFileBtn.addActionListener(evt -> selectButtonActionPerformed(evt));
    }
class showPrevCompressionDetails implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        if (hasBeenCompressed || fileCompressed) {
            File dest= new File(dst);
            File srctemp=new File(src);
            log = "<html>👍 Compression Successfully Done!<br><br>"
                    + "Source File: " + srcFile + " -- " + (srctemp.length() * 0.001) + " KB" + " <br>"
                    + "Destination File: " + destFile + " -- " + (dest.length() * 0.001) + " KB" + "</html>";

            JOptionPane.showMessageDialog(null, log, "Compression Status", JOptionPane.INFORMATION_MESSAGE);

        }
        else {
            JOptionPane.showMessageDialog(null,"No file has been Compressed Yet!","Compression Status",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
    class compressButtonActionPeformed implements ActionListener{
        public void actionPerformed (ActionEvent e){
                 StartCompression();
         }
        private void StartCompression() {
            JOptionPane.showMessageDialog(null,dst);
            if(dst.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please specify a location to save compressed file");
                return;
            }
            new ProgressBar().setVisible(true);
            HuffCompression.compress(src, dst);
            fileCompressed = true;
            File dest= new File(dst);
            File srctemp=new File(src);

            String srcFile=new File(src).getName();
            String destFile=new File(dst).getName();

            log = "<html>👍 Compression Successfully Done!<br><br>"
                    + "Source File: " + srcFile + " -- " + (srctemp.length() * 0.001) + " KB" + " <br>"
                    + "Destination File: " + destFile + " -- " + (dest.length() * 0.001) + " KB" + "</html>";

            JOptionPane.showMessageDialog(null, log, "Compression Status", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    class DecompressButtonActionPerformed implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            startDecompression();
        }
        private void startDecompression() {
            String path = CompressedFilePathField.getText();
            if (!path.isEmpty()) {
                File f = new File(path);
                String ext;
                int i;
                for (i = path.length() - 1; i >= 0; i--) {
                    if (path.charAt(i) == '.') break;
                }
                ext = path.substring(i, path.length());
                System.out.println(ext);
                String dir = f.getParent() + "\\decompressed" + ext;
                HuffCompression huffCompression = new HuffCompression();
                huffCompression.decompress(path, dir);
                JOptionPane.showMessageDialog(null, "File has been decompressed and saved at" +
                        " the location: " + dir);
            } else JOptionPane.showMessageDialog(null, "No file has been selected for decompression");
        }
    }
    private void openButtonAction() {
        JFileChooser fchooser = new JFileChooser();
        fchooser.showOpenDialog(null);

        if (fchooser.getSelectedFile() != null) {
            File f0 = fchooser.getSelectedFile();
            String filename = f0.getAbsolutePath();
            src=f0.getAbsolutePath();

            String dstDirectory = f0.getParent();

            String baseName = f0.getName().substring(0, f0.getName().lastIndexOf("."));  // Get file name without extension

            File compressedFile = new File(dstDirectory, baseName + "_compressed.txt");

            try {
                if (compressedFile.createNewFile()) {
                 //   JOptionPane.showMessageDialog(null, "File created: " + compressedFile.getAbsolutePath());
                } else {
                    //JOptionPane.showMessageDialog(null, "File already exists: " + compressedFile.getAbsolutePath());
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




    class HeadingSlider extends WindowAdapter {
        public void windowOpened(WindowEvent e) {
            ThreadedText T1 = new ThreadedText("Huffman Encoding Text File", HeadingLabel);
            T1.t.start();
        }
    }
    public static String convertSizeToReadableFormat(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " bytes";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.2f KB", sizeInBytes / 1024.0);
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", sizeInBytes / (1024.0 * 1024 * 1024));
        }
    }

    class ShowAction implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            ShowActionFunction();
        }

        private void ShowActionFunction() {
            if (!FilePathField.getText().isEmpty()) {
                String pathfile = FilePathField.getText();
                File file = new File(pathfile);

                if (file.exists()) {
                    try (BufferedReader bufReader = new BufferedReader(new FileReader(file))) {
                        StringBuilder content = new StringBuilder();
                        String line;

                        while ((line = bufReader.readLine()) != null) {
                            content.append(line).append('\n');
                        }

                        Displayer area = new Displayer();
                        area.displayArea.setText(content.toString()); // Set all text at once
                        area.displayArea.setCaretPosition(0); // Move caret to the beginning
                    } catch (IOException io) {
                        JOptionPane.showMessageDialog(null, "Invalid File", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "File Not Found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "File Not Selected. Please Select a File.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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


    public static void main(String[] args) {
        new HuffmanEncoderGUI();
    }
}
