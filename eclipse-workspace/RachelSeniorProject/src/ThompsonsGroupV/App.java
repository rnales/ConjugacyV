package ThompsonsGroupV;

/*
 * This app adapted from Nabil Houssain's ConjugacyF implementation
 * as part of his senior project at Bard College
 */

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JApplet;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.DropMode;


public class App extends JApplet {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App frame = new App();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public App() {
	//	setTitle("Software for checking conjugacy in Thompson's Group F");
	//	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000,700);
		setBounds(100, 100, 1055, 720);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		contentPane.add(panel, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		
		JPanel panel_2 = new JPanel();
		
		JLabel lblthisIsA = new JLabel("<html> Author: Rachel Nalecz<br>Bard College '18<br><br>\r\n<font size=\"4\">"
				+ "This interface is an implementation of the solution to the conjugacy problem in Thompson's Group V. "
				+ "It checks whether elements in the group V are conjugate. This software is the product of my senior project at Bard College. "
				+ "Feel free to use it, and please cite if it comes to any use!"
				+ "</font>\r\n<br><br><center><font size=\"5\"><font color=\"red\">IMPORTANT:</font> "
				+ "All input strings must be in the alphabet {A, B, C, P, a, b, c, p}. "
				+ "Note that P = pi<sub>0</sub>, "
				+ "a = A<sup>-1</sup>, b = B<sup>-1</sup>, c = C<sup>-1</sup>, and p =  pi<sub>0</sub><sup>-1</sup>\r\n</html>");
		
		JPanel panel_3 = new JPanel();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(19)
							.addComponent(lblthisIsA, 0, 0, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(27)
									.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE))
								.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
									.addContainerGap()
//									.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									))
							.addGap(28)
//							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							))
//					.addContainerGap(13, Short.MAX_VALUE)
					)
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(lblthisIsA)
					.addGap(27)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
//							.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
							)
//						.addComponent(panel_2, 0, 0, Short.MAX_VALUE)
						)
//					.addContainerGap(152, Short.MAX_VALUE)
					)
		);
		
//		JLabel lblNewLabel = new JLabel("<html><font size=\"4\"><b><center>Reduced diagram generator for elements of F</center></b></font><br></html>");
//		
//		JLabel lblElement = new JLabel("Element:");
//		
//		textField_3 = new JTextField();
//		textField_3.setColumns(10);
//		
//		JButton btnReducedStrandDiagram = new JButton("Generate Reduced Strand Diagram");
//		btnReducedStrandDiagram.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg1) {
//				String result = "";
//				try{
//					String word = textField_3.getText();  // assign the input word to this variable
//					Strand s = new Strand(word);
//					s.reduce();
//					result = "The reduced STRAND DIAGRAM for\n       "+word+"\nhas the following vertices:\n\n";
//					result+=s.generateData();
//				}
//				catch (IllegalArgumentException e){
//					result = e.getMessage();
//				}
//				JTextArea text = new JTextArea(result);
//				JOptionPane.showMessageDialog(null, text);
//			}
//		});
//		
//		JButton btnReducedAnnularStrand = new JButton("Generate Reduced Annular Strand Diagram");
//		btnReducedAnnularStrand.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg2) {
//				String result = "";
//				try{
//					String word = textField_3.getText();
//					Strand s = new Strand(word);
//					Annular asd = s.close(); 
//					asd.reduce();
//					Annular[] comps = asd.getComponents();
//					result +="The reduced ANNULAR STRAND DIAGRAM for\n       "+word+"\nhas the following structure:\n\n"; 
//					for (int i = 0; i < comps.length; i++) result+=("Component "+(i+1)+":\n"+comps[i].generateData()+"\n");
//				}
//				catch (IllegalArgumentException e){
//					result = e.getMessage();
//				}
//				JTextArea text = new JTextArea(result);
//				JOptionPane.showMessageDialog(null, text);
//			}
//		});
		
