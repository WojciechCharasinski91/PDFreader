package PDF;

import java.io.IOException;

public class Program {

	public static void main(String[] args) throws IOException {
		String plan="";
		plan=PdfProject.ReaderPDF("C:\\Users\\Wojtek\\Downloads\\Plany (4).pdf");
		String [][] tab;
		tab=PdfProject.PdfDoTablicy(plan);
		for(int i =0; i<tab.length;i++)System.out.println(tab[i][0]+" "+tab[i][1]+" "+tab[i][2]);
		PdfProject.ZapiszDoTxt(tab);
		
	}

}
