package kasogg.com.imageselector.resourceselect.model;

import java.io.Serializable;
import java.util.List;

/**
 * 相册对象
 */
public class ResourceBucket implements Serializable {
    private static final long serialVersionUID = -6930574978158998742L;

    public int count = 0;
    public String bucketName;
    public List<ResourceItem> imageList;
    public boolean selected = false;
}