//		JLabel lblEgXxxx = new JLabel("e.g:  x0x0x0x1");
//		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
//		gl_panel_3.setHorizontalGroup(
//			gl_panel_3.createParallelGroup(Alignment.TRAILING)
//				.addGroup(gl_panel_3.createSequentialGroup()
//					.addGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING)
//						.addGroup(gl_panel_3.createSequentialGroup()
//							.addGap(106)
//							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE)
//							.addGap(100))
//						.addGroup(gl_panel_3.createSequentialGroup()
//							.addGap(12)
//							.addComponent(lblElement, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING)
//								.addGroup(gl_panel_3.createSequentialGroup()
//									.addComponent(btnReducedStrandDiagram)
//									.addPreferredGap(ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
//									.addComponent(btnReducedAnnularStrand))
//								.addComponent(lblEgXxxx, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
//								.addComponent(textField_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))))
//					.addContainerGap())
//		);
//		gl_panel_3.setVerticalGroup(
//			gl_panel_3.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_panel_3.createSequentialGroup()
//					.addGap(18)
//					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.UNRELATED)
//					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
//						.addComponent(lblElement)
//						.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//					.addGap(4)
//					.addComponent(lblEgXxxx)
//					.addPreferredGap(ComponentPlacement.UNRELATED)
//					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
//						.addComponent(btnReducedAnnularStrand)
//						.addComponent(btnReducedStrandDiagram))
//					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//		);
//		panel_3.setLayout(gl_panel_3);
//		
//		JLabel lblsortAList = new JLabel("<html><font size=\"4\"><center><b>Sort list of elements into Conjugacy Classes</b></center></font><br>\r\nEnter a list of elements separated by white space. Alternatively, upload a txt file containing the elements separated by white spaces using \"Upload\". Then press \"Sort\" to sort the elements into conjugacy classes. \r\n</html>");
//		
//		textArea = new JTextArea(12,30);  //scrollbar Nabil
//		textArea.setLineWrap(true);
//		JScrollPane scrollPane = new JScrollPane(textArea);
//
//		JButton btnSort = new JButton("Sort");
//		btnSort.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				String result = "";
//				try{
//					String word = textArea.getText();
//					ConjugacyChecker checker = new ConjugacyChecker();
//					result = checker.checkConjugacy(word,false);
//					JTextArea text = new JTextArea(result);
//					JOptionPane.showMessageDialog(null, text);
//				}
//				catch (IllegalArgumentException e){
//					result = e.getMessage();
//					JTextArea text = new JTextArea(result);
//					JOptionPane.showMessageDialog(null, text);
//				} catch (IOException e) {
//					result = e.getMessage();	
//					JTextArea text = new JTextArea(result);
//					JOptionPane.showMessageDialog(null, text);
//				}					
//			}
//		});
//		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
//		gl_panel_2.setHorizontalGroup(
//			gl_panel_2.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_panel_2.createSequentialGroup()
//					.addContainerGap()
//					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
//						.addComponent(lblsortAList, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
//						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addComponent(btnSort))
//					.addContainerGap())
//		);
//		gl_panel_2.setVerticalGroup(
//			gl_panel_2.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_panel_2.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(lblsortAList, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//					.addGap(18)
//					.addComponent(btnSort)
//					.addContainerGap(33, Short.MAX_VALUE))
//		);
//		panel_2.setLayout(gl_panel_2);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		
		JLabel lblInput = new JLabel("Element 1:");
		
		JLabel lblInput_1 = new JLabel("Element 2:");
		
		JLabel lblConjugacyCheckerGiven = new JLabel("<html><font size=\"4\"><b><center>Conjugacy Checker given two elements of V</center></b></font><br>\r\nEnter the two input elements below and press \"Check Conjugacy\" to find whether the elements are conjugate\r\n</html>");
		
		JButton btnCheckConjugacy = new JButton("Check Conjugacy");
		btnCheckConjugacy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s = "";
				try{
					String w1 = textField.getText();
					String w2 = textField_1.getText();
					ConjugacyChecker checker = new ConjugacyChecker(w1,w2);
					if (checker.areConjugate()) s = "True:   ("+w1+")    and    ("+w2+")    are conjugate";
					else s = "False:    ("+w1+")    and    ("+w2+")    are NOT conjugate";
				}
				catch (IllegalArgumentException e){
					s = e.getMessage();
				}
				JOptionPane.showMessageDialog(null, s);
			}
		});
		
		JLabel lblEgXxyxyyy = new JLabel("e.g:  AbcPaBa");
		
		JLabel lblEgXyxyy = new JLabel("e.g:  CabPAAb");
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblConjugacyCheckerGiven, GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblInput_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblInput, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(lblEgXyxyy, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 242, Short.MAX_VALUE)
									.addComponent(btnCheckConjugacy))
								.addComponent(textField, GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
								.addComponent(textField_1, GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
								.addComponent(lblEgXxyxyyy))))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblConjugacyCheckerGiven, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
					.addGap(11)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblInput))
					.addGap(2)
					.addComponent(lblEgXxyxyyy)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblInput_1))
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(18)
							.addComponent(btnCheckConjugacy))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblEgXyxyy)))
					.addContainerGap(21, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		panel.setLayout(gl_panel);
	}
}

