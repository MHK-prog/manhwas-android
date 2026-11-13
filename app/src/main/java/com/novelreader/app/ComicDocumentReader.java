package com.novelreader.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Renders PDF pages and images stored in ZIP/CBZ archives without unpacking them. */
final class ComicDocumentReader implements AutoCloseable {
    private static final int MAX_ARCHIVE_IMAGES = 20000;
    private static final long MAX_IMAGE_BYTES = 128L * 1024L * 1024L;
    private static final long MAX_DECODE_PIXELS = 7_000_000L;
    private static final int MAX_RENDER_HEIGHT = 5000;

    private final ParcelFileDescriptor descriptor;
    private final PdfRenderer pdfRenderer;
    private final ZipFile zipFile;
    private final List<String> imageEntries;

    private ComicDocumentReader(ParcelFileDescriptor descriptor, PdfRenderer pdfRenderer,
                                ZipFile zipFile, List<String> imageEntries) {
        this.descriptor = descriptor;
        this.pdfRenderer = pdfRenderer;
        this.zipFile = zipFile;
        this.imageEntries = imageEntries;
    }

    static ComicDocumentReader open(File file) throws IOException {
        String name = file.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".pdf")) {
            ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            try {
                return new ComicDocumentReader(descriptor, new PdfRenderer(descriptor), null, Collections.emptyList());
            } catch (IOException | RuntimeException error) {
                descriptor.close();
                throw error;
            }
        }

        ZipFile zip = new ZipFile(file);
        try {
            List<String> images = new ArrayList<>();
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !isImageName(entry.getName())) continue;
                if (entry.getSize() < 0 || entry.getSize() > MAX_IMAGE_BYTES) continue;
                images.add(entry.getName());
                if (images.size() > MAX_ARCHIVE_IMAGES) throw new IOException("The archive contains too many pages.");
            }
            images.sort(ComicDocumentReader::compareNatural);
            if (images.isEmpty()) throw new IOException("No readable images were found in the archive.");
            return new ComicDocumentReader(null, null, zip, images);
        } catch (IOException | RuntimeException error) {
            zip.close();
            throw error;
        }
    }

    int pageCount() {
        return pdfRenderer != null ? pdfRenderer.getPageCount() : imageEntries.size();
    }

    Bitmap renderPage(int index, int targetWidth) throws IOException {
        if (index < 0 || index >= pageCount()) throw new IOException("Page number is out of range.");
        int widthLimit = Math.max(320, Math.min(2400, targetWidth));
        if (pdfRenderer != null) return renderPdfPage(index, widthLimit);
        return renderImagePage(index, widthLimit);
    }

    private Bitmap renderPdfPage(int index, int widthLimit) throws IOException {
        PdfRenderer.Page page = pdfRenderer.openPage(index);
        try {
            double scale = Math.min(widthLimit / (double) page.getWidth(),
                    MAX_RENDER_HEIGHT / (double) page.getHeight());
            int width = Math.max(1, (int) Math.round(page.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(page.getHeight() * scale));
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Matrix transform = new Matrix();
            transform.setScale((float) scale, (float) scale);
            page.render(bitmap, new Rect(0, 0, width, height), transform,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            return bitmap;
        } catch (RuntimeException error) {
            throw new IOException("Could not render this PDF page.", error);
        } finally {
            page.close();
        }
    }

    private Bitmap renderImagePage(int index, int widthLimit) throws IOException {
        ZipEntry entry = zipFile.getEntry(imageEntries.get(index));
        if (entry == null || entry.getSize() > MAX_IMAGE_BYTES) throw new IOException("This image page is too large.");
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream input = zipFile.getInputStream(entry)) {
            BitmapFactory.decodeStream(input, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0
                || (long) bounds.outWidth * bounds.outHeight > 250_000_000L) {
            throw new IOException("This archive entry is not a supported image.");
        }

        int sample = 1;
        while (bounds.outWidth / sample > widthLimit * 2
                || bounds.outHeight / sample > MAX_RENDER_HEIGHT
                || ((long) bounds.outWidth / sample) * ((long) bounds.outHeight / sample) > MAX_DECODE_PIXELS) {
            sample *= 2;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try (InputStream input = zipFile.getInputStream(entry)) {
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            if (bitmap == null) throw new IOException("Could not decode this image page.");
            return bitmap;
        }
    }

    private static boolean isImageName(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                || lower.endsWith(".webp") || lower.endsWith(".gif") || lower.endsWith(".bmp");
    }

    private static int compareNatural(String left, String right) {
        int a = 0;
        int b = 0;
        while (a < left.length() && b < right.length()) {
            char ca = left.charAt(a);
            char cb = right.charAt(b);
            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                int aEnd = a;
                int bEnd = b;
                while (aEnd < left.length() && Character.isDigit(left.charAt(aEnd))) aEnd++;
                while (bEnd < right.length() && Character.isDigit(right.charAt(bEnd))) bEnd++;
                int aSignificant = a;
                int bSignificant = b;
                while (aSignificant < aEnd - 1 && left.charAt(aSignificant) == '0') aSignificant++;
                while (bSignificant < bEnd - 1 && right.charAt(bSignificant) == '0') bSignificant++;
                int aLength = aEnd - aSignificant;
                int bLength = bEnd - bSignificant;
                if (aLength != bLength) return Integer.compare(aLength, bLength);
                int digits = left.regionMatches(aSignificant, right, bSignificant, aLength)
                        ? 0 : left.substring(aSignificant, aEnd).compareTo(right.substring(bSignificant, bEnd));
                if (digits != 0) return digits;
                a = aEnd;
                b = bEnd;
                continue;
            }
            int comparison = Character.compare(Character.toLowerCase(ca), Character.toLowerCase(cb));
            if (comparison != 0) return comparison;
            a++;
            b++;
        }
        return Integer.compare(left.length() - a, right.length() - b);
    }

    @Override public void close() throws IOException {
        IOException failure = null;
        if (pdfRenderer != null) {
            try { pdfRenderer.close(); }
            catch (RuntimeException error) { failure = new IOException("Could not close PDF renderer.", error); }
        }
        if (descriptor != null) {
            try { descriptor.close(); }
            catch (IOException error) { if (failure == null) failure = error; }
        }
        if (zipFile != null) {
            try { zipFile.close(); }
            catch (IOException error) { if (failure == null) failure = error; }
        }
        if (failure != null) throw failure;
    }
}
