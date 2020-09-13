package com.java.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class Base64ToPdf {

	public static void main(String[] args) {
		

		String string1 = "";
		String string2= "";
		String string3= "";
		String string4= "";
		String string5= "";

		String str[]= {string1,string2,string3,string4,string5};
		
		int n=str.length;
		byte[] arr[] = new byte[n][] ;
		
		for(int i=0;i<n;i++)
		{
			arr[i]=Base64.getMimeDecoder().decode(str[i]);
		}
		
		
		try {
			byte[] finalBytes = appendFiles(arr[0], arr[1]);
			int i = 2;
			while (i < n) {
				finalBytes = appendFiles(finalBytes, arr[i]);
				i++;
			}

			OutputStream out;
			out = new FileOutputStream("mergedOutput.pdf");
			out.write(finalBytes);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//The second byte[] array parameter will be appended to the first one and the resulting byte[] array will be returned.
	public static byte[] appendFiles(byte[] firstStr, byte[] secondStr) throws Exception {
		byte[] arr[] = {firstStr,secondStr };
		int i;
		
		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();
		PdfContentByte cb = writer.getDirectContent();
		PdfImportedPage page;
		int rotation;
		
		for(i=0;i<arr.length;i++)
		{
			int n=0;
			try
			{
				PdfReader reader1 = new PdfReader(arr[i]);
				 n = reader1.getNumberOfPages();
			}
			catch(IOException e)
			{
				
			}
			
			if(n>0)
			{
				PdfReader reader = new PdfReader(arr[i]);
				int j = 0;
				while (j < n) {
					j++;
					document.setPageSize(reader.getPageSizeWithRotation(j));
					document.newPage();
					page = writer.getImportedPage(reader, j);
					rotation = reader.getPageRotation(j);
					if (rotation == 90 || rotation == 270) {
						cb.addTemplate(page, 0, -1f, 1f, 0, 0, reader.getPageSizeWithRotation(j).getHeight());
					} else {
						cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
					}
				}

			}
			
			else
			{
				try {
					Image img = null;
					img = Image.getInstance(arr[i]);
					img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
					document.setPageSize(PageSize.A4);
					document.newPage();
					document.add(img);
				} catch (Exception e) {
				}
			}
		}
		
			document.close();
			return baos.toByteArray();
		
	}
}
