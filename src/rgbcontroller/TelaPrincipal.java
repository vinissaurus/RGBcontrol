/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcontroller;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author Vinicius-Desktop
 */
class LedStatus {

    private int enabled;
    private int r;
    private int g;
    private int b;

    public void setColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setEnabled(int b){
    this.enabled=b;
    }
    
    public int isEnabled(){
    return enabled;
    }
    
    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}

public class TelaPrincipal extends JFrame implements SerialPortEventListener{

    /**
     * Variáveis das portas
     */
    SerialPort serialPort;
  
    /**
     * VARIÁVEIS ABAIXO
     */

    
    Random rand = new Random();

   
  
    
    LedStatus led1_status;
    LedStatus led2_status;
    int speed=0;
    int smooth=0;
    int testOn=0;
    int randomMode=0;
   
    String message = "";//String destinada ao uso de mensagens no console

        
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
                        "/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyUSB0", // Linux
			"COM3","COM4","COM9" // Windows
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 115200;

	public void initialize() {
                // the next line is for Raspberry Pi and 
                // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
                //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
                message("Starting communication...");
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			message("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
                        message("Sucessfully found serial port:"+serialPort.toString());
		} catch (Exception e) {
                    message("FAIl to find serial port");
			message(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				message("BOARD:"+inputLine);
                                readSerialMessage(inputLine);
                                
			} catch (Exception e) {
                            message("Error in communication!");
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
    
    void message(String s) {
        message += s + "\n";
        consoleOut.setText(message);
        //System.out.println("message+=" + s);
    }

    void refreshFields(){
    switch(led1_status.isEnabled()){
        case 0:led1_checkBox.setSelected(false);
        case 1:led1_checkBox.setSelected(true);
    }
    led1_rSlider.setValue(led1_status.getR());
    led1_gSlider.setValue(led1_status.getG());
    led1_bSlider.setValue(led1_status.getB());
    }
    
    void readSerialMessage(String s){
    if(s.contains("(reportStatus")){
    message("Contains report status!");
    led1_status.setEnabled(Integer.parseInt(s.substring(s.indexOf("led1:en=")+8,s.indexOf(","))));
     s=s.substring(s.indexOf(",")+1,s.length()-1);//comma break
    led1_status.setR(Integer.parseInt(s.substring(s.indexOf("r=")+2,s.indexOf(","))));
    s=s.substring(s.indexOf(",")+1,s.length()-1);//comma break
    led1_status.setG(Integer.parseInt(s.substring(s.indexOf("g=")+2,s.indexOf(","))));
    s=s.substring(s.indexOf(",")+1,s.length()-1);//comma break
    led1_status.setB(Integer.parseInt(s.substring(s.indexOf("b=")+2,s.indexOf("]"))));
    s=s.substring(s.indexOf("[")+1,s.length()-1);//comma break
    
    led2_status.setEnabled(Integer.parseInt(s.substring(s.indexOf("led2:en=")+8,s.indexOf(","))));
     s=s.substring(s.indexOf(",")+1,s.length()-1);//comma break
    led2_status.setR(Integer.parseInt(s.substring(s.indexOf("r=")+2,s.indexOf(","))));
    s=s.substring(s.indexOf(",")+1,s.length()-1);//comma break
    led2_status.setG(Integer.parseInt(s.substring(s.indexOf("g=")+2,s.indexOf(","))));
    s=s.substring(s.indexOf(",")+1,s.length()-1);//comma break
    led2_status.setB(Integer.parseInt(s.substring(s.indexOf("b=")+2,s.indexOf("]"))));
    message("Report status read:\n"
            + "Led1 enabled:"+led1_status.isEnabled()+"\n"
            + "("+led1_status.getR()+","+led1_status.getG()+","+led1_status.getB()+")\n"
            + "Led2 enabled:"+led2_status.isEnabled()+"\n"
            + "("+led2_status.getR()+","+led2_status.getG()+","+led2_status.getB()+")\n");
    refreshFields();

    }
    }
    
    public void iniciarVariaveis() {
        led1_status = new LedStatus();
        led1_status.setColor(0, 0, 0);
        led1_status.setEnabled(0);

        led2_status = new LedStatus();
        led2_status.setColor(0, 0, 0);
        led2_status.setEnabled(0);

    }

    /**
     * Creates new form TelaPrincipal
     */
    public TelaPrincipal() {
        initComponents();
        iniciarVariaveis();
        initialize();


    }

    public void sendSerialMessage(String s){
        try {
		output.write(s.getBytes());
                message("CONSOLE:"+s);
			} catch (IOException e1) {
                            message("Error sending message!");
				e1.printStackTrace();
			}
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        led1_checkBox = new javax.swing.JCheckBox();
        led1_rSlider = new javax.swing.JSlider();
        led1_bSlider = new javax.swing.JSlider();
        led1_gSlider = new javax.swing.JSlider();
        led1_bPanel = new javax.swing.JPanel();
        led1_rPanel = new javax.swing.JPanel();
        led1_gPanel = new javax.swing.JPanel();
        led1_favorite = new javax.swing.JButton();
        led1_colorPanel = new javax.swing.JPanel();
        led1_random = new javax.swing.JButton();
        led1_clone = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSlider1 = new javax.swing.JSlider();
        jPanel3 = new javax.swing.JPanel();
        smoothCheckBox = new javax.swing.JCheckBox();
        randomCheckBox = new javax.swing.JCheckBox();
        testCheckBox = new javax.swing.JCheckBox();
        jPanel13 = new javax.swing.JPanel();
        led2_checkBox = new javax.swing.JCheckBox();
        led2_rSlider = new javax.swing.JSlider();
        led2_bSlider = new javax.swing.JSlider();
        led2_gSlider = new javax.swing.JSlider();
        led2_bPanel = new javax.swing.JPanel();
        led2_rPanel = new javax.swing.JPanel();
        led2_gPanel = new javax.swing.JPanel();
        led2_favorite = new javax.swing.JButton();
        led2_colorPanel = new javax.swing.JPanel();
        led2_random = new javax.swing.JButton();
        led2_clone = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        consoleOut = new javax.swing.JTextArea();
        consoleInput = new javax.swing.JTextField();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RGB Controller");
        setResizable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("LED1 - COMMON"));
        jPanel2.setPreferredSize(new java.awt.Dimension(200, 180));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        led1_checkBox.setText("Enabled");
        led1_checkBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                led1_checkBoxStateChanged(evt);
            }
        });
        led1_checkBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                led1_checkBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel2.add(led1_checkBox, gridBagConstraints);

