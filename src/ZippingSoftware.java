import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class ZippingSoftware extends JFrame {
    private JTextField folderPathTextField, zipFilePathTextField;
    private JButton selectFolderButton, zipButton, selectZipFileButton, unzipButton;
    private JLabel statusLabel;
    private String selectedFolderPath = "";
    private String selectedZipFilePath = "";

    public ZippingSoftware() {
        setTitle("Folder Zipping and Unzipping Software");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(null); // Using null layout for custom positioning

        // Folder path label
        JLabel folderPathLabel = new JLabel("Folder to Zip:");
        folderPathLabel.setBounds(20, 20, 100, 30);
        add(folderPathLabel);

        // Folder path text field
        folderPathTextField = new JTextField();
        folderPathTextField.setBounds(120, 20, 200, 30);
        folderPathTextField.setEditable(false); // Not editable directly
        add(folderPathTextField);

        // Select folder button
        selectFolderButton = new JButton("Select Folder");
        selectFolderButton.setBounds(120, 60, 120, 30);
        selectFolderButton.addActionListener(new SelectFolderButtonListener());
        add(selectFolderButton);

        // Zip button
        zipButton = new JButton("Zip Folder");
        zipButton.setBounds(250, 60, 100, 30);
        zipButton.addActionListener(new ZipButtonListener());
        add(zipButton);

        // Zip file path label
        JLabel zipFilePathLabel = new JLabel("ZIP File to Unzip:");
        zipFilePathLabel.setBounds(20, 120, 120, 30);
        add(zipFilePathLabel);

        // Zip file path text field
        zipFilePathTextField = new JTextField();
        zipFilePathTextField.setBounds(140, 120, 200, 30);
        zipFilePathTextField.setEditable(false); // Not editable directly
        add(zipFilePathTextField);

        // Select ZIP file button
        selectZipFileButton = new JButton("Select ZIP File");
        selectZipFileButton.setBounds(140, 160, 120, 30);
        selectZipFileButton.addActionListener(new SelectZipFileButtonListener());
        add(selectZipFileButton);

        // Unzip button
        unzipButton = new JButton("Unzip File");
        unzipButton.setBounds(270, 160, 100, 30);
        unzipButton.addActionListener(new UnzipButtonListener());
        add(unzipButton);

        // Status label
        statusLabel = new JLabel("Status: Waiting for folder selection...");
        statusLabel.setBounds(20, 210, 400, 30);
        add(statusLabel);
    }

    // Action listener for selecting a folder for zipping
    private class SelectFolderButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Select only directories
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = fileChooser.getSelectedFile();
                selectedFolderPath = selectedFolder.getAbsolutePath();
                folderPathTextField.setText(selectedFolderPath);
                statusLabel.setText("Status: Folder selected for zipping.");
            } else {
                statusLabel.setText("Status: No folder selected.");
            }
        }
    }

    // Action listener for selecting a ZIP file for unzipping
    private class SelectZipFileButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Select only files
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("ZIP files", "zip"));
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedZipFile = fileChooser.getSelectedFile();
                selectedZipFilePath = selectedZipFile.getAbsolutePath();
                zipFilePathTextField.setText(selectedZipFilePath);
                statusLabel.setText("Status: ZIP file selected for unzipping.");
            } else {
                statusLabel.setText("Status: No ZIP file selected.");
            }
        }
    }

    // Action listener for zipping the selected folder
    private class ZipButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedFolderPath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a folder first!");
                return;
            }

            String zipFileName = selectedFolderPath + ".zip";

            try {
                zipFolder(selectedFolderPath, zipFileName);
                statusLabel.setText("Status: Folder zipped successfully!");
                JOptionPane.showMessageDialog(null, "Folder zipped successfully!");
            } catch (IOException ioException) {
                statusLabel.setText("Status: Error occurred while zipping!");
                JOptionPane.showMessageDialog(null, "Error: " + ioException.getMessage());
            }
        }
    }

    // Action listener for unzipping the selected ZIP file
    private class UnzipButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedZipFilePath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a ZIP file first!");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Select only directories
            int result = fileChooser.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File destinationFolder = fileChooser.getSelectedFile();
                try {
                    unzipFile(selectedZipFilePath, destinationFolder.getAbsolutePath());
                    statusLabel.setText("Status: ZIP file unzipped successfully!");
                    JOptionPane.showMessageDialog(null, "ZIP file unzipped successfully!");
                } catch (IOException ioException) {
                    statusLabel.setText("Status: Error occurred while unzipping!");
                    JOptionPane.showMessageDialog(null, "Error: " + ioException.getMessage());
                }
            }
        }
    }

    // Method to zip the selected folder
    private void zipFolder(String sourceDirPath, String zipFilePath) throws IOException {
        Path zipFile = Files.createFile(Paths.get(zipFilePath));

        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            Path sourceDir = Paths.get(sourceDirPath);
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zipOut.putNextEntry(zipEntry);
                            Files.copy(path, zipOut);
                            zipOut.closeEntry();
                        } catch (IOException e) {
                            System.err.println("Error while zipping: " + e);
                        }
                    });
        }
    }

    // Method to unzip the selected ZIP file
    private void unzipFile(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                String filePath = destDir + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipIn, filePath);
                } else {
                    File dirEntry = new File(filePath);
                    dirEntry.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    // Method to extract individual file
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ZippingSoftware zippingSoftware = new ZippingSoftware();
            zippingSoftware.setVisible(true);
        });
    }
}
