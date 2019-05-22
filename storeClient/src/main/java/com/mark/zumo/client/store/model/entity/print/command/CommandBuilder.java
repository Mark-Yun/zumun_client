package com.mark.zumo.client.store.model.entity.print.command;

import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 19. 5. 22.
 */
public class CommandBuilder {
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_DEFAULT = -1;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 2;

    public static final int BARCODE_TYPE_CODABAR = 6;
    public static final int BARCODE_TYPE_CODE128 = 8;
    public static final int BARCODE_TYPE_CODE39 = 4;
    public static final int BARCODE_TYPE_CODE93 = 7;
    public static final int BARCODE_TYPE_ITF = 5;
    public static final int BARCODE_TYPE_JAN13_EAN13 = 2;
    public static final int BARCODE_TYPE_JAN8_EAN8 = 3;
    public static final int BARCODE_TYPE_UPC_A = 0;
    public static final int BARCODE_TYPE_UPC_E = 1;

    public static final int MODE = 0;

    public static final int PAPER_WIDTH_58MM = 384;
    public static final int PAPER_WIDTH_80MM = 576;

    private List<byte[]> mCommand = new ArrayList<>();

    public List<byte[]> build() {
        return this.mCommand;
    }

    public CommandBuilder setBitmap(final Bitmap paramBitmap) {
        if (paramBitmap != null) {
            int width = PAPER_WIDTH_58MM;
            byte[] arrayOfByte;
//            if (PreferenceUtil.getInstance(null).getSettingsUse58mm()) {
//                width = '��';
//            } else {
//                width = '��';
//            }
            if (width <= paramBitmap.getWidth()) {
                arrayOfByte = PrintPicture.POS_PrintBMP(paramBitmap, width, 0);
            } else {
                arrayOfByte = PrintPicture.POS_PrintBMP(paramBitmap, paramBitmap.getWidth(), 0);
            }
            if (arrayOfByte != null) {
                this.mCommand.add(PrinterCommand.commandInit());
                this.mCommand.add(arrayOfByte);
                this.mCommand.add(PrinterCommand.commandInit());
            }
        }
        return this;
    }

    public CommandBuilder setFeedLine() {
        this.mCommand.add(PrinterCommand.commandInit());
        this.mCommand.add(PrinterCommand.setFeed());
        return this;
    }

    public CommandBuilder setNewLine(final int line) {
        int counter = line;
        while (counter-- > 0) {
            try {
                this.mCommand.add("\n".getBytes("GBK"));
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
                unsupportedEncodingException.printStackTrace();
                break;
            }
        }
        return this;
    }

    public CommandBuilder setString(final List<String> stringList, final int paramInt, final boolean paramBoolean) {
        if (stringList != null && !stringList.isEmpty()) {
            if (paramInt != -1) {
                byte[] arrayOfByte = (byte[]) Command.ESC_Align.clone();
                if (paramInt == 0) {
                    arrayOfByte[2] = (byte) 0;
                } else if (paramInt == 1) {
                    arrayOfByte[2] = (byte) 1;
                } else if (paramInt == 2) {
                    arrayOfByte[2] = (byte) 2;
                }
                this.mCommand.add(arrayOfByte);
            }
            for (String str : stringList) {
                this.mCommand.add(str.getBytes());
                this.mCommand.add("\n".getBytes());
                if (paramBoolean) {
                    this.mCommand.add("--------------------------------".getBytes());
                }
            }
        }
        return this;
    }
}
