package kasogg.com.imageselector.resourceselect.imagefetcher;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kasogg.com.imageselector.resourceselect.ResourceSelectActivity;
import kasogg.com.imageselector.resourceselect.model.ResourceBucket;
import kasogg.com.imageselector.resourceselect.model.ResourceItem;

/**
 * 图片工具类
 */
public class ResourceFetcher {
    private static HashMap<String, ResourceBucket> mBucketMap = new HashMap<>();
    private static List<ResourceBucket> mImageList = new ArrayList<>();
    private static List<ResourceBucket> mVideoList = new ArrayList<>();
    private static int mFileType;

    public static List<ResourceBucket> getBucketList(Context context, int fileType) {
        return getBucketList(context, true, fileType);
    }

    /**
     * 得到图片集
     *
     * @param refresh 是否需要重新加载图库
     * @return List<ImageBucket>
     */
    public static List<ResourceBucket> getBucketList(Context context, boolean refresh, int fileType) {
        mFileType = fileType;
        if (refresh) {
            buildBucketMap(context, fileType);
            if (mFileType == ResourceSelectActivity.FILE_TYPE_IMAGE) {
                mImageList = new ArrayList<>();
            } else {
                mVideoList = new ArrayList<>();
            }
            for (Map.Entry<String, ResourceBucket> entry : mBucketMap.entrySet()) {
                ResourceBucket bucket = entry.getValue();
                Collections.sort(bucket.imageList);
                getCurrentList().add(bucket);
            }
        }
        return getCurrentList();
    }

    private static List<ResourceBucket> getCurrentList() {
        if (mFileType == ResourceSelectActivity.FILE_TYPE_IMAGE) {
            return mImageList;
        } else if (mFileType == ResourceSelectActivity.FILE_TYPE_VIDEO) {
            return mVideoList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 得到图片集
     */
    private static void buildBucketMap(Context context, int fileType) {
        String columns[] = new String[]{Media._ID, Media.BUCKET_ID, Media.DATE_MODIFIED, Media.DATA, Media.BUCKET_DISPLAY_NAME, Media.SIZE};
        Uri queryUri = Media.EXTERNAL_CONTENT_URI;
        if (fileType == ResourceSelectActivity.FILE_TYPE_VIDEO) {
            columns = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.SIZE};
            queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        mBucketMap = new LinkedHashMap<>();
        ResourceBucket bucketAll = new ResourceBucket();
        bucketAll.bucketName = mFileType == ResourceSelectActivity.FILE_TYPE_IMAGE ? "所有图片" : "所有视频";
        bucketAll.selected = true;
        bucketAll.imageList = new ArrayList<>();
        mBucketMap.put("-1", bucketAll);

        Cursor cursor = null;
        try {
            long startTime = System.currentTimeMillis();
            cursor = context.getContentResolver().query(queryUri, columns, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // 获取指定列的索引
                int photoIDIndex = cursor.getColumnIndexOrThrow(Media._ID);
                int photoPathIndex = cursor.getColumnIndexOrThrow(Media.DATA);
                int photoDateIndex = cursor.getColumnIndexOrThrow(Media.DATE_MODIFIED);
                int bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
                int bucketIdIndex = cursor.getColumnIndexOrThrow(Media.BUCKET_ID);
                int imageSizeIndex = cursor.getColumnIndexOrThrow(Media.SIZE);

                do {
                    if (cursor.getLong(imageSizeIndex) <= 0) { //过滤掉大小为0的损坏的图片
                        continue;
                    }
                    String _id = cursor.getString(photoIDIndex);
                    String path = cursor.getString(photoPathIndex);
                    long modifyDate = cursor.getLong(photoDateIndex);
                    String bucketName = cursor.getString(bucketDisplayNameIndex);
                    String bucketId = cursor.getString(bucketIdIndex);

                    ResourceBucket bucket = mBucketMap.get(bucketId);
                    if (bucket == null) {
                        bucket = new ResourceBucket();
                        mBucketMap.put(bucketId, bucket);
                        bucket.imageList = new ArrayList<>();
                        bucket.bucketName = bucketName;
                    }
                    ResourceItem resourceItem = new ResourceItem();
                    resourceItem.imageId = _id;
                    resourceItem.sourcePath = path;
                    resourceItem.modifyDate = modifyDate;
                    if (!bucket.imageList.contains(resourceItem)) {
                        bucket.imageList.add(resourceItem);
                        bucket.count++;
                        bucketAll.imageList.add(resourceItem);
                        bucketAll.count++;
                    }

                } while (cursor.moveToNext());
            }

            long endTime = System.currentTimeMillis();
            Log.d(ResourceFetcher.class.getName(), "use time: " + (endTime - startTime) + " ms");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
