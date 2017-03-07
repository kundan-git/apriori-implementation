package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

import Core.AprioriDriver;
import Exceptions.InputReaderAndEncoderException;
import Inputreader.InputDataDelimiters;

public class AprioriUi
{
    private static String ENTER = "Enter";
    static JButton enterButton;
    public static JTextArea output;
    public static JComboBox<String> hasHeaderCombo;
    public static JComboBox<String> delimCombo;
    public static JTextField inputPath;
    public static JTextField minSup;
    public static JTextField minConf;
    static JFrame frame;
    static JPanel panel;

    public static void main(String... args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        createFrame();
    }

    public static void createFrame()
    {
        
    	inputPath = new JTextField(45);
    	JLabel inputPathLabel = new JLabel("Data File:");
        inputPath.setToolTipText("Enter data file path.");

        delimCombo = new JComboBox<String>();
        delimCombo.addItem("SPACE");
        delimCombo.addItem("COMMA");
        JLabel delimLabel = new JLabel("Delimiter:");
        delimCombo.setToolTipText("Select delimiter used in file...");
        
        hasHeaderCombo = new JComboBox<String>();
        hasHeaderCombo.addItem("TRUE");
        hasHeaderCombo.addItem("FALSE");
        hasHeaderCombo.setToolTipText("Is file having headers?");
        
        JLabel supLabel = new JLabel("Min Support:");
        minSup = new JTextField(3);
        minSup.setToolTipText("Minimum support. Must be between 0 and 1.");
        
        JLabel confLabel = new JLabel("MinConfidence:");
        minConf = new JTextField(3);
        minConf.setToolTipText("Minimum confidence. Must be between 0 and 1.");
        
        enterButton = new JButton("Generate Rules");
        enterButton.setActionCommand(ENTER);
        
        AprioriCoreInvoker aprioriInvoker = new AprioriCoreInvoker();
        enterButton.addActionListener(aprioriInvoker);
        inputPath.setActionCommand(ENTER);
        inputPath.addActionListener(aprioriInvoker);
        
        output = new JTextArea(15, 100);
        output.setEditable(false);
        output.setWrapStyleWord(true);
        
        DefaultCaret caret = (DefaultCaret) output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        /* Row 1*/
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(inputPathLabel);
        inputPanel.add(inputPath);
        inputPanel.add(delimLabel);
        inputPanel.add(delimCombo);
        inputPanel.add(supLabel);
        inputPanel.add(minSup);
        inputPanel.add(confLabel);
        inputPanel.add(minConf);
        
        /* Row 2*/
        JPanel inputPanel_1 = new JPanel();
        inputPanel_1.setLayout(new FlowLayout());
        inputPanel_1.add(enterButton);
        
        /* Scroller */
        JScrollPane scroller = new JScrollPane(output);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        /*Update the panel*/
        panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(inputPanel);
        panel.add(inputPanel_1);
        panel.add(scroller);
        
        /* Update the entire frame*/
        frame = new JFrame("Apriori");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
        
        /*Set defaults*/
        inputPath.setText("./data1");
        minSup.setText("0.2");
        minConf.setText("0.2");
        inputPath.requestFocus();
    }

    public static class AprioriCoreInvoker implements ActionListener
    {

    	private boolean validateConfOrSup(String ip){
    		try{
    			float val = Float.parseFloat(ip);
    			if((val<0) || (val>1)){
    				return false;
    			}
    		}catch(Exception e){
    			return false;
    		}
    		return true;
    	}
    	
    	private String validateInput(){
    		String ret = "okay";
    		if (inputPath.getText().trim().equals("")){
    			ret = "Please enter the input file path.";
    			return ret;
    		}
    		
    		if (!validateConfOrSup(minSup.getText().trim())){
    			ret = "Invalid value for minimum support.";
    			return ret;
    		}
    		if (!validateConfOrSup(minConf.getText().trim())){
    			ret = "Invalid value for minimum confidence.";
    			return ret;
    		}
    		return ret;
    	}
    	
        public void actionPerformed(final ActionEvent ev)
        {
        	output.setText("");
        	
        	String validateOutput = validateInput(); 
        	if(validateOutput.equals("okay")){
        		String cmd = ev.getActionCommand();
                if (ENTER.equals(cmd))
                {
                	String ipFilePath = inputPath.getText();
                	String outFilePath = System.getProperty("user.dir")+File.separator+"Rules.txt";
                	float conf =  Float.parseFloat(minConf.getText());
                	float sup =  Float.parseFloat(minSup.getText());
                	String delim = delimCombo.getSelectedItem().toString();
                	
                	InputDataDelimiters delimEnum = 
                			delim.equals("SPACE") ?  InputDataDelimiters.SPACE: InputDataDelimiters.COMMA;
                	AprioriDriver aprioriDriver = new AprioriDriver();
                	String result="";
                	try {
						result = aprioriDriver.RunApriori(ipFilePath,delimEnum,true,sup,conf,outFilePath);
						output.append("The result is in:\n"+outFilePath+" file.");
	                	output.append("\n\n"+result);
					}catch (IOException e) {
						output.append("ERROR >> Unable to read/write file.\nCheck file path and permissions.");
						return;
					}catch( InputReaderAndEncoderException ireE){
						output.append("ERROR >> "+ireE.getMessage());
						return;
					}
                }	
        	}else{
        		output.append("Error >> Invalid input>"+validateOutput);
        	}
        	 
        }
    }
}