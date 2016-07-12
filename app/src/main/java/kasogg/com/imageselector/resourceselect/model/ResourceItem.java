package kasogg.com.imageselector.resourceselect.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class ResourceItem implements Serializable, Comparable, Parcelable {
    private static final long serialVersionUID = -1488567541115305971L;

    public String imageId;
    public String sourcePath;
    public String compressedPath;
    public long modifyDate;
    public boolean isSelected;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceItem resourceItem = (ResourceItem) o;
        return sourcePath.equals(resourceItem.sourcePath);

    }

    @Override
    public int hashCode() {
        return sourcePath.hashCode();
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (!(another instanceof ResourceItem)) {
            return -1;
        }
        ResourceItem item = (ResourceItem) another;
        return (int) (item.modifyDate - modifyDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageId);
        dest.writeString(this.sourcePath);
        dest.writeString(this.compressedPath);
        dest.writeLong(this.modifyDate);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public ResourceItem() {
    }

    protected ResourceItem(Parcel in) {
        this.imageId = in.readString();
        this.sourcePath = in.readString();
        this.compressedPath = in.readString();
        this.modifyDate = in.readLong();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ResourceItem> CREATOR = new Parcelable.Creator<ResourceItem>() {
        @Override
        public ResourceItem createFromParcel(Parcel source) {
            return new ResourceItem(source);
        }

        @Override
        public ResourceItem[] newArray(int size) {
            return new ResourceItem[size];
        }
    };
}
