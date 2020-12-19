package PDF;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class PdfProject {
	public static String ReaderPDF(String sciezkaPliku)throws IOException
	{
		 PDDocument document = PDDocument.load(new File(sciezkaPliku));
         String pdf=null;
         document.getClass();

         if (!document.isEncrypted()) {
         
             PDFTextStripperByArea stripper = new PDFTextStripperByArea();
             stripper.setSortByPosition(true);

             PDFTextStripper tStripper = new PDFTextStripper();

             pdf = tStripper.getText(document);

             }

         String newPdf=pdf.replace("\n", " ").replace("\r", " ");
         return newPdf;
	}
	public static String[][] PdfDoTablicy(String stringZPdf)
	{
		int licznikDat=0;
        int licznikGodzin=0;
        Pattern szukajDat = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d");
        Pattern szukajGodzin = Pattern.compile("\\d\\d:\\d\\d.\\d\\d:\\d\\d");
        Pattern szukajTyp = Pattern.compile("RPSÆwi|RPSwyk|PJLek");
        Matcher m = szukajDat.matcher(stringZPdf);
        Matcher b = szukajGodzin.matcher(stringZPdf);
     
         while (m.find())licznikDat++;
         while (b.find())licznikGodzin++;
         int[] indexDat=new int[licznikDat];
         int[] indexGodzin=new int[licznikGodzin];
         int[] lastIndexGodzin=new int[licznikGodzin];
         String [] datyZajec = new String[licznikDat];
         String [] godzinyZajec = new String[licznikGodzin];
         m.reset();
         for (int i = 0;m.find(); i++) {
             indexDat[i]=m.start();
         }
         b.reset();
         for (int i = 0;b.find(); i++) {
             indexGodzin[i]=b.start();
          }
         m.reset();
         for (int i = 0;m.find(); i++) {
             datyZajec[i]=m.group();
         }
         b.reset();
         for (int i = 0;b.find(); i++) {
             godzinyZajec[i]=b.group();
          }
         b.reset();
         for (int i = 0;b.find(); i++) {
             lastIndexGodzin[i]=b.end();
          }
    /*
     System.out.println("Liczba dni: "+licznikDat);
     System.out.println("Liczba zajêæ: "+licznikGodzin);
     System.out.println("Indeks pierwszej daty zajêæ: "+indexDat[1]);
     System.out.println("Wykaz indeksów dat zajêæ: ");
     for (int i = 1; i < indexDat.length; i++) {
         System.out.print(indexDat[i]+"; ");
     }
     System.out.println();
     System.out.println("Wykaz indeksów godzin zajêæ: ");
     for (int i = 0; i<indexGodzin.length; i++) {
         System.out.print(indexGodzin[i]+"; ");
     }
     */
     
     
     String [][] terminyZajec=new String[licznikGodzin][5];
    //dodaje godziny zajêæ do tabelki "terminyZajec" w 2 drugiej kolumnie
     for (int i = 0; i < terminyZajec.length; i++) {
         terminyZajec[i][1]=godzinyZajec[i];
     }
     //---------------------------------------------------
    
     //dodaje daty zajec w pierwszej kolumnie
     for (int i = 1; i < licznikDat+1; i++) {
         for (int j = 0; j < terminyZajec.length; j++) {
           if(i<licznikDat)
           {
        	 if (indexGodzin[j]>indexDat[i]) {
                 terminyZajec[j][0]=datyZajec[i];
             }    
           }
       }
     }
    
    //dodaje resztê danych do 3 kolumny i 4 wyjœciowej
    int[] suffixy=new int[licznikGodzin];
     for (int i = 0; i < terminyZajec.length; i++) {
    	 if(i<terminyZajec.length-1) {
         terminyZajec[i][2]=stringZPdf.substring(lastIndexGodzin[i]+1, indexGodzin[i+1]-1);
         terminyZajec[i][3]=terminyZajec[i][2].replace("n/z", " ").trim();
         Matcher x= szukajTyp.matcher(terminyZajec[i][3]);
         for (int j = 0;x.find(); j++) suffixy[i]=x.end();
         x.reset();
         terminyZajec[i][4]=terminyZajec[i][3].substring(0, suffixy[i]);
    	 }
         else if (i<terminyZajec.length)
         {
        	 terminyZajec[terminyZajec.length-1][2]=stringZPdf.substring(lastIndexGodzin[lastIndexGodzin.length-1]);
             terminyZajec[terminyZajec.length-1][3]=terminyZajec[terminyZajec.length-1][2].replace("n/z", " ").trim();
             Matcher x= szukajTyp.matcher(terminyZajec[i][3]);
             for (int j = 0;x.find(); j++) suffixy[i]=x.end();
             x.reset();
             terminyZajec[i][4]=terminyZajec[i][3].substring(0, suffixy[i]);
         }
       }
     String [][] tabelaKoncowa=new String[licznikGodzin][3];	
     for(int i=0; i<tabelaKoncowa.length;i++)
     {
    	 tabelaKoncowa[i][0]=terminyZajec[i][0];
    	 tabelaKoncowa[i][1]=terminyZajec[i][1];
    	 tabelaKoncowa[i][2]=terminyZajec[i][4];
     }
     return tabelaKoncowa;
     
    /*Wyœietla tabelkê
     for (int i = 0; i < terminyZajec.length; i++)System.out.println(i+"  "+terminyZajec[i][0]+" "+terminyZajec[i][1]+" "+terminyZajec[i][4]);
     System.out.println(licznikGodzin+"  "+terminyZajec.length);*/
		
	}
	public static void ZapiszDoTxt(String[][]tablicaZajec)
	{
		File txt = new File("plan.txt");
        try (BufferedWriter zapisz = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txt)))) {
            for (int i = 0; i < tablicaZajec.length; i++) {
                zapisz.write(tablicaZajec[i][0]+" "+tablicaZajec[i][1]+" "+tablicaZajec[i][2]);
                zapisz.newLine();
            }
            zapisz.close();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
	}
	
}

