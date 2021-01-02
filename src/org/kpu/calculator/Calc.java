package org.kpu.calculator;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Calc extends JFrame {
	int displayMode;
	final int INPUT_MODE = 0;
	final int RESULT_MODE = 1;
	final int ERROR_MODE = 2;

	boolean clearOnNextDigit; // 화면에 표시될 숫자를 지울지 말지 결정
	double lastNumber; // 마지막에 기억될 수
	String lastOperator; // 마지막에 누른 연산자
	int opCount;
	final int MAX_INPUT_LENGTH = 20; // 최대 입력 가능 길이 제한

	static JLabel label;
	static JLabel info;
	private JButton[] bt;

	public Calc() {
		setTitle("Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();
		c.setLayout(new BorderLayout(5, 5));
		c.setBackground(Color.BLACK);

		TopPanel TP = new TopPanel();
		c.add(TP, BorderLayout.BEFORE_FIRST_LINE);

		OutputPanel OP = new OutputPanel();
		c.add(OP, BorderLayout.EAST);

		BtnPanel BP = new BtnPanel();
		c.add(BP, BorderLayout.SOUTH);

		setSize(500, 600);
		setVisible(true);
	}

	class TopPanel extends JPanel { //상태
		public TopPanel() {
			setLayout(new GridLayout(3, 1));
			setBackground(Color.BLACK);
			info = new JLabel();

			info.setFont(new Font("맑은 고딕", 0, 20));
			info.setBackground(Color.BLACK);
			info.setForeground(Color.WHITE);
			info.setHorizontalAlignment(SwingConstants.RIGHT);
			add(info);
		}
	}

	class OutputPanel extends JPanel { //계산식, 결과
		public OutputPanel() {
			setLayout(new GridLayout(3, 2));
			setBackground(new Color(255, 0, 0, 0)); // 투명

			label = new JLabel("0");

			label.setFont(new Font("맑은 고딕", 0, 40));
			label.setBackground(Color.BLACK);
			label.setForeground(Color.WHITE);
			label.setHorizontalAlignment(SwingConstants.RIGHT);

			add(label);
			label.addMouseListener(new MyMouse()); //계산식 더블클릭하면 초기화
		}
	}

	class BtnPanel extends JPanel { //버튼
		public BtnPanel() {
			bt = new JButton[20];
			setBackground(Color.BLACK);
			setLayout(new GridLayout(5, 4, 5, 5));

			for (int i = 0; i <= 9; i++) {
				bt[i] = new JButton(String.valueOf(i));
				bt[i].setFont(new Font("궁서", 0, 30));
				bt[i].setBackground(Color.DARK_GRAY);
				bt[i].setForeground(Color.WHITE);
			}
			bt[10] = new JButton("C");
			bt[11] = new JButton("±");
			bt[12] = new JButton("%");
			for (int i = 10; i <= 12; i++) {
				bt[i].setFont(new Font("맑은 고딕 굵게", 0, 30));
				bt[i].setBackground(Color.LIGHT_GRAY);
				bt[i].setForeground(Color.BLACK);
			}
			bt[13] = new JButton("÷");
			bt[14] = new JButton("×");
			bt[15] = new JButton("-");
			bt[16] = new JButton("+");
			bt[17] = new JButton(".");
			bt[18] = new JButton("←");
			for (int i = 17; i <= 18; i++) {
				bt[i].setFont(new Font("맑은 고딕", 0, 30));
				bt[i].setBackground(Color.DARK_GRAY);
				bt[i].setForeground(Color.WHITE);
			}
			bt[19] = new JButton("=");
			for (int i = 13; i <= 19; i++) {
				if (i != 17 && i != 18) {
					bt[i].setFont(new Font("맑은 고딕", 0, 30));
					bt[i].setBackground(new Color(234, 150, 72));
					bt[i].setForeground(Color.WHITE);
				}
			}
			add(bt[10]); // C
			add(bt[11]); // ±
			add(bt[12]); // %
			add(bt[13]); // ÷
			for (int i = 7; i <= 9; i++)
				add(bt[i]);
			add(bt[14]); // x
			for (int i = 4; i <= 6; i++)
				add(bt[i]);
			add(bt[15]); // -
			for (int i = 1; i <= 3; i++)
				add(bt[i]);
			add(bt[16]); // +
			add(bt[0]); // 0
			add(bt[17]); // .
			add(bt[18]); // ←
			add(bt[19]); // =

			clearAll();

			for (int i = 0; i < bt.length; i++) {
				bt[i].addActionListener(new MyListener());
				bt[i].addKeyListener(new MyKeyListener());
			}
		}
	}

	private class MyMouse extends MouseAdapter { //계산식 더블클릭하면 초기화
		public void mousePressed(MouseEvent e) {
			if (e.getClickCount() == 2) {
				clearAll();
			}
		}
	}

	private class MyListener implements ActionListener { //계산
		@Override
		public void actionPerformed(ActionEvent e) {
			double result = 0;
			for (int i = 0; i < bt.length; i++) {
				if (e.getSource() == bt[i]) {
					if (i < 10) {
						addToDisplay(i);
						break;
					} else {
						switch (i) {
						case 10:
							clearAll();
							break;
						case 11: // ±
							processSingChange();
							break;
						case 12: // %
							if (displayMode != ERROR_MODE) {
								try {
									result = getNumberInDisplay() / 100;
									displayResult(result);
								}

								catch (Exception ex) {
									displayError("Invalid input for function!");
									displayMode = ERROR_MODE;
								}
							}
							break;
						case 13: // ÷
							processOperator("/");
							break;
						case 14: // x
							processOperator("*");
							break;
						case 15: // -
							processOperator("-");
							break;
						case 16: // +
							processOperator("+");
							break;
						case 17: // .
							addPoint();
							break;
						case 18: // ←
							backspace();
							break;
						case 19: // =
							processEquals();
							break;
						}
					}
				}
			}
		}
	}

	private class MyKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyChar();
			switch (keycode) {
			case KeyEvent.VK_0:
				addToDisplay(0);
				break;
			case KeyEvent.VK_1:
				addToDisplay(1);
				break;
			case KeyEvent.VK_2:
				addToDisplay(2);
				break;
			case KeyEvent.VK_3:
				addToDisplay(3);
				break;
			case KeyEvent.VK_4:
				addToDisplay(4);
				break;
			case KeyEvent.VK_5:
				addToDisplay(5);
				break;
			case KeyEvent.VK_6:
				addToDisplay(6);
				break;
			case KeyEvent.VK_7:
				addToDisplay(7);
				break;
			case KeyEvent.VK_8:
				addToDisplay(8);
				break;
			case KeyEvent.VK_9:
				addToDisplay(9);
				break;
			case 46: // .
				addPoint();
				break;
			case 10: // =
				processEquals();
				break;
			case 47: // /
				processOperator("/");
				break;
			case 42: // *
				processOperator("*");
				break;
			case 43: // +
				processOperator("+");
				break;
			case 45: // -
				processOperator("-");
				break;
			case 8: // backspace
				backspace();
				break;
			case 27: // ESC
				clearAll();
				break;
			}
		}
	}

	private void clearAll() {
		setDisplayString("0");
		opCount = 0;
		lastOperator = "0";
		lastNumber = 0;
		displayMode = INPUT_MODE;
		clearOnNextDigit = true;
		Container c = getContentPane();
		c.setBackground(Color.BLACK);
		setDisplayTopPanel("수식을 입력하세요");
	}

	private void backspace() {
		if (displayMode != ERROR_MODE) {
			setDisplayString(getDisplayString().substring(0, getDisplayString().length() - 1));

			if (getDisplayString().length() < 1)
				setDisplayString("0");
		}
	}

	private void addPoint() {
		displayMode = INPUT_MODE;
		if (clearOnNextDigit)
			setDisplayString("");
		String inputString = getDisplayString();
		if (inputString.indexOf(".") < 0) // 이미 점이 찍혀 있으면 안 찍음.
			setDisplayString(new String(inputString + "."));
	}

	private void processOperator(String string) {
		if (displayMode != ERROR_MODE) {
			double numberInDisplay = getNumberInDisplay();
			if (!lastOperator.equals("0")) {
				try {
					double result = processLastOperator();
					displayResult(result);
					lastNumber = result;
				} catch (Exception e) {
				}

			} else {
				lastNumber = numberInDisplay;
			}
			clearOnNextDigit = true;
			lastOperator = string;
			opCount ++;
		}
	}

	private double processLastOperator() throws Exception {
		double result = 0;
		double numberInDisplay = getNumberInDisplay();
		if (lastOperator.equals("/")) {
			if (numberInDisplay == 0)
				throw (new Exception());
			result = lastNumber / numberInDisplay;
		}
		if (lastOperator.equals("*")) {
			result = lastNumber * numberInDisplay;
		}
		if (lastOperator.equals("-")) {
			result = lastNumber - numberInDisplay;
		}
		if (lastOperator.equals("+")) {
			result = lastNumber + numberInDisplay;
		}
		if (opCount == 0) {
			result = getNumberInDisplay();
		}

		return result;
	}

	private void processSingChange() {
		if (displayMode == INPUT_MODE) {
			String input = getDisplayString();
			if (input.length() > 0 && !input.equals("0")) {
				if (input.indexOf("-") == 0)
					setDisplayString(input.substring(1));
				else
					setDisplayString("-" + input);
			}
		} else if (displayMode == RESULT_MODE) {
			double numberInDisplay = getNumberInDisplay();

			if (numberInDisplay != 0)
				displayResult(-numberInDisplay);
		}
	}

	private void addToDisplay(int i) {
		if (clearOnNextDigit)
			setDisplayString("");
		String inputString = getDisplayString();

		if (inputString.indexOf("0") == 0) {
			inputString = inputString.substring(1);
		}
		if ((!inputString.equals("0") || i > 0) && inputString.length() < MAX_INPUT_LENGTH) {
			setDisplayString(inputString + i);
		}

		displayMode = INPUT_MODE;
		clearOnNextDigit = false;
	}

	private void processEquals() {
		double result = 0;
		if (displayMode != ERROR_MODE) {
			try {
				setDisplayTopPanel("계산결과");
				result = processLastOperator();
				displayResult(result);
			} catch (Exception e) {
				displayError("영으로 나눌 수 없습니다.");
			}
			lastOperator = "0";
		}
	}

	private void displayError(String error) {
		setDisplayTopPanel(error);
		lastNumber = 0;
		displayMode = ERROR_MODE;
		clearOnNextDigit = true;
	}

	private void displayResult(double result) {
		setDisplayString(Double.toString(result));
		lastNumber = result;
		displayMode = RESULT_MODE;
		clearOnNextDigit = true;
	}

	private String getDisplayString() {
		return label.getText();
	}

	private void setDisplayString(String string) {
		label.setText(string);
	}

	private void setDisplayTopPanel(String string) {
		info.setText(string);
	}

	private double getNumberInDisplay() {
		String input = label.getText();
		return Double.parseDouble(input);
	}

}