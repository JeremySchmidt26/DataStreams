import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsGUI extends JFrame
{
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;

    private Path currentFile;

    public DataStreamsGUI()
    {
        setTitle("Data Streams Search");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildGUI();

        setVisible(true);
    }

    private void buildGUI()
    {
        JPanel topPanel = new JPanel();

        searchField = new JTextField(20);

        JButton loadButton = new JButton("Load File");
        JButton searchButton = new JButton("Search");
        JButton quitButton = new JButton("Quit");

        topPanel.add(new JLabel("Search String:"));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);

        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();

        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane leftScrollPane = new JScrollPane(originalTextArea);
        JScrollPane rightScrollPane = new JScrollPane(filteredTextArea);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftScrollPane,
                rightScrollPane);

        splitPane.setDividerLocation(450);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private void loadFile()
    {
        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if(result == JFileChooser.APPROVE_OPTION)
        {
            currentFile = chooser.getSelectedFile().toPath();

            try
            {
                List<String> lines = Files.readAllLines(currentFile);

                originalTextArea.setText("");

                for(String line : lines)
                {
                    originalTextArea.append(line + "\n");
                }

                filteredTextArea.setText("");
            }
            catch(IOException ex)
            {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to load file.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchFile()
    {
        if(currentFile == null)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Load a file first.");
            return;
        }

        String searchText = searchField.getText().trim();

        if(searchText.isEmpty())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a search string.");
            return;
        }

        try(Stream<String> lines = Files.lines(currentFile))
        {
            List<String> results =
                    lines.filter(line ->
                                    line.toLowerCase()
                                            .contains(searchText.toLowerCase()))
                            .collect(Collectors.toList());

            filteredTextArea.setText("");

            for(String line : results)
            {
                filteredTextArea.append(line + "\n");
            }
        }
        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Search failed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(DataStreamsGUI::new);
    }
}