        led1_rSlider.setMaximum(255);
        led1_rSlider.setValue(0);
        led1_rSlider.setEnabled(false);
        led1_rSlider.setValue(0);
        led1_rSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                led1_rSliderStateChanged(evt);
            }
        });
        led1_rSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                led1_rSliderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 250.0;
        jPanel2.add(led1_rSlider, gridBagConstraints);

        led1_bSlider.setMaximum(255);
        led1_bSlider.setValue(0);
        led1_bSlider.setEnabled(false);
        led1_bSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                led1_bSliderStateChanged(evt);
            }
        });
        led1_bSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                led1_bSliderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 250.0;
        jPanel2.add(led1_bSlider, gridBagConstraints);

        led1_gSlider.setMaximum(255);
        led1_gSlider.setValue(0);
        led1_gSlider.setEnabled(false);
        led1_gSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                led1_gSliderStateChanged(evt);
            }
        });
        led1_gSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                led1_gSliderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 250.0;
        jPanel2.add(led1_gSlider, gridBagConstraints);

        led1_bPanel.setBackground(new java.awt.Color(0, 0, 0));
        led1_bPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 204), 2, true));
        led1_bPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout led1_bPanelLayout = new javax.swing.GroupLayout(led1_bPanel);
        led1_bPanel.setLayout(led1_bPanelLayout);
        led1_bPanelLayout.setHorizontalGroup(
            led1_bPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led1_bPanelLayout.setVerticalGroup(
            led1_bPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 20.0;
        gridBagConstraints.weighty = 20.0;
        jPanel2.add(led1_bPanel, gridBagConstraints);

        led1_rPanel.setBackground(new java.awt.Color(0, 0, 0));
        led1_rPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 51, 51), 2, true));
        led1_rPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout led1_rPanelLayout = new javax.swing.GroupLayout(led1_rPanel);
        led1_rPanel.setLayout(led1_rPanelLayout);
        led1_rPanelLayout.setHorizontalGroup(
            led1_rPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led1_rPanelLayout.setVerticalGroup(
            led1_rPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 20.0;
        gridBagConstraints.weighty = 20.0;
        jPanel2.add(led1_rPanel, gridBagConstraints);

        led1_gPanel.setBackground(new java.awt.Color(0, 0, 0));
        led1_gPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 0), 2, true));
        led1_gPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout led1_gPanelLayout = new javax.swing.GroupLayout(led1_gPanel);
        led1_gPanel.setLayout(led1_gPanelLayout);
        led1_gPanelLayout.setHorizontalGroup(
            led1_gPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led1_gPanelLayout.setVerticalGroup(
            led1_gPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 20.0;
        gridBagConstraints.weighty = 20.0;
        jPanel2.add(led1_gPanel, gridBagConstraints);

        led1_favorite.setText("ADD FAVORITE");
        led1_favorite.setEnabled(false);
        led1_favorite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                led1_favoriteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(led1_favorite, gridBagConstraints);

        led1_colorPanel.setBackground(new java.awt.Color(0, 0, 0));
        led1_colorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        led1_colorPanel.setPreferredSize(new java.awt.Dimension(100, 20));

        javax.swing.GroupLayout led1_colorPanelLayout = new javax.swing.GroupLayout(led1_colorPanel);
        led1_colorPanel.setLayout(led1_colorPanelLayout);
        led1_colorPanelLayout.setHorizontalGroup(
            led1_colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led1_colorPanelLayout.setVerticalGroup(
            led1_colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(led1_colorPanel, gridBagConstraints);

        led1_random.setText("RANDOM");
        led1_random.setEnabled(false);
        led1_random.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                led1_randomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(led1_random, gridBagConstraints);

        led1_clone.setText("CLONE LED2");
        led1_clone.setEnabled(false);
        led1_clone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                led1_cloneActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(led1_clone, gridBagConstraints);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Favorites"));
        jPanel11.setLayout(new java.awt.GridBagLayout());

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setOpaque(true);
        jLabel1.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel1, new java.awt.GridBagConstraints());

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setOpaque(true);
        jLabel2.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel2, new java.awt.GridBagConstraints());

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setOpaque(true);
        jLabel3.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel3, new java.awt.GridBagConstraints());

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setOpaque(true);
        jLabel4.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel4, new java.awt.GridBagConstraints());

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setOpaque(true);
        jLabel5.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel5, new java.awt.GridBagConstraints());

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setOpaque(true);
        jLabel6.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel6, new java.awt.GridBagConstraints());

        jLabel7.setBackground(new java.awt.Color(0, 0, 0));
        jLabel7.setOpaque(true);
        jLabel7.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel7, new java.awt.GridBagConstraints());

        jLabel8.setBackground(new java.awt.Color(0, 0, 0));
        jLabel8.setOpaque(true);
        jLabel8.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel8, new java.awt.GridBagConstraints());

        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setOpaque(true);
        jLabel9.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel9, new java.awt.GridBagConstraints());

        jLabel10.setBackground(new java.awt.Color(0, 0, 0));
        jLabel10.setOpaque(true);
        jLabel10.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel10, new java.awt.GridBagConstraints());

        jLabel11.setBackground(new java.awt.Color(0, 0, 0));
        jLabel11.setOpaque(true);
        jLabel11.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel11, new java.awt.GridBagConstraints());

        jLabel12.setBackground(new java.awt.Color(0, 0, 0));
        jLabel12.setOpaque(true);
        jLabel12.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel12, new java.awt.GridBagConstraints());

        jLabel13.setBackground(new java.awt.Color(0, 0, 0));
        jLabel13.setOpaque(true);
        jLabel13.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel13, new java.awt.GridBagConstraints());

        jLabel14.setBackground(new java.awt.Color(0, 0, 0));
        jLabel14.setOpaque(true);
        jLabel14.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel14, new java.awt.GridBagConstraints());

        jLabel15.setBackground(new java.awt.Color(0, 0, 0));
        jLabel15.setOpaque(true);
        jLabel15.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel15, new java.awt.GridBagConstraints());

        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setOpaque(true);
        jLabel16.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel11.add(jLabel16, new java.awt.GridBagConstraints());

        jLabel17.setBackground(new java.awt.Color(0, 0, 0));
        jLabel17.setOpaque(true);
        jLabel17.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel17, gridBagConstraints);

        jLabel18.setBackground(new java.awt.Color(0, 0, 0));
        jLabel18.setOpaque(true);
        jLabel18.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel18, gridBagConstraints);

        jLabel19.setBackground(new java.awt.Color(0, 0, 0));
        jLabel19.setOpaque(true);
        jLabel19.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel19, gridBagConstraints);

        jLabel20.setBackground(new java.awt.Color(0, 0, 0));
        jLabel20.setOpaque(true);
        jLabel20.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel20, gridBagConstraints);

        jLabel21.setBackground(new java.awt.Color(0, 0, 0));
        jLabel21.setOpaque(true);
        jLabel21.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 15;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel21, gridBagConstraints);

        jLabel22.setBackground(new java.awt.Color(0, 0, 0));
        jLabel22.setOpaque(true);
        jLabel22.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel22, gridBagConstraints);

        jLabel23.setBackground(new java.awt.Color(0, 0, 0));
        jLabel23.setOpaque(true);
        jLabel23.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel23, gridBagConstraints);

        jLabel24.setBackground(new java.awt.Color(0, 0, 0));
        jLabel24.setOpaque(true);
        jLabel24.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel24, gridBagConstraints);

        jLabel25.setBackground(new java.awt.Color(0, 0, 0));
        jLabel25.setOpaque(true);
        jLabel25.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel25, gridBagConstraints);

        jLabel26.setBackground(new java.awt.Color(0, 0, 0));
        jLabel26.setOpaque(true);
        jLabel26.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel26, gridBagConstraints);

        jLabel27.setBackground(new java.awt.Color(0, 0, 0));
        jLabel27.setOpaque(true);
        jLabel27.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel27, gridBagConstraints);

        jLabel28.setBackground(new java.awt.Color(0, 0, 0));
        jLabel28.setOpaque(true);
        jLabel28.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel28, gridBagConstraints);

        jLabel29.setBackground(new java.awt.Color(0, 0, 0));
        jLabel29.setOpaque(true);
        jLabel29.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel29, gridBagConstraints);

        jLabel30.setBackground(new java.awt.Color(0, 0, 0));
        jLabel30.setOpaque(true);
        jLabel30.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel30, gridBagConstraints);

        jLabel31.setBackground(new java.awt.Color(0, 0, 0));
        jLabel31.setOpaque(true);
        jLabel31.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel31, gridBagConstraints);

        jLabel32.setBackground(new java.awt.Color(0, 0, 0));
        jLabel32.setOpaque(true);
        jLabel32.setPreferredSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel32, gridBagConstraints);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Other config"));

        jButton2.setText("SAVE TO EEPROM");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Speed"));
        jPanel1.add(jSlider1);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.GridBagLayout());

        smoothCheckBox.setText("Smooth transition");
        smoothCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smoothCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(smoothCheckBox, gridBagConstraints);

        randomCheckBox.setText("Random mode");
        randomCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(randomCheckBox, gridBagConstraints);

        testCheckBox.setText("Test sequence on load");
        testCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(testCheckBox, gridBagConstraints);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("LED2 - STRIP"));
        jPanel13.setPreferredSize(new java.awt.Dimension(200, 180));
        jPanel13.setLayout(new java.awt.GridBagLayout());

        led2_checkBox.setText("Enabled");
        led2_checkBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                led2_checkBoxStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel13.add(led2_checkBox, gridBagConstraints);

        led2_rSlider.setMaximum(255);
        led2_rSlider.setValue(0);
        led2_rSlider.setEnabled(false);
        led1_rSlider.setValue(0);
        led2_rSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                led2_rSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 250.0;
        jPanel13.add(led2_rSlider, gridBagConstraints);

        led2_bSlider.setMaximum(255);
        led2_bSlider.setValue(0);
        led2_bSlider.setEnabled(false);
        led2_bSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                led2_bSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 250.0;
        jPanel13.add(led2_bSlider, gridBagConstraints);

        led2_gSlider.setMaximum(255);
        led2_gSlider.setValue(0);
        led2_gSlider.setEnabled(false);
        led2_gSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                led2_gSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 250.0;
        jPanel13.add(led2_gSlider, gridBagConstraints);

        led2_bPanel.setBackground(new java.awt.Color(0, 0, 0));
        led2_bPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 204), 2, true));
        led2_bPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout led2_bPanelLayout = new javax.swing.GroupLayout(led2_bPanel);
        led2_bPanel.setLayout(led2_bPanelLayout);
        led2_bPanelLayout.setHorizontalGroup(
            led2_bPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led2_bPanelLayout.setVerticalGroup(
            led2_bPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 20.0;
        gridBagConstraints.weighty = 20.0;
        jPanel13.add(led2_bPanel, gridBagConstraints);

        led2_rPanel.setBackground(new java.awt.Color(0, 0, 0));
        led2_rPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 51, 51), 2, true));
        led2_rPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout led2_rPanelLayout = new javax.swing.GroupLayout(led2_rPanel);
        led2_rPanel.setLayout(led2_rPanelLayout);
        led2_rPanelLayout.setHorizontalGroup(
            led2_rPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led2_rPanelLayout.setVerticalGroup(
            led2_rPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 20.0;
        gridBagConstraints.weighty = 20.0;
        jPanel13.add(led2_rPanel, gridBagConstraints);

        led2_gPanel.setBackground(new java.awt.Color(0, 0, 0));
        led2_gPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 0), 2, true));
        led2_gPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout led2_gPanelLayout = new javax.swing.GroupLayout(led2_gPanel);
        led2_gPanel.setLayout(led2_gPanelLayout);
        led2_gPanelLayout.setHorizontalGroup(
            led2_gPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led2_gPanelLayout.setVerticalGroup(
            led2_gPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 20.0;
        gridBagConstraints.weighty = 20.0;
        jPanel13.add(led2_gPanel, gridBagConstraints);

        led2_favorite.setText("ADD FAVORITE");
        led2_favorite.setEnabled(false);
        led2_favorite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                led2_favoriteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel13.add(led2_favorite, gridBagConstraints);

        led2_colorPanel.setBackground(new java.awt.Color(0, 0, 0));
        led2_colorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        led2_colorPanel.setPreferredSize(new java.awt.Dimension(100, 20));

        javax.swing.GroupLayout led2_colorPanelLayout = new javax.swing.GroupLayout(led2_colorPanel);
        led2_colorPanel.setLayout(led2_colorPanelLayout);
        led2_colorPanelLayout.setHorizontalGroup(
            led2_colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led2_colorPanelLayout.setVerticalGroup(
            led2_colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel13.add(led2_colorPanel, gridBagConstraints);

        led2_random.setText("RANDOM");
        led2_random.setEnabled(false);
        led2_random.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                led2_randomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel13.add(led2_random, gridBagConstraints);

        led2_clone.setText("CLONE LED1");
        led2_clone.setEnabled(false);
        led2_clone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                led2_cloneActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel13.add(led2_clone, gridBagConstraints);

        consoleOut.setEditable(false);
        consoleOut.setBackground(new java.awt.Color(0, 0, 0));
        consoleOut.setColumns(20);
        consoleOut.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        consoleOut.setForeground(new java.awt.Color(153, 204, 0));
        consoleOut.setLineWrap(true);
        consoleOut.setRows(5);
        consoleOut.setAutoscrolls(false);
        consoleOut.setDragEnabled(true);
        jScrollPane2.setViewportView(consoleOut);

        consoleInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consoleInputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(consoleInput))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(consoleInput, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void led1_checkBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_led1_checkBoxStateChanged
        led1_rSlider.setEnabled(led1_checkBox.isSelected());
        led1_gSlider.setEnabled(led1_checkBox.isSelected());
        led1_bSlider.setEnabled(led1_checkBox.isSelected());
        led1_clone.setEnabled(led1_checkBox.isSelected());
        led1_random.setEnabled(led1_checkBox.isSelected());
        led1_favorite.setEnabled(led1_checkBox.isSelected());
        
        if(led1_checkBox.isSelected())led1_status.setEnabled(1);
        if(!led1_checkBox.isSelected())led1_status.setEnabled(0);
        //sendSettings();
    }//GEN-LAST:event_led1_checkBoxStateChanged

    private void led1_rSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_led1_rSliderStateChanged
        led1_rPanel.setBackground(new Color(led1_rSlider.getValue(), 0, 0));
        led1_status.setR(led1_rSlider.getValue());
        led1_colorPanel.setBackground(new Color(led1_status.getR(), led1_status.getG(), led1_status.getB()));
    }//GEN-LAST:event_led1_rSliderStateChanged

    private void led1_gSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_led1_gSliderStateChanged
        led1_gPanel.setBackground(new Color(0, led1_gSlider.getValue(), 0));
        led1_status.setG(led1_gSlider.getValue());
        led1_colorPanel.setBackground(new Color(led1_status.getR(), led1_status.getG(), led1_status.getB()));
    }//GEN-LAST:event_led1_gSliderStateChanged

    private void led1_bSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_led1_bSliderStateChanged
        led1_bPanel.setBackground(new Color(0, 0, led1_bSlider.getValue()));
        led1_status.setB(led1_bSlider.getValue());
        led1_colorPanel.setBackground(new Color(led1_status.getR(), led1_status.getG(), led1_status.getB()));
    }//GEN-LAST:event_led1_bSliderStateChanged

    private void led2_checkBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_led2_checkBoxStateChanged
        led2_rSlider.setEnabled(led2_checkBox.isSelected());
        led2_gSlider.setEnabled(led2_checkBox.isSelected());
        led2_bSlider.setEnabled(led2_checkBox.isSelected());
        led2_clone.setEnabled(led2_checkBox.isSelected());
        led2_random.setEnabled(led2_checkBox.isSelected());
        led2_favorite.setEnabled(led2_checkBox.isSelected());
    }//GEN-LAST:event_led2_checkBoxStateChanged

    private void led2_rSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_led2_rSliderStateChanged
        led2_rPanel.setBackground(new Color(led2_rSlider.getValue(), 0, 0));
        led2_status.setR(led2_rSlider.getValue());
        led2_colorPanel.setBackground(new Color(led2_status.getR(), led2_status.getG(), led2_status.getB()));
    }//GEN-LAST:event_led2_rSliderStateChanged

    private void led2_gSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_led2_gSliderStateChanged
        led2_gPanel.setBackground(new Color(0, led2_gSlider.getValue(), 0));
        led2_status.setG(led2_gSlider.getValue());
        led2_colorPanel.setBackground(new Color(led2_status.getR(), led2_status.getG(), led2_status.getB()));
    }//GEN-LAST:event_led2_gSliderStateChanged

    private void led2_bSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_led2_bSliderStateChanged
        led2_bPanel.setBackground(new Color(0, 0, led2_bSlider.getValue()));
        led2_status.setB(led2_bSlider.getValue());
        led2_colorPanel.setBackground(new Color(led2_status.getR(), led2_status.getG(), led2_status.getB()));
    }//GEN-LAST:event_led2_bSliderStateChanged

    private void led1_cloneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_led1_cloneActionPerformed
        led1_status.setColor(led2_status.getR(), led2_status.getG(), led2_status.getB());
        led1_rSlider.setValue(led1_status.getR());
        led1_gSlider.setValue(led1_status.getG());
        led1_bSlider.setValue(led1_status.getB());
        sendSettings();
    }//GEN-LAST:event_led1_cloneActionPerformed

    private void led1_randomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_led1_randomActionPerformed
        led1_status.setColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        led1_rSlider.setValue(led1_status.getR());
        led1_gSlider.setValue(led1_status.getG());
        led1_bSlider.setValue(led1_status.getB());
        sendSettings();
    }//GEN-LAST:event_led1_randomActionPerformed

    private void led1_favoriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_led1_favoriteActionPerformed

    }//GEN-LAST:event_led1_favoriteActionPerformed

    private void led2_cloneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_led2_cloneActionPerformed
        led2_status.setColor(led1_status.getR(), led1_status.getG(), led1_status.getB());
        led2_rSlider.setValue(led2_status.getR());
        led2_gSlider.setValue(led2_status.getG());
        led2_bSlider.setValue(led2_status.getB());
    }//GEN-LAST:event_led2_cloneActionPerformed

    private void led2_randomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_led2_randomActionPerformed
        led2_status.setColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        led2_rSlider.setValue(led2_status.getR());
        led2_gSlider.setValue(led2_status.getG());
        led2_bSlider.setValue(led2_status.getB());
    }//GEN-LAST:event_led2_randomActionPerformed

    private void led2_favoriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_led2_favoriteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_led2_favoriteActionPerformed

    private void led1_rSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_led1_rSliderMouseReleased
