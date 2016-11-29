package com.example.ocr;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenCV {

    private static final Logger LOG = LoggerFactory.getLogger(OpenCV.class);
    
    private OpenCV() {
    }

    public static void main(String[] args) {
        String filename = "F:/20151215152519.png";
        IplImage img = cvLoadImage(filename);

        IplImage greyImg = grey(img);
        cvSaveImage("F:/greyImg.png", greyImg);

        IplImage bzImg = binaryzation(greyImg);
        cvSaveImage("F:/bzImg.png", bzImg);

        IplImage dilateImg = dilate(bzImg, 4);
        cvSaveImage("F:/dilateImg.png", dilateImg);

        IplImage edgesImg = edges(dilateImg);
        cvSaveImage("F:/edgesImg.png", edgesImg);

        // IplImage lineImg = line(edgesImg);
        // cvSaveImage("F:/lineImg.png", lineImg);

        IplImage rotateImg = rotate(greyImg, -4);
        cvSaveImage("F:/rotateImg.png", rotateImg);
    }

    /**
     * 灰度处理
     * 
     * @param src
     * @return
     */
    public static IplImage grey(IplImage src) {

        // 将RGB色彩空间转换成BGR色彩空间 8位 3通道
        IplImage pImg = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 3);
        /*
         * src是源图像； dst是转换后的图像； flags是转换的模式，可以取0：没有变化；1：垂直翻转，即沿x轴翻转；2：交换红蓝信道；
         */
        cvConvertImage(src, pImg, 2);

        // 将RGB转换成Gray度图
        IplImage pGrayImg = cvCreateImage(cvGetSize(pImg), IPL_DEPTH_8U, 1);
        cvCvtColor(pImg, pGrayImg, CV_RGB2GRAY);
        cvReleaseImage(pImg);
        return pGrayImg;

    }

    /**
     * 二值化处理
     * 
     * @param src
     * @return
     */
    public static IplImage binaryzation(IplImage src) {

        IplImage pImg = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 0);
        // CvScalar s;
        // for (int i = 0; i < src.height(); i++) {
        // for (int j = 0; j < src.width(); j++) {
        // s = cvGet2D(src, i, j);
        // if (s.val(0) <= 127) {
        // s.val(0, 256);
        // } else {
        // s.val(0, 0);
        // }
        // cvSet2D(pImg, i, j, s);
        // }
        //
        // }
        cvThreshold(src, pImg, 127, 255, CV_THRESH_BINARY_INV);
        return pImg;
    }

    /**
     * 膨胀处理
     * 
     * @param src
     * @param times
     * @return
     */
    public static IplImage dilate(IplImage src, int times) {
        IplImage dst = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1);
        cvDilate(src, dst, null, times);
        return dst;
    }

    /**
     * 边缘检测处理
     * 
     * @param src
     * @return
     */
    public static IplImage edges(IplImage src) {
        IplImage edges = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1);
        cvCanny(src, edges, 50, 200, 3);
        return edges;
    }

    public static IplImage line(IplImage src) {
        IplImage dst = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 3);
        // cvCvtColor(src, dst, CV_GRAY2BGR);
        CvMat lineStorage = cvCreateMat(100, 1, CV_32SC4);
        CvSeq lines = cvHoughLines2(src, lineStorage, CV_HOUGH_STANDARD, 1, Math.PI / 180, 150);
        for (int i = 0; i < lines.total(); i++) {
            try (FloatPointer line = new FloatPointer(cvGetSeqElem(lines, i))) {
                cvLine(src, new CvPoint(line.position(0)), new CvPoint(line.position(1)), CV_RGB(0, 255, 0), 1, CV_AA,
                        0);
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        return dst;
    }

    /**
     * 旋转修正（倾斜）
     * 
     * @param src
     * @param angle
     *            旋转角度
     * @return
     */
    public static IplImage rotate(IplImage src, double angle) {
        //angle = -4;
        // int w =
        // Double.valueOf(src.width()-src.height()*Math.sin(angle)).intValue();
        // int h =
        // Double.valueOf(src.height()-src.width()*Math.cos(angle)).intValue();

        IplImage dst = IplImage.create(src.width(), src.height(), src.depth(), src.nChannels());
        CvPoint2D32f point = cvPoint2D32f(0, 0); // 旋转点
        Pointer data = point;
        CvMat mapMatrix = cvMat(2, 3, CV_32F, data);
        cv2DRotationMatrix(point, angle, 1, mapMatrix);
        int flags = CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS;
        cvWarpAffine(src, dst, mapMatrix, flags, cvScalarAll(0));
        return dst;
    }
}
