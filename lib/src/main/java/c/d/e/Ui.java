package c.d.e;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.SOUTHEAST;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static java.awt.FlowLayout.RIGHT;


public class Ui extends JFrame {

	private static final long serialVersionUID = -6837779234457967793L;

	private static final Insets INSETS = new Insets(5, 5, 0, 5);

    private JTextField tfInput = new JTextField(30);
    private JTextField tfOutput = new JTextField(30);
    private JTextField tfKey = new JTextField();
    private JLabel imageLabel;
    private JTextArea taMessage = new JTextArea(4, 30);

    public Ui() {
        super("pngde");
    }

    void init() throws Exception {

		setLookAndFeel(getSystemLookAndFeelClassName());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // Input image path
        JButton inputBrowse = new JButton("Browse...");
        add(new JLabel("Input:"), gbc(0, 0, 1, 1, 0, 0, WEST, NONE));
        add(tfInput,              gbc(1, 0, 1, 1, 1.0, 0, WEST, HORIZONTAL));
        add(inputBrowse,          gbc(2, 0, 1, 1, 0, 0, WEST, NONE));

        inputBrowse.addActionListener(e -> {
            chooseFile(tfInput, true);
            loadImage();
            setOutputPath();
        });

        // Output image path
        JButton outputBrowse = new JButton("Browse...");
        add(new JLabel("Output:"), gbc(0, 1, 1, 1, 0, 0, WEST, NONE));
        add(tfOutput,              gbc(1, 1, 1, 1, 0, 0, WEST, HORIZONTAL));
        add(outputBrowse,          gbc(2, 1, 1, 1, 0, 0, WEST, NONE));

        outputBrowse.addActionListener(e -> chooseFile(tfOutput, false));

        // Key input
        add(new JLabel("Key:"), gbc(0, 2, 1, 1, 0, 0, WEST, NONE));
        add(tfKey,              gbc(1, 2, 2, 1, 0, 0, WEST, BOTH));

        // Image panel
        imageLabel = new JLabel("PNG image panel", CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));

        add(new JScrollPane(imageLabel), gbc(0, 3, 3, 1, 0, 1, WEST, BOTH));
        
        // Message area
        add(new JScrollPane(taMessage), gbc(0, 4, 3, 1, 0, 0.3, WEST, BOTH));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(RIGHT));
        JButton decodeBtn = new JButton("Decode");
        JButton encodeBtn = new JButton("Encode");
        buttonPanel.add(decodeBtn);
        buttonPanel.add(encodeBtn);

        add(buttonPanel, gbc(0, 5, 3, 1, 0, 0, SOUTHEAST, NONE));

        // Button actions
        decodeBtn.addActionListener(this::onDecode);
        encodeBtn.addActionListener(this::onEncode);
    }

    private void setOutputPath() {
		File input = new File(tfInput.getText());
		String fileName = input.getName();
		int i = fileName.lastIndexOf('.');
		if (i < 0) {
			throw new RuntimeException("Bad file name");
		}
		String fname = fileName.substring(0, i);
		tfOutput.setText(new File(input.getParentFile(), fname + ".out" + fileName.substring(i)).getAbsolutePath());
	}

	private static GridBagConstraints gbc(int gridx, int gridy,
            int gridwidth, int gridheight,
            double weightx, double weighty,
            int anchor, int fill) {

    	return new GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, INSETS, 0, 0);
	}

	private void chooseFile(JTextField targetField, boolean open) {
        JFileChooser chooser = new JFileChooser();
        int result = open ? chooser.showOpenDialog(this) : chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            targetField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void loadImage() {
        try {
            File file = new File(tfInput.getText());
            if (file.exists()) {
                Image image = new ImageIcon(ImageIO.read(file)).getImage();
                imageLabel.setIcon(new ImageIcon(image.getScaledInstance(
                        imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH)));
                imageLabel.setText(null);
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("Image file not found");
            }
        } catch (IOException e) {
            imageLabel.setText("Unable to load image");
        }
    }

    private void onEncode(ActionEvent event) {
    	String[] params = new String[] {
    			 "-e",
    			 "-i", tfInput.getText(),
    			 "-o", tfOutput.getText(),
    			 "-p", tfKey.getText(),
    			 "-m", taMessage.getText()
    	};
    	doInTry(() -> M.main(params), "Message encoded");
    }

    private void onDecode(ActionEvent event) {
    	String[] params = new String[] {
   			 "-d",
   			 "-i", tfInput.getText(),
   			 "-p", tfKey.getText()
	   	};
    	doInTry(() -> M.main(params, t -> taMessage.setText(t)), "Message decoded");
    }

    private void doInTry(Runnable run, String message) {
	   	try {
	   		run.run();
			showMessageDialog(this, message);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			showMessageDialog(this, e.getMessage(), "Error", ERROR_MESSAGE);
		}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	Ui ui = new Ui();
			try {
				ui.init();
				ui.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace(System.err);
				showMessageDialog(ui, e.getMessage(), "Error", ERROR_MESSAGE);
			}
		});
    }

}

