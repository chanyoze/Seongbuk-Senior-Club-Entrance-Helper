package main;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Removed {

	public static void main(String[] args) {
		// TODO 자동 생성된 메소드 스텁
		
		/* 너무 무식한 방식이라 폐기함
		 
		JButton btn1 = new JButton("노노케어");
		JButton btn2 = new JButton("스쿨존");
		JButton btn3 = new JButton("도서관도우미");
		JButton btn4 = new JButton("사회서비스형");
		JButton btn5 = new JButton("복지시설도우미");
		JButton btn6 = new JButton("일반취업");
		JButton btn7 = new JButton("고령자취업");
		JButton btn8 = new JButton("프로그램");
		JButton btn9 = new JButton("탁구");
		JButton btn10 = new JButton("장기·바둑");
		
		frm.getContentPane().add(btn1);
		frm.getContentPane().add(btn2);
		frm.getContentPane().add(btn3);
		frm.getContentPane().add(btn4);
		frm.getContentPane().add(btn5);
		frm.getContentPane().add(btn6);
		frm.getContentPane().add(btn7);
		frm.getContentPane().add(btn8);
		frm.getContentPane().add(btn9);
		frm.getContentPane().add(btn10); 
		
		*/
		
	 	btn[0].addActionListener(event -> {
			StringSelection data1 = new StringSelection(arr[0]);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(data1, null);
		});

	}

}
