import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.qount.invoice.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
 
/**
 *
 * @author iText
 */
public class RupeeSymbol {
 
    public static final String DEST = "F:/1.pdf";
    public static final String FONT1 = "PlayfairDisplay-Regular.ttf";
    public static final String RUPEE = "\u20B9 "+"12.21";
 
    public static void main(String[] args) throws IOException, DocumentException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new RupeeSymbol().createPdf(DEST);
    }
 
    public void createPdf(String dest) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(DEST));
        document.open();
        Font f2 = FontFactory.getFont(Constants.FONT1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
        
        PdfPTable table = new PdfPTable(1);
        Paragraph c10 = new Paragraph(RUPEE, f2);
		PdfPCell cell_10 = new PdfPCell(c10);
		cell_10.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell_10);
		document.add(table);
		
        document.close();
    }
}