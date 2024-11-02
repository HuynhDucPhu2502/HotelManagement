package iuh.fit.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import iuh.fit.dao.RoomUsageServiceDAO;
import iuh.fit.models.Invoice;
import iuh.fit.models.RoomUsageService;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class PDFHelper {
    public static void createInvoicePDF(Invoice invoice) throws DocumentException, IOException {
        ArrayList<RoomUsageService> roomUsageServices = (ArrayList<RoomUsageService>) RoomUsageServiceDAO
                .getByReservationFormID(invoice.getReservationForm().getReservationID());

        // Hiển thị hộp thoại để chọn đường dẫn và tên tệp
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu hóa đơn PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("HoaDon.pdf");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file == null) {
            throw new IllegalArgumentException("Bạn đã hủy bỏ quy trình tạo PDF");
        }

        // Tạo tài liệu PDF
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Load font hỗ trợ UTF-8
        InputStream fontStream = PDFHelper.class.getResourceAsStream("/iuh/fit/fonts/arial-unicode-ms.ttf");
        if (fontStream == null) {
            throw new IOException("Font not found");
        }
        BaseFont unicodeFont = BaseFont.createFont("arial-unicode-ms.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontStream.readAllBytes(), null);

        Font font = new Font(unicodeFont, 12);
        Font titleFont = new Font(unicodeFont, 18, Font.BOLD, BaseColor.BLACK);
        Font headerFont = new Font(unicodeFont, 12, Font.BOLD, BaseColor.BLACK);

        // ======================================================================
        // Thêm Watermark logo
        // ======================================================================
        InputStream watermarkPath = PDFHelper.class.getResourceAsStream("/iuh/fit/imgs/hotel_logo.png");
        if (watermarkPath == null) {
            throw new IOException("Watermark image not found");
        }
        Image watermarkLogo = Image.getInstance(watermarkPath.readAllBytes());
        float pageWidth = document.getPageSize().getWidth();
        float pageHeight = document.getPageSize().getHeight();
        watermarkLogo.scaleToFit(300, 300);
        float watermarkX = (pageWidth - watermarkLogo.getScaledWidth()) / 2;
        float watermarkY = (pageHeight - watermarkLogo.getScaledHeight()) / 2;
        watermarkLogo.setAbsolutePosition(watermarkX, watermarkY);

        PdfContentByte canvas = writer.getDirectContentUnder();
        canvas.saveState();
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.2f);
        canvas.setGState(gs1);
        canvas.addImage(watermarkLogo);
        canvas.restoreState();

        // ======================================================================
        // Phần đầu hóa đơn với bảng 3 cột
        // ======================================================================
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new int[]{1, 3, 1});

        // Cột 1: Logo
        InputStream logoPath = PDFHelper.class.getResourceAsStream("/iuh/fit/icons/login_panel_icons/ic_hotel.png");
        Image logo = Image.getInstance(Objects.requireNonNull(logoPath).readAllBytes());
        logo.scaleToFit(100, 100);
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(PdfPCell.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.addCell(logoCell);

        // Cột 2: Tiêu đề và ngày xuất hóa đơn
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(PdfPCell.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph titleParagraph = new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(titleParagraph);

        Paragraph dateParagraph = new Paragraph("Ngày " + invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), font);
        dateParagraph.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(dateParagraph);

        headerTable.addCell(titleCell);

        // Cột 3: Mã hóa đơn
        PdfPCell invoiceInfoCell = new PdfPCell();
        invoiceInfoCell.setBorder(PdfPCell.NO_BORDER);
        invoiceInfoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceInfoCell.addElement(new Paragraph("Mã hóa đơn: " + invoice.getInvoiceID(), new Font(unicodeFont, 12, Font.BOLD, BaseColor.BLACK)));
        headerTable.addCell(invoiceInfoCell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));

        // ======================================================================
        // Table khách sạn và khách hàng
        // ======================================================================
        PdfPTable infoTable = getPdfPTable(invoice, titleFont, font);
        document.add(infoTable);
        document.add(new Paragraph("\n"));

        // ======================================================================
        // Phần Thông Tin Phiếu Đặt Phòng
        // ======================================================================
        Paragraph bookingInfoTitle = new Paragraph("Thông Tin Phiếu Đặt Phòng", titleFont);
        document.add(bookingInfoTitle);
        document.add(new Paragraph("\n"));

        PdfPTable bookingInfoTable = new PdfPTable(2);
        bookingInfoTable.setWidthPercentage(100);

        PdfPCell leftCell = new PdfPCell(new Phrase("Số phòng: " + invoice.getReservationForm().getRoom().getRoomNumber(), font));
        leftCell.setBorder(PdfPCell.NO_BORDER);
        bookingInfoTable.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell(new Phrase("Loại phòng: " + invoice.getReservationForm().getRoom().getRoomCategory().getRoomCategoryName(), font));
        rightCell.setBorder(PdfPCell.NO_BORDER);
        bookingInfoTable.addCell(rightCell);

        leftCell = new PdfPCell(new Phrase("Ngày nhận phòng: " + invoice.getReservationForm().getCheckInDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), font));
        leftCell.setBorder(PdfPCell.NO_BORDER);
        bookingInfoTable.addCell(leftCell);

        rightCell = new PdfPCell(new Phrase("Ngày trả phòng: " + invoice.getReservationForm().getCheckOutDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), font));
        rightCell.setBorder(PdfPCell.NO_BORDER);
        bookingInfoTable.addCell(rightCell);

        leftCell = new PdfPCell(new Phrase("Số ngày lưu trú: " + Calculator.calculateStayLengthToString(invoice.getReservationForm().getCheckInDate(), invoice.getReservationForm().getCheckOutDate()), font));
        leftCell.setBorder(PdfPCell.NO_BORDER);
        bookingInfoTable.addCell(leftCell);

        rightCell = new PdfPCell(new Phrase(" ", font));  // Ô trống để cân bằng bảng
        rightCell.setBorder(PdfPCell.NO_BORDER);
        bookingInfoTable.addCell(rightCell);

        document.add(bookingInfoTable);
        document.add(new Paragraph("\n"));

        // ======================================================================
        // Bảng Dịch Vụ Đã Sử Dụng
        // ======================================================================
        Paragraph serviceTitle = new Paragraph("Dịch vụ đã sử dụng", titleFont);
        serviceTitle.setSpacingAfter(5f);
        document.add(serviceTitle);

        PdfPTable serviceTable = new PdfPTable(4);
        serviceTable.setWidthPercentage(100);
        serviceTable.setWidths(new int[]{3, 1, 2, 2});

        serviceTable.addCell(createBorderedCell("Tên dịch vụ", headerFont, Element.ALIGN_CENTER));
        serviceTable.addCell(createBorderedCell("SL", headerFont, Element.ALIGN_CENTER));
        serviceTable.addCell(createBorderedCell("Đơn giá (VND)", headerFont, Element.ALIGN_CENTER));
        serviceTable.addCell(createBorderedCell("Thành tiền (VND)", headerFont, Element.ALIGN_CENTER));

        for (RoomUsageService service : roomUsageServices) {
            serviceTable.addCell(createBorderedCell(service.getHotelService().getServiceName(), font, Element.ALIGN_LEFT));
            serviceTable.addCell(createBorderedCell(String.valueOf(service.getQuantity()), font, Element.ALIGN_CENTER));
            serviceTable.addCell(createBorderedCell(String.format("%,.0f", service.getUnitPrice()), font, Element.ALIGN_RIGHT));
            serviceTable.addCell(createBorderedCell(String.format("%,.0f", service.getQuantity() * service.getUnitPrice()), font, Element.ALIGN_RIGHT));
        }

        document.add(serviceTable);
        document.add(new Paragraph("\n"));

        // ======================================================================
        // Bảng Tổng Tiền
        // ======================================================================
        Paragraph paymentSummaryTitle = new Paragraph("Thống kê tiền", titleFont);
        paymentSummaryTitle.setSpacingAfter(5f);
        document.add(paymentSummaryTitle);

        double totalRoomCost = invoice.getRoomCharge();
        double deposit = invoice.getReservationForm().getRoomBookingDeposit();
        double taxRate = invoice.getTax().getTaxRate();
        PdfPTable totalTable = createTotalTable(invoice.getServicesCharge(), totalRoomCost, deposit, taxRate, headerFont, font);
        document.add(totalTable);

        document.close();
        System.out.println("PDF created successfully.");
    }

    private static PdfPTable getPdfPTable(Invoice invoice, Font titleFont, Font font) {
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(5f);

        PdfPCell hotelCell = new PdfPCell(new Phrase("Khách sạn Chức Phú Gia Tiến", titleFont));
        hotelCell.setBorder(PdfPCell.NO_BORDER);
        hotelCell.setPaddingBottom(10f);
        infoTable.addCell(hotelCell);

        PdfPCell customerCell = new PdfPCell(new Phrase("Thông tin khách hàng", titleFont));
        customerCell.setBorder(PdfPCell.NO_BORDER);
        customerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        customerCell.setPaddingBottom(10f);
        infoTable.addCell(customerCell);

        hotelCell = new PdfPCell(new Phrase("Địa chỉ: 120 Xóm Chiếu, P14, Q4, TPHCM", font));
        hotelCell.setBorder(PdfPCell.NO_BORDER);
        infoTable.addCell(hotelCell);

        customerCell = new PdfPCell(new Phrase("Tên khách hàng: " + invoice.getReservationForm().getCustomer().getFullName(), font));
        customerCell.setBorder(PdfPCell.NO_BORDER);
        customerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoTable.addCell(customerCell);

        hotelCell = new PdfPCell(new Phrase("Số điện thoại: (84) 123-456-789", font));
        hotelCell.setBorder(PdfPCell.NO_BORDER);
        infoTable.addCell(hotelCell);

        customerCell = new PdfPCell(new Phrase("Email: " + invoice.getReservationForm().getCustomer().getEmail(), font));
        customerCell.setBorder(PdfPCell.NO_BORDER);
        customerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoTable.addCell(customerCell);

        hotelCell = new PdfPCell(new Phrase("", font));
        hotelCell.setBorder(PdfPCell.NO_BORDER);
        infoTable.addCell(hotelCell);

        customerCell = new PdfPCell(new Phrase("SĐT: " + invoice.getReservationForm().getCustomer().getPhoneNumber(), font));
        customerCell.setBorder(PdfPCell.NO_BORDER);
        customerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoTable.addCell(customerCell);
        return infoTable;
    }

    private static PdfPTable createTotalTable(double totalServiceCost, double totalRoomCost, double deposit, double taxRate, Font headerFont, Font font) {
        PdfPTable totalTable = new PdfPTable(2); // 2 columns
        totalTable.setWidthPercentage(100);
        try {
            totalTable.setWidths(new int[]{3, 2});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        double totalDue = totalServiceCost + totalRoomCost - deposit;
        double taxAmount = totalDue * taxRate;
        double netDue = totalDue + taxAmount;

        // Header row
        PdfPCell titleCellLeft = new PdfPCell(new Phrase("Tên tiền thanh toán", headerFont));
        titleCellLeft.setBorder(PdfPCell.BOTTOM);
        titleCellLeft.setPadding(8f);
        titleCellLeft.setHorizontalAlignment(Element.ALIGN_LEFT);
        totalTable.addCell(titleCellLeft);

        PdfPCell titleCellRight = new PdfPCell(new Phrase("Số tiền", headerFont));
        titleCellRight.setBorder(PdfPCell.BOTTOM);
        titleCellRight.setPadding(8f);
        titleCellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(titleCellRight);

        // Dòng "Tổng tiền dịch vụ"
        totalTable.addCell(createAlignedCell("Tổng tiền dịch vụ", font, Element.ALIGN_LEFT));
        totalTable.addCell(createAlignedCell(String.format("%,.0f VND", totalServiceCost), font, Element.ALIGN_RIGHT));

        // Dòng "Tổng tiền phòng"
        totalTable.addCell(createAlignedCell("Tổng tiền phòng", font, Element.ALIGN_LEFT));
        totalTable.addCell(createAlignedCell(String.format("%,.0f VND", totalRoomCost), font, Element.ALIGN_RIGHT));

        // Dòng "Tiền đặt cọc"
        totalTable.addCell(createAlignedCell("Tiền đặt cọc", font, Element.ALIGN_LEFT));
        totalTable.addCell(createAlignedCell("-" + String.format("%,.0f VND", deposit), font, Element.ALIGN_RIGHT));

        // Dòng "Tổng tiền" với border trên dày hơn
        PdfPCell totalTitleCell = createAlignedCell("Tổng tiền", font, Element.ALIGN_LEFT);
        totalTitleCell.setBorderWidthTop(2f);
        totalTable.addCell(totalTitleCell);

        PdfPCell totalDueCell = createAlignedCell(String.format("%,.0f VND", totalDue), font, Element.ALIGN_RIGHT);
        totalDueCell.setBorderWidthTop(2f);
        totalTable.addCell(totalDueCell);

        // Dòng "Thuế"
        totalTable.addCell(createAlignedCell("Thuế (" + taxRate * 100 + "%)", font, Element.ALIGN_LEFT));
        totalTable.addCell(createAlignedCell(String.format("%,.0f VND", taxAmount), font, Element.ALIGN_RIGHT));

        // Dòng "Thành tiền" với border trên dày hơn
        PdfPCell finalTitleCell = createAlignedCell("Thành tiền", headerFont, Element.ALIGN_LEFT);
        finalTitleCell.setBorderWidthTop(2f);
        totalTable.addCell(finalTitleCell);

        PdfPCell netDueCell = createAlignedCell(String.format("%,.0f VND", netDue), headerFont, Element.ALIGN_RIGHT);
        netDueCell.setBorderWidthTop(2f);
        totalTable.addCell(netDueCell);

        return totalTable;
    }

    private static PdfPCell createAlignedCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static PdfPCell createBorderedCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(1f);
        return cell;
    }



}
