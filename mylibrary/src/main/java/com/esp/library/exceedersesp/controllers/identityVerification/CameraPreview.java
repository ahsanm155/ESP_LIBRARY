package com.esp.library.exceedersesp.controllers.identityVerification;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Iterator;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mContext = context;
        mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    private int getFrontFacingCameraId() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // create the surface and start camera preview
            if (mCamera == null) {
                mCamera.setDisplayOrientation(CameraManager.getCameraOrientation());
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            mCamera.setDisplayOrientation(CameraManager.getCameraOrientation());
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        refreshCamera(mCamera);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //setMeasuredDimension(width, width);
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
        try {
            Camera.Parameters param;
            param = camera.getParameters();

            Camera.Size bestSize = null;
            List<Camera.Size> sizeList = param.getSupportedPictureSizes();
            bestSize = sizeList.get(0);
            for (int i = 1; i < sizeList.size(); i++) {
                Log.d("Size", "" + sizeList.get(i).width + "," + sizeList.get(i).height);
                if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                    bestSize = sizeList.get(i);
                }
            }
            Log.d("Best Size", "" + bestSize.width + "," + bestSize.height);

            param.setPictureSize(bestSize.width, bestSize.height);
            List<Integer> supportedPreviewFormats = param.getSupportedPreviewFormats();
            Iterator<Integer> supportedPreviewFormatsIterator = supportedPreviewFormats.iterator();
            while (supportedPreviewFormatsIterator.hasNext()) {
                Integer previewFormat = supportedPreviewFormatsIterator.next();
                if (previewFormat == ImageFormat.JPEG) {
                    param.setPreviewFormat(previewFormat);
                }
            }


            List<Camera.Size> previewSizeList = param.getSupportedPreviewSizes();
            bestSize = null;
            bestSize = previewSizeList.get(0);
            for (int i = 1; i < previewSizeList.size(); i++) {
                if ((previewSizeList.get(i).width * previewSizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                    bestSize = previewSizeList.get(i);
                }
            }

            param.setPreviewSize(bestSize.width, bestSize.height);
            if (param.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            mCamera.setParameters(param);
        }
        catch (Exception e){
            Log.e("camera",e.getMessage());
        }
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //mCamera.release();

    }


}