sendSettings();
    }//GEN-LAST:event_led1_rSliderMouseReleased

    private void led1_gSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_led1_gSliderMouseReleased
sendSettings();
    }//GEN-LAST:event_led1_gSliderMouseReleased

    private void led1_bSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_led1_bSliderMouseReleased
sendSettings();
    }//GEN-LAST:event_led1_bSliderMouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
sendSerialMessage("SAVE");        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void led1_checkBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_led1_checkBoxActionPerformed
sendSettings();    // TODO add your handling code here:
    }//GEN-LAST:event_led1_checkBoxActionPerformed

    private void consoleInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consoleInputActionPerformed
sendSerialMessage(consoleInput.getText());
consoleInput.setText("");
    }//GEN-LAST:event_consoleInputActionPerformed

    private void randomCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomCheckBoxActionPerformed
 if(randomCheckBox.isSelected())randomMode=1;
  if(!randomCheckBox.isSelected())randomMode=0;
  sendSettings();
    }//GEN-LAST:event_randomCheckBoxActionPerformed

    private void smoothCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smoothCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_smoothCheckBoxActionPerformed

    private void testCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testCheckBoxActionPerformed
 if(testCheckBox.isSelected())testOn=1;
  if(!testCheckBox.isSelected())testOn=0;
  sendSettings();
    }//GEN-LAST:event_testCheckBoxActionPerformed

    
