package org.nutz.plugins.zdoc.msword;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.nutz.img.Images;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.plugins.zdoc.NutDSet;

public class LocalMsWordDSetRender extends AbstractMsWordDSetRender {

    private File dSrc;

    private File fTa;

    @Override
    protected MsWordImageInfo readPictureInfo(String aph, boolean openStream) {
        MsWordImageInfo mwii = new MsWordImageInfo();

        File fImg = Files.getFile(dSrc, aph);
        if (!fImg.exists())
            throw Lang.makeThrow("e.zdoc.msword.imgnoexist : %s", aph);

        // 得到图像类型
        String suffixName = Files.getSuffixName(aph);
        if (null == suffixName) {
            throw Lang.makeThrow("e.zdoc.msword.imgnotype : %s", aph);
        }
        String imgType = suffixName.toLowerCase();
        if (imgType.equals("jpg") || imgType.equals("jpeg")) {
            mwii.pictureType = XWPFDocument.PICTURE_TYPE_JPEG;
        } else if (imgType.equals("gif")) {
            mwii.pictureType = XWPFDocument.PICTURE_TYPE_GIF;
        } else if (imgType.equals("png")) {
            mwii.pictureType = XWPFDocument.PICTURE_TYPE_PNG;
        } else {
            throw Lang.makeThrow("e.zdoc.msword.invalidImageType : %s", aph);
        }

        // 解析图像信息
        BufferedImage im = Images.read(fImg);
        mwii.fileName = Files.getName(aph);
        mwii.width = im.getWidth();
        mwii.height = im.getHeight();

        // 打开输入流
        if (openStream) {
            mwii.ins = Streams.buff(Streams.fileIn(fImg));
        }

        // 返回结果
        return mwii;
    }

    @Override
    protected void writeToTarget(XWPFDocument wdDoc) {
        OutputStream ops = Streams.buff(Streams.fileOut(fTa));
        try {
            wdDoc.write(ops);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(ops);
        }
    }

    @Override
    protected void checkTarget(String target) {
        this.fTa = Files.createFileIfNoExists2(target);
    }

    @Override
    protected void checkPrimerObj(NutDSet ds) {
        this.dSrc = (File) ds.getPrimerObj();
        if (null == dSrc || !dSrc.exists() || !dSrc.isDirectory()) {
            throw Lang.makeThrow("e.zdoc.html.render.dsrc_invalid", dSrc);
        }
    }

}
