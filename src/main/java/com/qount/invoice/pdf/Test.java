package com.qount.invoice.pdf;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class Test {

	/** The resulting PDF. */
	public static final String RESULT = "F:/1.pdf";
 
    /**
     * Main method.
     *
     * @param    args    no arguments needed
     * @throws DocumentException 
     * @throws IOException
     */
    public static void main(String[] args)
        throws IOException, DocumentException {
    	// step 1
        Document document = new Document(new Rectangle(200, 120));
        // step 2
        PdfWriter writer
             = PdfWriter.getInstance(document, new FileOutputStream(RESULT));
        // step 3
        document.open();
        // step 4
        PdfContentByte canvas = writer.getDirectContent();
        // state 1:
//        canvas.setRGBColorFill(0xFF, 0x45, 0x00);
        // fill a rectangle in state 1
        canvas.roundRectangle(10f,10,100f,100f,10f);
//        canvas.fill();
//        canvas.saveState();
//        // state 2;
//        canvas.setLineWidth(3);
//        canvas.setRGBColorFill(0x8B, 0x00, 0x00);
//        // fill and stroke a rectangle in state 2
//        canvas.rectangle(40, 20, 60, 60);
//        canvas.fillStroke();
//        canvas.saveState();
//        // state 3:
//        canvas.concatCTM(1, 0, 0.1f, 1, 0, 0);
//        canvas.setRGBColorStroke(0xFF, 0x45, 0x00);
//        canvas.setRGBColorFill(0xFF, 0xD7, 0x00);
//        // fill and stroke a rectangle in state 3
//        canvas.rectangle(70, 30, 60, 60);
//        canvas.fillStroke();
//        canvas.restoreState();
//        // stroke a rectangle in state 2
//        canvas.rectangle(100, 40, 60, 60);
        canvas.stroke();
//        canvas.restoreState();
//        // fill and stroke a rectangle in state 1
//        canvas.rectangle(130, 50, 60, 60);
//        canvas.fillStroke();
//        // step 5
        document.close();
    }
}