public void sendSettings(){//    int speed=0;int smooth=0; int testOn=0;
sendSerialMessage("l1en@"+led1_status.isEnabled()
        + "@l1enr@"+led1_rSlider.getValue()
        +"@rg@"+led1_gSlider.getValue()
        +"@gb@"+led1_bSlider.getValue()
        +"@bl2en@"+led2_status.isEnabled()
        +"@l2enR@"+led2_status.getR()
        +"@RG@"+led2_status.getG()
        +"@GB@"+led2_status.getB()
        +"@Bsm@"+smooth
        +"@smsp@"+speed
        +"@spts@"+testOn
        +"@tsrdm@"+randomMode+"@rdm");
}
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception{
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
     
      
//		@SuppressWarnings("unchecked")
//		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
//		while(ports.hasMoreElements()){
//			CommPortIdentifier port = ports.nextElement(); 
//			
//		}
            
           java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
                
                
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField consoleInput;
    private javax.swing.JTextArea consoleOut;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel led1_bPanel;
    private javax.swing.JSlider led1_bSlider;
    private javax.swing.JCheckBox led1_checkBox;
    private javax.swing.JButton led1_clone;
    private javax.swing.JPanel led1_colorPanel;
    private javax.swing.JButton led1_favorite;
    private javax.swing.JPanel led1_gPanel;
    private javax.swing.JSlider led1_gSlider;
    private javax.swing.JPanel led1_rPanel;
    private javax.swing.JSlider led1_rSlider;
    private javax.swing.JButton led1_random;
    private javax.swing.JPanel led2_bPanel;
    private javax.swing.JSlider led2_bSlider;
    private javax.swing.JCheckBox led2_checkBox;
    private javax.swing.JButton led2_clone;
    private javax.swing.JPanel led2_colorPanel;
    private javax.swing.JButton led2_favorite;
    private javax.swing.JPanel led2_gPanel;
    private javax.swing.JSlider led2_gSlider;
    private javax.swing.JPanel led2_rPanel;
    private javax.swing.JSlider led2_rSlider;
    private javax.swing.JButton led2_random;
    private javax.swing.JCheckBox randomCheckBox;
    private javax.swing.JCheckBox smoothCheckBox;
    private javax.swing.JCheckBox testCheckBox;
    // End of variables declaration//GEN-END:variables


  

    
}
