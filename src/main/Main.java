package main;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
 

public class Main {

	public static void main(String[] args) {
		
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource)
				UIManager.put(key, new FontUIResource("굴림", Font.PLAIN, 14));
		}
		
		// 글꼴, 효과, 크기 순
		
		
				JFrame frm = new JFrame();
				
				frm.setTitle("이찬호");
				frm.setSize(700,700);
				frm.setLocationRelativeTo(null);
				//프레임 화면 가운데 배치
				frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				 // 프레임을 닫았을 때 메모리에서 제거되도록 설정
				frm.getContentPane().setLayout(null);
				//레이아웃 설정
				frm.getContentPane().setBackground(Color.LIGHT_GRAY);
				//배경색상
				
				Runtime rt = Runtime.getRuntime();
				String exeFile = "C:\\Program Files (x86)\\경성정보기술\\RF출석관리\\ATTEND_RF.exe";
				Process p;
				                     
				try {
				    p = rt.exec(exeFile);
				    p.waitFor();
				} catch (Exception e) {
				    e.printStackTrace();
				}
				//프로그램 동시 실행 시키기
				
				
				JLabel lab1 = new JLabel();
				lab1.setBounds(30, 10, 150, 50);
				lab1.setText("출입관리 도우미");
				lab1.setHorizontalAlignment(JLabel.CENTER);
				lab1.setFont(new Font("굴림", Font.BOLD, 18));
				frm.getContentPane().add(lab1);
				//라벨로 맨 위 텍스트 표기
				
				TextArea textarea = new TextArea();
				textarea.setBounds(390, 60, 270, 270);
				textarea.append("\n");
				frm.add(textarea);
				//복붙 테스트용 창
				
				JTextField textfield = new JTextField();
				
				textfield.setBounds(210,540,150,50);
				textfield.setText("값을 입력하세요!");
				textfield.setHorizontalAlignment(JTextField.CENTER);
				frm.getContentPane().add(textfield);
				//11번 버튼, 특정 키워드 입력 가능 칸
				
				String[] prgname = {"노노케어", "스쿨존", "도서관도우미","사회서비스형","복지시설도우미",
						"일반 취업","고령자 취업","프로그램","탁구","장기·바둑","시설 이용","행사 참여","사용자 지정"};
				JButton[] btn = new JButton[prgname.length];
				
				for(int i=0; i<prgname.length; i++) {
					btn[i] = new JButton(prgname[i]);
					btn[i].setBackground(Color.darkGray);
					btn[i].setForeground(Color.white);
					frm.getContentPane().add(btn[i]);
				}
				
				for(int i=0; i<prgname.length;i++) {
					if(i <= 5) {
						btn[i].setBounds(30, 60+(i*80), 150, 50);
						btn[i+6].setBounds(210, 60+(i*80), 150, 50);
					}
				
					else
						btn[12].setBounds(30,540,150,50); // 사용자 지정 버튼
				}
				
				
				/*
				btn[0].setBounds(30,60,150,50); 
				btn[1].setBounds(30,140,150,50);
				btn[2].setBounds(30,220,150,50);
				btn[3].setBounds(30,300,150,50);
				btn[4].setBounds(30,380,150,50);
				btn[5].setBounds(30,460,150,50); 
			
				btn[6].setBounds(210,60,150,50);
				btn[7].setBounds(210,140,150,50);	
				btn[8].setBounds(210,220,150,50);
				btn[9].setBounds(210,300,150,50);
				btn[10].setBounds(210,380,150,50);
				btn[11].setBounds(210,460,150,50); 
				btn[12].setBounds(30,540,150,50);
				*/
				
				btn[12].setBounds(30,540,150,50); // 사용자 지정 -> 10,11,12 위치 뒤죽박죽 수정 필요
				btn[12].setToolTipText("옆에 창에 입력한 내용이 적용됨");
				
				/*
				
				for(int c=0; c<prgname.length-1; c++) {
					btn[c].addActionListener(event ->{
						StringSelection data = new StringSelection(prgname[c]);
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(data, data);
					});
				}
				
				람다식 관련 문제 해결해야함*/
				
				btn[0].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[0]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
							
				
				btn[1].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[1]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[2].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[2]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[3].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[3]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[4].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[4]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[5].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[5]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[6].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[6]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[7].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[7]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[8].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[8]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[9].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[9]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[10].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[10]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				btn[11].addActionListener(event -> {
					StringSelection data1 = new StringSelection(prgname[11]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				});
				
				
				
				btn[prgname.length-1].addActionListener(event -> {
					StringSelection data1 = new StringSelection(textfield.getText());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data1, data1);
				}); 
				//마지막 버튼, 옆에 텍스트필드에 입력된 값을 getText 해옴
				
				frm.setVisible(true);
				//프레임 보이게 설정

	

}
}
	
